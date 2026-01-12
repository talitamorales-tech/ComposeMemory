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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R
import com.talitamorales.composememory.ui.theme.PinkCardFaceUp
import com.talitamorales.composememory.ui.theme.PurpleCardFaceDown


@Composable
fun MemoryCard(card: Card, isMemorizing: Boolean, onClick: () -> Unit) {

    var isFlipped by remember(card.id) { mutableStateOf(card.isFaceUp || card.isMatched) }
    val targetFlip = card.isFaceUp || card.isMatched

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
               .background(
                   color = if (card.isFaceUp || card.isMatched) PinkCardFaceUp else PurpleCardFaceDown,
                   shape = RoundedCornerShape(12.dp)
               ),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFaceUp || card.isMatched) {
                Image(
                    painter = painterResource(id = card.imageRes),
                    contentDescription = "null",
                    modifier = Modifier.size(85.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.card_back),
                    contentDescription = "null",
                    modifier = Modifier.size(85.dp),
                    contentScale = ContentScale.Fit
                )
            }

        }
    }
}




