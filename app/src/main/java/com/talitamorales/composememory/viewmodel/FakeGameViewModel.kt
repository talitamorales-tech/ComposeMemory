package com.talitamorales.composememory.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.gamelogic.GameDifficulty
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.createCardsForTheme

class FakeGameViewModel : GameViewModelContract {
    private var boardPairCount: Int = GameDifficulty.Easy.pairCount

    override val cards = mutableStateListOf<Card>().apply {
        addAll(createCardsForTheme(GameTheme.Animals, boardPairCount))
    }
    override val isMemorizing = false
    override  val gameWon = false
    override var currentTheme = GameTheme.Animals
    override var currentDifficulty = GameDifficulty.Easy

    override fun updateBoardPairCount(pairCount: Int) {
        boardPairCount = pairCount.coerceAtLeast(1)
        resetGame()
    }

    override fun resetGame() {
        cards.clear()
        cards.addAll(createCardsForTheme(currentTheme, boardPairCount))
    }

    override fun onCardClicked(card: Card) {
        card.isFaceUp = !card.isFaceUp
    }
}
