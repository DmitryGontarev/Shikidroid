package com.shikidroid.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    btnWidth: Dp = 64.dp,
    btnHeight: Dp = 36.dp,
    enabled: Boolean = true,
    enabledColor: Color = Color.White,
    disabledColor: Color = Color.Black,
    shape: Shape = RoundedCornerShape(4.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Transparent,
    content: @Composable (RowScope) -> Unit
) {
    Card(
        modifier = modifier
            .wrapContentSize(),
        shape = shape,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        backgroundColor =
        if (enabled) {
            enabledColor
        } else {
            disabledColor
        }
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = btnWidth,
                    minHeight = btnHeight
                )
                .clickable {
                    if (enabled) {
                        onClick()
                    } else {
                        return@clickable
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content(this)
        }
    }
}

@Preview
@Composable
fun MyButtonPreview() {
    MyButton(onClick = { /*TODO*/ }) {
        it.apply {
            Text(
                text = "TEST",
                color = Color.Black
            )
        }
    }
}