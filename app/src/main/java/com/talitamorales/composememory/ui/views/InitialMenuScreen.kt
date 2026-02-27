package com.talitamorales.composememory.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.talitamorales.composememory.R

private val MenuCardShape = RoundedCornerShape(30.dp)
private val MenuButtonShape = RoundedCornerShape(24.dp)

@Composable
fun InitialMenuScreen(navController: NavController) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0A2B),
                        Color(0xFF2B1452),
                        Color(0xFF0E081A)
                    )
                )
            )
    ) {
        val compactLayout = maxWidth < 720.dp

        MenuBackgroundDecor()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compactLayout) 20.dp else 36.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, MenuCardShape, clip = false)
                    .clip(MenuCardShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0x772D1450),
                                Color(0xAA4A1F7A),
                                Color(0x77201438)
                            )
                        )
                    )
                    .border(1.6.dp, Color(0xFFEDCC86), MenuCardShape)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.memory_friends_logo),
                    contentDescription = stringResource(id = R.string.logo_content_description),
                    modifier = Modifier
                        .fillMaxWidth(if (compactLayout) 0.68f else 0.52f)
                        .heightIn(max = if (compactLayout) 210.dp else 250.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(if (compactLayout) 20.dp else 28.dp))

            Text(
                text = stringResource(id = R.string.initial_choose_how_to_play),
                color = Color(0xFFFFF4D8),
                fontSize = if (compactLayout) 30.sp else 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (compactLayout) 20.dp else 26.dp))

            if (compactLayout) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_cards_carousel_title),
                        subtitle = stringResource(id = R.string.menu_cards_carousel_subtitle),
                        iconRes = R.drawable.cat,
                        accentColor = Color(0xFF5AC88A),
                        onClick = { navController.navigate("cardsCarousel") }
                    )
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_memory_game_title),
                        subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                        iconRes = R.drawable.plane,
                        accentColor = Color(0xFF61B6FF),
                        onClick = { navController.navigate("themeSelection") }
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 900.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_cards_carousel_title),
                        subtitle = stringResource(id = R.string.menu_cards_carousel_subtitle),
                        iconRes = R.drawable.cat,
                        accentColor = Color(0xFF5AC88A),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("cardsCarousel") }
                    )
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_memory_game_title),
                        subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                        iconRes = R.drawable.plane,
                        accentColor = Color(0xFF61B6FF),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("themeSelection") }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuActionButton(
    title: String,
    subtitle: String,
    iconRes: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .shadow(7.dp, MenuButtonShape, clip = false)
            .clip(MenuButtonShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2A1248),
                        accentColor.copy(alpha = 0.42f),
                        Color(0xFF1D0C31)
                    )
                )
            )
            .border(1.4.dp, Color(0xAAFFE3AC), MenuButtonShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(Color(0x28FFFFFF))
                    .border(1.dp, Color(0x9BFFE9BB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.72f),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color(0xFFFFF7E4),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFFFFE6B8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MenuBackgroundDecor() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(86.dp)
                .clip(CircleShape)
                .background(Color(0x2E67C0FF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0x27FFD56A))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0x30C894FF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0x2369B8FF))
        )
    }
}
