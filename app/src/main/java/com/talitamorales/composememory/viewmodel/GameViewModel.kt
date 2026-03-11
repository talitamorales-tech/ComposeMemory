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
import com.talitamorales.composememory.gamelogic.GameDifficulty
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.createCardsForTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val VIEWMODEL_DEBUG_TAG = "CM-GameViewModel"

class GameViewModel(
    private val themeId: Int,
    private val difficultyId: Int
) : ViewModel(), GameViewModelContract {
    final override var cards = mutableStateListOf<Card>()
        private set

    override var isMemorizing by mutableStateOf(true)

    private var selectedCards = mutableListOf<Card>()

    override var gameWon  by mutableStateOf(false)

    override var currentTheme by mutableStateOf(GameTheme.fromId(themeId))
    override var currentDifficulty by mutableStateOf(GameDifficulty.fromId(difficultyId))

    init {
        resetGame()
    }

    override fun resetGame() {
        Log.d(
            VIEWMODEL_DEBUG_TAG,
            "resetGame theme=$currentTheme difficulty=$currentDifficulty pairs=${currentDifficulty.pairCount}"
        )
        cards.clear()
        cards.addAll(createCardsForTheme(currentTheme, currentDifficulty))

        selectedCards.clear()
        gameWon = false

        cards.forEach{it.isFaceUp = true}
        isMemorizing = true
        val memorizeDelayMs = when (currentDifficulty) {
            GameDifficulty.Easy -> 4000L
            GameDifficulty.Medium -> 6000L
            GameDifficulty.Hard -> 8000L
        }
        viewModelScope.launch {
            delay(memorizeDelayMs)
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
