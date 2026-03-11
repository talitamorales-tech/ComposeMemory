package com.talitamorales.composememory.gamelogic


import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.talitamorales.composememory.R
import com.talitamorales.composememory.ui.theme.PinkCardFaceUp

private const val MEMORY_CARD_TAG = "CM-MemoryCard"

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
                    shape = RoundedCornerShape(12.dp)
                )

            )
            .clickable (enabled = !isMemorizing && !card.isMatched) { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
           modifier = Modifier
               .fillMaxSize()
               .clip(RoundedCornerShape(12.dp))
               .background(
                   color = if (card.isFaceUp || card.isMatched) PinkCardFaceUp else Color.Transparent,
                   ),
            contentAlignment = Alignment.Center
        ) {

            if (card.isFaceUp || card.isMatched) {
                val isAnimalThemeImage = card.soundRes != null
                val imageModifier = if (isAnimalThemeImage) {
                    Modifier.fillMaxSize(0.72f)
                } else {
                    Modifier.fillMaxSize(0.88f)
                }
                val imageResToRender = remember(card.imageRes) {
                    if (isDrawableLoadable(context, card.imageRes)) {
                        card.imageRes
                    } else {
                        Log.e(
                            MEMORY_CARD_TAG,
                            "Unsupported drawable for card imageRes=${card.imageRes}; using fallback"
                        )
                        R.drawable.memory_friends_logo
                    }
                }

                AndroidView(
                    factory = { viewContext ->
                        ImageView(viewContext).apply {
                            scaleType = ImageView.ScaleType.FIT_CENTER
                            adjustViewBounds = true
                        }
                    },
                    modifier = if (imageResToRender == R.drawable.memory_friends_logo) {
                        Modifier.fillMaxSize(0.8f)
                    } else {
                        imageModifier
                    },
                    update = { imageView ->
                        imageView.setImageResource(imageResToRender)
                    }
                )
            } else {
                FantasyCardBack(modifier = Modifier.fillMaxSize())
            }
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
private fun FantasyCardBack(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF170821),
                        Color(0xFF6A2AB4),
                        Color(0xFF2D0E48)
                    )
                )
            )
            .border(1.5.dp, Color(0xFFF3CF83), RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x8A64B9FF),
                            Color(0x55321860),
                            Color(0x00250F45)
                        )
                    )
                )
                .border(1.dp, Color(0x88FFE7B8), RoundedCornerShape(8.dp))
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
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
                .size(14.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0A1))
        )
    }
}
