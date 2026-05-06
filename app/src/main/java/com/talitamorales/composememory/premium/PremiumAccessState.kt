package com.talitamorales.composememory.premium

data class PremiumAccessState(
    val ownsUnlockAllThemesNoAds: Boolean = false,
    val isLoading: Boolean = false,
    val canPurchase: Boolean = true
) {
    val hasPremiumAccess: Boolean
        get() = ownsUnlockAllThemesNoAds

    val shouldShowAds: Boolean
        get() = !hasPremiumAccess
}
