package com.talitamorales.composememory.ui.views

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

internal fun Modifier.playgroundSkyBackground(): Modifier = drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF86D9FF),
                Color(0xFFBCEFFF),
                Color(0xFFEAFBFF)
            ),
            startY = 0f,
            endY = size.height
        )
    )

    fun drawCloud(centerX: Float, centerY: Float, radius: Float, alpha: Float) {
        val cloudColor = Color.White.copy(alpha = alpha)
        drawCircle(cloudColor, radius * 0.82f, Offset(centerX - radius * 0.86f, centerY + radius * 0.08f))
        drawCircle(cloudColor, radius, Offset(centerX, centerY - radius * 0.1f))
        drawCircle(cloudColor, radius * 0.72f, Offset(centerX + radius * 0.88f, centerY + radius * 0.12f))
        drawCircle(cloudColor, radius * 0.58f, Offset(centerX + radius * 1.45f, centerY + radius * 0.22f))
    }

    val minDimension = size.minDimension
    drawCloud(
        centerX = size.width * 0.18f,
        centerY = size.height * 0.12f,
        radius = minDimension * 0.075f,
        alpha = 0.56f
    )
    drawCloud(
        centerX = size.width * 0.78f,
        centerY = size.height * 0.2f,
        radius = minDimension * 0.06f,
        alpha = 0.46f
    )

    val groundTop = size.height * 0.72f
    val waveHeight = size.height * 0.055f
    val groundPath = Path().apply {
        moveTo(0f, groundTop + waveHeight * 0.2f)
        cubicTo(
            size.width * 0.18f,
            groundTop - waveHeight,
            size.width * 0.36f,
            groundTop + waveHeight * 1.05f,
            size.width * 0.55f,
            groundTop + waveHeight * 0.2f
        )
        cubicTo(
            size.width * 0.72f,
            groundTop - waveHeight * 0.75f,
            size.width * 0.88f,
            groundTop + waveHeight * 0.86f,
            size.width,
            groundTop
        )
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    drawPath(
        path = groundPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFBFF29B),
                Color(0xFF7ADB73),
                Color(0xFF42B967)
            ),
            startY = groundTop,
            endY = size.height
        )
    )

    val hillPath = Path().apply {
        moveTo(0f, size.height * 0.82f)
        cubicTo(
            size.width * 0.22f,
            size.height * 0.76f,
            size.width * 0.44f,
            size.height * 0.89f,
            size.width * 0.68f,
            size.height * 0.82f
        )
        cubicTo(
            size.width * 0.82f,
            size.height * 0.78f,
            size.width * 0.94f,
            size.height * 0.81f,
            size.width,
            size.height * 0.79f
        )
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    drawPath(
        path = hillPath,
        color = Color(0xFF5CCF73).copy(alpha = 0.55f)
    )
}
