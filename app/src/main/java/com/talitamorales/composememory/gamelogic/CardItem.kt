package com.talitamorales.composememory.gamelogic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {

    Surface(
        onClick = onClick,
        modifier = Modifier.size(70.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (card.isFaceUp || card.isMatched)
            Color.White
        else
            Color(0xFF1565C0)
    ) {
        Box (contentAlignment = Alignment.Center){
            if (card.isFaceUp || card.isMatched) {
                Text(text = card.value, style = MaterialTheme.typography.headlineMedium, color = Color.Black)
            }
        }
    }
}