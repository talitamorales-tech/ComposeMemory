package com.talitamorales.composememory.ui.views

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.MemoryCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale

@Composable
fun MemoryGameScreen(viewModel: GameViewModelContract) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var isSoundEnabled by remember { mutableStateOf(true) }
    var imageSound by remember { mutableStateOf(R.drawable.sound_on) }
    val soundColor = if (isSoundEnabled) Color(0xFF2196F3) else Color (0xFFF44336)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 10.dp)
    ) {
        // Toolbar
        Row(Modifier.fillMaxWidth().height(60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            /*if (viewModel.gameWon) {
                Text(
                    text =  "\uD83C\uDF1F You’re a star!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.gameWon)
                            Color(0xFFFF9800)
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.memory_friends_logo_banner),
                    contentDescription = "Memory Friends Game",
                    modifier = Modifier
                        .size(180.dp)
                        .clickable {
                            viewModel.resetGame()
                        },
                    alignment = Alignment.TopEnd
                )
            }

            Spacer(Modifier.width(100.dp))
*/
            ThemeButton(viewModel)

            ToolbarButton(btnTitle = if (isSoundEnabled) "Sound on" else "Sound off",
                btnImage = imageSound,
                containerColor = soundColor
            ) {
                if (isSoundEnabled) {
                    imageSound = R.drawable.sound_off
                    isSoundEnabled = false
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                } else {
                    imageSound = R.drawable.sound_on
                    isSoundEnabled = true
                    /*if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }*/
                }
                Toast.makeText(context,if(isSoundEnabled) "Sound_on 🔊" else "Sound_off 🔇",Toast.LENGTH_SHORT).show()
            }

            RestartButton(viewModel)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 16.dp
            )
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

    val (btnThemeTitle, btnThemeImage, btnColor) =  when (viewModel.currentTheme) {
        GameTheme.Animals -> Triple("Theme: Animals", R.drawable.cat, Color(0xFF4CAF50))
        GameTheme.Toys -> Triple( "Theme: Toys", R.drawable.plane, Color(0xFF9C27B0))
    }
    /*val btnThemeImage = when (viewModel.currentTheme) {
        GameTheme.Animals -> R.drawable.cat
        GameTheme.Toys -> R.drawable.plane
    }*/
    ToolbarButton(btnThemeTitle, btnThemeImage, btnColor) {
        viewModel.currentTheme =
            if (viewModel.currentTheme == GameTheme.Animals)
                GameTheme.Toys
            else GameTheme.Animals

        viewModel.resetGame()
    }
}

@Composable
fun RestartButton(viewModel: GameViewModelContract) {
   Image(
       painter = painterResource(id = R.drawable.restart),
       contentDescription = "Restart",
       modifier = Modifier
           .size(100.dp)
           .clickable {
               viewModel.resetGame()
           },
       alignment = Alignment.TopCenter,
       contentScale = ContentScale.FillBounds
   )
}

// - Helper Functions

@Composable
fun ToolbarButton(
    btnTitle: String,
    btnImage: Int,
    containerColor: Color,
    contentColor: Color = Color.White,
    btnClick: () -> Unit
) {
    Button(modifier = Modifier.size(width = 180.dp, height = 40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
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


//Arrangement.spacedBy(12.dp)