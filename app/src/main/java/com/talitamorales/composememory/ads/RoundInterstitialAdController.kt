package com.talitamorales.composememory.ads

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

private const val ROUND_INTERSTITIAL_LOG_TAG = "CM-RoundInterstitial"
private const val ROUND_INTERSTITIAL_INTERVAL = 3
private const val INTERSTITIAL_MAX_CACHE_AGE_MS = 60 * 60 * 1000L

class RoundInterstitialAdController(
    private val appContext: Context,
    private val adUnitId: String
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var isShowing = false
    private var completedRoundsSinceLastShow = 0
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

        isLoading = true
        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    loadedAtMillis = SystemClock.elapsedRealtime()
                    isLoading = false
                    Log.d(ROUND_INTERSTITIAL_LOG_TAG, "Round interstitial loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    loadedAtMillis = 0L
                    isLoading = false
                    Log.w(
                        ROUND_INTERSTITIAL_LOG_TAG,
                        "Round interstitial failed to load code=${loadAdError.code}"
                    )
                }
            }
        )
    }

    fun showAfterRoundIfAllowed(
        context: Context,
        onContinue: () -> Unit
    ) {
        if (!adsEnabled) {
            onContinue()
            return
        }

        completedRoundsSinceLastShow += 1

        if (completedRoundsSinceLastShow < ROUND_INTERSTITIAL_INTERVAL || isShowing) {
            preload()
            onContinue()
            return
        }

        val activity = context.findActivity()
        val ad = interstitialAd.takeIf { isAdReady() }
        if (activity == null || ad == null) {
            preload()
            onContinue()
            return
        }

        interstitialAd = null
        loadedAtMillis = 0L
        isShowing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowing = false
                completedRoundsSinceLastShow = 0
                preload()
                onContinue()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isShowing = false
                Log.w(
                    ROUND_INTERSTITIAL_LOG_TAG,
                    "Round interstitial failed to show code=${adError.code}"
                )
                preload()
                onContinue()
            }
        }
        ad.show(activity)
    }

    fun clear() {
        interstitialAd = null
        isLoading = false
        isShowing = false
        loadedAtMillis = 0L
    }

    private fun isAdReady(): Boolean {
        val ad = interstitialAd ?: return false
        val isFresh = SystemClock.elapsedRealtime() - loadedAtMillis < INTERSTITIAL_MAX_CACHE_AGE_MS
        if (!isFresh) {
            interstitialAd = null
            loadedAtMillis = 0L
        }
        return ad != null && isFresh
    }
}

@Composable
fun rememberRoundInterstitialAdController(adUnitId: String): RoundInterstitialAdController {
    return rememberRoundInterstitialAdController(
        adUnitId = adUnitId,
        adsEnabled = true
    )
}

@Composable
fun rememberRoundInterstitialAdController(
    adUnitId: String,
    adsEnabled: Boolean
): RoundInterstitialAdController {
    val appContext = LocalContext.current.applicationContext
    val controller = remember(appContext, adUnitId) {
        RoundInterstitialAdController(
            appContext = appContext,
            adUnitId = adUnitId
        )
    }

    LaunchedEffect(controller, adsEnabled) {
        controller.setAdsEnabled(adsEnabled)
        if (adsEnabled) {
            controller.preload()
        }
    }

    DisposableEffect(controller) {
        onDispose {
            controller.clear()
        }
    }

    return controller
}
