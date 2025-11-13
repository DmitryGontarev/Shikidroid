package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TvRailLayout(
    isShift: Boolean = false,
    shiftClose: Dp = 40.dp,
    shiftOpen: Dp = 120.dp,
    backgroundColor: Color = Color.Transparent,
    tvRail: @Composable (offsetX: Dp) -> Unit,
    content: @Composable () -> Unit
) {

    val shiftX: Dp by animateDpAsState(
        targetValue = if (isShift) shiftOpen else shiftClose,
        animationSpec = tween(
            durationMillis = 450
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {

        /** ширина родительского контейнера */
        val parentWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        tvRail(shiftOpen)

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(width = parentWidth - shiftClose)
                .offset(x = shiftX)
        ) {
            content()
        }
    }
}