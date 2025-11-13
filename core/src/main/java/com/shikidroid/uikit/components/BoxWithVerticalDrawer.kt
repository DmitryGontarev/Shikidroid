package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BoxWithVerticalDrawer(
    mainModifier: Modifier = Modifier,
    isDrawerOpen: Boolean,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable (BoxScope) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {

        /** текущее разрешение экрана */
        val localDensity = LocalDensity.current

        /** положения устройства в пространстве */
        val localOrientation = LocalConfiguration.current.orientation

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

        /** сдвиг по высоте контейнера бокового меню */
        val offsetDrawer: Dp by animateDpAsState(
            targetValue = if (isDrawerOpen) parentHeight - drawerHeight * 2 else drawerHeight + parentHeight,
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

        /** модификатор для composable функции бокового меню */
        val drawerModifier =
            Modifier
                .wrapContentSize()
                .offset(y = offsetDrawer)

        Box(
            modifier = mainContentModifier
        ) {
            mainContent(this)
        }

        Box(
            modifier = drawerModifier
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        drawerHeight = with(localDensity) {
                            it.size.height.toDp()
                        }
                        drawerWidth = with(localDensity) {
                            it.size.width.toDp()
                        }
                    }
                    .offset(y = drawerHeight)
            ) {
                drawerContent()
            }
        }
    }
}