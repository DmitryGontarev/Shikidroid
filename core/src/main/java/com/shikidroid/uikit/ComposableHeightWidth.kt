package com.shikidroid.uikit

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/** Возвращает высоту экрана в Dp */
@Composable
fun getScreenHeightConfigurationDp(): Dp {
    return LocalConfiguration.current.screenHeightDp.dp
}

/** Возвращает ширину экрана в Dp */
@Composable
fun getScreenWidthConfigurationDp(): Dp {
    return LocalConfiguration.current.screenWidthDp.dp
}

@Composable
fun composableHeight(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp
}
@Composable
fun composableWidth(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}

@Composable
fun displayMetricsHeight(): Int {
    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    return displayMetrics.heightPixels
}

@Composable
fun displayMetricsWidth(): Int {
    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    return displayMetrics.widthPixels
}

/**
 * Функция для измерения размера Composable
 *
 * @param widthDp обратный вызов, отбрасывающий ширину контента в Dp
 * @param heightDp обратный вызов, отбрасывающий высоту контента в Dp
 * @param content контент, который нужно измерить
 */
@Composable
fun GetComposableSizeDp(
    widthDp: ((Dp) -> Unit)? = null,
    heightDp: ((Dp) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    /** экземпляр Density для пересчёта размеров экрана */
    val density = LocalDensity.current

    /** свойство для хранения размера в виде IntSize */
    val size = remember { mutableStateOf(IntSize.Zero) }

    SubcomposeLayout { constraints ->
        val placeables = subcompose(slotId = "measure") { content() }.map { it.measure(constraints) }

        size.value = placeables.firstOrNull()?.let { placebale ->
            IntSize(
                width = placebale.width,
                height = placebale.height
            )
        } ?: IntSize.Zero

        layout(
            width = 0,
            height = 0
        ) {
            // на экран не нужно ничего выводить, только измеряем контент
        }
    }

    widthDp?.let { widthCallback ->
        val dp = with(density) { size.value.width.toDp() }
        widthCallback.invoke(dp)
    }

    heightDp?.let { heightCallback ->
        val dp = with(density) { size.value.height.toDp() }
        heightCallback.invoke(dp)
    }
}

@Composable
fun GetIncreaseValueByScreenRatio(
    scaleState: Boolean,
    videoHeight: Int,
    videoWidth: Int,
    ratio: (ratio: Float) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val height = constraints.maxHeight
        val width = constraints.maxWidth

        if (scaleState) {
            when {
                videoHeight == height && videoWidth == width -> {
                    ratio(1f)
                }
                videoHeight != height && videoWidth == width -> {
                    val scale = height.toFloat() / videoHeight.toFloat()
                    ratio(scale)
                }
                videoHeight == height && videoWidth != width -> {
                    val scale = width.toFloat() / videoWidth.toFloat()
                    ratio(scale)
                }
                else -> {
                    ratio(1f)
                }
            }
        } else {
            ratio(1f)
        }
    }
}