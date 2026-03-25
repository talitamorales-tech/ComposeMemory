package com.talitamorales.composememory.gamelogic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun glowingBorderModifier(
    isVisible: Boolean,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier {
    if (!isVisible) return Modifier

    val transition = rememberInfiniteTransition(label = "glow")

    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = LinearEasing
            )
        ),
        label = "offset"
    )

    return Modifier.border(
        width = 4.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700), // dourado
                Color(0xFFB388FF), // lilás
                Color(0xFF80D8FF),
                Color(0xFFFFA726),
                Color(0xFFFF80AB)
            ),
            start = Offset(offset, 0f),
            end = Offset(offset + 200f, 200f)
        ),
        shape = shape
    )
}