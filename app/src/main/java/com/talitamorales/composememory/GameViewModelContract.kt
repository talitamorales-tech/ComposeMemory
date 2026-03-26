package com.talitamorales.composememory

import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.GameDifficulty
import com.talitamorales.composememory.gamelogic.GameTheme

interface GameViewModelContract {

    val cards: List<Card>
    val isMemorizing: Boolean
    val gameWon: Boolean
    var currentTheme: GameTheme
    var currentDifficulty: GameDifficulty

    fun updateBoardPairCount(pairCount: Int)
    fun resetGame()
    fun onCardClicked(card: Card)
}
