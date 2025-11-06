package com.talitamorales.composememory.gamelogic


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R
import com.talitamorales.composememory.ui.theme.PinkCardFaceUp
import com.talitamorales.composememory.ui.theme.PurpleCardFaceDown
import kotlinx.coroutines.delay


@Composable
fun MemoryCard(card: Card, isMemorizing: Boolean, onClick: () -> Unit) {

    var isFlipped by remember(card.id) { mutableStateOf(card.isFaceUp || card.isMatched) }
    val targetFlip = card.isFaceUp || card.isMatched

    // Animação suave de 0° a 180°
    val rotation by animateFloatAsState(
        targetValue = if (targetFlip) 180f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "flip"
    )

    // Atualiza o estado interno apenas quando a animação termina
    LaunchedEffect(targetFlip) {
        if (!targetFlip) {
            delay(300) // espera metade da animação para "esconder" a frente
        }
        isFlipped = targetFlip
    }

    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable (enabled = !isMemorizing && !card.isMatched) { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
           modifier = Modifier
               .fillMaxSize()
               .graphicsLayer {
                   rotationY = rotation
                   cameraDistance = 12f * density // essencial para 3D
               }
               .background(
                   color = if (rotation <= 90f) PurpleCardFaceDown else PinkCardFaceUp,
                   shape = RoundedCornerShape(12.dp)
               ),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f && (card.isFaceUp || card.isMatched)) {
                Image(
                    painter = painterResource(id = card.imageRes),
                    contentDescription = "Animal",
                    modifier = Modifier.size(85.dp)
                        .graphicsLayer { rotationY = 0f }
                )
            }
            if (rotation >= 90f && !(card.isFaceUp || card.isMatched)) {
                Image(
                    painter = painterResource(id = R.drawable.card_back),
                    contentDescription = "Card Back",
                    modifier = Modifier.size(85.dp)
                        .graphicsLayer { rotationY = 80f }
                )
            }
        }
    }
}




