package com.talitamorales.composememory.gamelogic

data class Card(
    val id: Int,
    val value: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

fun createCards(): List<Card> {
    val values = listOf("A", "A", "B", "B", "C", "C", "D", "D")
    return values.shuffled().mapIndexed { index, value -> Card(index, value) }
}
