package com.shikidroid.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.shikidroid.uikit.AbsoluteRounded50dp
import com.shikidroid.uikit.BackgroundLightGray
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.thirtyFiveDP

/**
 * Текст в круге
 *
 * @param iconBoxModifier модификатор контейнера
 * @param iconBoxPadding отступ со всех сторон
 * @param iconBoxSize размер контейнера
 * @param shape форма вокруг иконки
 * @param backgroundColor цвет заднего фона вокруг иконки
 * @param onClick действие по нажатию
 * @param verticalArrangement выравнение по вертикали в основном контейнере
 * @param horizontalAlignment выравнение по горизонтали в основном контейнере
 * @param boxContentAlignment выравнивание изображение/иконки в контейнере иконки
 * @param text текст под иконкой
 * @param textAlign выравнивание текста
 * @param textStyle стиль текста
 * @param textMaxLines количество строк текста
 */
@Composable
fun RoundedTextButton(
    modifier: Modifier = Modifier,
    iconBoxPadding: Dp = sevenDP,
    iconBoxSize: Dp = thirtyFiveDP,
    shape: Shape = AbsoluteRounded50dp,
    backgroundColor: Color = BackgroundLightGray,
    onClick: (() -> Unit)? = null,
    boxContentAlignment: Alignment = Alignment.Center,
    text: String? = null,
    textModifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    textColor: Color = MaterialTheme.colors.onPrimary,
    textMaxLines: Int = 1
) {
    Box(
        modifier = modifier
            .padding(all = iconBoxPadding)
            .size(size = iconBoxSize)
            .clip(shape = shape)
            .background(color = backgroundColor)
            .clickable(
                onClick = onClick ?: { }
            ),
        contentAlignment = boxContentAlignment
    ) {
        Text(
            modifier = textModifier,
            text = text.orEmpty(),
            textAlign = textAlign,
            style = textStyle,
            color = textColor,
            maxLines = textMaxLines
        )
    }
}