package com.shikidroid.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * Две строки текста
 *
 * @param modifier модификатор основного контейнера
 * @param verticalArrangement выравнивание в контейнере по вертикали
 * @param horizontalAlignment выравнивание в контейнере по горизонтали
 * @param textModifier модификатор текста
 * @param text текст
 * @param textColor цвет текста
 * @param textStyle стиль текста
 * @param textAlign выравнивание текста
 * @param secondTextModifier модификатор второго текста
 * @param secondText второй текст
 * @param secondTextColor цвет второго текста
 * @param secondTextStyle стиль второго текста
 * @param secondTextAlign выравнивание второго текста
 */
@Composable
fun TwoLinesText(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textModifier: Modifier = Modifier,
    text: String = "",
    textColor: Color = MaterialTheme.colors.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.h5,
    textAlign: TextAlign = TextAlign.Center,
    textMaxLines: Int = Int.MAX_VALUE,
    secondTextModifier: Modifier = Modifier,
    secondText: String = "",
    secondTextColor: Color = MaterialTheme.colors.onBackground,
    secondTextStyle: TextStyle = MaterialTheme.typography.h6,
    secondTextAlign: TextAlign = TextAlign.Center,
    secondTextMaxLines: Int = Int.MAX_VALUE
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            modifier = textModifier,
            text = text,
            color = textColor,
            style = textStyle,
            textAlign = textAlign,
            maxLines = textMaxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = secondTextModifier,
            text = secondText,
            color = secondTextColor,
            style = secondTextStyle,
            textAlign = secondTextAlign,
            maxLines = secondTextMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}