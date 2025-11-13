package com.shikidroid.uikit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.Orange
import kotlin.math.roundToInt

/**
 * Слайдер в виде линии
 */
@Composable
fun LineSlider(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    initialValue: Int,
    minValue: Int,
    maxValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    strokeWidth: Float,
    onPositionChange: (Int) -> Unit
) {
    /** текущее значение слайдера */
    var positionValue by remember {
        mutableStateOf(initialValue)
    }

    /** предыдущее значение слайдера */
    var oldPositionValue by remember {
        mutableStateOf(initialValue)
    }

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (enabled) {
                        detectDragGestures(
                            onDragStart = { offset ->

                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                positionValue += dragAmount.x.roundToInt()
                            },
                            onDragEnd = {
                                oldPositionValue = positionValue
                                onPositionChange(positionValue)
                            }
                        )
                    }
                },
        ) {
            /** ширина */
            val width = size.width

            /** высота */
            val height = size.height

            /** начало линии слайдера */
            val sliderStart = Offset(x = 0f, y = center.y)

            /** конец линии слайдера */
            val sliderEnd = Offset(x = width, y = center.y)

            /** вся линия слайдера от минимума до максимума */
            drawLine(
                color = secondaryColor,
                start = sliderStart,
                end = sliderEnd,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            /** начальная точка активной линии */
            val sliderValueStart = Offset(
                x = (sliderStart.x + (sliderEnd.x - sliderStart.x) * minValue).coerceAtLeast(sliderStart.x),
                y = center.y
            )

            /** конечная точка активной линии по текущей позиции */
            val sliderValueEnd = Offset(
                x = ((sliderEnd.x / maxValue) * positionValue).coerceAtMost(sliderEnd.x),
                y = center.y
            )

            /** линия от минимума до текущей позиции */
            drawLine(
                color = primaryColor,
                start = sliderValueStart,
                end = sliderValueEnd,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun PreviewLineSlider() {
    LineSlider(
        modifier = Modifier
            .background(
                color = Color.White
            )
            .padding(horizontal = 14.dp),
        initialValue = 33,
        primaryColor = Orange,
        secondaryColor = Color.DarkGray,
        minValue = 0,
        maxValue = 100,
        strokeWidth = 10f,
        onPositionChange = { }
    )
}