package com.talitamorales.composememory.gamelogic

import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.talitamorales.composememory.R

private const val MEMORY_CARD_TAG = "CM-MemoryCard"

private val MemoryCardShape = RoundedCornerShape(16.dp)
private val MemoryCardInnerShape = RoundedCornerShape(13.dp)

@Composable
fun MemoryCard(
    card: Card,
    isMemorizing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .then(
                glowingBorderModifier(
                    isVisible = card.isMatched,
                    shape = MemoryCardShape
                )
            )
            .clickable(enabled = !isMemorizing && !card.isMatched) { onClick() },
        shape = MemoryCardShape,
        elevation = CardDefaults.cardElevation(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(MemoryCardShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF2B8),
                            Color(0xFFD888FF),
                            Color(0xFF74BFFF)
                        )
                    )
                )
                .border(1.3.dp, Color.White.copy(alpha = 0.8f), MemoryCardShape)
                .padding(2.dp)
        ) {
            if (card.isFaceUp || card.isMatched) {
                val isDanceThemeImage = remember(card.imageRes) {
                    Card.danceAssets.any { asset -> asset.first == card.imageRes }
                }
                val imageModifier = if (isDanceThemeImage) {
                    Modifier
                        .fillMaxSize(0.98f)
                        .scale(1.18f)
                } else {
                    Modifier
                        .fillMaxSize(0.94f)
                        .scale(1.04f)
                }

                val imageResToRender = remember(card.imageRes) {
                    if (isDrawableLoadable(context, card.imageRes)) {
                        card.imageRes
                    } else {
                        Log.e(
                            MEMORY_CARD_TAG,
                            "Unsupported drawable for card imageRes=${card.imageRes}; using fallback"
                        )
                        R.drawable.memory_friends_logo_banner
                    }
                }

                PremiumCardFront(
                    imageResToRender = imageResToRender,
                    imageModifier = imageModifier
                )
            } else {
                PremiumCardBack(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun PremiumCardFront(
    imageResToRender: Int,
    imageModifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MemoryCardInnerShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFAEC),
                        Color(0xFFFFEAF8),
                        Color(0xFFE1EFFF)
                    )
                )
            )
            .border(1.dp, Color(0xFFFFF9D2), MemoryCardInnerShape)
            .cardDust(alpha = 0.14f)
            .padding(7.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .height(26.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.26f))
                .border(1.dp, Color.White.copy(alpha = 0.66f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { viewContext ->
                    ImageView(viewContext).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        adjustViewBounds = true
                    }
                },
                modifier = if (imageResToRender == R.drawable.memory_friends_logo_banner) {
                    Modifier.fillMaxSize(0.96f).scale(1.2f)
                } else {
                    imageModifier
                },
                update = { imageView ->
                    imageView.setImageResource(imageResToRender)
                }
            )
        }
    }
}

private fun isDrawableLoadable(
    context: android.content.Context,
    drawableRes: Int
): Boolean {
    return runCatching { context.resources.getDrawable(drawableRes, context.theme) != null }
        .getOrDefault(false)
}

@Composable
private fun PremiumCardBack(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MemoryCardInnerShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF170821),
                        Color(0xFF6A2AB4),
                        Color(0xFF2D0E48)
                    )
                )
            )
            .border(1.3.dp, Color(0xFFF3CF83), MemoryCardInnerShape)
            .cardDust(alpha = 0.2f)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(9.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x8A64B9FF),
                            Color(0x55321860),
                            Color(0x00250F45)
                        )
                    )
                )
                .border(1.dp, Color(0x88FFE7B8), RoundedCornerShape(9.dp))
        )

        listOf(
            Alignment.TopStart,
            Alignment.TopEnd,
            Alignment.BottomStart,
            Alignment.BottomEnd
        ).forEach { alignment ->
            Box(
                modifier = Modifier
                    .align(alignment)
                    .padding(5.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0x44FFE5AA))
                    .border(0.8.dp, Color(0x99FFDFA2), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF4CF),
                            Color(0xFF63B9FF),
                            Color(0xFF5C209F)
                        )
                    )
                )
                .border(1.dp, Color(0xFFFFE8B4), CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0A1))
        )
    }
}

private fun Modifier.cardDust(alpha: Float = 0.18f): Modifier = drawBehind {
    val points = listOf(
        0.08f to 0.16f,
        0.22f to 0.11f,
        0.34f to 0.2f,
        0.48f to 0.14f,
        0.64f to 0.21f,
        0.8f to 0.12f,
        0.9f to 0.2f,
        0.14f to 0.42f,
        0.29f to 0.5f,
        0.47f to 0.38f,
        0.61f to 0.49f,
        0.79f to 0.44f,
        0.11f to 0.75f,
        0.3f to 0.82f,
        0.49f to 0.72f,
        0.68f to 0.84f,
        0.86f to 0.76f
    )

    points.forEachIndexed { index, (x, y) ->
        val radius = size.minDimension * if (index % 4 == 0) 0.011f else 0.006f
        drawCircle(
            color = Color.White.copy(alpha = if (index % 2 == 0) alpha else alpha * 0.68f),
            radius = radius,
            center = Offset(size.width * x, size.height * y)
        )
    }
}
