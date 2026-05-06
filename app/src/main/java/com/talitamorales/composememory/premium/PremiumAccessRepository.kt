package com.talitamorales.composememory.premium

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumAccessRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "premium_access",
        Context.MODE_PRIVATE
    )

    private val _state = MutableStateFlow(loadState())
    val state: StateFlow<PremiumAccessState> = _state.asStateFlow()

    val currentState: PremiumAccessState
        get() = _state.value

    fun buyUnlockAllThemesNoAds() {
        preferences.edit()
            .putBoolean(KEY_UNLOCK_ALL_THEMES_NO_ADS, true)
            .apply()
        _state.value = loadState()
    }

    fun restorePurchases() {
        _state.value = loadState()
    }

    private fun loadState(): PremiumAccessState {
        return PremiumAccessState(
            ownsUnlockAllThemesNoAds = preferences.getBoolean(
                KEY_UNLOCK_ALL_THEMES_NO_ADS,
                false
            ),
            isLoading = false,
            canPurchase = true
        )
    }

    companion object {
        private const val KEY_UNLOCK_ALL_THEMES_NO_ADS = PremiumProducts.UnlockAllThemesNoAds
    }
}
