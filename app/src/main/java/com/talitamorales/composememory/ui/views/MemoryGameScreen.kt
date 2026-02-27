package com.talitamorales.composememory.ui.views

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.talitamorales.composememory.GameViewModelContract
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.Card as GameCardModel
import com.talitamorales.composememory.gamelogic.GameTheme
import com.talitamorales.composememory.gamelogic.MemoryCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private val GridHorizontalSpacing = 12.dp
private val GridVerticalSpacing = 12.dp
private val GridTopPadding = 12.dp
private val GridBottomPadding = 16.dp
private const val WinCelebrationDurationMs = 3200
private const val GAME_DEBUG_TAG = "CM-MemoryGame"

private val ToolbarContainerShape = RoundedCornerShape(22.dp)
private val ToolbarButtonShape = RoundedCornerShape(16.dp)

@Composable
fun MemoryGameScreen(viewModel: GameViewModelContract) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var isSoundEnabled by remember { mutableStateOf(true) }
    var showWinCelebration by remember { mutableStateOf(false) }
    val recompositions = remember { mutableIntStateOf(0) }
    val soundOnToast = stringResource(id = R.string.toast_sound_on)
    val soundOffToast = stringResource(id = R.string.toast_sound_off)

    SideEffect {
        recompositions.intValue += 1
        if (recompositions.intValue == 1 || recompositions.intValue % 25 == 0) {
            Log.d(
                GAME_DEBUG_TAG,
                "MemoryGameScreen recompositions=${recompositions.intValue} " +
                    "cards=${viewModel.cards.size} memorizing=${viewModel.isMemorizing} " +
                    "won=${viewModel.gameWon} celebration=$showWinCelebration " +
                    "configOrientation=${context.resources.configuration.orientation}"
            )
        }
    }

    LaunchedEffect(
        viewModel.cards.size,
        viewModel.isMemorizing,
        viewModel.gameWon,
        viewModel.currentTheme,
        showWinCelebration
    ) {
        Log.d(
            GAME_DEBUG_TAG,
            "State changed cards=${viewModel.cards.size} memorizing=${viewModel.isMemorizing} " +
                "won=${viewModel.gameWon} theme=${viewModel.currentTheme} " +
                "celebration=$showWinCelebration"
        )
    }

    LaunchedEffect(viewModel.gameWon) {
        if (viewModel.gameWon && !showWinCelebration) {
            showWinCelebration = true
            delay(WinCelebrationDurationMs.toLong())
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            viewModel.resetGame()
            showWinCelebration = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF12061F),
                        Color(0xFF2B1248),
                        Color(0xFF0B0416)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        val compactToolbar = maxWidth < 700.dp
        val toolbarButtonHeight = if (compactToolbar) 42.dp else 48.dp
        val restartButtonSize = if (compactToolbar) 50.dp else 58.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            GameToolbar(
                viewModel = viewModel,
                isSoundEnabled = isSoundEnabled,
                onToggleSound = {
                    if (isSoundEnabled) {
                        isSoundEnabled = false
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    } else {
                        isSoundEnabled = true
                    }
                    Toast.makeText(
                        context,
                        if (isSoundEnabled) soundOnToast else soundOffToast,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onRestart = {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    viewModel.resetGame()
                },
                enabled = !showWinCelebration,
                modifier = Modifier.fillMaxWidth(),
                buttonHeight = toolbarButtonHeight,
                restartButtonSize = restartButtonSize
            )

            Spacer(modifier = Modifier.size(8.dp))

            ResponsiveGameGrid(
                cards = viewModel.cards,
                isMemorizing = viewModel.isMemorizing || showWinCelebration,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { card ->
                mediaPlayer?.release()
                mediaPlayer = null
                if (isSoundEnabled && card.soundRes != null) {
                    mediaPlayer = MediaPlayer.create(context, card.soundRes)
                    mediaPlayer?.start()
                }

                viewModel.onCardClicked(card)

                scope.launch {
                    delay(900)
                    if (isSoundEnabled && viewModel.gameWon) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                        mediaPlayer = MediaPlayer.create(context, R.raw.victory)
                        mediaPlayer?.start()
                    }
                }
            }
        }

        if (showWinCelebration) {
            WinCelebrationOverlay()
        }
    }
}

@Composable
private fun GameToolbar(
    viewModel: GameViewModelContract,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onRestart: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 44.dp,
    restartButtonSize: Dp = 54.dp
) {
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.6f)
            .shadow(8.dp, ToolbarContainerShape, clip = false)
            .clip(ToolbarContainerShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1B0A2B),
                        Color(0xFF35155B),
                        Color(0xFF13071F)
                    )
                )
            )
            .border(1.6.dp, Color(0xFFF2CF85), ToolbarContainerShape)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeButton(
            viewModel = viewModel,
            modifier = Modifier.weight(1f),
            buttonHeight = buttonHeight,
            enabled = enabled
        )

        SoundButton(
            isSoundEnabled = isSoundEnabled,
            modifier = Modifier.weight(1f),
            buttonHeight = buttonHeight,
            enabled = enabled,
            onToggleSound = onToggleSound
        )

        RestartButton(
            modifier = Modifier.size(restartButtonSize),
            enabled = enabled,
            onRestart = onRestart
        )
    }
}

@Composable
private fun ResponsiveGameGrid(
    cards: List<GameCardModel>,
    isMemorizing: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: (GameCardModel) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val cardCount = cards.size.coerceAtLeast(1)
        val minColumns = if (cardCount == 1) 1 else 2
        val maxColumns = min(cardCount, 6)

        var bestColumns = minColumns
        var bestCardSize = 0.dp

        for (columns in minColumns..maxColumns) {
            val rows = ceil(cardCount / columns.toDouble()).toInt()
            val widthPerCard = (
                maxWidth - GridHorizontalSpacing * (columns - 1).toFloat()
            ).coerceAtLeast(0.dp) / columns.toFloat()
            val heightPerCard = (
                maxHeight
                    - GridTopPadding
                    - GridBottomPadding
                    - GridVerticalSpacing * (rows - 1).toFloat()
            ).coerceAtLeast(0.dp) / rows.toFloat()

            val candidate = minOf(widthPerCard, heightPerCard)
            if (candidate > bestCardSize) {
                bestCardSize = candidate
                bestColumns = columns
            }
        }

        val minimumCardSize = if (maxWidth < 500.dp) 72.dp else 88.dp
        val cardSize = bestCardSize.coerceAtLeast(minimumCardSize)

        LaunchedEffect(maxWidth, maxHeight, bestColumns, cardSize, cardCount) {
            Log.d(
                GAME_DEBUG_TAG,
                "Grid metrics width=$maxWidth height=$maxHeight cards=$cardCount " +
                    "columns=$bestColumns cardSize=$cardSize"
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(bestColumns),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(GridHorizontalSpacing),
            verticalArrangement = Arrangement.spacedBy(GridVerticalSpacing),
            contentPadding = PaddingValues(top = GridTopPadding, bottom = GridBottomPadding),
            userScrollEnabled = false
        ) {
            items(cards, key = { it.id }) { card ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MemoryCard(
                        card = card,
                        isMemorizing = isMemorizing,
                        onClick = { onCardClick(card) },
                        modifier = Modifier.size(cardSize)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeButton(
    viewModel: GameViewModelContract,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 44.dp,
    enabled: Boolean = true
) {
    val (btnThemeTitle, accentColor) = when (viewModel.currentTheme) {
        GameTheme.Animals -> Pair(stringResource(id = R.string.toolbar_theme_animals), Color(0xFF5AC88A))
        GameTheme.Toys -> Pair(stringResource(id = R.string.toolbar_theme_toys), Color(0xFF69B6FF))
    }

    ToolbarButton(
        btnTitle = btnThemeTitle,
        btnImage = R.drawable.ic_theme_magic,
        accentColor = accentColor,
        modifier = modifier,
        buttonHeight = buttonHeight,
        enabled = enabled
    ) {
        viewModel.currentTheme =
            if (viewModel.currentTheme == GameTheme.Animals) GameTheme.Toys else GameTheme.Animals
        viewModel.resetGame()
    }
}

@Composable
fun SoundButton(
    isSoundEnabled: Boolean,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 44.dp,
    enabled: Boolean = true,
    onToggleSound: () -> Unit
) {
    ToolbarButton(
        btnTitle = if (isSoundEnabled) {
            stringResource(id = R.string.toolbar_sound_on)
        } else {
            stringResource(id = R.string.toolbar_sound_off)
        },
        btnImage = if (isSoundEnabled) R.drawable.ic_sound_on_magic else R.drawable.ic_sound_off_magic,
        accentColor = if (isSoundEnabled) Color(0xFF4AB8FF) else Color(0xFFE87484),
        modifier = modifier,
        buttonHeight = buttonHeight,
        enabled = enabled,
        btnClick = onToggleSound
    )
}

@Composable
fun RestartButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onRestart: () -> Unit
) {
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.6f)
            .shadow(5.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF7B35D3),
                        Color(0xFF331157)
                    )
                )
            )
            .border(1.5.dp, Color(0xFFFFE2A8), CircleShape)
            .clickable(enabled = enabled) { onRestart() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.74f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x4467C0FF),
                            Color(0x11FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_refresh_magic),
            contentDescription = stringResource(id = R.string.restart),
            tint = Color(0xFFFFF1D0),
            modifier = Modifier.fillMaxSize(0.46f)
        )
    }
}

@Composable
fun ToolbarButton(
    btnTitle: String,
    btnImage: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 44.dp,
    enabled: Boolean = true,
    btnClick: () -> Unit
) {
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.65f)
            .height(buttonHeight)
            .shadow(4.dp, ToolbarButtonShape, clip = false)
            .clip(ToolbarButtonShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2D114A),
                        accentColor.copy(alpha = 0.40f),
                        Color(0xFF1F0C34)
                    )
                )
            )
            .border(1.dp, Color(0x99FFE4B5), ToolbarButtonShape)
            .clickable(enabled = enabled) { btnClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(buttonHeight - 14.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x66FFECC2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = btnImage),
                    contentDescription = btnTitle,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.58f)
                )
            }

            Text(
                text = btnTitle,
                color = Color(0xFFFFF4DD),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class ConfettiPiece(
    val startX: Float,
    val startY: Float,
    val fallDistance: Float,
    val speedMultiplier: Float,
    val waveAmplitude: Float,
    val wavePhase: Float,
    val sizeFactor: Float,
    val rotationOffset: Float,
    val color: Color
)

@Composable
private fun BoxScope.WinCelebrationOverlay() {
    val blocker = remember { MutableInteractionSource() }
    val progress = remember { Animatable(0f) }
    val confetti = remember {
        val palette = listOf(
            Color(0xFFFFD166),
            Color(0xFF6BCBFF),
            Color(0xFFFF8DC7),
            Color(0xFF89F7A1),
            Color(0xFFFFA861),
            Color(0xFFB39DFF)
        )
        val random = Random(2026)
        List(170) {
            ConfettiPiece(
                startX = random.nextFloat(),
                startY = -(0.1f + random.nextFloat() * 0.9f),
                fallDistance = 1.7f + random.nextFloat() * 0.9f,
                speedMultiplier = 0.75f + random.nextFloat() * 0.8f,
                waveAmplitude = 0.012f + random.nextFloat() * 0.04f,
                wavePhase = random.nextFloat() * (2f * PI.toFloat()),
                sizeFactor = 0.01f + random.nextFloat() * 0.016f,
                rotationOffset = random.nextFloat() * 360f,
                color = palette[random.nextInt(palette.size)]
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = WinCelebrationDurationMs,
                easing = LinearEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xD6180928),
                        Color(0xC1220E3A),
                        Color(0xD20E0619)
                    )
                )
            )
            .clickable(
                interactionSource = blocker,
                indication = null
            ) {}
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val currentProgress = progress.value
            val screenWidth = size.width
            val screenHeight = size.height
            val minDimension = size.minDimension

            confetti.forEachIndexed { index, piece ->
                val pieceY =
                    (piece.startY + currentProgress * piece.fallDistance * piece.speedMultiplier) *
                        screenHeight
                if (pieceY < -90f || pieceY > screenHeight + 100f) return@forEachIndexed

                val xWave =
                    sin((currentProgress * 11f + piece.wavePhase).toDouble()).toFloat() *
                        piece.waveAmplitude * screenWidth
                val pieceX = piece.startX * screenWidth + xWave
                val pieceSize = piece.sizeFactor * minDimension

                if (index % 2 == 0) {
                    drawCircle(
                        color = piece.color,
                        radius = pieceSize * 0.46f,
                        center = Offset(pieceX, pieceY)
                    )
                } else {
                    rotate(
                        degrees = currentProgress * 720f + piece.rotationOffset,
                        pivot = Offset(pieceX, pieceY)
                    ) {
                        drawRect(
                            color = piece.color,
                            topLeft = Offset(pieceX - pieceSize / 2f, pieceY - pieceSize / 2f),
                            size = Size(pieceSize, pieceSize * 0.58f)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.win_congrats),
                color = Color(0xFFFFF3D7),
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(id = R.string.win_prepare_next_round),
                color = Color(0xFFFFE5AF),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
