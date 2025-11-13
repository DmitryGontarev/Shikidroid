package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Контейнер с нижним баром
 *
 * @param backgroundColor цвет фона
 * @param bottomBar нижни бар
 * @param content основной контент экрана
 */
@Composable
fun BoxWithBottomBar(
    backgroundColor: Color,
    bottomBar: @Composable () -> Unit,
    content: @Composable (bottomBarHeight: Dp) -> Unit
) {

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** высота нижнего бара в пикселях */
    val bottomBarHeight = remember { mutableStateOf(0.dp) }

    /** свдиг нижнего бара по высоте */
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** анимированный сдиг нижнего бара по высое в пикселях */
    val bottomBarOffset: Dp by animateDpAsState(
        targetValue = bottomBarOffsetHeightPx.value.dp,
        animationSpec = tween(
            durationMillis = 350
        )
    )

    /** нижний отступ для основного контента */
    val bottomPadding: Dp by animateDpAsState(
        targetValue =
        if (bottomBarOffset.value < 0f) {
            0.dp
        } else {
            bottomBarHeight.value
        }
    )

    /** отступ нижней системной панели навигации */
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    /** флаг скрытия бара */
    val isBarHide = remember {
        mutableStateOf(false)
    }

    /** слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!isBarHide.value) {
                    val delta = available.y
                    val newOffset = bottomBarOffsetHeightPx.value + delta
                    bottomBarOffsetHeightPx.value =
                        newOffset.coerceIn(-bottomBarHeight.value.value - bottomInsetPadding.value, 0f)
                }
                return super.onPreScroll(available, source)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = backgroundColor
            )
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            content(bottomPadding + bottomInsetPadding)
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Нижний бар навигации
        //////////////////////////////////////////////////////////////////////////////////////////
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentHeight()
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (dragAmount > 0) {
                            bottomBarOffsetHeightPx.value = -bottomBarHeight.value.value - bottomInsetPadding.value
                            isBarHide.value = true
                        } else {
                            if (bottomBarOffsetHeightPx.value < 0) {
                                bottomBarOffsetHeightPx.value = 0f
                                isBarHide.value = false
                            }
                        }
                    }
                }
                .background(color = Color.Transparent)
                .onGloballyPositioned {
                    bottomBarHeight.value = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .align(alignment = Alignment.BottomCenter)
                .offset(y = -bottomBarOffset),
            contentAlignment = Alignment.Center
        ) {
            bottomBar()
        }
    }
}

/**
 * Контейнер с нижним баром
 *
 * @param backgroundColor цвет фона
 * @param bottomBar нижни бар
 * @param content основной контент экрана
 */
@Composable
fun BoxWithSlideBottomBar(
    backgroundColor: Color,
    bottomBar: @Composable () -> Unit,
    content: @Composable (bottomBarHeight: Dp) -> Unit
) {

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** высота нижнего бара в пикселях */
    val bottomBarHeight = remember { mutableStateOf(0.dp) }

    /** свдиг нижнего бара по высоте */
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** отступ нижней системной панели навигации */
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    /** флаг скрытия бара */
    val isBarHide = remember {
        mutableStateOf(false)
    }

    /** область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** функция для сдвига бара навигации за нижнюю область экрана */
    fun shiftBarDown() {
        var value = (-bottomBarHeight.value.value - bottomInsetPadding.value) * localDensity.density

        coroutineScope.launch {
            while (value < 0) {
                bottomBarOffsetHeightPx.value -= 1
                value += 1f
                delay(1)
            }
        }
    }

    /** функция для поднятия вверх бара навигации из нижней области экрана */
    fun shiftBarUp() {
        coroutineScope.launch {
            while (bottomBarOffsetHeightPx.value < 0) {
                bottomBarOffsetHeightPx.value += 1
                delay(1)
            }
        }
    }

    /** слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!isBarHide.value) {
                    val delta = available.y
                    val newOffset = bottomBarOffsetHeightPx.value + delta
                    bottomBarOffsetHeightPx.value =
                        newOffset.coerceIn(
                            (-bottomBarHeight.value.value - bottomInsetPadding.value) * localDensity.density,
                            0f
                        )
                }
                return super.onPreScroll(available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!isBarHide.value) {
                    if (consumed.y <= 0) {
                        shiftBarDown()
                    } else {
                        shiftBarUp()
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = backgroundColor
            )
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            content(bottomBarHeight.value + bottomInsetPadding)
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Нижний бар навигации
        //////////////////////////////////////////////////////////////////////////////////////////
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentHeight()
                .wrapContentWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 0) {
                                shiftBarDown()
                                isBarHide.value = true
                            } else {
                                if (bottomBarOffsetHeightPx.value < 0) {
                                    shiftBarUp()
                                    isBarHide.value = false
                                }
                            }
                        }
                    )
                }
                .background(color = Color.Transparent)
                .onGloballyPositioned {
                    bottomBarHeight.value = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .align(alignment = Alignment.BottomCenter)
                .offset(
                    y = (-bottomBarOffsetHeightPx.value / localDensity.density).dp
                ),
            contentAlignment = Alignment.Center
        ) {
            bottomBar()
        }
    }
}

/**
 * Нижний бар
 *
 * @param modifier модификатор нижнего бара
 * @param backgroundColor цвет фона
 * @param shape форма нижнего бара
 * @param border рамка вокруг нижнего бара
 * @param elevation высота над основым контентом
 * @param bottomBar нижний бар
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(30.dp),
    border: BorderStroke = BorderStroke(width = 1.dp, color = backgroundColor),
    elevation: Dp = 1.dp,
    bottomBar: @Composable (rowScope: RowScope) -> Unit
) {

    /** стандартная высота нижнего бара */
    val bottomBarHeight: Dp = 56.dp

    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        shape = shape,
        border = border,
        elevation = elevation
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(bottomBarHeight)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bottomBar(this)
        }
    }
}