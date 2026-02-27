package com.talitamorales.composememory.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.talitamorales.composememory.R
import kotlinx.coroutines.delay

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeCard(
                title = stringResource(id = R.string.theme_animals),
                imageRes = R.drawable.onboarding_cat,
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("memoryGame/1")
                }
            )
            ThemeCard(
                title = stringResource(id = R.string.theme_toys),
                imageRes = R.drawable.onboarding_plane,
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("memoryGame/2")
                }
            )
        }
    }
}
@Composable
fun ThemeCard(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(300.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(255, 192, 203))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(modifier = Modifier.fillMaxSize().background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Blue

                )
            }
        }
    }
}
