package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun IconRotation(
    modifier: Modifier = Modifier,
    icon: Int,
    tint: Color,
    isIconRotate: Boolean,
    rotateValue: Float
) {
    /** состояние поворота иконки */
    val rotationState by animateFloatAsState(
        targetValue = if (isIconRotate) rotateValue else 0f
    )

    Icon(
        modifier = modifier
            .rotate(rotationState),
        painter = painterResource(id = icon),
        tint = tint,
        contentDescription = null
    )
}