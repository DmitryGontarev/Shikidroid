package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.TwoLinesText
import com.shikidroid.uikit.sevenDP

@Composable
internal fun LabelText(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    text: String,
    textColor: Color = ShikidroidTheme.colors.onPrimary,
    textStyle: TextStyle = ShikidroidTheme.typography.body13sp,
    labelText: String,
    labelTextStyle: TextStyle = ShikidroidTheme.typography.body12sp,
    textAlign: TextAlign = TextAlign.Center,
    textLines: Int = Int.MAX_VALUE,
    labelTextAlign: TextAlign = TextAlign.Center,
    labelTextLines: Int = Int.MAX_VALUE
) {
    TwoLinesText(
        modifier = modifier.padding(sevenDP),
        horizontalAlignment = horizontalAlignment,
        text = text,
        textColor = textColor,
        textStyle = textStyle,
        secondText = labelText,
        secondTextColor = ShikidroidTheme.colors.onBackground,
        secondTextStyle = labelTextStyle,
        textAlign = textAlign,
        textMaxLines = textLines,
        secondTextAlign = labelTextAlign,
        secondTextMaxLines = labelTextLines
    )
}