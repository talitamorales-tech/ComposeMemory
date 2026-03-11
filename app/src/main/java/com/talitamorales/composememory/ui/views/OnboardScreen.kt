package com.talitamorales.composememory.ui.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.GameDifficulty
import com.talitamorales.composememory.gamelogic.GameTheme
import kotlinx.coroutines.delay

private const val THEME_SELECTION_TAG = "CM-ThemeSelection"

@Composable
fun OnboardScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(5000)
        navController.navigate("initialMenu") {
            popUpTo("splash") { inclusive = true }
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.memory_friends_logo),
            contentDescription = stringResource(id = R.string.logo_content_description),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ThemeSelectionScreen(navController: NavController) {
    var selectedDifficultyId by rememberSaveable { mutableIntStateOf(GameDifficulty.Easy.id) }
    val themeDisplayOrder = listOf(
        GameTheme.Dogs,
        GameTheme.Animals,
        GameTheme.Dinosaurs,
        GameTheme.FarmTractors,
        GameTheme.Toys
    )

    fun navigateToGame(theme: GameTheme) {
        Log.d(
            THEME_SELECTION_TAG,
            "navigateToGame theme=${theme.name} id=${theme.id} difficultyId=$selectedDifficultyId"
        )
        navController.navigate("memoryGame/${theme.id}/$selectedDifficultyId")
    }

    LaunchedEffect(Unit) {
        Log.d(
            THEME_SELECTION_TAG,
            "ThemeSelectionScreen opened freeThemes=" +
                GameTheme.entries.filter { it.isFree }.joinToString { it.name } +
                " premiumThemes=" +
                GameTheme.entries.filter { !it.isFree }.joinToString { it.name }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.choose_theme_to_play),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.choose_difficulty),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameDifficulty.entries.forEach { difficulty ->
                DifficultyCard(
                    title = stringResource(id = difficulty.titleRes),
                    selected = selectedDifficultyId == difficulty.id,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedDifficultyId = difficulty.id }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        themeDisplayOrder.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowThemes.forEach { theme ->
                    ThemeCard(
                        title = stringResource(id = theme.titleRes),
                        imageRes = theme.previewRes,
                        isPremium = !theme.isFree,
                        isLocked = false,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToGame(theme) }
                    )
                }
                if (rowThemes.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DifficultyCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFB39DDB) else Color(0xFFEDE7F6)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (selected) Color.White else Color(0xFF4A2A73),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
@Composable
fun ThemeCard(
    title: String,
    imageRes: Int,
    isPremium: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(230.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6E8FF))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(0.88f),
                    contentScale = ContentScale.Fit
                )

                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(36.dp)
                            .background(Color(0xD020102E), CircleShape)
                            .border(1.dp, Color(0xFFFFDDA0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock_magic),
                            contentDescription = null,
                            tint = Color(0xFFFFDDA0)
                        )
                    }
                }

                Text(
                    text = if (isPremium) {
                        stringResource(id = R.string.premium_label)
                    } else {
                        stringResource(id = R.string.free_label)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            if (isPremium) Color(0xFF6A1B9A) else Color(0xFF2E7D32),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2A1650)

                )
            }
        }
    }
}
