package com.talitamorales.composememory.audio

import android.util.Log
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.talitamorales.composememory.R

private const val LOOPING_AUDIO_TAG = "CM-GameAudio"

@Composable
fun LoopingGameMusicEffect(
    @RawRes musicResId: Int,
    volume: Float,
    paused: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mediaVolumeMutedToast = stringResource(id = R.string.toast_media_volume_muted)
    var isSoundEnabled by remember(context) {
        mutableStateOf(GameSoundPreferences.isSoundEnabled(context))
    }
    var isAppInForeground by remember { mutableStateOf(true) }
    var warnedAboutMutedMediaVolume by remember { mutableStateOf(false) }

    LaunchedEffect(context, isSoundEnabled) {
        val mediaVolumeInfo = getGameMediaVolumeInfo(context)
        if (isSoundEnabled && mediaVolumeInfo?.isMuted == true && !warnedAboutMutedMediaVolume) {
            warnedAboutMutedMediaVolume = true
            Toast.makeText(context, mediaVolumeMutedToast, Toast.LENGTH_LONG).show()
        } else if (mediaVolumeInfo?.isMuted == false) {
            warnedAboutMutedMediaVolume = false
        }
    }

    DisposableEffect(context) {
        val listener = GameSoundPreferences.registerSoundEnabledListener(context) { enabled ->
            isSoundEnabled = enabled
        }
        onDispose {
            GameSoundPreferences.unregisterSoundEnabledListener(context, listener)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        isAppInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> isAppInForeground = true
                Lifecycle.Event.ON_STOP -> isAppInForeground = false
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(context, musicResId, volume, isSoundEnabled, paused, isAppInForeground) {
        if (!isSoundEnabled || !isAppInForeground) {
            Log.d(
                LOOPING_AUDIO_TAG,
                "Music skipped soundEnabled=$isSoundEnabled foreground=$isAppInForeground"
            )
            SharedGameMusicPlayer.stop(reason = if (isSoundEnabled) "background" else "sound-disabled")
            return@LaunchedEffect
        }

        if (paused) {
            SharedGameMusicPlayer.pause()
        } else {
            SharedGameMusicPlayer.start(
                context = context.applicationContext,
                musicResId = musicResId,
                volume = volume
            )
        }
    }

}
