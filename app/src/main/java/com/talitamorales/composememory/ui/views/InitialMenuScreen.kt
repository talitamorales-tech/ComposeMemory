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

private val MenuButtonShape = RoundedCornerShape(24.dp)
private val MenuHeroShape = RoundedCornerShape(
    bottomStart = 34.dp,
    bottomEnd = 34.dp
)
private val MenuBottomShape = RoundedCornerShape(
    topStart = 34.dp,
    topEnd = 34.dp
)

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
        val tabletLayout = maxWidth >= 900.dp
        val shortLayout = maxHeight < 500.dp
        val useSideBySideButtons = tabletLayout || shortLayout

        MenuBackgroundDecor()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (compactLayout) 14.dp else 28.dp,
                    vertical = if (shortLayout) 6.dp else 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (shortLayout) 0.42f else 1f)
                    .shadow(16.dp, MenuHeroShape, clip = false)
                    .clip(MenuHeroShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xE23C1A6F),
                                Color(0xC62D145A),
                                Color(0xD81A0A3A),
                                Color(0xEE15072E)
                            )
                        )
                    )
                    .border(1.8.dp, Color(0xFFF7D389), MenuHeroShape)
                    .padding(
                        horizontal = if (tabletLayout) 34.dp else if (shortLayout) 14.dp else 18.dp,
                        vertical = if (shortLayout) 10.dp else if (compactLayout) 18.dp else 24.dp
                    )
                    .drawBehind {
                        drawCircle(
                            color = Color(0x4E8A5CFF),
                            radius = size.minDimension * 0.28f,
                            center = Offset(size.width * 0.15f, size.height * 0.16f)
                        )
                        drawCircle(
                            color = Color(0x36FFD47E),
                            radius = size.minDimension * 0.2f,
                            center = Offset(size.width * 0.84f, size.height * 0.2f)
                        )
                        drawCircle(
                            color = Color(0x2D6AB8FF),
                            radius = size.minDimension * 0.24f,
                            center = Offset(size.width * 0.82f, size.height * 0.84f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.memory_friends_logo),
                    contentDescription = stringResource(id = R.string.logo_content_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (shortLayout) 0.58f else 1f)
                    .padding(top = if (shortLayout) 6.dp else 8.dp)
                    .shadow(16.dp, MenuBottomShape, clip = false)
                    .clip(MenuBottomShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xF12B0F4E),
                                Color(0xF01F0B3A),
                                Color(0xF0140728)
                            )
                        )
                    )
                    .border(1.4.dp, Color(0xA3FFDFA0), MenuBottomShape)
                    .padding(
                        horizontal = if (shortLayout) 12.dp else if (compactLayout) 14.dp else 24.dp,
                        vertical = if (shortLayout) 10.dp else if (compactLayout) 14.dp else 20.dp
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(id = R.string.initial_choose_how_to_play),
                        color = Color(0xFFFFF6DE),
                        fontSize = if (tabletLayout) 36.sp else if (shortLayout) 22.sp else 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xB52A0D4A),
                                offset = Offset(0f, 3f),
                                blurRadius = 8f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = if (shortLayout) 2.dp else if (compactLayout) 8.dp else 12.dp,
                                bottom = if (shortLayout) 8.dp else 14.dp
                            )
                    )

                    if (!useSideBySideButtons) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 560.dp)
                                .weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            MenuActionButton(
                                title = stringResource(id = R.string.menu_cards_carousel_title),
                                subtitle = stringResource(id = R.string.menu_cards_carousel_subtitle),
                                iconRes = R.drawable.cat_persa,
                                iconFill = 0.92f,
                                accentColor = Color(0xFF5AC88A),
                                compact = false,
                                onClick = { navController.navigate("cardsCarousel") }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            MenuActionButton(
                                title = stringResource(id = R.string.menu_memory_game_title),
                                subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                                iconRes = R.drawable.memory_game_plane_icon,
                                iconFill = 0.94f,
                                accentColor = Color(0xFF61B6FF),
                                compact = false,
                                onClick = { navController.navigate("themeSelection") }
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = if (tabletLayout) 1100.dp else 760.dp)
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(if (shortLayout) 10.dp else 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MenuActionButton(
                                title = stringResource(id = R.string.menu_cards_carousel_title),
                                subtitle = stringResource(id = R.string.menu_cards_carousel_subtitle),
                                iconRes = R.drawable.cat_persa,
                                iconFill = 0.92f,
                                accentColor = Color(0xFF5AC88A),
                                modifier = Modifier.weight(1f),
                                compact = shortLayout,
                                onClick = { navController.navigate("cardsCarousel") }
                            )
                            MenuActionButton(
                                title = stringResource(id = R.string.menu_memory_game_title),
                                subtitle = stringResource(id = R.string.menu_memory_game_subtitle),
                                iconRes = R.drawable.memory_game_plane_icon,
                                iconFill = 0.94f,
                                accentColor = Color(0xFF61B6FF),
                                modifier = Modifier.weight(1f),
                                compact = shortLayout,
                                onClick = { navController.navigate("themeSelection") }
                            )
                        }
                    }
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
    compact: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 84.dp else 98.dp)
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
            .padding(
                horizontal = if (compact) 10.dp else 14.dp,
                vertical = if (compact) 8.dp else 10.dp
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 56.dp else 74.dp)
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
                    fontSize = if (compact) 16.sp else 19.sp,
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
                    fontSize = if (compact) 12.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(if (compact) 28.dp else 34.dp)
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
