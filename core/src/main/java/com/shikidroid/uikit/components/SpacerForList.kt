package com.shikidroid.uikit.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shikidroid.uikit.oneHundredFiftyDP

/**
 * Стандартный оступ для нижней границы экрана в списках
 */
@Composable
fun SpacerForList() {
    Spacer(
        modifier = Modifier
            .height(oneHundredFiftyDP)
    )
}