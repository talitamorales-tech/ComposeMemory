package com.talitamorales.composememory.gamelogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.talitamorales.composememory.R

data class Card(
    val id: Int,
    val imageRes: Int
) {
    var isFaceUp by mutableStateOf(false)
    var isMatched by mutableStateOf(false)
}

fun createCards(): List<Card> {
    val images = listOf(
        R.drawable.dog,
        R.drawable.dog,
        R.drawable.cat,
        R.drawable.cat,
        R.drawable.lion,
        R.drawable.lion,
        R.drawable.elephant,
        R.drawable.elephant
    )
    return images.shuffled().mapIndexed {index, resId -> Card(id = index, imageRes = resId)}
}
