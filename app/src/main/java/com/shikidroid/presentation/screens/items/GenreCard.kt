package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*

@Composable
internal fun GenreCard(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = ShikidroidTheme.typography.body12sp,
    textColor: Color = ShikidroidTheme.colors.secondary,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = ShikidroidTheme.colors.secondaryLightVariant.copy(alpha = 0.4f)
) {
    Card(
        modifier = modifier
            .wrapContentSize(),
        backgroundColor = backgroundColor,
        border = BorderStroke(
            width = oneDP,
            color = borderColor
        ),
        shape = ShikidroidTheme.shapes.roundedCorner30dp,
        elevation = zeroDP
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = fiveDP, horizontal = tenDP),
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}

@Composable
internal fun SelectableGenreCard(
    modifier: Modifier = Modifier,
    isSelect: Boolean = false,
    text: String,
    textStyle: TextStyle = ShikidroidTheme.typography.body12sp,
    textColor: Color = ShikidroidTheme.colors.secondary,
    selectBackgroundColor: Color = ShikidroidTheme.colors.secondaryVariant,
    backgroundColor: Color = Color.Transparent,
    selectBorderColor: Color = ShikidroidTheme.colors.secondary,
    borderColor: Color = ShikidroidTheme.colors.secondaryLightVariant.copy(alpha = 0.2f)
) {
    Card(
        modifier = modifier
            .wrapContentSize(),
        backgroundColor =
        if (isSelect) {
            selectBackgroundColor
        } else {
            backgroundColor
        },
        border = BorderStroke(
            width = oneDP,
            color =
            when (isSelect) {
                true -> selectBorderColor
                false -> borderColor
            }
        ),
        shape = ShikidroidTheme.shapes.roundedCorner30dp,
        elevation = zeroDP
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = fiveDP, horizontal = tenDP),
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}