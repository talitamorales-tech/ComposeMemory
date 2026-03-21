package com.talitamorales.composememory.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.util.Log
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.talitamorales.composememory.R
import com.talitamorales.composememory.gamelogic.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

private val CarouselHeaderShape = RoundedCornerShape(32.dp)
private val CarouselFrameShape = RoundedCornerShape(34.dp)
private val CarouselFrameInnerShape = RoundedCornerShape(28.dp)
private val CarouselImageShape = RoundedCornerShape(23.dp)
private const val CAROUSEL_AUTO_ADVANCE_MS = 5000L
private const val EDGE_CROP_PADDING_PX = 12
private const val CAROUSEL_MAX_CUTOUT_DIM_PX = 900
private const val BG_ALPHA_SCAN_MIN = 16
private const val BG_LIGHT_LUMA_MIN = 205
private const val BG_MAX_SATURATION = 0.35f
private const val BG_MIN_BORDER_RATIO = 0.08f
private const val CAROUSEL_LOG_TAG = "CM-CardsCarousel"

@Composable
fun CardsCarouselScreen(onClose: () -> Unit) {
    val cardImages = remember { Card.allThemeImageResources() }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var autoAdvanceResetToken by rememberSaveable { mutableIntStateOf(0) }
    val totalCards = cardImages.size

    fun nextIndex(): Int {
        if (totalCards == 0) return 0
        return (currentIndex + 1) % totalCards
    }

    fun previousIndex(): Int {
        if (totalCards == 0) return 0
        return if (currentIndex == 0) totalCards - 1 else currentIndex - 1
    }

    fun advanceManuallyToNext() {
        currentIndex = nextIndex()
        autoAdvanceResetToken++
    }

    fun advanceManuallyToPrevious() {
        currentIndex = previousIndex()
        autoAdvanceResetToken++
    }

    LaunchedEffect(totalCards) {
        if (totalCards == 0) {
            currentIndex = 0
            return@LaunchedEffect
        }
        if (currentIndex !in 0 until totalCards) {
            currentIndex = 0
        }
    }

    LaunchedEffect(totalCards, autoAdvanceResetToken) {
        if (totalCards <= 1) return@LaunchedEffect
        while (isActive) {
            delay(CAROUSEL_AUTO_ADVANCE_MS)
            currentIndex = nextIndex()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7CCBFF),
                        Color(0xFFBCE6FF),
                        Color(0xFF89CBFF),
                        Color(0xFF62B9FF)
                    )
                )
            )
            .carouselClouds()
            .carouselDust(alpha = 0.22f)
    ) {
        val compactLayout = maxWidth < 620.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compactLayout) 16.dp else 24.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .widthIn(max = 640.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarouselHeader(
                    currentIndex = currentIndex,
                    totalCards = totalCards,
                    compactLayout = compactLayout
                )

                Spacer(modifier = Modifier.height(14.dp))

                CarouselStage(
                    cardImages = cardImages,
                    currentIndex = currentIndex,
                    compactLayout = compactLayout,
                    onTapNext = { advanceManuallyToNext() },
                    onTapPrevious = { advanceManuallyToPrevious() },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CarouselIndicators(
                    totalCards = totalCards,
                    currentIndex = currentIndex
                )
            }

            CarouselCloseButton(
                onClose = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun CarouselHeader(
    currentIndex: Int,
    totalCards: Int,
    compactLayout: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, CarouselHeaderShape, clip = false)
            .clip(CarouselHeaderShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE8FFFFFF),
                        Color(0xD8F6FFFF),
                        Color(0xC8DFF3FF)
                    )
                )
            )
            .border(1.6.dp, Color.White.copy(alpha = 0.95f), CarouselHeaderShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .carouselDust(alpha = 0.16f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.carousel_title),
                color = Color(0xFF0A5AAE),
                fontSize = if (compactLayout) 35.sp else 41.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White.copy(alpha = 0.9f),
                        offset = Offset(0f, 3f),
                        blurRadius = 9f
                    )
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.carousel_tap_to_advance),
                color = Color(0xFF2B5079),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF39B962), Color(0xFF2A9950))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.92f), RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                val counterText = if (totalCards > 0) "${currentIndex + 1} / $totalCards" else "0 / 0"
                Text(
                    text = counterText,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun CarouselStage(
    cardImages: List<Int>,
    currentIndex: Int,
    compactLayout: Boolean,
    onTapNext: () -> Unit,
    onTapPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, CarouselFrameShape, clip = false)
            .clip(CarouselFrameShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFE978),
                        Color(0xFFFF95DA),
                        Color(0xFF6EC8FF)
                    )
                )
            )
            .border(1.8.dp, Color.White.copy(alpha = 0.9f), CarouselFrameShape)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CarouselFrameInnerShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF9ED),
                            Color(0xFFFFF0F9),
                            Color(0xFFE8F3FF)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.8f), CarouselFrameInnerShape)
                .carouselDust(alpha = 0.15f)
                .padding(if (compactLayout) 10.dp else 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(if (compactLayout) 92.dp else 108.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.34f),
                                Color.Transparent
                            )
                        )
                    )
            )

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
                CarouselImagePanel(
                    imageRes = imageRes,
                    safeIndex = safeIndex,
                    canAdvance = cardImages.size > 1,
                    onTapNext = onTapNext
                )
            }

            if (cardImages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CarouselNavButton(
                        iconRes = android.R.drawable.ic_media_previous,
                        onClick = onTapPrevious
                    )
                    CarouselNavButton(
                        iconRes = android.R.drawable.ic_media_next,
                        onClick = onTapNext
                    )
                }
            }
        }
    }
}

@Composable
private fun CarouselImagePanel(
    imageRes: Int?,
    safeIndex: Int,
    canAdvance: Boolean,
    onTapNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CarouselImageShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD9ECFF),
                        Color(0xFFE6DEFF),
                        Color(0xFFD6F1FF),
                        Color(0xFFFFDFF1)
                    )
                )
            )
            .border(1.1.dp, Color(0xFFF7EAC4), CarouselImageShape)
            .carouselDust(alpha = 0.1f)
            .clickable(enabled = canAdvance) { onTapNext() }
            .padding(if (canAdvance) 12.dp else 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.48f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD5E8FF),
                            Color(0xFFE7DBFF),
                            Color(0xFFD2F0FF)
                        )
                    )
                )
                .border(1.dp, Color(0x8FB8D9FF), RoundedCornerShape(18.dp))
                .carouselDust(alpha = 0.08f),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.97f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x3A6AB8FF),
                                    Color(0x38777BFF),
                                    Color(0x3BFF95D0)
                                )
                            )
                        )
                        .border(1.dp, Color(0x8BB0D4FF), RoundedCornerShape(22.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProcessedCarouselImage(
                        imageRes = imageRes,
                        contentDescription = stringResource(
                            id = R.string.carousel_card_content_description,
                            safeIndex + 1
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x3A6AB8FF),
                                    Color(0x38777BFF),
                                    Color(0x3BFF95D0)
                                )
                            )
                        )
                        .border(1.dp, Color(0x8BB0D4FF), RoundedCornerShape(22.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.memory_friends_logo_banner),
                        contentDescription = stringResource(id = R.string.logo_content_description),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcessedCarouselImage(
    imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var processedImage by remember(imageRes) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageRes) {
        processedImage = withContext(Dispatchers.Default) {
            createForegroundCutoutBitmap(context, imageRes)
        }
    }

    val imageBitmap = processedImage
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        AsyncImage(
            model = imageRes,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

private fun createForegroundCutoutBitmap(
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
            maxDimension = CAROUSEL_MAX_CUTOUT_DIM_PX
        )
        val sourceBitmap = drawable.toBitmap(width = width, height = height, config = Bitmap.Config.ARGB_8888)
        val mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Avoid background-cutout on assets that already have a transparent background.
        if (!hasMeaningfulTransparency(mutableBitmap)) {
            clearLightBackgroundConnectedToOuterOpaqueBounds(mutableBitmap)
        }

        val croppedBitmap = cropToOpaqueContent(mutableBitmap, padding = EDGE_CROP_PADDING_PX)
        croppedBitmap.asImageBitmap()
    } catch (oom: OutOfMemoryError) {
        Log.e(CAROUSEL_LOG_TAG, "OOM while processing carousel imageRes=$imageRes", oom)
        null
    } catch (throwable: Throwable) {
        Log.e(CAROUSEL_LOG_TAG, "Failed to process carousel imageRes=$imageRes", throwable)
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

private fun hasMeaningfulTransparency(bitmap: Bitmap): Boolean {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 0 || height <= 0) return false

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun alphaAt(x: Int, y: Int): Int {
        return AndroidColor.alpha(pixels[y * width + x])
    }

    val cornersTransparent =
        listOf(
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

private fun clearLightBackgroundConnectedToOuterOpaqueBounds(bitmap: Bitmap) {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 1 || height <= 1) return

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun luma(red: Int, green: Int, blue: Int): Int {
        return ((red * 2126) + (green * 7152) + (blue * 722)) / 10_000
    }

    fun saturation(red: Int, green: Int, blue: Int): Float {
        val maxChannel = maxOf(red, green, blue)
        if (maxChannel == 0) return 0f
        val minChannel = minOf(red, green, blue)
        return (maxChannel - minChannel).toFloat() / maxChannel.toFloat()
    }

    fun isLightBackground(index: Int): Boolean {
        val pixel = pixels[index]
        val alpha = AndroidColor.alpha(pixel)
        if (alpha < BG_ALPHA_SCAN_MIN) return false
        val red = AndroidColor.red(pixel)
        val green = AndroidColor.green(pixel)
        val blue = AndroidColor.blue(pixel)
        val pixelLuma = luma(red, green, blue)
        if (pixelLuma < BG_LIGHT_LUMA_MIN) return false
        val pixelSaturation = saturation(red, green, blue)
        val channelDelta = maxOf(red, green, blue) - minOf(red, green, blue)
        return pixelSaturation <= BG_MAX_SATURATION || channelDelta <= 30
    }

    var minX = width
    var minY = height
    var maxX = -1
    var maxY = -1
    for (y in 0 until height) {
        for (x in 0 until width) {
            val alpha = AndroidColor.alpha(pixels[y * width + x])
            if (alpha >= BG_ALPHA_SCAN_MIN) {
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
        if (AndroidColor.alpha(pixels[index]) < BG_ALPHA_SCAN_MIN) return
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
    if (borderBackgroundRatio < BG_MIN_BORDER_RATIO) return

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

private fun cropToOpaqueContent(bitmap: Bitmap, padding: Int): Bitmap {
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

@Composable
private fun CarouselIndicators(
    totalCards: Int,
    currentIndex: Int
) {
    if (totalCards <= 0) return

    val safeCurrent = currentIndex.coerceIn(0, totalCards - 1)
    val maxVisible = 7
    val startIndex = if (totalCards <= maxVisible) {
        0
    } else {
        (safeCurrent - (maxVisible / 2)).coerceIn(0, totalCards - maxVisible)
    }
    val endExclusive = minOf(totalCards, startIndex + maxVisible)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x6AFFFFFF))
            .border(1.dp, Color.White.copy(alpha = 0.76f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (startIndex > 0) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0x77FFFFFF))
                    )
                }

                for (index in startIndex until endExclusive) {
                    val selected = index == safeCurrent
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(if (selected) 10.dp else 8.dp)
                            .width(if (selected) 28.dp else 10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) Color(0xFF2D77DF) else Color(0x63FFFFFF)
                            )
                            .border(
                                width = if (selected) 1.dp else 0.dp,
                                color = if (selected) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }

                if (endExclusive < totalCards) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0x77FFFFFF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${safeCurrent + 1} / $totalCards",
                color = Color(0xFF2D4E75),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CarouselNavButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(10.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xC0435E86),
                        Color(0xD72A4268)
                    )
                )
            )
            .border(1.dp, Color(0xE9FFE9BC), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color(0xFFFFF3D0)
        )
    }
}

@Composable
private fun CarouselCloseButton(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .shadow(10.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFF6F78),
                        Color(0xFFE03D3F)
                    )
                )
            )
            .border(1.1.dp, Color.White.copy(alpha = 0.92f), CircleShape)
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
            contentDescription = stringResource(id = R.string.close),
            tint = Color.White
        )
    }
}

private fun Modifier.carouselClouds(): Modifier = drawBehind {
    drawCircle(
        color = Color.White.copy(alpha = 0.24f),
        radius = size.minDimension * 0.18f,
        center = Offset(size.width * 0.12f, size.height * 0.12f)
    )
    drawCircle(
        color = Color(0xFFE7F4FF).copy(alpha = 0.24f),
        radius = size.minDimension * 0.22f,
        center = Offset(size.width * 0.9f, size.height * 0.18f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.18f),
        radius = size.minDimension * 0.24f,
        center = Offset(size.width * 0.1f, size.height * 0.86f)
    )
    drawCircle(
        color = Color(0xFFD6EBFF).copy(alpha = 0.2f),
        radius = size.minDimension * 0.28f,
        center = Offset(size.width * 0.88f, size.height * 0.87f)
    )
}

private fun Modifier.carouselDust(alpha: Float = 0.2f): Modifier = drawBehind {
    val stars = listOf(
        0.06f to 0.14f,
        0.18f to 0.09f,
        0.30f to 0.17f,
        0.42f to 0.11f,
        0.57f to 0.18f,
        0.72f to 0.1f,
        0.86f to 0.19f,
        0.92f to 0.12f,
        0.12f to 0.35f,
        0.28f to 0.4f,
        0.46f to 0.3f,
        0.62f to 0.37f,
        0.8f to 0.33f,
        0.11f to 0.64f,
        0.24f to 0.72f,
        0.42f to 0.68f,
        0.6f to 0.75f,
        0.78f to 0.7f,
        0.9f to 0.78f,
        0.17f to 0.89f,
        0.37f to 0.84f,
        0.57f to 0.92f,
        0.78f to 0.86f
    )

    stars.forEachIndexed { index, (x, y) ->
        val radius = size.minDimension * if (index % 5 == 0) 0.01f else 0.006f
        drawCircle(
            color = Color.White.copy(alpha = if (index % 2 == 0) alpha else alpha * 0.7f),
            radius = radius,
            center = Offset(size.width * x, size.height * y)
        )
    }
}
