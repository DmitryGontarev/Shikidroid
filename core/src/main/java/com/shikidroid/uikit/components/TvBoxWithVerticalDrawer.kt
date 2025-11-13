package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TvBoxWithVerticalDrawer(
    mainModifier: Modifier = Modifier,
    isDrawerOpen: Boolean,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable (BoxScope) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** высота родительского контейнера */
        val parentHeight = with(LocalDensity.current) {
            constraints.maxHeight.toDp()
        }

        /** ширина родительского контейнера */
        val parentWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        /** высота бокового меню */
        var drawerHeight by remember {
            mutableStateOf(0.dp)
        }

        /** ширина бокового меню */
        var drawerWidth by remember {
            mutableStateOf(0.dp)
        }

        /** сдвиг родительского контейнера по высоте */
        val offsetMainContentHeight: Dp by animateDpAsState(
            targetValue = if (isDrawerOpen) -drawerHeight else 0.dp,
            animationSpec = tween(
                durationMillis = 750
            )
        )

        /** модификатор для composable функции основного экрана */
        val mainContentModifier =
            mainModifier
                .size(
                    height = parentHeight,
                    width = parentWidth
                )
                .offset(x = 0.dp, y = offsetMainContentHeight)

        MeasureComposableDp(content = {
            drawerContent()
        }, size = { width, height ->
            drawerHeight = height
            drawerWidth = width
        })

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                if (offsetMainContentHeight < 0.dp) {
                    drawerContent()
                }
            }
        }

        Box(
            modifier = mainContentModifier
        ) {
            if (offsetMainContentHeight != -drawerHeight) {
                mainContent(this)
            }
        }
    }
}