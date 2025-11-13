package com.shikidroid.uikit.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.RoundedCornerTopStartTopEnd30dp
import com.shikidroid.uikit.oneDP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxWithBottomSheet(
    isBottomSheetOpen: Boolean,
    sheetElevation: Dp = oneDP,
    sheetBackgroundColor: Color,
    sheetBorderStrokeColor: Color = Color.White.copy(alpha = 0.3f),
    sheetShape: Shape = RoundedCornerTopStartTopEnd30dp,
    sheetContent: @Composable ColumnScope.() -> Unit,
    onDismiss: () -> Unit,
    mainContent: (@Composable () -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** флаг горизонтального положения устройства */
        val isHorizontal =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        /** текущее разрешение экрана */
        val localDensity = LocalDensity.current

        /** высота родительского контейнера */
        val parentHeight = with(LocalDensity.current) {
            constraints.maxHeight.toDp()
        }

        /** ширина родительского контейнера */
        val parentWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        /** флаг касания шторки */
        var isDrag by remember {
            mutableStateOf(false)
        }

        /** величина вертикального сдвига по касанию */
        var offsetY by remember {
            mutableStateOf(0f)
        }

        /** высота нижней шторки */
        var sheetHeight by remember {
            mutableStateOf(0.dp)
        }

        // если величина свайпа больше высоты шторки, то скрыть шторку
        LaunchedEffect(key1 = offsetY) {
            if (offsetY.dp > sheetHeight) {
                onDismiss()
            }
        }

        /** сдвиг по высоте контейнера нижней шторки */
        val sheetOffset: Dp by animateDpAsState(
            targetValue =
            when {
                isBottomSheetOpen && !isDrag -> {
                    parentHeight - sheetHeight
                }

                isBottomSheetOpen && isDrag -> {
                    parentHeight + offsetY.dp
                }

                else -> {
                    parentHeight + sheetHeight
                }
            },
            animationSpec = tween(
                durationMillis = 750
            )
        )

        /** модификатор для composable функции нижней шторки */
        val sheetModifier =
            Modifier
                .wrapContentHeight()
                .width(
                    if (!isHorizontal) {
                        (parentWidth.value / 1.03).dp
                    } else {
                        (parentWidth.value / 1.8).dp
                    }
                )
                .align(alignment = Alignment.TopCenter)
                .offset(y = sheetOffset)

        mainContent?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                it()
            }
        }

        AnimatedVisibility(
            visible = isBottomSheetOpen,
            enter = fadeIn(animationSpec = tween(700)),
            exit = fadeOut(animationSpec = tween(700))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDismiss()
                            }
                        )
                    }
            )
        }

        Card(
            modifier = sheetModifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            offsetY = 0f
                            isDrag = true
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            isDrag = true
                            offsetY += dragAmount.y
                        },
                        onDragEnd = {
                            isDrag = false
                        },
                        onDragCancel = {
                            isDrag = false
                        }
                    )
                },
            backgroundColor = sheetBackgroundColor,
            border = BorderStroke(
                width = 1.dp,
                color = sheetBorderStrokeColor
            ),
            elevation = sheetElevation,
            shape = sheetShape
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        sheetHeight = with(localDensity) {
                            it.size.height.toDp()
                        }
                    }
            ) {
                sheetContent(this)
            }
        }
    }
}


@Composable
fun BoxWithSlideBottomSheet(
    isBottomSheetOpen: Boolean,
    sheetElevation: Dp = oneDP,
    sheetBackgroundColor: Color,
    sheetBorderStrokeColor: Color = Color.White.copy(alpha = 0.3f),
    sheetShape: Shape = RoundedCornerTopStartTopEnd30dp,
    sheetContent: @Composable ColumnScope.() -> Unit,
    onDismiss: () -> Unit,
    mainContent: (@Composable () -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** текущее разрешение экрана */
        val localDensity = LocalDensity.current

        /** текущая конфигурация экрана */
        val localConfiguration = LocalConfiguration.current

        /** флаг горизонтального положения устройства */
        val isHorizontal =
            localConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE

        /** высота родительского контейнера */
        var parentHeight = (constraints.maxHeight / localDensity.density).dp

        /** ширина родительского контейнера */
        var parentWidth = (constraints.maxWidth / localDensity.density).dp

        /** высота нижней шторки */
        var sheetHeight by remember {
            mutableStateOf(0.dp)
        }

        /** флаг касания шторки */
        var isDrag by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = isHorizontal, block = {
            val height = (constraints.maxHeight / localDensity.density).dp
            val width = (constraints.maxWidth / localDensity.density).dp

            if (isHorizontal && height > width) {
                parentHeight = width
                parentWidth = height
            }

            if (!isHorizontal && height < width) {
                parentHeight = width
                parentWidth = height
            }
        })

        /** сдвиг по высоте контейнера нижней шторки */
        var sheetOffset by remember {
            mutableStateOf((parentHeight + sheetHeight).value)
        }

        /** сдвиг по высоте контейнера нижней шторки */
        val sheetAnimateOffset: Dp by animateDpAsState(
            targetValue =
            when {
                isBottomSheetOpen -> {
                    parentHeight - sheetHeight
                }

                else -> {
                    parentHeight + sheetHeight
                }
            },
            animationSpec = tween(
                durationMillis = 750
            )
        )

        /** область выполнения корутин */
        val coroutineScope = rememberCoroutineScope()

        val alphaValue = 0.6f

        var alpha by remember {
            mutableStateOf(0.0f)
        }

        fun shiftSheetUp() {
            coroutineScope.launch {
                while (alpha < alphaValue) {
                    alpha += 0.01f
                    delay(3)
                }
            }
            coroutineScope.launch {
                while (sheetOffset > (parentHeight - sheetHeight).value) {
                    sheetOffset -= 1f
                    delay(1)
                }
            }
        }

        fun shiftSheetDown() {
            val c = coroutineScope.launch {
                while (sheetOffset < (parentHeight + sheetHeight).value) {
                    val newOffset = sheetOffset + 3f
                    sheetOffset = newOffset.coerceIn(
                        (parentHeight - sheetHeight).value,
                        (parentHeight + sheetHeight).value
                    )
                    delay(1)
                }
            }
            coroutineScope.launch {
                c.join()
                isDrag = false
            }
        }

        LaunchedEffect(key1 = isBottomSheetOpen) {
            when {
                isBottomSheetOpen -> {
                    alpha = 0.6f
                    sheetOffset = (parentHeight - sheetHeight).value
                }

                !isBottomSheetOpen -> {
                    shiftSheetDown()
                }

                else -> {
                    sheetOffset = (parentHeight + sheetHeight).value
                }
            }
        }

        mainContent?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                it()
            }
        }

        AnimatedVisibility(
            visible = isBottomSheetOpen,
            enter = fadeIn(animationSpec = tween(700)),
            exit = fadeOut(animationSpec = tween(700))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = alpha)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDismiss()
                            }
                        )
                    }
            )
        }

        fun handleDrag(pointerInputScope: PointerInputScope) {
            coroutineScope.launch {
                pointerInputScope.detectVerticalDragGestures(
                    onDragStart = {
                        isDrag = true
                    },
                    onDragCancel = {
                        isDrag = false
                    },
                    onDragEnd = {
                        if (parentHeight.value - sheetOffset > parentHeight.value * 0.3) {
                            shiftSheetUp()
                        }

                        if (parentHeight.value - sheetOffset < parentHeight.value * 0.3) {
                            onDismiss()
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        val newOffset = sheetOffset + (dragAmount / localDensity.density)
                        sheetOffset = newOffset.coerceIn(
                            (parentHeight - sheetHeight).value,
                            (parentHeight + sheetHeight).value
                        )

                        val newAlpha = alpha + if (dragAmount > 0) -0.01f else 0.01f
                        alpha = newAlpha.coerceIn(
                            0.0f,
                            alphaValue
                        )
                    }
                )
            }
        }

        Card(
            modifier = Modifier
                .wrapContentHeight()
                .width(
                    if (!isHorizontal) {
                        (parentWidth.value * 0.97).dp
                    } else {
                        (parentWidth.value * 0.55).dp
                    }
                )
                .align(alignment = Alignment.TopCenter)
                .offset(
                    y = if (isDrag) {
                        sheetOffset.dp
                    } else {
                        sheetAnimateOffset
                    }
                )
                .pointerInput(isHorizontal) {
                    if (isHorizontal) {
                        handleDrag(pointerInputScope = this)
                    } else {
                        handleDrag(pointerInputScope = this)
                    }
                },
            backgroundColor = sheetBackgroundColor,
            border = BorderStroke(
                width = 1.dp,
                color = sheetBorderStrokeColor
            ),
            elevation = sheetElevation,
            shape = sheetShape
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        sheetHeight = with(localDensity) {
                            it.size.height.toDp()
                        }
                    }
            ) {
                sheetContent(this)
            }
        }
    }
}