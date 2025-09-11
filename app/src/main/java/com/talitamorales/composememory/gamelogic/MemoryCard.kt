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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.R

@Composable
fun MemoryCard(card: Card, isMemorizing: Boolean, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .size(80.dp)
            .clickable { if (!isMemorizing) onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
           modifier = Modifier
               .fillMaxSize()
               .background(if (card.isFaceUp || card.isMatched) Color(0xFFE1BEE7) else Color(0xFF7B1FA2)),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFaceUp || card.isMatched) {
                Image(
                    painter = painterResource(id = card.imageRes),
                    contentDescription = "Animal",
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.card_back),
                    contentDescription = "Card Back",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


