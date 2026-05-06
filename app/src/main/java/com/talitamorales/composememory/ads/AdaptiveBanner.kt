package com.talitamorales.composememory.ads

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val ADAPTIVE_BANNER_LOG_TAG = "CM-AdaptiveBanner"

enum class BannerLoadState {
    Loading,
    Loaded,
    Failed
}

@Composable
fun AdaptiveBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
    onLoadStateChanged: (BannerLoadState) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val context = LocalContext.current
        val widthDp = maxWidth.value.toInt().coerceAtLeast(1)

        if (widthDp <= 1) return@BoxWithConstraints

        val adSize = remember(context, widthDp) {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
        }
        val bannerHeight = adSize.height.coerceAtLeast(50).dp
        var loadState by remember(adUnitId, widthDp) { mutableStateOf(BannerLoadState.Loading) }

        LaunchedEffect(loadState) {
            onLoadStateChanged(loadState)
        }

        key(adUnitId, widthDp) {
            AndroidView(
                factory = { androidContext ->
                    AdView(androidContext).apply {
                        setSaveEnabled(false)
                        setSaveFromParentEnabled(false)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setAdSize(adSize)
                        this.adUnitId = adUnitId
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                loadState = BannerLoadState.Loaded
                                Log.d(
                                    ADAPTIVE_BANNER_LOG_TAG,
                                    "Banner loaded unit=$adUnitId size=${adSize.width}x${adSize.height}"
                                )
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                loadState = BannerLoadState.Failed
                                Log.w(
                                    ADAPTIVE_BANNER_LOG_TAG,
                                    "Banner failed unit=$adUnitId code=${loadAdError.code} " +
                                        "domain=${loadAdError.domain} message=${loadAdError.message} " +
                                        "response=${loadAdError.responseInfo}"
                                )
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (loadState == BannerLoadState.Loaded) bannerHeight else 1.dp)
                    .alpha(if (loadState == BannerLoadState.Loaded) 1f else 0f),
                onRelease = { adView -> adView.destroy() }
            )
        }
    }
}
