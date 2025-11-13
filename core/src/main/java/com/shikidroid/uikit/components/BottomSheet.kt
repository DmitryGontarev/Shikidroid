package com.shikidroid.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.RoundedCornerTopStartTopEnd30dp
import com.shikidroid.uikit.oneDP

@Composable
fun BottomSheet(
    isBottomSheetOpen: Boolean,
    sheetElevation: Dp = oneDP,
    sheetBackgroundColor: Color,
    sheetContent: @Composable ColumnScope.() -> Unit,
    onDismiss: () -> Unit,
    sheetShape: Shape = RoundedCornerTopStartTopEnd30dp,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** текущее разрешение экрана */
        val localDensity = LocalDensity.current

        /** высота родительского контейнера */
        val parentHeight = with(LocalDensity.current) {
            constraints.maxHeight.toDp()
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
            if ((offsetY / localDensity.density) > sheetHeight.value / 1.5) {
                onDismiss()
                offsetY = 0f
            }
        }

        val sheetOffset: Dp by animateDpAsState(
            targetValue = if (isBottomSheetOpen) 0.dp else parentHeight,
            animationSpec = tween(
                durationMillis = 750
            )
        )

        var y by remember {
            mutableStateOf(0f)
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = sheetOffset),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        sheetHeight = with(localDensity) {
                            it.size.height.toDp()
                        }
                        y = it.positionInParent().y
                    }
                    .offset(y = ((offsetY / localDensity.density).dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
//                                offsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY = (offsetY + dragAmount.y).coerceAtLeast(-sheetHeight.value / localDensity.density)
                            },
                            onDragEnd = {
                            },
                            onDragCancel = {
                            }
                        )
                    },
                backgroundColor = sheetBackgroundColor,
                elevation = sheetElevation,
                shape = sheetShape
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    sheetContent(this)
                }
            }
        }
    }
}