package com.talitamorales.composememory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talitamorales.composememory.viewmodel.GameViewModel

/**
 * TODO: document your custom view class.
 */
@Composable
fun OnboardScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            text = "Choose a theme",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(modifier = Modifier.size(width = 150.dp, height = 40.dp),
            onClick = {
                navController.navigate("memoryGame/1")
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
            Text("Animals")
        }
        Button(modifier = Modifier.size(width = 150.dp, height = 40.dp),
            onClick = {
                navController.navigate("memoryGame/2")
            },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
            Text("Toys")
        }
    }
}