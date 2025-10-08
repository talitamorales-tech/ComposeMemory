package com.talitamorales.composememory.gamelogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.talitamorales.composememory.R

data class Card(
    var id: Int,
    val imageRes: Int,
    val soundRes: Int?
) {
    var isFaceUp by mutableStateOf(false)
    var isMatched by mutableStateOf(false)
}

fun createCards(): List<Card> {
    val images: List<Pair<Int, Int?>> = listOf(
        Pair(R.drawable.dog, R.raw.dog),
        Pair(R.drawable.dog, R.raw.dog),
        Pair(R.drawable.cat, R.raw.cat),
        Pair(R.drawable.cat, R.raw.cat),
        Pair(R.drawable.lion, R.raw.lion),
        Pair(R.drawable.lion, R.raw.lion),
        Pair(R.drawable.elephant, R.raw.elefant),
        Pair(R.drawable.elephant, R.raw.elefant)
    )
    val ids = (1..5000).shuffled().take(images.count())
    return images.shuffled().mapIndexed {index, pair -> Card(id = ids[index], imageRes = pair.first, soundRes = pair.second)}
}
