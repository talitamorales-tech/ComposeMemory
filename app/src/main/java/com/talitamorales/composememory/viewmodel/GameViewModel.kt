package com.talitamorales.composememory.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.createCards
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {


    var cards = mutableStateListOf<Card>()
        private set

    var isMemorizing by mutableStateOf(true)
        private set

    private var selectedCards = mutableListOf<Card>()
    var gameWon  by mutableStateOf(false)
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        cards.clear()
        cards.addAll(createCards())
        selectedCards.clear()
        gameWon = false

        cards.forEach{it.isFaceUp = true}
        isMemorizing = true
        viewModelScope.launch {
            delay(5000)
            cards.forEach{it.isFaceUp = false}
            isMemorizing = false
        }
    }

    fun onCardClicked(card: Card) {
        if (card.isFaceUp || card.isMatched || selectedCards.size >= 2) return

        card.isFaceUp = true
        selectedCards.add(card)

        if (selectedCards.size == 2) {
            val first = selectedCards[0]
            val second = selectedCards[1]

            viewModelScope.launch {
                delay(800)
                if (first.imageRes == second.imageRes) {
                    first.isMatched = true
                    second.isMatched = true
                } else {
                    first.isFaceUp = false
                    second.isFaceUp = false
                }
                selectedCards.clear()
                if (cards.all { it.isMatched }) gameWon = true
            }

        }
    }
}