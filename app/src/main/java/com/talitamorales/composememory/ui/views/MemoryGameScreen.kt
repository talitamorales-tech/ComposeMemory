package com.talitamorales.composememory.ui.views

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.MemoryCard
import com.talitamorales.composememory.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(viewModel: GameViewModelContract) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var isSoundEnabled by remember { mutableStateOf(true) }
    var imageSound by remember { mutableStateOf(R.drawable.sound_on) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        // Toolbar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = if (viewModel.gameWon) "🎉 You won!" else "Memory Game",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, end = 200.dp)
            )

            ThemeButton(viewModel)

            ToolbarButton(if (isSoundEnabled) "Sound on" else "Sound off", imageSound) {
                if (isSoundEnabled) {
                    imageSound = R.drawable.sound_off
                    isSoundEnabled = false
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                } else {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                    imageSound = R.drawable.sound_on
                    isSoundEnabled = true
                }
                Toast.makeText(context,if(isSoundEnabled) "Sound_on 🔊" else "Sound_off 🔇",Toast.LENGTH_SHORT).show()
            }

            RestartButton(viewModel)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(80.0f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.cards, key = {it.id}) { card ->
                MemoryCard(
                    card = card,
                    isMemorizing = viewModel.isMemorizing,
                    onClick =  {
                        // Toca som do animal
                        mediaPlayer?.release()
                        mediaPlayer = null
                        if (isSoundEnabled && card.soundRes != null) {
                            mediaPlayer = MediaPlayer.create(context, card.soundRes)
                            mediaPlayer?.start()
                        }

                        viewModel.onCardClicked(card)
                        // Esperamos 900 milisegunods porque o evento que verifica
                        // se o jogador ganhou o jogo é assincrono e demora 800 milisegundos na GameViewModel (onCardClicked)
                        // scope é uma CoRoutine
                        scope.launch {
                            delay(900)
                            // Toca musica da vitoria se ganhou o jogo
                            if (isSoundEnabled && viewModel.gameWon) {
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
    }
}

// - Composable Objects

@Composable
fun ThemeButton(viewModel: GameViewModelContract) {
    val btnThemeTitle =  when (viewModel.currentTheme) {
        GameTheme.Animals -> "Theme: Animals"
        GameTheme.Toys -> "Theme: Toys"
    }
    val btnThemeImage = when (viewModel.currentTheme) {
        GameTheme.Animals -> R.drawable.cat
        GameTheme.Toys -> R.drawable.plane
    }
    ToolbarButton(btnThemeTitle, btnThemeImage) {
        viewModel.currentTheme =
            if (viewModel.currentTheme == GameTheme.Animals)
                GameTheme.Toys
            else GameTheme.Animals

        viewModel.resetGame()
    }
}

// TODO: - Fazer o botao restart com a imagem apenas. Diferente do ToolbarButton
@Composable
fun RestartButton(viewModel: GameViewModelContract) {
    Button(
        onClick = { viewModel.resetGame()},
        modifier = Modifier.fillMaxWidth(),
    ) {
        // TODO: - Atualizar aqui ao inves to Text usar uma Image com o novo icone do restart
        Text("Restart")
    }
}

// - Helper Functions

@Composable
fun ToolbarButton(btnTitle: String, btnImage: Int, btnClick: () -> Unit) {
    Button(modifier = Modifier.size(width = 180.dp, height = 40.dp),
        onClick = {
            btnClick()
        },
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            painterResource(id = btnImage),
            contentDescription = btnTitle,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(btnTitle)
    }
}