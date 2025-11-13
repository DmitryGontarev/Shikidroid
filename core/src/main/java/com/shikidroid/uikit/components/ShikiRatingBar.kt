package com.shikidroid.uikit.components

import android.view.MotionEvent
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.shikidroid.uikit.BackgroundLightBlue
import com.shikidroid.uikit.Orange
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShikiRatingBar(
    modifier: Modifier = Modifier,
    value: Float,
    enabled: Boolean = true,
    isStepSize: Boolean = false,
    numStars: Int,
    starPadding: Dp = 2.dp,
    starSize: Dp = 26.dp,
    activeColor: Color,
    inactiveColor: Color,
    isFillEmptyStar: Boolean = false,
    filledStarStrokeWidth: Float = 1f,
    emptyStarStrokeWidth: Float = 1f,
    onValueChange: (Float) -> Unit,
    onRatingChanged: (Float) -> Unit
) {
    /** размер строки с звёздами */
    var rowSize by remember { mutableStateOf(Size.Zero) }

    /** последнее значение при касании экрана */
    var lastDraggedValue by remember { mutableStateOf(0f) }

    /** направление контейнера */
    val direction = LocalLayoutDirection.current

    Row(
        modifier = modifier
            .onSizeChanged {
                rowSize = it.toSize()
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (!enabled) {
                            return@detectHorizontalDragGestures
                        }
                        onRatingChanged(lastDraggedValue)
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!enabled) {
                            return@detectHorizontalDragGestures
                        }
                        change.consume()
                        val x1 = change.position.x.coerceIn(
                            minimumValue = 0f,
                            maximumValue = rowSize.width
                        )
                        val calculatedStars =
                            calculateStars(
                                draggedWidth = x1,
                                width = rowSize.width,
                                numStars = numStars,
                                padding = starPadding.value.toInt()
                            )
                        var newValue =
                            calculatedStars
                                .stepSized(isStepSize = isStepSize)
                                .coerceIn(
                                    minimumValue = 0f,
                                    maximumValue = numStars.toFloat()
                                )
                        if (direction == LayoutDirection.Rtl)
                            newValue = numStars - newValue
                        onValueChange(newValue)
                        lastDraggedValue = newValue
                    }
                )
            }
            .pointerInteropFilter {
                if (!enabled) {
                    return@pointerInteropFilter false
                } else {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val calculatedStars =
                                calculateStars(
                                    draggedWidth = it.x,
                                    width = rowSize.width,
                                    numStars = numStars,
                                    padding = starPadding.value.toInt()
                                )
                            val newValue =
                                calculatedStars
                                    .stepSized(isStepSize = isStepSize)
                                    .coerceIn(
                                        minimumValue = 0f,
                                        maximumValue = numStars.toFloat()
                                    )
                            onValueChange(newValue)
                            onRatingChanged(newValue)
                        }
                    }
                }
                true
            }
    ) {
        ComposeStars(
            value = value,
            numStars = numStars,
            starSize = starSize,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            filledStarStrokeWidth = filledStarStrokeWidth,
            emptyStarStrokeWidth = emptyStarStrokeWidth,
            isFillEmptyStar = isFillEmptyStar
        )
    }
}

/**
 * Возвращает значение количества звёзд, которое должно быть отрисовано выбранными
 */
private fun calculateStars(
    draggedWidth: Float,
    width: Float,
    numStars: Int,
    padding: Int
): Float {
    var overAllComposeWidth = width
    val spacerWidth = numStars * (2 * padding)

    //удаление ширины отступа
    overAllComposeWidth -= spacerWidth
    return if (draggedWidth != 0f)
        ((draggedWidth / overAllComposeWidth) * numStars)
    else 0f
}

/**
 * Возвращает Float значение количества звёзд, в зависимости от того, включен ли isStepSize
 *
 * 1: если количество выбранных звёзд 3.234345 и [isStepSize] == false,
 * то вернёт 3.0
 *
 * ПРИ [isStepSize] == false НЕ ОКРУГЛЯЕТ ДО БОЛЬШЕГО ЧИСЛА
 *
 * если 3.634345, то вернёт значение 3
 * если 4.96367, то вернёт значение 4
 *
 * 2: если количество выбранных звёзд 3.234345 и [isStepSize] == true,
 * то функция вернёт значение 3.5, то есть выбрано три с половиной звезды
 *
 * ПРИ [isStepSize] == true ОКРУГЛЯЕТ ДО БОЛЬШЕГО ЧИСЛА
 *
 * если 3.634345, то вернёт значение 4
 *
 * @param isStepSize флаг рисовать целую звезду или половину
 */
private fun Float.stepSized(isStepSize: Boolean): Float {
    return if (isStepSize) {
        var value = this.toInt().toFloat()
        if (this < value.plus(0.5)) {
            if (this == 0f)
                return 0f
            value = value.plus(0.5).toFloat()
            value
        } else {
            this.roundToInt().toFloat()
        }
    } else {
        this.roundToInt().toFloat()
    }
}

@Composable
fun ComposeStars(
    value: Float,
    numStars: Int,
    starPadding: Dp = 2.dp,
    starSize: Dp,
    activeColor: Color,
    inactiveColor: Color,
    filledStarStrokeWidth: Float,
    emptyStarStrokeWidth: Float,
    isFillEmptyStar: Boolean
) {
    val ratingPerStar = 1f
    var remainingRating = value

    Row(
        modifier = Modifier
    ) {
        for (i in 1..numStars) {
            val starRating =
                when {
                    remainingRating == 0f -> {
                        0f
                    }
                    remainingRating >= ratingPerStar -> {
                        remainingRating -= ratingPerStar
                        1f
                    }
                    else -> {
                        val fraction = remainingRating / ratingPerStar
                        remainingRating = 0f
                        fraction
                    }
                }
            RatingStar(
                modifier = Modifier
                    .padding(
                        start = if (i > 1) {
                            starPadding
                        } else {
                            0.dp
                        },
                        end = if (i < numStars) {
                            starPadding
                        } else {
                            0.dp
                        }
                    )
                    .size(size = starSize),
                fraction = starRating,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                filledStarStrokeWidth = filledStarStrokeWidth,
                emptyStarStrokeWidth = emptyStarStrokeWidth,
                isFillEmptyStar = isFillEmptyStar
            )
        }
    }
}

@Composable
fun RatingStar(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float,
    activeColor: Color,
    inactiveColor: Color,
    filledStarStrokeWidth: Float,
    emptyStarStrokeWidth: Float,
    isFillEmptyStar: Boolean
) {
    Box(
        modifier = modifier
    ) {
        FilledStar(color = activeColor, fraction = fraction, strokeWidth = filledStarStrokeWidth)
        EmptyStar(color = inactiveColor, fraction = fraction, strokeWidth = emptyStarStrokeWidth, isFillEmptyStar = isFillEmptyStar)
    }
}

@Composable
fun FilledStar(
    fraction: Float,
    color: Color,
    strokeWidth: Float
) {

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                shape =
                    if (isRtl) {
                        rtlFilledStarFractionalShape(fraction = fraction)
                    } else {
                        FractionalRectangleShape(
                            startFraction = 0f,
                            endFraction = fraction
                        )
                    }
            )
    ) {
        val path = Path().addStar(size)

        drawPath(path = path, color = color, style = Fill)
        drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
    }
}

@Composable
fun EmptyStar(
    fraction: Float,
    color: Color,
    strokeWidth: Float,
    isFillEmptyStar: Boolean
) {

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                shape =
                    if (isRtl) {
                        rtlEmptyStarFractionalShape(fraction = fraction)
                    } else {
                        FractionalRectangleShape(
                            startFraction = fraction,
                            endFraction = 1f
                        )
                    }
            )
    ) {
        val path = Path().addStar(size)

        if (isFillEmptyStar) {
            drawPath(path = path, color = color, style = Fill)
        } else {
            drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
        }
    }
}

fun Path.addStar(
    size: Size,
    spikes: Int = 5,
    @FloatRange(from = 0.0, to = 0.5) outerRadiusFraction: Float = 0.5f,
    @FloatRange(from = 0.0, to = 0.5) innerRadiusFraction: Float = 0.2f
): Path {
    /** радиус внешнего круга шпилей звезды */
    val outerRadius = size.minDimension * outerRadiusFraction

    /** радиус внутреннего круга шпилей звезды */
    val innerRadius = size.minDimension * innerRadiusFraction

    /** центр по оси X */
    val centerX = size.width / 2

    /** центр по оси Y*/
    val centerY = size.height / 2

    /** угол наклона линий, начинается с Top.Center и равен 90 градусов */
    var totalAngle = PI / 2

    /** градус наклона каждой секции */
    val degreesPerSection = (2 * PI) / spikes

    /** сдвиг к Top.Center от границ */
    moveTo(centerX, 0f)

    var x: Double
    var y: Double

    for (i in 1..spikes) {
        // Линия от внешнего круга к внутреннему
        totalAngle += degreesPerSection / 2
        x = centerX + cos(totalAngle) * innerRadius
        y = centerY - sin(totalAngle) * innerRadius
        lineTo(x.toFloat(), y.toFloat())


        // Линия от внутреннего круга к внешнему
        totalAngle += degreesPerSection / 2
        x = centerX + cos(totalAngle) * outerRadius
        y = centerY - sin(totalAngle) * outerRadius
        lineTo(x.toFloat(), y.toFloat())
    }

    // Закрытие Path, чтобы не было вероятности открытия фигуры
    close()

    return this
}

@Stable
class FractionalRectangleShape(
    @FloatRange(from = 0.0, to = 1.0) private val startFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) private val endFraction: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                left = (startFraction * size.width).coerceAtMost(size.width - 1f),
                top = 0f,
                right = (endFraction * size.width).coerceAtLeast(1f),
                bottom = size.height
            )
        )
    }
}

fun rtlEmptyStarFractionalShape(fraction: Float): FractionalRectangleShape {
    return if (fraction == 1f || fraction == 0f)
        FractionalRectangleShape(fraction, 1f)
    else FractionalRectangleShape(0f, 1f - fraction)
}

fun rtlFilledStarFractionalShape(fraction: Float): FractionalRectangleShape {
    return if (fraction == 0f || fraction == 1f)
        FractionalRectangleShape(0f, fraction)
    else FractionalRectangleShape(1f - fraction, 1f)
}

@Preview()
@Composable
fun Star() {
    val a = remember {
        mutableStateOf(3.7f)
    }
    ShikiRatingBar(
        modifier = Modifier,
        value = a.value,
        isStepSize = true,
        starSize = 7.dp,
        numStars = 10,
        activeColor = Orange,
        inactiveColor = BackgroundLightBlue,
        onValueChange = {
            a.value = it / 2
        },
        onRatingChanged = {

        }
    )
}