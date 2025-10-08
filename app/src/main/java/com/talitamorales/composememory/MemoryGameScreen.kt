package com.talitamorales.composememory

import android.media.MediaPlayer
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.gamelogic.MemoryCard
import com.talitamorales.composememory.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(viewModel: GameViewModel = viewModel()) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            text = if (viewModel.gameWon) "🎉 You won!" else "Memory Game",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
            )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.cards, key = {it.id}) { card ->
                MemoryCard(
                    card = card,
                    isMemorizing = viewModel.isMemorizing,
                    onClick =  {
                        if (mediaPlayer?.isPlaying == true) {

                        }
                        mediaPlayer?.release()
                        mediaPlayer = null
                        if (card.soundRes != null) {
                            mediaPlayer = MediaPlayer.create(context, card.soundRes)
                            mediaPlayer?.start()
                        }
                        viewModel.onCardClicked(card)
                        // Esperamos 900 milisegunods porque o evento que verifica
                        // se o jogador ganhou o jogo é assincrono e demora 800 milisegundos na GameViewModel (onCardClicked)
                        // scope é uma CoRoutine
                        scope.launch {
                            delay(900)
                            if (viewModel.gameWon) {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null
                                mediaPlayer = MediaPlayer.create(context, R.raw.victory)
                                mediaPlayer?.start()
                            }
                        }
                    }
                )
            }
        }

        Button(
            onClick = { viewModel.resetGame()},
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Restart")
        }
    }
}