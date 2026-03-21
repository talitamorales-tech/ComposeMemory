package com.talitamorales.composememory.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.GameDifficulty
import com.talitamorales.composememory.gamelogic.GameTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val THEME_SELECTION_TAG = "CM-ThemeSelection"
private const val THEME_EDGE_CROP_PADDING_PX = 12
private const val THEME_MAX_CUTOUT_DIM_PX = 720
private const val THEME_BG_ALPHA_SCAN_MIN = 16
private const val THEME_BG_LIGHT_LUMA_MIN = 205
private const val THEME_BG_MAX_SATURATION = 0.35f
private const val THEME_BG_MIN_BORDER_RATIO = 0.08f

private val DifficultyButtonShape = RoundedCornerShape(20.dp)
private val ThemeCardShape = RoundedCornerShape(28.dp)
private val ThemeCardInnerShape = RoundedCornerShape(25.dp)

private data class DifficultyPalette(
    val base: List<Color>,
    val border: Color
)

private data class ThemeCardPalette(
    val frame: List<Color>,
    val fill: List<Color>,
    val title: Color,
    val badge: Color
)

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
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF75C9FF),
                        Color(0xFFBEE7FF),
                        Color(0xFF7CC3FF)
                    )
                )
            )
            .fantasyClouds()
            .magicSparkles(alpha = 0.26f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xE9FFFFFF),
                            Color(0xD9F2FAFF)
                        )
                    )
                )
                .border(1.6.dp, Color.White.copy(alpha = 0.95f), RoundedCornerShape(34.dp))
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .magicSparkles(alpha = 0.2f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.memory_friends_logo_banner),
                contentDescription = stringResource(id = R.string.logo_content_description),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(148.dp)
                    .scale(1.12f),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = stringResource(id = R.string.app_name),
            color = Color(0xFF0C5EAF),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.9f),
                    offset = Offset(0f, 3f),
                    blurRadius = 8f
                )
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
        )
    }
}

@Composable
fun ThemeSelectionScreen(navController: NavController) {
    var selectedDifficultyId by rememberSaveable { mutableIntStateOf(GameDifficulty.Easy.id) }
    val selectionScrollState = rememberScrollState()
    val themeDisplayOrder = listOf(
        GameTheme.Dogs,
        GameTheme.Animals,
        GameTheme.Dinosaurs,
        GameTheme.JungleAnimals,
        GameTheme.Toys,
        GameTheme.Dolls,
        GameTheme.Cars,
        GameTheme.Music
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF80CFFF),
                        Color(0xFFBFE3FF),
                        Color(0xFF9FD2FF)
                    )
                )
            )
            .fantasyClouds()
            .magicSparkles(alpha = 0.23f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(selectionScrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xD9FFFFFF),
                                Color(0xCCF0F8FF),
                                Color(0xB8DCEFFF)
                            )
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.92f), RoundedCornerShape(30.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .magicSparkles(alpha = 0.2f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.choose_theme_to_play),
                        color = Color(0xFF0A5AAE),
                        fontSize = 44.sp,
                        lineHeight = 45.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.White.copy(alpha = 0.9f),
                                offset = Offset(0f, 4f),
                                blurRadius = 12f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = R.string.choose_difficulty),
                        color = Color(0xFF27476B),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        GameDifficulty.entries.forEach { difficulty ->
                            DifficultyCard(
                                title = stringResource(id = difficulty.titleRes),
                                difficulty = difficulty,
                                selected = selectedDifficultyId == difficulty.id,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedDifficultyId = difficulty.id }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            themeDisplayOrder.chunked(2).forEach { rowThemes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowThemes.forEach { theme ->
                        ThemeCard(
                            theme = theme,
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

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    title: String,
    difficulty: GameDifficulty,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = difficultyPalette(difficulty)

    Box(
        modifier = modifier
            .height(58.dp)
            .scale(if (selected) 1f else 0.95f)
            .shadow(if (selected) 10.dp else 5.dp, DifficultyButtonShape)
            .clip(DifficultyButtonShape)
            .background(Brush.horizontalGradient(palette.base))
            .border(
                width = if (selected) 2.2.dp else 1.4.dp,
                color = if (selected) Color.White else palette.border,
                shape = DifficultyButtonShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f),
                            Color.Transparent
                        )
                    )
                )
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.28f),
                    offset = Offset(0f, 2f),
                    blurRadius = 5f
                )
            )
        )
    }
}

@Composable
fun ThemeCard(
    theme: GameTheme,
    title: String,
    imageRes: Int,
    isPremium: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = themePalette(theme)

    Card(
        modifier = modifier
            .height(252.dp)
            .shadow(18.dp, ThemeCardShape)
            .clickable { onClick() },
        shape = ThemeCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(palette.frame))
                .border(1.dp, Color.White.copy(alpha = 0.86f), ThemeCardShape)
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ThemeCardInnerShape)
                    .background(Brush.verticalGradient(palette.fill))
                    .border(1.4.dp, Color.White.copy(alpha = 0.62f), ThemeCardInnerShape)
                    .magicSparkles(alpha = 0.16f)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(ThemeCardInnerShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.27f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPremium) {
                            stringResource(id = R.string.premium_label)
                        } else {
                            stringResource(id = R.string.free_label)
                        },
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(17.dp))
                            .clip(RoundedCornerShape(17.dp))
                            .background(palette.badge)
                            .border(1.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(17.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xD020102E), CircleShape)
                                .border(1.dp, Color(0xFFFFDDA0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_lock_lock),
                                contentDescription = null,
                                tint = Color(0xFFFFDDA0)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(34.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.58f),
                                    Color.White.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ThemePreviewImage(
                        theme = theme,
                        imageRes = imageRes,
                        title = title,
                        modifier = Modifier.fillMaxSize(0.92f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.58f))
                        .border(1.dp, Color.White.copy(alpha = 0.78f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = palette.title,
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun difficultyPalette(difficulty: GameDifficulty): DifficultyPalette {
    return when (difficulty) {
        GameDifficulty.Easy -> DifficultyPalette(
            base = listOf(Color(0xFF39CC78), Color(0xFF1FA35A)),
            border = Color(0xFF1A8E4E)
        )

        GameDifficulty.Medium -> DifficultyPalette(
            base = listOf(Color(0xFFFFD84F), Color(0xFFFFA905)),
            border = Color(0xFFF28D00)
        )

        GameDifficulty.Hard -> DifficultyPalette(
            base = listOf(Color(0xFFFF62B2), Color(0xFFE12C8D)),
            border = Color(0xFFC11775)
        )
    }
}

@Composable
private fun ThemePreviewImage(
    theme: GameTheme,
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    val needsCutout = theme == GameTheme.Cars || theme == GameTheme.Dolls
    if (!needsCutout) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
        return
    }

    val context = LocalContext.current
    var processedImage by remember(imageRes) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageRes) {
        processedImage = withContext(Dispatchers.Default) {
            createThemeForegroundCutoutBitmap(context, imageRes)
        }
    }

    val imageBitmap = processedImage
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = title,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

private fun createThemeForegroundCutoutBitmap(
    context: Context,
    imageRes: Int
): ImageBitmap? {
    return try {
        val drawable = context.resources.getDrawable(imageRes, context.theme) ?: return null
        val sourceWidth = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1024
        val sourceHeight = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1024
        val (width, height) = scaleDimensionsForCutout(
            width = sourceWidth,
            height = sourceHeight,
            maxDimension = THEME_MAX_CUTOUT_DIM_PX
        )
        val sourceBitmap = drawable.toBitmap(width = width, height = height, config = Bitmap.Config.ARGB_8888)
        val mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)

        if (!themeHasMeaningfulTransparency(mutableBitmap)) {
            clearThemeLightBackgroundConnectedToOuterOpaqueBounds(mutableBitmap)
        }

        val croppedBitmap = cropThemeToOpaqueContent(mutableBitmap, padding = THEME_EDGE_CROP_PADDING_PX)
        croppedBitmap.asImageBitmap()
    } catch (oom: OutOfMemoryError) {
        Log.e(THEME_SELECTION_TAG, "OOM while cutting out theme preview imageRes=$imageRes", oom)
        null
    } catch (throwable: Throwable) {
        Log.e(THEME_SELECTION_TAG, "Failed to process theme preview imageRes=$imageRes", throwable)
        null
    }
}

private fun scaleDimensionsForCutout(
    width: Int,
    height: Int,
    maxDimension: Int
): Pair<Int, Int> {
    val safeWidth = width.coerceAtLeast(1)
    val safeHeight = height.coerceAtLeast(1)
    val largestSide = maxOf(safeWidth, safeHeight)
    if (largestSide <= maxDimension) return safeWidth to safeHeight

    val scaledWidth = ((safeWidth.toLong() * maxDimension) / largestSide).toInt().coerceAtLeast(1)
    val scaledHeight = ((safeHeight.toLong() * maxDimension) / largestSide).toInt().coerceAtLeast(1)
    return scaledWidth to scaledHeight
}

private fun themeHasMeaningfulTransparency(bitmap: Bitmap): Boolean {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 0 || height <= 0) return false

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun alphaAt(x: Int, y: Int): Int = AndroidColor.alpha(pixels[y * width + x])

    val cornersTransparent = listOf(
        alphaAt(0, 0),
        alphaAt(width - 1, 0),
        alphaAt(0, height - 1),
        alphaAt(width - 1, height - 1)
    ).count { it <= 20 }
    if (cornersTransparent >= 3) return true

    var translucentCount = 0
    for (pixel in pixels) {
        if (AndroidColor.alpha(pixel) < 245) translucentCount++
    }

    return translucentCount > (pixels.size * 0.01f)
}

private fun clearThemeLightBackgroundConnectedToOuterOpaqueBounds(bitmap: Bitmap) {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 1 || height <= 1) return

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun luma(red: Int, green: Int, blue: Int): Int =
        ((red * 2126) + (green * 7152) + (blue * 722)) / 10_000

    fun saturation(red: Int, green: Int, blue: Int): Float {
        val maxChannel = maxOf(red, green, blue)
        if (maxChannel == 0) return 0f
        val minChannel = minOf(red, green, blue)
        return (maxChannel - minChannel).toFloat() / maxChannel.toFloat()
    }

    fun isLightBackground(index: Int): Boolean {
        val pixel = pixels[index]
        val alpha = AndroidColor.alpha(pixel)
        if (alpha < THEME_BG_ALPHA_SCAN_MIN) return false
        val red = AndroidColor.red(pixel)
        val green = AndroidColor.green(pixel)
        val blue = AndroidColor.blue(pixel)
        val pixelLuma = luma(red, green, blue)
        if (pixelLuma < THEME_BG_LIGHT_LUMA_MIN) return false
        val pixelSaturation = saturation(red, green, blue)
        val channelDelta = maxOf(red, green, blue) - minOf(red, green, blue)
        return pixelSaturation <= THEME_BG_MAX_SATURATION || channelDelta <= 30
    }

    var minX = width
    var minY = height
    var maxX = -1
    var maxY = -1
    for (y in 0 until height) {
        for (x in 0 until width) {
            val alpha = AndroidColor.alpha(pixels[y * width + x])
            if (alpha >= THEME_BG_ALPHA_SCAN_MIN) {
                if (x < minX) minX = x
                if (y < minY) minY = y
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
            }
        }
    }
    if (maxX < minX || maxY < minY) return

    val visited = BooleanArray(width * height)
    val queue = IntArray(width * height)
    var head = 0
    var tail = 0

    fun enqueue(index: Int) {
        if (index !in pixels.indices || visited[index] || !isLightBackground(index)) return
        visited[index] = true
        queue[tail++] = index
    }

    var borderOpaqueSamples = 0
    var borderBackgroundSamples = 0

    fun seedFromBorder(x: Int, y: Int) {
        if (x !in minX..maxX || y !in minY..maxY) return
        val index = y * width + x
        if (AndroidColor.alpha(pixels[index]) < THEME_BG_ALPHA_SCAN_MIN) return
        borderOpaqueSamples++
        if (isLightBackground(index)) {
            borderBackgroundSamples++
            enqueue(index)
        }
    }

    for (x in minX..maxX) {
        seedFromBorder(x, minY)
        if (maxY != minY) seedFromBorder(x, maxY)
    }
    for (y in (minY + 1) until maxY) {
        seedFromBorder(minX, y)
        if (maxX != minX) seedFromBorder(maxX, y)
    }

    if (borderOpaqueSamples == 0) return
    val borderBackgroundRatio = borderBackgroundSamples.toFloat() / borderOpaqueSamples.toFloat()
    if (borderBackgroundRatio < THEME_BG_MIN_BORDER_RATIO) return

    fun canSpreadTo(index: Int): Boolean {
        val x = index % width
        val y = index / width
        return x in minX..maxX && y in minY..maxY
    }

    while (head < tail) {
        val index = queue[head++]
        val red = AndroidColor.red(pixels[index])
        val green = AndroidColor.green(pixels[index])
        val blue = AndroidColor.blue(pixels[index])
        pixels[index] = AndroidColor.argb(0, red, green, blue)

        val x = index % width
        val y = index / width
        if (x > minX) {
            val left = index - 1
            if (canSpreadTo(left)) enqueue(left)
        }
        if (x < maxX) {
            val right = index + 1
            if (canSpreadTo(right)) enqueue(right)
        }
        if (y > minY) {
            val up = index - width
            if (canSpreadTo(up)) enqueue(up)
        }
        if (y < maxY) {
            val down = index + width
            if (canSpreadTo(down)) enqueue(down)
        }
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
}

private fun cropThemeToOpaqueContent(bitmap: Bitmap, padding: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    var minX = width
    var minY = height
    var maxX = -1
    var maxY = -1

    for (y in 0 until height) {
        for (x in 0 until width) {
            val alpha = AndroidColor.alpha(pixels[y * width + x])
            if (alpha > 14) {
                if (x < minX) minX = x
                if (y < minY) minY = y
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
            }
        }
    }

    if (maxX < minX || maxY < minY) return bitmap

    val cropMinX = (minX - padding).coerceAtLeast(0)
    val cropMinY = (minY - padding).coerceAtLeast(0)
    val cropMaxX = (maxX + padding).coerceAtMost(width - 1)
    val cropMaxY = (maxY + padding).coerceAtMost(height - 1)
    val cropWidth = (cropMaxX - cropMinX + 1).coerceAtLeast(1)
    val cropHeight = (cropMaxY - cropMinY + 1).coerceAtLeast(1)

    return Bitmap.createBitmap(bitmap, cropMinX, cropMinY, cropWidth, cropHeight)
}

private fun themePalette(theme: GameTheme): ThemeCardPalette {
    return when (theme) {
        GameTheme.Dogs -> ThemeCardPalette(
            frame = listOf(Color(0xFFFFE870), Color(0xFFF9A825)),
            fill = listOf(Color(0xFFFFF8B4), Color(0xFFFFE56A)),
            title = Color(0xFF7A3A15),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Animals -> ThemeCardPalette(
            frame = listOf(Color(0xFFFF98DA), Color(0xFFFF60B8)),
            fill = listOf(Color(0xFFFFD8EE), Color(0xFFFFB4DE)),
            title = Color(0xFF8E2E66),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Dinosaurs -> ThemeCardPalette(
            frame = listOf(Color(0xFFB8F36D), Color(0xFF5CBF47)),
            fill = listOf(Color(0xFFD7FFB2), Color(0xFFA5E77E)),
            title = Color(0xFF316A2B),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.JungleAnimals -> ThemeCardPalette(
            frame = listOf(Color(0xFFFFE35F), Color(0xFFEFAB22)),
            fill = listOf(Color(0xFFFFF6B7), Color(0xFFFFD86B)),
            title = Color(0xFF7D4A13),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Toys -> ThemeCardPalette(
            frame = listOf(Color(0xFF7AD7FF), Color(0xFF48A7FF)),
            fill = listOf(Color(0xFFCFF3FF), Color(0xFF97DEFF)),
            title = Color(0xFF1C4E7A),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Dolls -> ThemeCardPalette(
            frame = listOf(Color(0xFFFFB2E5), Color(0xFFE77AD4)),
            fill = listOf(Color(0xFFFFE4F6), Color(0xFFFFC4EC)),
            title = Color(0xFF7F2A65),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Cars -> ThemeCardPalette(
            frame = listOf(Color(0xFF7CB7FF), Color(0xFF3768D9)),
            fill = listOf(Color(0xFFDCEBFF), Color(0xFFAACBFF)),
            title = Color(0xFF1A3F83),
            badge = Color(0xFF2E9A45)
        )

        GameTheme.Music -> ThemeCardPalette(
            frame = listOf(Color(0xFFBEA5FF), Color(0xFF7F62E3)),
            fill = listOf(Color(0xFFECE4FF), Color(0xFFCDBDFF)),
            title = Color(0xFF4A2D8E),
            badge = Color(0xFF2E9A45)
        )
    }
}

private fun Modifier.magicSparkles(alpha: Float = 0.2f): Modifier = drawBehind {
    val stars = listOf(
        0.07f to 0.12f,
        0.20f to 0.08f,
        0.32f to 0.19f,
        0.44f to 0.10f,
        0.58f to 0.16f,
        0.73f to 0.09f,
        0.85f to 0.20f,
        0.92f to 0.12f,
        0.14f to 0.34f,
        0.31f to 0.41f,
        0.52f to 0.30f,
        0.78f to 0.37f,
        0.91f to 0.32f,
        0.10f to 0.64f,
        0.25f to 0.72f,
        0.47f to 0.66f,
        0.68f to 0.74f,
        0.86f to 0.69f,
        0.18f to 0.90f,
        0.39f to 0.85f,
        0.61f to 0.93f,
        0.81f to 0.88f
    )

    stars.forEachIndexed { index, (x, y) ->
        val radius = size.minDimension * if (index % 4 == 0) 0.010f else 0.006f
        drawCircle(
            color = Color.White.copy(alpha = if (index % 2 == 0) alpha else alpha * 0.7f),
            radius = radius,
            center = Offset(size.width * x, size.height * y)
        )
    }
}

private fun Modifier.fantasyClouds(): Modifier = drawBehind {
    drawCircle(
        color = Color.White.copy(alpha = 0.23f),
        radius = size.minDimension * 0.17f,
        center = Offset(size.width * 0.14f, size.height * 0.1f)
    )
    drawCircle(
        color = Color(0xFFE8F5FF).copy(alpha = 0.24f),
        radius = size.minDimension * 0.22f,
        center = Offset(size.width * 0.9f, size.height * 0.2f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.18f),
        radius = size.minDimension * 0.25f,
        center = Offset(size.width * 0.12f, size.height * 0.88f)
    )
    drawCircle(
        color = Color(0xFFD6ECFF).copy(alpha = 0.2f),
        radius = size.minDimension * 0.27f,
        center = Offset(size.width * 0.88f, size.height * 0.86f)
    )
}
