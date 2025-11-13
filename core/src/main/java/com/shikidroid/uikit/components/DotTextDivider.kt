package com.shikidroid.uikit.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.shikidroid.uikit.threeDP

@Composable
fun DotTextDivider(
    text: String = "·",
    modifier: Modifier = Modifier.padding(horizontal = threeDP),
    style: TextStyle = MaterialTheme.typography.body2,
    color: Color = MaterialTheme.colors.onBackground
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color
    )
}