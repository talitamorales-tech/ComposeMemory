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
import kotlinx.coroutines.Job
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

    private val selectedCards = mutableListOf<Card>()
    private var memorizationJob: Job? = null
    private var matchResolutionJob: Job? = null
    private var gameGeneration: Long = 0L
    private var boardPairCount: Int? = null

    override var gameWon  by mutableStateOf(false)

    override var currentTheme by mutableStateOf(GameTheme.fromId(themeId))
    override var currentDifficulty by mutableStateOf(GameDifficulty.fromId(difficultyId))

    init {
        resetGame()
    }

    override fun updateBoardPairCount(pairCount: Int) {
        val sanitizedPairCount = pairCount.coerceAtLeast(1)
        if (boardPairCount == sanitizedPairCount) return
        boardPairCount = sanitizedPairCount
        resetGame()
    }

    override fun resetGame() {
        gameGeneration += 1
        val generation = gameGeneration
        memorizationJob?.cancel()
        matchResolutionJob?.cancel()
        val effectivePairCount = boardPairCount ?: currentDifficulty.pairCount

        Log.d(
            VIEWMODEL_DEBUG_TAG,
            "resetGame theme=$currentTheme difficulty=$currentDifficulty pairs=$effectivePairCount"
        )
        cards.clear()
        cards.addAll(createCardsForTheme(currentTheme, effectivePairCount))

        selectedCards.clear()
        gameWon = false

        cards.forEach{it.isFaceUp = true}
        isMemorizing = true
        val extraPairs = (effectivePairCount - currentDifficulty.pairCount).coerceAtLeast(0)
        val memorizeDelayMs = when (currentDifficulty) {
            GameDifficulty.Easy -> 4000L + extraPairs * 250L
            GameDifficulty.Medium -> 6000L + extraPairs * 220L
            GameDifficulty.Hard -> 8000L + extraPairs * 180L
        }
        val job = viewModelScope.launch {
            delay(memorizeDelayMs)
            if (generation != gameGeneration) return@launch
            cards.forEach{it.isFaceUp = false}
            isMemorizing = false
        }
        memorizationJob = job
        job.invokeOnCompletion {
            if (memorizationJob === job) {
                memorizationJob = null
            }
        }
    }

    override fun onCardClicked(card: Card) {
        if (isMemorizing || card.isFaceUp || card.isMatched || selectedCards.size >= 2) return

        Log.d(VIEWMODEL_DEBUG_TAG, "onCardClicked id=${card.id} image=${card.imageRes}")
        card.isFaceUp = true
        selectedCards.add(card)

        if (selectedCards.size == 2) {
            val first = selectedCards[0]
            val second = selectedCards[1]
            val generation = gameGeneration

            // Codigo Assincrono
            val job = viewModelScope.launch {
                delay(800)
                if (generation != gameGeneration) return@launch
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
            matchResolutionJob = job
            job.invokeOnCompletion {
                if (matchResolutionJob === job) {
                    matchResolutionJob = null
                }
            }
        }
    }
}
