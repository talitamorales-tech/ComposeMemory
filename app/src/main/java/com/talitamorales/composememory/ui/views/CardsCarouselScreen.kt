package com.talitamorales.composememory.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.Card
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private val CarouselFrameShape = RoundedCornerShape(28.dp)

@Composable
fun CardsCarouselScreen(onClose: () -> Unit) {
    val cardImages = remember {
        (Card.animalsAssets.map { it.first } + Card.toysAssets.map { it.first }).distinct()
    }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }

    fun nextIndex(): Int {
        if (cardImages.isEmpty()) return 0
        return (currentIndex + 1) % cardImages.size
    }

    LaunchedEffect(cardImages.size) {
        if (cardImages.size <= 1) return@LaunchedEffect
        while (isActive) {
            delay(5000)
            currentIndex = nextIndex()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF170825),
                        Color(0xFF2A1150),
                        Color(0xFF0C0618)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.carousel_title),
                    color = Color(0xFFFFF1D2),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.carousel_tap_to_advance),
                    color = Color(0xFFFFDFA8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 14.dp)
                    .shadow(9.dp, CarouselFrameShape, clip = false)
                    .clip(CarouselFrameShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xBB2F1452),
                                Color(0xAA5B27A4),
                                Color(0xBB1A0D2D)
                            )
                        )
                    )
                    .border(1.7.dp, Color(0xFFFFD792), CarouselFrameShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        (
                            slideInHorizontally(
                                animationSpec = tween(durationMillis = 360),
                                initialOffsetX = { fullWidth -> fullWidth }
                            ) + fadeIn(animationSpec = tween(360))
                            ).togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(durationMillis = 360),
                                targetOffsetX = { fullWidth -> -fullWidth }
                            ) + fadeOut(animationSpec = tween(360))
                            ).using(SizeTransform(clip = false))
                    },
                    label = "cards_carousel_transition",
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    val safeIndex = index.coerceIn(0, (cardImages.size - 1).coerceAtLeast(0))
                    val imageRes = cardImages.getOrNull(safeIndex)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(enabled = cardImages.size > 1) {
                                currentIndex = nextIndex()
                            }
                    ) {
                        if (imageRes != null) {
                            AsyncImage(
                                model = imageRes,
                                contentDescription = stringResource(
                                    id = R.string.carousel_card_content_description,
                                    safeIndex + 1
                                ),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.memory_friends_logo),
                                contentDescription = stringResource(id = R.string.logo_content_description),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                cardImages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(if (currentIndex == index) 10.dp else 8.dp)
                            .width(if (currentIndex == index) 26.dp else 10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (currentIndex == index) Color(0xFFFFD171)
                                else Color(0x77FFFFFF)
                            )
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(46.dp),
            shape = CircleShape,
            containerColor = Color(0xFFE53935),
            contentColor = Color.White
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = stringResource(id = R.string.close),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
