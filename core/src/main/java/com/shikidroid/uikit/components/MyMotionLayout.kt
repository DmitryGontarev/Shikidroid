package com.shikidroid.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyMotionLayout(
    maxHeight: Float = 200f,
    minHeight: Float = 60f,
    image: @Composable () -> Unit,
    underImage: @Composable () -> Unit,
    text: String
) {
    /** плотность пикселей экрана */
    val density = LocalDensity.current.density

    /** высота верхнего бара */
    val toolbarHeightPx = with(LocalDensity.current) {
        maxHeight.dp.roundToPx().toFloat()
    }

    /** минимальная высота верхнего бара */
    val toolbarMinHeightPx = with(LocalDensity.current) {
        minHeight.dp.roundToPx().toFloat()
    }

//    logD("toolbarHeightPx = $toolbarHeightPx")
//    logD("toolbarMinHeightPx = $toolbarMinHeightPx")

    /** сдвиг высоты верхнего бара */
    var toolbarOffsetHeightPx by remember {
        mutableStateOf(0f)
    }

    /** интерфейс вложенной прокрутки */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(
                    minimumValue = toolbarMinHeightPx - toolbarHeightPx,
                    maximumValue = 0f
                )
                return Offset.Zero
            }
        }
    }

    /** значение прогресса прокрутки */
    var progress by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(key1 = toolbarOffsetHeightPx) {
        progress =
            ((toolbarHeightPx + toolbarOffsetHeightPx) / toolbarHeightPx - minHeight / maxHeight) / (1f - minHeight / maxHeight)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(
                connection = nestedScrollConnection
            )
            .statusBarsPadding()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = maxHeight.dp)
        ) {
            repeat(33) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "$it",
                        color = Color.White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = ((toolbarHeightPx + toolbarOffsetHeightPx) / density).dp)
                .background(color = Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .width(width = 24.dp)
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = progress + 0.001f)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight = 1f)
                            .padding(vertical = 10.dp)
                            .aspectRatio(ratio = 1f)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    ) {
                        image()
                    }

//                    Text(
//                        modifier = Modifier
//                            .alpha(alpha = progress)
//                            .padding((8 * (progress * progress * progress)).dp),
//                        text = text,
//                        color = Color.White,
//                        fontSize = (24 * progress).sp,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )

                    Box(
                        modifier = Modifier
                            .alpha(alpha = progress)
                            .wrapContentSize()
                            .padding((8 * (progress * progress * progress)).dp),
                    ) {
                        underImage()
                    }
                }
                Text(
                    modifier = Modifier
                        .alpha(alpha = 1f - progress)
                        .weight(weight = 1.001f - progress)
                        .padding(start = 20.dp),
                    text = text,
                    color = Color.White,
                    fontSize = (24 * (1f - progress)).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f)
                )
                Spacer(
                    modifier = Modifier
                        .width(width = 24.dp)
                )
            }
        }
    }
}