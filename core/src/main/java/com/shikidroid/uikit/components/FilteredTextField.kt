package com.shikidroid.uikit.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Текстовое поле для ввода с фильтром регулярного выражения
 *
 * @param modifier модификатор текстового поля
 * @param value текст в поле
 * @param onValueChange функция для изменения текста, если он удовлетворяет выражению
 * @param colors цвета текстового поля
 * @param regex регулярное выражение
 * @param label текст над полем
 * @param trailingIcon иконки в конце поля
 * @param keyboardOptions опции ввода для клавиатуры
 * @param regex регулярное выражение
 * @param isIgnoreRegex фильтровать по соответствию/несоответствию выражению
 * @param handleColor цвет указателя курсора
 * @param backgroundColor цвет фона выделения текста
 */
@Composable
fun FilteredTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        textColor = Color.Black,
        backgroundColor = Color.White,
        cursorColor = Color.Black,
        unfocusedIndicatorColor = Color.White
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text
    ),
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    isRegexEnabled: Boolean = false,
    regex: Regex? = null,
    isIgnoreRegex: Boolean = false,
    handleColor: Color = MaterialTheme.colors.secondary,
    backgroundColor: Color = handleColor.copy(alpha = 0.4f)
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = handleColor,
        backgroundColor = backgroundColor.copy(alpha = 0.4f)
    )
    CompositionLocalProvider(
        LocalTextSelectionColors provides customTextSelectionColors
    ) {
        SelectionContainer {
            TextField(
                modifier = modifier,
                value = value,
                onValueChange = { value ->
                    if (isRegexEnabled) {
                        regex?.let { regex ->
                            if (isIgnoreRegex) {
                                if (!value.contains(regex)) {
                                    onValueChange(value)
                                }
                            } else {
                                if (value.contains(regex)) {
                                    onValueChange(value)
                                }
                            }
                        }
                    } else {
                        onValueChange(value)
                    }
                },
                enabled = enabled,
                readOnly = readOnly,
                colors = colors,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                interactionSource = interactionSource,
                shape = shape
            )
        }
    }
}