package com.talitamorales.composememory.ui.views

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalConfiguration
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
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private val GridHorizontalSpacingClosed = 8.dp
private val GridHorizontalSpacingOpen = 9.dp
private val GridVerticalSpacingClosed = 8.dp
private val GridVerticalSpacingOpen = 9.dp
private val GridTopPaddingClosed = 4.dp
private val GridTopPaddingOpen = 8.dp
private val GridBottomPaddingClosed = 8.dp
private val GridBottomPaddingOpen = 10.dp
private const val WinCelebrationDurationMs = 3200
private const val GAME_DEBUG_TAG = "CM-MemoryGame"
private val GAMEPLAY_MUSIC_RES = R.raw.flip_it_match_it
private val VICTORY_MUSIC_RES = R.raw.victory
private const val GAMEPLAY_MUSIC_VOLUME = 0.35f
private const val VICTORY_MUSIC_VOLUME = 0.9f
private val ToolbarContainerShape = RoundedCornerShape(22.dp)
private val ToolbarButtonShape = RoundedCornerShape(16.dp)
private val ToolbarDrawerTabShape = RoundedCornerShape(
    topStart = 10.dp,
    topEnd = 10.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

private fun MediaPlayer?.safeStopAndRelease(): MediaPlayer? {
    if (this == null) return null
    runCatching { stop() }
    runCatching { release() }
    return null
}

@Composable
fun MemoryGameScreen(viewModel: GameViewModelContract) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var backgroundMusicPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var victoryPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isToolbarExpanded by rememberSaveable { mutableStateOf(false) }
    var showWinCelebration by remember { mutableStateOf(false) }
    var gameplayMusicRestartToken by remember { mutableIntStateOf(0) }
    val recompositions = remember { mutableIntStateOf(0) }
    val soundOnToast = stringResource(id = R.string.toast_sound_on)
    val soundOffToast = stringResource(id = R.string.toast_sound_off)

    DisposableEffect(Unit) {
        onDispose {
            backgroundMusicPlayer.safeStopAndRelease()
            victoryPlayer.safeStopAndRelease()
        }
    }

    SideEffect {
        recompositions.intValue += 1
        if (recompositions.intValue == 1 || recompositions.intValue % 25 == 0) {
            Log.d(
                GAME_DEBUG_TAG,
                "MemoryGameScreen recompositions=${recompositions.intValue} " +
                    "cards=${viewModel.cards.size} memorizing=${viewModel.isMemorizing} " +
                    "won=${viewModel.gameWon} difficulty=${viewModel.currentDifficulty} " +
                    "celebration=$showWinCelebration " +
                    "configOrientation=${configuration.orientation}"
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
                "difficulty=${viewModel.currentDifficulty} " +
                "celebration=$showWinCelebration"
        )
    }

    LaunchedEffect(
        isSoundEnabled,
        viewModel.gameWon,
        showWinCelebration,
        gameplayMusicRestartToken
    ) {
        val shouldPlayMusic = isSoundEnabled && !viewModel.gameWon && !showWinCelebration
        if (!shouldPlayMusic) {
            backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
            return@LaunchedEffect
        }

        if (backgroundMusicPlayer == null) {
            backgroundMusicPlayer = MediaPlayer.create(context, GAMEPLAY_MUSIC_RES)?.apply {
                isLooping = true
                setVolume(GAMEPLAY_MUSIC_VOLUME, GAMEPLAY_MUSIC_VOLUME)
            }
        }

        runCatching {
            if (backgroundMusicPlayer?.isPlaying == false) {
                backgroundMusicPlayer?.start()
            }
        }.onFailure {
            backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
        }
    }

    LaunchedEffect(viewModel.gameWon) {
        if (viewModel.gameWon && !showWinCelebration) {
            showWinCelebration = true
            backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
            if (isSoundEnabled) {
                victoryPlayer = victoryPlayer.safeStopAndRelease()
                victoryPlayer = MediaPlayer.create(context, VICTORY_MUSIC_RES)?.apply {
                    isLooping = false
                    setVolume(VICTORY_MUSIC_VOLUME, VICTORY_MUSIC_VOLUME)
                    setOnCompletionListener { completedPlayer ->
                        runCatching { completedPlayer.release() }
                        if (victoryPlayer === completedPlayer) {
                            victoryPlayer = null
                        }
                    }
                    start()
                }
            }
            delay(WinCelebrationDurationMs.toLong())
            backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
            victoryPlayer = victoryPlayer.safeStopAndRelease()
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
            .padding(horizontal = 8.dp, vertical = 0.dp)
    ) {
        val compactToolbar = maxWidth < 700.dp
        val toolbarButtonHeight = if (compactToolbar) 40.dp else 46.dp
        val restartButtonSize = if (compactToolbar) 48.dp else 54.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            ToolbarTopDrawer(
                isOpen = isToolbarExpanded,
                enabled = !showWinCelebration,
                onToggle = { isToolbarExpanded = !isToolbarExpanded },
                modifier = Modifier.fillMaxWidth(),
                content = {
                    GameToolbar(
                        viewModel = viewModel,
                        isSoundEnabled = isSoundEnabled,
                        onToggleSound = {
                            if (isSoundEnabled) {
                                isSoundEnabled = false
                                backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
                                victoryPlayer = victoryPlayer.safeStopAndRelease()
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
                            backgroundMusicPlayer = backgroundMusicPlayer.safeStopAndRelease()
                            victoryPlayer = victoryPlayer.safeStopAndRelease()
                            isToolbarExpanded = false
                            viewModel.resetGame()
                            gameplayMusicRestartToken += 1
                        },
                        enabled = !showWinCelebration,
                        modifier = Modifier.fillMaxWidth(),
                        buttonHeight = toolbarButtonHeight,
                        restartButtonSize = restartButtonSize
                    )
                }
            )

            ResponsiveGameGrid(
                cards = viewModel.cards,
                isMemorizing = viewModel.isMemorizing || showWinCelebration,
                menuExpanded = isToolbarExpanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 2.dp)
            ) { card ->
                viewModel.onCardClicked(card)
            }
        }

        if (showWinCelebration) {
            WinCelebrationOverlay()
        }
    }
}

@Composable
private fun ToolbarTopDrawer(
    isOpen: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 140))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                content()
            }
        }

        Box(
            modifier = Modifier
                .padding(top = if (isOpen) 2.dp else 0.dp)
                .width(if (isOpen) 94.dp else 78.dp)
                .height(if (isOpen) 30.dp else 26.dp)
                .alpha(if (enabled) 1f else 0.68f)
                .shadow(6.dp, ToolbarDrawerTabShape, clip = false)
                .clip(ToolbarDrawerTabShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2B1148),
                            Color(0xFF4A2280),
                            Color(0xFF2A0F44)
                        )
                    )
                )
                .border(1.2.dp, Color(0xFFFFDFA0), ToolbarDrawerTabShape)
                .clickable(enabled = enabled) { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(Color(0x80FFF4D2))
                )

                Icon(
                    painter = painterResource(
                        id = if (isOpen) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float
                    ),
                    contentDescription = if (isOpen) "Close menu" else "Open menu",
                    tint = Color(0xFFFFF2CF),
                    modifier = Modifier.size(16.dp)
                )
            }
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
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
    menuExpanded: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: (GameCardModel) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val horizontalSpacing = if (menuExpanded) GridHorizontalSpacingOpen else GridHorizontalSpacingClosed
        val verticalSpacing = if (menuExpanded) GridVerticalSpacingOpen else GridVerticalSpacingClosed
        val topPadding = if (menuExpanded) GridTopPaddingOpen else GridTopPaddingClosed
        val bottomPadding = if (menuExpanded) GridBottomPaddingOpen else GridBottomPaddingClosed
        val cardCount = cards.size.coerceAtLeast(1)
        val minColumns = if (cardCount == 1) 1 else 2
        val maxColumns = min(cardCount, 6)

        var bestColumns = minColumns
        var bestCardSize = 0.dp

        for (columns in minColumns..maxColumns) {
            val rows = ceil(cardCount / columns.toDouble()).toInt()
            val widthPerCard = (
                maxWidth - horizontalSpacing * (columns - 1).toFloat()
            ).coerceAtLeast(0.dp) / columns.toFloat()
            val heightPerCard = (
                maxHeight
                    - topPadding
                    - bottomPadding
                    - verticalSpacing * (rows - 1).toFloat()
            ).coerceAtLeast(0.dp) / rows.toFloat()

            val candidate = minOf(widthPerCard, heightPerCard)
            if (candidate > bestCardSize) {
                bestCardSize = candidate
                bestColumns = columns
            }
        }

        val minimumCardSize = if (maxWidth < 500.dp) 70.dp else 82.dp
        val cardScale = if (menuExpanded) 0.95f else 1f
        val cardSize = (bestCardSize * cardScale).coerceAtLeast(minimumCardSize)

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
            horizontalArrangement = Arrangement.spacedBy(
                horizontalSpacing,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            contentPadding = PaddingValues(
                start = 4.dp,
                end = 4.dp,
                top = topPadding,
                bottom = bottomPadding
            ),
            userScrollEnabled = true
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
    val btnThemeTitle = stringResource(id = viewModel.currentTheme.toolbarTitleRes)
    val accentColor = when (viewModel.currentTheme) {
        GameTheme.Animals -> Color(0xFF5AC88A)
        GameTheme.Dance -> Color(0xFF6EA7FF)
        GameTheme.Music -> Color(0xFFB08CFF)
        GameTheme.Dinosaurs -> Color(0xFF8BC34A)
        GameTheme.Dogs -> Color(0xFFFFB37A)
        GameTheme.JungleAnimals -> Color(0xFFFFA552)
    }

    ToolbarButton(
        btnTitle = btnThemeTitle,
        btnImage = R.drawable.ic_memorygame,
        accentColor = accentColor,
        modifier = modifier,
        buttonHeight = buttonHeight,
        enabled = enabled
    ) {
        val themeCycle = listOf(
            GameTheme.Animals,
            GameTheme.Dance,
            GameTheme.Music,
            GameTheme.Dinosaurs,
            GameTheme.Dogs,
            GameTheme.JungleAnimals
        )
        val currentIndex = themeCycle.indexOf(viewModel.currentTheme).takeIf { it >= 0 } ?: 0
        viewModel.currentTheme = themeCycle[(currentIndex + 1) % themeCycle.size]
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
        btnImage = if (isSoundEnabled) R.drawable.sound_on else R.drawable.sound_off,
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
            painter = painterResource(id = R.drawable.restart),
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
