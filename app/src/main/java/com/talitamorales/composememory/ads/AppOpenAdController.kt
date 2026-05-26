package com.talitamorales.composememory.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

private const val APP_OPEN_MAX_CACHE_AGE_MS = 4 * 60 * 60 * 1000L
private const val MIN_BACKGROUND_TIME_BEFORE_APP_OPEN_MS = 30 * 1000L

class AppOpenAdController(
    private val appContext: Context,
    private val adUnitId: String
) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoading = false
    private var isShowing = false
    private var wasBackgrounded = false
    private var backgroundedAtMillis = 0L
    private var loadedAtMillis = 0L
    private var adsEnabled = true

    fun setAdsEnabled(enabled: Boolean) {
        if (adsEnabled == enabled) return

        adsEnabled = enabled
        if (enabled) {
            preload()
        } else {
            clear()
        }
    }

    fun preload() {
        if (!adsEnabled || isLoading || isAdReady()) return
        if (!appContext.hasValidatedInternetConnection()) return

        isLoading = true
        AppOpenAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    loadedAtMillis = SystemClock.elapsedRealtime()
                    isLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    loadedAtMillis = 0L
                    isLoading = false
                }
            }
        )
    }

    fun markAppBackgrounded() {
        if (!adsEnabled) return
        if (isShowing) return

        wasBackgrounded = true
        backgroundedAtMillis = SystemClock.elapsedRealtime()
    }

    fun showOnReturnIfAvailable(activity: Activity) {
        if (!adsEnabled) return

        val now = SystemClock.elapsedRealtime()
        val backgroundDuration = now - backgroundedAtMillis
        val canShowAfterReturn = wasBackgrounded &&
            backgroundDuration >= MIN_BACKGROUND_TIME_BEFORE_APP_OPEN_MS

        wasBackgrounded = false
        if (!canShowAfterReturn || isShowing) {
            preload()
            return
        }

        val ad = appOpenAd.takeIf { isAdReady() }
        if (ad == null) {
            preload()
            return
        }

        appOpenAd = null
        loadedAtMillis = 0L
        isShowing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isShowing = false
                preload()
            }
        }
        ad.show(activity)
    }

    fun clear() {
        appOpenAd = null
        isLoading = false
        isShowing = false
        wasBackgrounded = false
        backgroundedAtMillis = 0L
        loadedAtMillis = 0L
    }

    private fun isAdReady(): Boolean {
        val ad = appOpenAd ?: return false
        val isFresh = SystemClock.elapsedRealtime() - loadedAtMillis < APP_OPEN_MAX_CACHE_AGE_MS
        if (!isFresh) {
            appOpenAd = null
            loadedAtMillis = 0L
        }
        return ad != null && isFresh
    }
}
