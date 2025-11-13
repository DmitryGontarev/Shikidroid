package com.shikidroid.uikit.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.GetComposableSizeDp

@Composable
fun ExpandableBox(
    modifier: Modifier = Modifier,
    initialHeight: Dp = 50.dp,
    showTextModifier: Modifier = Modifier,
    showMoreText: String =  "Развернуть",
    showLessText: String = "Свернуть",
    showTextColor: Color = LocalContentColor.current,
    showTextStyle: TextStyle = LocalTextStyle.current,
    showTextAlign: TextAlign = TextAlign.End,
    content: @Composable () -> Unit
) {
    /** высота контейнера с текстом */
    val textHeight = remember { mutableStateOf(0.dp) }

    /** флаг открыт или закрыт блок */
    val isExpanded = remember { mutableStateOf(value = false) }

    /** высота развёртки контейнера с анимацией */
    val heightFraction by animateDpAsState(
        targetValue = if (isExpanded.value) textHeight.value else initialHeight,
        animationSpec = tween(
            durationMillis = 700
        )
    )

    GetComposableSizeDp(
        heightDp = { height ->
            if (textHeight.value == 0.dp && height != 0.dp) {
                textHeight.value = height
            }
        }
    ) {
        content()
    }

    Column(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 700,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Box(
            modifier = if (isExpanded.value) {
                Modifier
                    .fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(heightFraction)
            }
        ) {
            content()
        }
        if (textHeight.value > initialHeight) {
            Text(
                modifier = showTextModifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded.value = !isExpanded.value
                    },
                text = if (isExpanded.value) {
                    showLessText
                } else {
                    showMoreText
                },
                color = showTextColor,
                style = showTextStyle,
                textAlign = showTextAlign
            )
        }
    }
}