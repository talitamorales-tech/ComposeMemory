package com.talitamorales.composememory.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.createCards

class FakeGameViewModel : GameViewModelContract {

    override val cards = mutableStateListOf<Card>().apply {
        addAll(createCards(Card.animalsAssets))
    }
    override val isMemorizing = false
    override  val gameWon = false
    override var currentTheme = GameTheme.Animals

    override fun resetGame() {}

    override fun onCardClicked(card: Card) {
        card.isFaceUp = !card.isFaceUp
    }
}