package com.shikidroid.uikit.components

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxWithFloatingButton(
    prefs: SharedPreferencesProvider,
    xAxisKey: String,
    yAxisKey: String,
    showButton: Boolean,
    floatingButton: @Composable () -> Unit,
    content: @Composable (BoxScope) -> Unit
) {
    /** стартовая позиция кнопки по оси X */
    val startX = 0f

    /** стартовая позиция кнопки по оси Y */
    val startY = -175f


    /** сохранённое положение кнопки по оси X */
    val x = prefs.getFloat(
        key = xAxisKey,
        default = startX
    )

    /** сохранённое положение кнопки по оси Y */
    val y = prefs.getFloat(
        key = yAxisKey,
        default = startY
    )

    //значения 0f по оси X и -175f по оси Y помещают кнопку в нижний правый угол экрана

    // если по оси X ушли за ноль или минус 1000, то установить стандартное значение 0
    // и сбросить значение Y
    if (x > 0f || x < -1000f) {
        prefs.putFloat(
            key = xAxisKey,
            float = startX
        )
        prefs.putFloat(
            key = yAxisKey,
            float = startY
        )
    }

    // если по оси Y ушли за ноль или минус 2000, то установить стандартное значение -175
    // и сбросить значение X
    if (y > 0f || y < -2000f) {
        prefs.putFloat(
            key = xAxisKey,
            float = startX
        )
        prefs.putFloat(
            key = yAxisKey,
            float = startY
        )
    }

    /** сдвиг по оси X */
    var offsetX by remember {
        mutableStateOf(
            value = prefs.getFloat(
                key = xAxisKey, default = startX
            )
        )
    }

    /** сдвиг по оси X */
    var offsetY by remember {
        mutableStateOf(
            value = prefs.getFloat(
                key = yAxisKey, default = startY
            )
        )
    }

    /** флаг возможности свдига кнопки при скролле */
    val canShiftButton = remember {
        mutableStateOf(true)
    }

    /** слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (canShiftButton.value) {
                    val delta = available.y
                    val newOffset = offsetX - delta
                    offsetX =
                        newOffset.coerceIn(startX, 230f)
                }
                return super.onPreScroll(available, source)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = nestedScrollConnection)
    ) {
        /** разрешение экрана */
        val localDensity = LocalDensity.current

        content(this)

        if (showButton) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .wrapContentSize()
                    .offset(
                        x = (offsetX / localDensity.density).dp,
                        y = (offsetY / localDensity.density).dp
                    )
                    .align(alignment = Alignment.BottomEnd)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            prefs.putFloat(
                                key = xAxisKey,
                                float = (offsetX + dragAmount.x)
                            )
                            prefs.putFloat(
                                key = yAxisKey,
                                float = (offsetY + dragAmount.y)
                            )
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y

                            canShiftButton.value = !(offsetX != startX || offsetY != startY)
                        }
                    }
            ) {
                floatingButton()
            }
        }
    }
}

@Composable
fun BoxWithSlideFloatingButton(
    showButton: Boolean,
    floatingButton: @Composable () -> Unit,
    content: @Composable (BoxScope) -> Unit
) {
    /** стартовая позиция кнопки по оси X */
    val startX = 0f

    /** стартовая позиция кнопки по оси Y */
    val startY = -175f

    /** величина сдвига вправо для скрыти кнопки */
    val offsetHide = 230f

    /** сдвиг по оси X */
    var offsetX by remember {
        mutableStateOf(startX)
    }

    /** флаг возможности свдига кнопки при скролле */
    val isButtonHide = remember {
        mutableStateOf(false)
    }

    /** область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** функция для сдвига кнопки влево за правую область экрана */
    fun shiftButtonLeft() {
        coroutineScope.launch {
            while (offsetX < offsetHide) {
                offsetX += 1f
                delay(1)
            }
        }
    }

    /** функция для сдвига кнопки вправо из правой области экрана */
    fun shiftButtonRight() {
        coroutineScope.launch {
            while (offsetX > 0f) {
                offsetX -= 1f
                delay(1)
            }
        }
    }

    /** слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!isButtonHide.value) {
                    val delta = available.y
                    val newOffset = offsetX - delta
                    offsetX =
                        newOffset.coerceIn(startX, offsetHide)
                }
                return super.onPreScroll(available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!isButtonHide.value) {
                    if (consumed.y <= 0) {
                        shiftButtonLeft()
                    } else {
                        shiftButtonRight()
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = nestedScrollConnection)
    ) {
        /** флаг горизонтального положения устройства */
        val isHorizontal =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        /** разрешение экрана */
        val localDensity = LocalDensity.current

        /** ширина родительского контейнера */
        val parentWidth = with(localDensity) {
            constraints.maxWidth.toDp()
        }

        content(this)

        if (showButton) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .wrapContentHeight()
                    .width(parentWidth * if (isHorizontal) 0.15f else 0.35f)
                    .offset(
                        x = (offsetX / localDensity.density).dp,
                        y = (startY / localDensity.density).dp
                    )
                    .align(alignment = Alignment.BottomEnd)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            if (dragAmount > 0) {
                                shiftButtonLeft()
                                isButtonHide.value = true
                            } else {
                                if (offsetX > 0) {
                                    shiftButtonRight()
                                    isButtonHide.value = false
                                }
                            }
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(alignment = Alignment.CenterEnd)
                ) {
                    floatingButton()
                }
            }
        }
    }
}