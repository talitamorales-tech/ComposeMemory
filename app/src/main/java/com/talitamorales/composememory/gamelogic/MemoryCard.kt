package com.talitamorales.composememory.gamelogic


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R
import com.talitamorales.composememory.ui.theme.PinkCardFaceUp
import com.talitamorales.composememory.ui.theme.PurpleCardFaceDown


@Composable
fun MemoryCard(card: Card, isMemorizing: Boolean, onClick: () -> Unit) {

    //var isFlipped by remember(card.id) { mutableStateOf(card.isFaceUp || card.isMatched) }
    //val targetFlip = card.isFaceUp || card.isMatched

    Card(
        modifier = Modifier
            .size(130.dp)
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
                   color = if (card.isFaceUp || card.isMatched) PinkCardFaceUp else PurpleCardFaceDown,
                   ),
            contentAlignment = Alignment.Center
        ) {

            val imageRes = if (card.isFaceUp || card.isMatched) {
                card.imageRes
            } else {
                R.drawable.card_back
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(85.dp),
                contentScale = ContentScale.Fit
            )

        }
    }
}




