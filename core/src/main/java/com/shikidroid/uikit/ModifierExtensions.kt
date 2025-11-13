package com.shikidroid.uikit

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.components.isSystemUiHide

/**
 * Функция расширение, которая добавляет отступы к элементу в зависимости от его положения в списке
 *
 * @param items список элементов
 * @param item элемент в списке
 * @param top отступ сверху
 * @param bottom отступ снизу
 * @param defaultPadding стандартный отступ
 * @param startEndPadding отступ для начального и конечного элемента
 */
fun <T> Modifier.paddingByList(
    items: List<T>,
    item: T,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp,
    defaultPadding: Dp = fiveDP,
    startEndPadding: Dp = fourteenDP
): Modifier {
    if (items.isEmpty()) {
        return this
            .padding(
                top = top,
                start = defaultPadding,
                end = defaultPadding,
                bottom = bottom
            )
    } else {
        return this
            .padding(
                top = top,
                start = if (item == items.first()) startEndPadding else defaultPadding,
                end = if (item == items.last()) startEndPadding else defaultPadding,
                bottom = bottom
            )
    }
}

/**
 * Функция расширение, которая добавляет верхний отступ верхнего бара
 */
fun Modifier.setStatusBarPadding(): Modifier = composed {

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    this.padding(
        top = topInsetPadding
    )
}


/**
 * Функция расширение, которая добавляет верхний отступ в зависимости от видимости верхнего бара
 */
fun Modifier.setTopBarPadding(): Modifier = composed {

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    if (isSystemUiHide()) {
        this.padding(
            top =
            when {
                isComposableHorizontalOrientation() -> {
                    if (topInsetPadding == 0.dp) 0.dp else topInsetPadding
                }

                !isComposableHorizontalOrientation() -> {
                    if (topInsetPadding == 0.dp) 30.dp else topInsetPadding
                }

                else -> topInsetPadding
            }
        )
    } else {
        this.padding(
            top = topInsetPadding
        )
    }
}