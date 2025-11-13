package com.shikidroid.uikit

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Функция, которая возвращает true, если текущая Composable в горизонтальном положении
 */
@Composable
fun isComposableHorizontalOrientation() : Boolean {
    val orientation = LocalConfiguration.current.orientation
    return orientation == Configuration.ORIENTATION_LANDSCAPE
}