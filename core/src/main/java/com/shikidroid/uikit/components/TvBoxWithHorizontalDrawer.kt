package com.shikidroid.uikit.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TvBoxWithHorizontalDrawer(
    modifier: Modifier = Modifier,
    mainModifier: Modifier = Modifier,
    isLeftToRight: Boolean = true,
    isAlwaysDrawer: Boolean = false,
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

        /** модификатор для composable функции основного экрана */
        val mainContentModifier =
            mainModifier
                .size(
                    height = parentHeight,
                    width = parentWidth
                )
                .offset(x = offsetMainContentWidth, y = offsetMainContentHeight)

        MeasureComposableDp(
            content = {
                drawerContent()
            },
            size = { width, height ->
                drawerHeight = height
                drawerWidth = width
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .offset(
                        x =
                        if (isLeftToRight) {
                            0.dp
                        } else {
                            parentWidth - drawerWidth
                        }
                    )
            ) {
                when {
                    isAlwaysDrawer -> {
                        drawerContent()
                    }
                    offsetMainContentWidth != 0.dp -> {
                        drawerContent()
                    }
                    else -> Unit
                }
            }
        }

        Box(
            modifier = mainContentModifier
        ) {
            mainContent(this)
        }
    }
}

@Composable
fun TvBoxWithLeftHorizontalDrawer(
    modifier: Modifier = Modifier,
    mainModifier: Modifier = Modifier,
    isAlwaysDrawer: Boolean = false,
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
            when (localOrientation) {
                Configuration.ORIENTATION_PORTRAIT -> drawerWidth
                Configuration.ORIENTATION_LANDSCAPE -> drawerWidth + horizontalDrawerOffset
                else -> drawerWidth + horizontalDrawerOffset
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

        /** модификатор для composable функции основного экрана */
        val mainContentModifier =
            mainModifier
                .size(
                    height = parentHeight,
                    width = parentWidth
                )
                .offset(x = offsetMainContentWidth, y = offsetMainContentHeight)

        MeasureComposableDp(
            content = {
                drawerContent()
            },
            size = { width, height ->
                drawerHeight = height
                drawerWidth = width
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .offset(
                        x = 0.dp
                    )
            ) {
                when {
                    isAlwaysDrawer -> {
                        drawerContent()
                    }
                    offsetMainContentWidth != 0.dp -> {
                        drawerContent()
                    }
                    else -> Unit
                }
            }
        }

        Box(
            modifier = mainContentModifier
        ) {
            mainContent(this)
        }
    }
}

@Composable
fun TvBoxWithRightHorizontalDrawer(
    modifier: Modifier = Modifier,
    mainModifier: Modifier = Modifier,
    isAlwaysDrawer: Boolean = false,
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
            when (localOrientation) {
                Configuration.ORIENTATION_PORTRAIT -> -drawerWidth
                Configuration.ORIENTATION_LANDSCAPE -> -drawerWidth - horizontalDrawerOffset
                else -> -drawerWidth - horizontalDrawerOffset
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

        /** модификатор для composable функции основного экрана */
        val mainContentModifier =
            mainModifier
                .size(
                    height = parentHeight,
                    width = parentWidth
                )
                .offset(x = offsetMainContentWidth, y = offsetMainContentHeight)

        MeasureComposableDp(
            content = {
                drawerContent()
            },
            size = { width, height ->
                drawerHeight = height
                drawerWidth = width
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .offset(
                        x = parentWidth - drawerWidth
                    )
            ) {
                when {
                    isAlwaysDrawer -> {
                        drawerContent()
                    }
                    offsetMainContentWidth != 0.dp -> {
                        drawerContent()
                    }
                    else -> Unit
                }
            }
        }

        Box(
            modifier = mainContentModifier
        ) {
            mainContent(this)
        }
    }
}