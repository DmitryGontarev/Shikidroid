package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyHorizontalPager(
    page: Int = 0,
    screens: List<@Composable () -> Unit>
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** ширина родительского контейнера */
        val parentWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        var currentIndex by remember {
            mutableStateOf(0)
        }


        val offsetMainContentWidth: Dp by animateDpAsState(
            targetValue =
                when (page) {
                    0 -> 0.dp
                    else -> parentWidth * page
                },
            animationSpec = tween(
                durationMillis = 450
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (screens.isNotEmpty()) {
                screens.forEachIndexed { index, function ->
                    if (page == index) {
                        currentIndex = index
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(
                                x =
                                if (index == 0) {
                                    0.dp - offsetMainContentWidth
                                } else {
                                    (parentWidth * index) - offsetMainContentWidth
                                }
                            )
                    ) {
                        function()
                    }
                }
            }
        }
    }
}