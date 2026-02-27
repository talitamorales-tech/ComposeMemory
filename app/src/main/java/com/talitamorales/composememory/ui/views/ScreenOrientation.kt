package com.talitamorales.composememory.ui.views

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun LockScreenOrientation(orientation: Int) {
    val activity = LocalContext.current.findActivity()
    DisposableEffect(activity) {
        val previousOrientation = activity?.requestedOrientation
            ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        onDispose {
            activity?.requestedOrientation = previousOrientation
        }
    }
    SideEffect {
        if (activity != null && activity.requestedOrientation != orientation) {
            activity.requestedOrientation = orientation
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
