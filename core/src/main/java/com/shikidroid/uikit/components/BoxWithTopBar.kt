package com.shikidroid.uikit.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxWithTopBar(
    toolbar: @Composable (BoxScope) -> Unit,
    pullRefresh: (@Composable (BoxScope) -> Unit)? = null,
    content: @Composable (Dp) -> Unit
) {

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** высота тулбара в пикселях */
    val toolbarHeight = remember { mutableStateOf(0.dp) }

    /** сдвиг тулбара по высоте в пикселях */
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** свойство для плавного изменения значения сдвига */
    val toolbarOffset: Dp by animateDpAsState(
        targetValue = toolbarOffsetHeightPx.value.dp,
        animationSpec = tween(
            durationMillis = 350
        )
    )

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

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
                    val newOffset = toolbarOffsetHeightPx.value + delta
                    toolbarOffsetHeightPx.value =
                        newOffset.coerceIn(-toolbarHeight.value.value - topInsetPadding.value, 0f)
                }
                return super.onPreScroll(available, source)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {

        /** флаг горизонтального положения устройства */
        val isHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        /** ширина родительского контейнера */
        val parentWidth = with(localDensity) {
            constraints.maxWidth.toDp()
        }

        /** вычисляемая ширина для тулбара */
        val width = remember {
            mutableStateOf(0.dp)
        }

        LaunchedEffect(key1 = parentWidth.value) {
            if (isHorizontal) {
                width.value = (parentWidth.value * 0.7).dp
            } else {
                width.value = parentWidth
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = nestedScrollConnection),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(toolbarHeight.value + topInsetPadding)
        }

        pullRefresh?.let {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = toolbarOffset)
            ) {
                it(this)
            }
        }

        // Верхний бар экрана
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .wrapContentHeight()
                .width(width.value)
                .onGloballyPositioned {
                    toolbarHeight.value = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .align(alignment = Alignment.TopCenter)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (dragAmount < 0) {

                            toolbarOffsetHeightPx.value =
                                -toolbarHeight.value.value - topInsetPadding.value

                            isBarHide.value = true
                        } else {
                            if (toolbarOffsetHeightPx.value < 0) {
                                toolbarOffsetHeightPx.value = 0f
                                isBarHide.value = false
                            }
                        }
                    }
                }
                .offset(y = toolbarOffset)
        ) {
            toolbar(this)
        }
    }
}

@Composable
fun BoxWithSlideTopBar(
    toolbar: @Composable (BoxScope) -> Unit,
    pullRefresh: (@Composable (BoxScope) -> Unit)? = null,
    content: @Composable (Dp) -> Unit
) {

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** высота тулбара в пикселях */
    val toolbarHeight = remember { mutableStateOf(0.dp) }

    /** сдвиг тулбара по высоте в пикселях */
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    /** флаг скрытия бара */
    val isBarHide = remember {
        mutableStateOf(false)
    }

    /** область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** функция для сдвига бара за верхнюю область экрана */
    fun shiftBarUp() {

        var value = (-toolbarHeight.value.value - topInsetPadding.value) * localDensity.density

        coroutineScope.launch {
            while (value < 0) {
                toolbarOffsetHeightPx.value -= 1
                value += 1f
                delay(1)
            }
        }
    }

    /** функция для сдвига вниз бара из верхней области экрана */
    fun shiftBarDown() {
        coroutineScope.launch {
            while (toolbarOffsetHeightPx.value < 0) {
                toolbarOffsetHeightPx.value += 1
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
                    val newOffset = toolbarOffsetHeightPx.value + delta
                    toolbarOffsetHeightPx.value =
                        newOffset.coerceIn(
                            (-toolbarHeight.value.value - topInsetPadding.value) * localDensity.density,
                            0f
                        )
                }
                return super.onPreScroll(available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!isBarHide.value) {
                    if (consumed.y <= 0) {
                        shiftBarUp()
                    } else {
                        shiftBarDown()
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {

        /** флаг горизонтального положения устройства */
        val isHorizontal =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        /** ширина родительского контейнера */
        val parentWidth = with(localDensity) {
            constraints.maxWidth.toDp()
        }

        /** вычисляемая ширина для тулбара */
        val width = remember {
            mutableStateOf(0.dp)
        }

        LaunchedEffect(key1 = parentWidth.value) {
            if (isHorizontal) {
                width.value = (parentWidth.value * 0.7).dp
            } else {
                width.value = parentWidth
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = nestedScrollConnection),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(toolbarHeight.value + topInsetPadding)
        }

        pullRefresh?.let {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset(y = (toolbarOffsetHeightPx.value / localDensity.density).dp)
            ) {
                it(this)
            }
        }

        // Верхний бар экрана
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .wrapContentHeight()
                .width(width.value)
                .onGloballyPositioned {
                    toolbarHeight.value = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .align(alignment = Alignment.TopCenter)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount < 0) {
                                shiftBarUp()
                                isBarHide.value = true
                            } else {
                                if (toolbarOffsetHeightPx.value < 0) {
                                    shiftBarDown()
                                    isBarHide.value = false
                                }
                            }
                        }
                    )
                }
                .offset(y = (toolbarOffsetHeightPx.value / localDensity.density).dp)
        ) {
            toolbar(this)
        }
    }
}