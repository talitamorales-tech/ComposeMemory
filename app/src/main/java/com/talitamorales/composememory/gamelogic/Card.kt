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
    companion object {
        val animalsAssets : List<Pair<Int, Int>> = listOf(
            Pair(R.drawable.dog, R.raw.dog),
            Pair(R.drawable.dog, R.raw.dog),
            Pair(R.drawable.cat, R.raw.cat),
            Pair(R.drawable.cat, R.raw.cat),
            Pair(R.drawable.lion, R.raw.lion),
            Pair(R.drawable.lion, R.raw.lion),
            Pair(R.drawable.elephant, R.raw.elefant),
            Pair(R.drawable.elephant, R.raw.elefant)
        )
        val toysAssets: List<Pair<Int, Int?>> = listOf(
            Pair(R.drawable.ball, null),
            Pair(R.drawable.ball, null),
            Pair(R.drawable.doll, null),
            Pair(R.drawable.doll, null),
            Pair(R.drawable.toy_car, null),
            Pair(R.drawable.toy_car, null),
            Pair(R.drawable.plane, null),
            Pair(R.drawable.plane, null)
        )
    }
    var isFaceUp by mutableStateOf(false)
    var isMatched by mutableStateOf(false)
}

fun createCards(cards: List<Pair<Int, Int?>>): List<Card> {
    val ids = (1..5000).shuffled().take(cards.count())
    return cards.shuffled().mapIndexed {index, pair -> Card(id = ids[index], imageRes = pair.first, soundRes = pair.second)}
}



