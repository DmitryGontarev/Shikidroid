package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.oneDP

@Composable
internal fun SelectableCard(
    interactionSource: MutableInteractionSource? = null,
    isFocused: Boolean,
    scale: Float,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    selectColor: Color = ShikidroidTheme.colors.secondary,
    unselectColor: Color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f),
    backgroundColor: Color = ShikidroidTheme.colors.background,
    elevation: Dp = ShikidroidTheme.elevation,
    shape: Shape = ShikidroidTheme.shapes.roundedCorner7dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .wrapContentSize()
            .focusable(focusable, interactionSource)
            .scale(scale = scale),
        backgroundColor = backgroundColor,
        elevation = elevation,
        border = BorderStroke(
            width = oneDP,
            color = if (isFocused) {
                selectColor
            } else {
                unselectColor
            }
        ),
        shape = shape
    ) {
        content()
    }
}