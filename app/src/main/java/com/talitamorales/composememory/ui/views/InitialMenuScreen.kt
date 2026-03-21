package com.talitamorales.composememory.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
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
                        Color(0xFF21043F),
                        Color(0xFF2E0E5A),
                        Color(0xFF140429),
                        Color(0xFF0A0318)
                    )
                )
            )
            .menuSparkles()
    ) {
        val compactLayout = maxWidth < 720.dp
        val menuScrollState = rememberScrollState()

        MenuBackgroundDecor()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(menuScrollState)
                .padding(horizontal = if (compactLayout) 20.dp else 36.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(if (compactLayout) 8.dp else 18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, MenuCardShape, clip = false)
                    .clip(MenuCardShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCB3A1A6E),
                                Color(0xB32A1456),
                                Color(0xCC1B0D3B)
                            )
                        )
                    )
                    .border(1.7.dp, Color(0xFFF7D389), MenuCardShape)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0x4E8A5CFF),
                            radius = size.minDimension * 0.22f,
                            center = Offset(size.width * 0.16f, size.height * 0.14f)
                        )
                        drawCircle(
                            color = Color(0x36FFD47E),
                            radius = size.minDimension * 0.16f,
                            center = Offset(size.width * 0.84f, size.height * 0.18f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.memory_friends_logo),
                    contentDescription = stringResource(id = R.string.logo_content_description),
                    modifier = Modifier
                        .fillMaxWidth(if (compactLayout) 0.56f else 0.42f)
                        .heightIn(max = if (compactLayout) 215.dp else 240.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(if (compactLayout) 20.dp else 28.dp))

            Text(
                text = stringResource(id = R.string.initial_choose_how_to_play),
                color = Color(0xFFFFF6DE),
                fontSize = if (compactLayout) 30.sp else 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xB52A0D4A),
                        offset = Offset(0f, 3f),
                        blurRadius = 8f
                    )
                ),
                modifier = Modifier.fillMaxWidth()
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
                        iconRes = R.drawable.cat_persa,
                        iconFill = 0.9f,
                        accentColor = Color(0xFF5AC88A),
                        onClick = { navController.navigate("cardsCarousel") }
                    )
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_memory_game_title),
                        subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                        iconRes = R.drawable.memory_game_plane_icon,
                        iconFill = 0.92f,
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
                        iconRes = R.drawable.cat_persa,
                        iconFill = 0.9f,
                        accentColor = Color(0xFF5AC88A),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("cardsCarousel") }
                    )
                    MenuActionButton(
                        title = stringResource(id = R.string.menu_memory_game_title),
                        subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                        iconRes = R.drawable.memory_game_plane_icon,
                        iconFill = 0.92f,
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
    iconFill: Float = 0.84f,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 102.dp)
            .shadow(11.dp, MenuButtonShape, clip = false)
            .clip(MenuButtonShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF311456),
                        accentColor.copy(alpha = 0.5f),
                        Color(0xFF261045)
                    )
                )
            )
            .border(1.5.dp, Color(0xD7FFE2AE), MenuButtonShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color(0x37FFFFFF))
                    .border(1.2.dp, Color(0xBBFFE9BB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(iconFill),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color(0xFFFFF7E4),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.22f),
                            offset = Offset(0f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFFFFE6B8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(1.dp, Color(0x8EFFE8BB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFFFEDC9)
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
                .size(94.dp)
                .clip(CircleShape)
                .background(Color(0x3267C0FF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0x2EFFD56A))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(86.dp)
                .clip(CircleShape)
                .background(Color(0x30C894FF))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(124.dp)
                .clip(CircleShape)
                .background(Color(0x2869B8FF))
        )
    }
}

private fun Modifier.menuSparkles(): Modifier = drawBehind {
    val stars = listOf(
        0.08f to 0.14f,
        0.18f to 0.25f,
        0.30f to 0.1f,
        0.42f to 0.2f,
        0.58f to 0.16f,
        0.72f to 0.12f,
        0.88f to 0.22f,
        0.14f to 0.56f,
        0.34f to 0.48f,
        0.49f to 0.6f,
        0.67f to 0.52f,
        0.86f to 0.46f,
        0.1f to 0.82f,
        0.26f to 0.9f,
        0.44f to 0.84f,
        0.62f to 0.9f,
        0.8f to 0.86f
    )

    stars.forEachIndexed { index, (x, y) ->
        val radius = size.minDimension * if (index % 3 == 0) 0.008f else 0.005f
        drawCircle(
            color = Color.White.copy(alpha = if (index % 2 == 0) 0.2f else 0.12f),
            radius = radius,
            center = Offset(size.width * x, size.height * y)
        )
    }
}
