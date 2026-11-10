package com.talitamorales.composememory.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.RawRes

private const val GAME_AUDIO_TAG = "CM-GameAudio"

data class GameMediaVolumeInfo(
    val currentVolume: Int,
    val maxVolume: Int
) {
    val isMuted: Boolean = currentVolume <= 0
}

private fun gameMusicAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
}

private fun gameAudioManager(context: Context): AudioManager? {
    return context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
}

fun getGameMediaVolumeInfo(context: Context): GameMediaVolumeInfo? {
    val audioManager = gameAudioManager(context) ?: return null
    return GameMediaVolumeInfo(
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    )
}

class GameAudioFocusHandle internal constructor(
    private val audioManager: AudioManager,
    private val focusRequest: AudioFocusRequest?
) {
    fun abandon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
            audioManager.abandonAudioFocusRequest(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }
}

fun requestGameAudioFocus(context: Context): GameAudioFocusHandle? {
    val audioManager = gameAudioManager(context)
    if (audioManager == null) {
        Log.e(GAME_AUDIO_TAG, "AudioManager unavailable")
        return null
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(gameMusicAudioAttributes())
            .setAcceptsDelayedFocusGain(false)
            .build()
        val result = audioManager.requestAudioFocus(focusRequest)
        Log.d(GAME_AUDIO_TAG, "Audio focus result=$result")
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            GameAudioFocusHandle(audioManager, focusRequest)
        } else {
            null
        }
    } else {
        @Suppress("DEPRECATION")
        val result = audioManager.requestAudioFocus(
            null,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        Log.d(GAME_AUDIO_TAG, "Audio focus result=$result")
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            GameAudioFocusHandle(audioManager, null)
        } else {
            null
        }
    }
}

fun createGameMusicPlayer(
    context: Context,
    @RawRes musicResId: Int,
    volume: Float,
    looping: Boolean = true
): MediaPlayer? {
    return runCatching {
        val safeVolume = volume.coerceIn(0f, 1f)
        val mediaVolumeInfo = getGameMediaVolumeInfo(context)
        val player = MediaPlayer.create(
            context,
            musicResId,
            gameMusicAudioAttributes(),
            0
        )?.apply {
            isLooping = looping
            setVolume(safeVolume, safeVolume)
            setOnErrorListener { mediaPlayer, what, extra ->
                Log.e(
                    GAME_AUDIO_TAG,
                    "MediaPlayer error musicResId=$musicResId what=$what extra=$extra"
                )
                mediaPlayer.safeStopAndRelease()
                true
            }
        }
        checkNotNull(player) { "MediaPlayer.create returned null for musicResId=$musicResId" }
        Log.d(
            GAME_AUDIO_TAG,
            "Prepared musicResId=$musicResId looping=$looping volume=$safeVolume " +
                "mediaVolume=${mediaVolumeInfo?.currentVolume}/${mediaVolumeInfo?.maxVolume} " +
                "mediaMuted=${mediaVolumeInfo?.isMuted}"
        )
        player
    }.onFailure { error ->
        Log.e(GAME_AUDIO_TAG, "Failed to prepare musicResId=$musicResId", error)
    }.getOrNull()
}

fun MediaPlayer?.startGameAudio(@RawRes musicResId: Int): Boolean {
    if (this == null) {
        Log.e(GAME_AUDIO_TAG, "Cannot start null player musicResId=$musicResId")
        return false
    }

    return runCatching {
        if (!isPlaying) {
            start()
        }
        Log.d(GAME_AUDIO_TAG, "Started musicResId=$musicResId isPlaying=$isPlaying")
        true
    }.onFailure { error ->
        Log.e(GAME_AUDIO_TAG, "Failed to start musicResId=$musicResId", error)
    }.getOrDefault(false)
}

fun MediaPlayer?.safeStopAndRelease(): MediaPlayer? {
    if (this == null) return null
    runCatching {
        if (isPlaying) {
            stop()
        }
    }
    runCatching { release() }
    return null
}

object SharedGameMusicPlayer {
    private var musicPlayer: MediaPlayer? = null
    private var audioFocus: GameAudioFocusHandle? = null
    private var currentMusicResId: Int? = null

    @Synchronized
    fun start(
        context: Context,
        @RawRes musicResId: Int,
        volume: Float
    ) {
        val appContext = context.applicationContext
        val safeVolume = volume.coerceIn(0f, 1f)

        if (musicPlayer == null || currentMusicResId != musicResId) {
            stop(reason = "switch-track")
            audioFocus = requestGameAudioFocus(appContext)
            musicPlayer = createGameMusicPlayer(
                context = appContext,
                musicResId = musicResId,
                volume = safeVolume,
                looping = true
            )
            currentMusicResId = musicResId
        } else {
            musicPlayer?.setVolume(safeVolume, safeVolume)
            if (audioFocus == null) {
                audioFocus = requestGameAudioFocus(appContext)
            }
        }

        if (!musicPlayer.startGameAudio(musicResId)) {
            stop()
        }
    }

    @Synchronized
    fun pause() {
        runCatching {
            if (musicPlayer?.isPlaying == true) {
                musicPlayer?.pause()
            }
        }
        audioFocus?.abandon()
        audioFocus = null
    }

    @Synchronized
    fun stop(reason: String = "manual") {
        Log.d(GAME_AUDIO_TAG, "Stopping shared music reason=$reason hasPlayer=${musicPlayer != null}")
        musicPlayer = musicPlayer.safeStopAndRelease()
        audioFocus?.abandon()
        audioFocus = null
        currentMusicResId = null
    }
}
