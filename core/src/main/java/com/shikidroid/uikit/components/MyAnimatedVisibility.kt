package com.shikidroid.uikit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyAnimatedVisibility(
    modifier: Modifier,
    isVisible: Boolean = false,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.minWidth,
            height = constraints.minHeight
        ) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }
            var x = 0
            placeables[0].place(x = x, y = 0)
            x += placeables[0].width
            if (isVisible) {
                placeables[1].place(x = x, y = 0)
            }
        }
    }
}

@Composable
fun MeasureComposableInt(
    content: @Composable () -> Unit,
    size: (width: Int, height: Int) -> Unit
) {
    Layout(
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.minWidth,
            height = constraints.minHeight
        ) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }

            size(placeables[0].width, placeables[0].height)
        }
    }
}

@Composable
fun MeasureComposableDp(
    content: @Composable () -> Unit,
    size: (width: Dp, height: Dp) -> Unit
) {
    /** текущее разрешение экрана */
    val localDensity = LocalDensity.current

    Layout(
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.minWidth,
            height = constraints.minHeight
        ) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }

            var width = 0.dp
            var height = 0.dp

            if (placeables.isNotEmpty()) {
                width = with(localDensity) {
                    placeables[0].width.toDp()
                }

                height = with(localDensity) {
                    placeables[0].height.toDp()
                }
            }

            size(width, height)
        }
    }
}

@Composable
fun MyColumn(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }
            var y = 0
            placeables.forEach { placeable ->
                placeable.place(x = 0, y = y)
                y += placeable.height
            }
        }
    }
}

@Composable
fun MyRow(
    modifier: Modifier,
    clipIfOverSize: Boolean = false,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.minWidth,
            height = constraints.minHeight
        ) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }
            var x = 0
            placeables.forEach { placeable ->

                // если ширина контента больше родительского, то не показываем элементы,
                // которые не помещаются
                if (clipIfOverSize && x + placeable.width > constraints.maxWidth) {
                    return@forEach
                }

                placeable.place(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun RowToColumnLayout(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {

            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )

            val placeables = measurable.map { measurable ->
                measurable.measure(constraints = looseConstraints)
            }

            var x = 0
            var y = 0
            var maxY = 0

            placeables.forEach { placeable ->
                if (x + placeable.width > constraints.maxWidth) {
                    x = 0
                    y += maxY
                    maxY = 0
                }

                placeable.place(x = x, y = y)
                x += placeable.width

                if (maxY < placeable.height) {
                    maxY = placeable.height
                }
            }
        }
    }
}