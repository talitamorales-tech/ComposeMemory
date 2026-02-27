package com.talitamorales.composememory.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.createCards
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val VIEWMODEL_DEBUG_TAG = "CM-GameViewModel"

class GameViewModel( private val themeId: Int) : ViewModel(), GameViewModelContract {
    final override var cards = mutableStateListOf<Card>()
        private set

    override var isMemorizing by mutableStateOf(true)

    private var selectedCards = mutableListOf<Card>()

    override var gameWon  by mutableStateOf(false)

    override var currentTheme by mutableStateOf(
        when (themeId) {
            1 -> GameTheme.Animals
            2 -> GameTheme.Toys
            else -> GameTheme.Animals
        }
    )

    init {
        resetGame()
    }

    override fun resetGame() {
        Log.d(VIEWMODEL_DEBUG_TAG, "resetGame theme=$currentTheme")
        cards.clear()
        cards.addAll(
            when (currentTheme) {
                GameTheme.Animals -> createCards(Card.animalsAssets)
                GameTheme.Toys -> createCards(Card.toysAssets)
            }
        )

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

    override fun onCardClicked(card: Card) {
        if (card.isFaceUp || card.isMatched || selectedCards.size >= 2) return

        Log.d(VIEWMODEL_DEBUG_TAG, "onCardClicked id=${card.id} image=${card.imageRes}")
        card.isFaceUp = true
        selectedCards.add(card)

        if (selectedCards.size == 2) {
            val first = selectedCards[0]
            val second = selectedCards[1]

            // Codigo Assincrono
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
