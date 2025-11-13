package com.shikidroid.uikit.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * Контейнер с возможностью свайпа в левую и правую сторону
 *
 * @param swipeableState состояние свайпа
 * @param backgroundItem задний фон элемента
 * @param itemContent основной элемент
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableBox(
    swipeableState: SwipeableState<Int>,
    isLeftToRight: Boolean = true,
    isRightToLeft: Boolean = true,
    leftToRightSize: Float = 100f,
    rightToLeftSize: Float = 100f,
    backgroundItem: @Composable () -> Unit,
    itemContent: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .swipeable(
                state = swipeableState,
                anchors =
                when {
                    isLeftToRight && !isRightToLeft -> {
                        mapOf(
                            0f to 0,
                            dipToPx(context = LocalContext.current, dipValue = leftToRightSize) to 1
                        )
                    }
                    !isLeftToRight && isRightToLeft -> {
                        mapOf(
                            0f to 0,
                            -dipToPx(context = LocalContext.current, dipValue = rightToLeftSize) to -1
                        )
                    }
                    else -> {
                        mapOf(
                            0f to 0,
                            dipToPx(context = LocalContext.current, dipValue = leftToRightSize) to 1,
                            -dipToPx(context = LocalContext.current, dipValue = rightToLeftSize) to -1,
                        )
                    }
                },
                thresholds = { _, _ ->
                    FractionalThreshold(0.5f)
                },
                orientation = Orientation.Horizontal
            )
            .background(Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
        ) {
            backgroundItem()
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .offset {
                    IntOffset(x = swipeableState.offset.value.roundToInt(), y = 0)
                }
        ) {
            itemContent()
        }
    }
}

/**
 * Метод для перевода dip в Px
 *
 * @param context контекст системы
 * @param dipValue значение в пикселях
 */
fun dipToPx(context: Context, dipValue: Float): Float {
    return dipValue * context.resources.displayMetrics.density
}