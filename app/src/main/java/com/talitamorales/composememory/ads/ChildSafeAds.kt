package com.talitamorales.composememory.ads

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

fun configureChildSafeAds() {
    // Apply before initialization or any ad request. Use child treatment for everyone,
    // including adults, so a shared device never enables personalized advertising.
    MobileAds.setRequestConfiguration(
        MobileAds.getRequestConfiguration()
            .toBuilder()
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
    )
}
