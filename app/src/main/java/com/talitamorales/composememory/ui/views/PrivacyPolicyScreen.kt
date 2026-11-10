package com.talitamorales.composememory.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val policy = remember(context) {
        context.assets.open("privacy-policy.txt").bufferedReader().use { it.readText() }
    }
    BackHandler(onBack = onBack)
    Surface {
        Column(
            Modifier.fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(20.dp)
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.privacy_back)) }
            Text(
                text = policy,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            )
        }
    }
}
