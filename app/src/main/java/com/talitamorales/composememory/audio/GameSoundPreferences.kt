package com.talitamorales.composememory.audio

import android.content.Context
import android.content.SharedPreferences

object GameSoundPreferences {
    private const val PREFERENCES_NAME = "game_sound_preferences"
    private const val KEY_SOUND_ENABLED = "sound_enabled"

    fun isSoundEnabled(context: Context): Boolean {
        return preferences(context).getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        preferences(context)
            .edit()
            .putBoolean(KEY_SOUND_ENABLED, enabled)
            .apply()
    }

    fun registerSoundEnabledListener(
        context: Context,
        onChanged: (Boolean) -> Unit
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_SOUND_ENABLED) {
                onChanged(isSoundEnabled(context))
            }
        }
        preferences(context).registerOnSharedPreferenceChangeListener(listener)
        return listener
    }

    fun unregisterSoundEnabledListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        preferences(context).unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun preferences(context: Context) =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
}
