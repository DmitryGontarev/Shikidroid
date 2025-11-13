package com.shikidroid.uikit.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun isSystemUiHide(): Boolean {

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    /** отступ бара навигации */
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val configuration = LocalConfiguration.current

    val density = LocalDensity.current

    val height = with(density) {
        configuration.screenHeightDp.dp.roundToPx()
    }

    return (height - topInsetPadding.value) == height.toFloat()
}