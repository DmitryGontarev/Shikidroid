package com.shikidroid.uikit.components

import android.content.res.Configuration
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
fun BoxWithHorizontalDrawer(
    modifier: Modifier = Modifier,
    mainModifier: Modifier = Modifier,
    isLeftToRight: Boolean = true,
    isDrawerOpen: Boolean,
    mainContentHeightOffset: Dp = 0.dp,
    horizontalDrawerOffset: Dp = 0.dp,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable (BoxScope) -> Unit
) {

    BoxWithConstraints(
        modifier = modifier
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

        /** сдвиг по ширине в зависимости от положения */
        val widthOffset =
            if (isLeftToRight) {
                when (localOrientation) {
                    Configuration.ORIENTATION_PORTRAIT -> drawerWidth
                    Configuration.ORIENTATION_LANDSCAPE -> drawerWidth + horizontalDrawerOffset
                    else -> drawerWidth + horizontalDrawerOffset
                }
            } else {
                when (localOrientation) {
                    Configuration.ORIENTATION_PORTRAIT -> -drawerWidth
                    Configuration.ORIENTATION_LANDSCAPE -> -drawerWidth - horizontalDrawerOffset
                    else -> -drawerWidth - horizontalDrawerOffset
                }
            }

        /** сдвиг родительского контейнера по высоте */
        val offsetMainContentHeight: Dp by animateDpAsState(
            targetValue = if (isDrawerOpen) mainContentHeightOffset else 0.dp,
            animationSpec = tween(
                durationMillis = 450
            )
        )

        /** сдвиг родительского контейнера по ширине */
        val offsetMainContentWidth: Dp by animateDpAsState(
            targetValue = if (isDrawerOpen) widthOffset else 0.dp,
            animationSpec = tween(
                durationMillis = 450
            )
        )

        /** сдвиг по ширине контейнера бокового меню */
        val offsetDrawer: Dp by animateDpAsState(
            targetValue =
            if (isLeftToRight) {
                if (isDrawerOpen) -widthOffset else -(widthOffset + parentWidth)
            } else {
                if (isDrawerOpen) parentWidth + widthOffset else parentWidth
            },
            animationSpec = tween(
                durationMillis = 450
            )
        )

        /** модификатор для composable функции основного экрана */
        val mainContentModifier =
            mainModifier
                .size(
                    height = parentHeight,
                    width = parentWidth
                )
                .offset(x = offsetMainContentWidth, y = offsetMainContentHeight)

        /** модификатор для composable функции бокового меню */
        val drawerModifier =
            Modifier
                .wrapContentSize()
                .offset(x = offsetDrawer)

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
                    .offset(
                        x = if (isLeftToRight) widthOffset else 0.dp
                    )
            ) {
                drawerContent()
            }
        }
    }
}