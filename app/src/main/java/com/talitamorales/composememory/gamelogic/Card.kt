package com.talitamorales.composememory.gamelogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Card(
    val id: Int,
    val imageRes: Int
) {
    var isFaceUp by mutableStateOf(false)
    var isMatched by mutableStateOf(false)
}

fun createCards(): List<Card> {
    val images = listOf("A", "A", "B", "B", "C", "C", "D", "D")
    return images.shuffled().mapIndexed { index, resId -> Card(index, resId) }
}
