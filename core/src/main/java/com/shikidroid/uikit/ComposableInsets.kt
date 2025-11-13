package com.shikidroid.uikit

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.components.isSystemUiHide
import kotlinx.coroutines.delay

/** Возвращает значение высоты отступа статус бара в пикселях */
@Composable
fun getStatusBarPadding(): Dp {

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    return if (isSystemUiHide()) {
        when {
            isComposableHorizontalOrientation() -> {
                if (topInsetPadding == 0.dp) 0.dp else topInsetPadding
            }
            !isComposableHorizontalOrientation() -> {
                if (topInsetPadding == 0.dp) 30.dp else topInsetPadding
            }
            else -> topInsetPadding
        }
    } else {
        topInsetPadding
    }
}

/** Возвращает высоту системного статус бара в Dp */
@Composable
fun getStatusBarPaddingValue(): Dp {
    return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
}

/** Возвращает высоту системной, нижней панели навигации в Dp */
@Composable
fun getNavigationBarPaddingValue(): Dp {
    return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
}

/** Возвращает значение высоты отступа бара навигации в пикселях */
@Composable
fun getNavigationBarPadding(): Dp {
    return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
}

@Composable
fun SpacerStatusBar() {

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Spacer(
        modifier = Modifier
            .height(height = topInsetPadding)
    )
}

@Composable
fun SpacerNavigationBar() {

    /** отступ бара навигации */
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Spacer(
        modifier = Modifier
            .height(height = bottomInsetPadding)
    )
}

/** Возвращает флаг видимости клавиатуры */
@Composable
fun getIsKeyboardVisible(): Boolean {
    return WindowInsets.ime.getBottom(LocalDensity.current) > 0
}

/**
 * Возвращает флаг видимости клавиатуры в обратном вызове
 * позволяет сразу узнавать о вызове открытия/скрытия клавиатуры
 *
 * @param isVisible обратный вызов, отбрасывающий флаг открытия/скрытия клавиатуры
 */
@Composable
fun GetIsKeyboardCallbackVisible(isVisible: (Boolean) -> Unit) {
    // переменная для хранения прошлого значения высоты клавиатуры
    val tempValue = remember { mutableStateOf(0) }

    // текущее значение высоты клавиатуры
    val currentValue = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(currentValue) {
        // ставим задержку после обновления прошлого значения,
        // так как нет смысла обновлять переменную при каждом изменении
        isVisible.invoke(currentValue > tempValue.value)
        tempValue.value = currentValue
        delay(10)
    }
}

