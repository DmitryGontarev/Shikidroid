package com.shikidroid.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.shikidroid.uikit.*

/**
 * Картинка или иконка в круге
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
 * @param iconModifier модификатор иконки
 * @param icon иконка
 * @param contentDescription строка с описанием иконки для прочитки
 * @param isIcon флаг иконка или картинка в контейнере
 * @param tint оттенок иконки
 * @param text текст под иконкой
 * @param textAlign выравнивание текста
 * @param textStyle стиль текста
 * @param textMaxLines количество строк текста
 */
@Composable
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isIconRotate: Boolean? = null,
    rotateValue: Float? = null,
    iconBoxModifier: Modifier = Modifier,
    iconBoxPadding: Dp = sevenDP,
    iconBoxSize: Dp = thirtyFiveDP,
    shape: Shape = AbsoluteRounded50dp,
    backgroundColor: Color = BackgroundLightGray,
    onClick: (() -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    boxContentAlignment: Alignment = Alignment.Center,
    iconModifier: Modifier = Modifier,
    icon: Int,
    contentDescription: String? = null,
    isIcon: Boolean = true,
    tint: Color = MaterialTheme.colors.onPrimary,
    text: String? = null,
    textModifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    textColor: Color = MaterialTheme.colors.onPrimary,
    textMaxLines: Int = 1
) {

    /** состояние поворота иконки */
    val rotationState by animateFloatAsState(
        targetValue = if (isIconRotate == true) rotateValue ?: 0f else 0f
    )

    val columnModifier =
        if (isIconRotate != null) {
            modifier
                .rotate(degrees = rotationState)
        } else {
            modifier
        }

    Column(
        modifier = columnModifier
            .wrapContentSize(),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = iconBoxModifier
                .padding(all = iconBoxPadding)
                .size(size = iconBoxSize)
                .clip(shape = shape)
                .background(color = backgroundColor)
                .clickable(
                    onClick =
                        if (enabled) {
                            onClick ?: { }
                        } else {
                            { }
                        }
                ),
            contentAlignment = boxContentAlignment
        ) {
            if (isIcon) {
                Icon(
                    modifier = iconModifier,
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription,
                    tint = tint
                )
            } else {
                Image(
                    modifier = iconModifier,
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription
                )
            }
        }
        if (text.isNullOrEmpty().not()) {
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
}