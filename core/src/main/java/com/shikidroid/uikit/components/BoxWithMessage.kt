package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun BoxWithMessage(
    showMessage: Boolean,
    delayIn: Long = 0L,
    delayOut: Long = 1000L,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /** текущее разрешение экрана */
        val localDensity = LocalDensity.current

        /** высота родительского контейнера */
        val parentHeight = with(localDensity) {
            constraints.maxHeight.toDp()
        }

        /** ширина родительского контейнера */
        val parentWidth = with(localDensity) {
            constraints.maxWidth.toDp()
        }

        /** высота контента */
        var contentHeight by remember {
            mutableStateOf(parentHeight)
        }

        val isShow = remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = showMessage) {
            if (showMessage) {
                delay(delayIn)
                isShow.value = showMessage
            } else {
                delay(delayOut)
                isShow.value = showMessage
            }
        }

        /** отступ верхнего бара */
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        /** сдвиг контента по высоте */
        val contentOffset: Dp by animateDpAsState(
            targetValue = if (isShow.value) 0.dp else -contentHeight - statusBarPadding,
            animationSpec = tween(
                durationMillis = 750
            )
        )

        /** значение альфа канала цвета заднего фона */
        val backgroundAlpha: Float by animateFloatAsState(
            targetValue = if (isShow.value) 0.6f else 0f,
            animationSpec = tween(
                durationMillis = 750
            )
        )

//        if (showMessage) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        color = Color.Black.copy(alpha = backgroundAlpha)
//                    )
//            )
//        }

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .systemBarsPadding()
                .onGloballyPositioned {
                    contentHeight = with(localDensity) {
                        it.size.height.toDp()
                    }
                }
                .offset(
                    y = contentOffset
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}