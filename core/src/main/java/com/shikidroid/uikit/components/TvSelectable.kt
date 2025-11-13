package com.shikidroid.uikit.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch

@Composable
fun TvSelectable(
    scaleAnimation: Float = 1.05f,
    content: @Composable (
        interactionSource: MutableInteractionSource,
        isFocused: Boolean,
        scale: Animatable<Float, AnimationVector1D>) -> Unit
) {

    /** слушатель взаимодействия с элементом  */
    val interactionSource = remember {
        MutableInteractionSource()
    }

    /** состояние фокуса */
    val isFocused by interactionSource.collectIsFocusedAsState()

    /** масштаб элемента */
    val scale = remember {
        Animatable(1f)
    }

    LaunchedEffect(key1 = isFocused) {
        if (isFocused) {
            launch {
                scale.animateTo(
                    targetValue = scaleAnimation,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
            }
        } else {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
            }
        }
    }

    content(interactionSource, isFocused, scale)
}