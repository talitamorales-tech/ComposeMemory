package com.talitamorales.composememory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.gamelogic.Card
import com.talitamorales.composememory.gamelogic.CardItem
import com.talitamorales.composememory.gamelogic.createCards
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen() {
    val scope = rememberCoroutineScope()
    var cards by remember { mutableStateOf(createCards()) }
    var selectedCards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var gameWon by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cards = cards.map { it.copy(isFaceUp = true) }
        delay(5000)
        cards = cards.map { it.copy(isFaceUp = false) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            if (gameWon) "🎉 You won!" else "Memory Game",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards) { card ->
                CardItem(
                    card = card,
                    onClick = {
                        if (selectedCards.size < 2 && !card.isFaceUp && !card.isMatched) {
                            val updated = cards.map {
                                if (it.id == card.id) it.copy(isFaceUp = true) else it
                            }
                            cards = updated
                            selectedCards = selectedCards + card

                            if (selectedCards.size == 2) {
                                scope.launch {
                                    delay(1000)
                                    val first = updated.first { it.id == selectedCards[0].id }
                                    val second = updated.first{it.id == selectedCards[1].id}
                                    if (first.value == second.value) {
                                        cards = cards.map {
                                            if (it.id == first.id || it.id == second.id)
                                                it.copy(isMatched = true)
                                            else it
                                        }
                                    } else {
                                        cards = cards.map {
                                            if (it.id == first.id || it.id == second.id)
                                                it.copy(isFaceUp = false)
                                            else it
                                        }
                                    }
                                    selectedCards = emptyList()
                                    if (cards.all { it.isMatched }) {
                                        gameWon = true
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        Button(
            onClick =  {
                cards = createCards()
                gameWon = false
                scope.launch {
                    cards = cards.map { it.copy(isFaceUp = true) }
                    delay(2000)
                    cards = cards.map { it.copy(isFaceUp = false) }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart")
        }
    }
}