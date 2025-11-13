package com.shikidroid.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Дата класс цветов приложения
 *
 * @property primary основной цвет фона нижнего меню и подобных элементов
 * @property primaryVariant второй вариант основного цвета фона нижнего меню и подобных элементов
 * @property secondary цветовой акцент поверх primary или background цвета приложения
 * @property secondaryVariant второй вариант цветового акцент поверх primary или background цвета приложения
 * @property background цвет фона основной области экрана
 * @property surface цвет фона компонентов, которые лежат поверх фона приложения (BottomSheet, Drawer, Card и так далее)
 * @property onPrimary цвет элементов поверх основно цвета primary (например текст)
 * @property onSecondary цвет поверх цвета secondary
 * @property onBackground цвет поверх цвета background
 * @property onSurface цвет поверх цвета surface
 * @property statusBar цвет статус бара
 * @property tvSelectable цвет выбранного элемента для AndroidTV
 */
data class ShikidroidColors(
    val primary: Color,
    val primaryVariant: Color,
    val primaryBorderVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val secondaryLightVariant: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val statusBar: Color,
    val tvSelectable: Color
)

/**
 * Дата класс стилей текста приложения
 */
data class ShikidroidTypography(
    val header24sp: TextStyle,
    val body12sp: TextStyle,
    val body13sp: TextStyle,
    val body14sp: TextStyle,
    val body16sp: TextStyle,
    val bodySemiBold13sp: TextStyle,
    val bodySemiBold16sp: TextStyle,
    val bodySemiBold17sp: TextStyle,
    val bodySemiBold18sp: TextStyle
)

/**
 * Дата класс с формами элементов приложения
 */
data class ShikidroidShapes(
    val roundedCorner7dp: Shape,
    val roundedCorner30dp: Shape,
    val roundedCornerTopStartBottomEnd7dp: Shape,
    val roundedCornerTopEndBottomEnd30dp: Shape,
    val roundedCornerTopStartBottomStart30dp: Shape,
    val roundedCornerTopStartTopEnd30dp: Shape,
    val absoluteRounded50dp: Shape
)

object ShikidroidTheme {

    val colors: ShikidroidColors
        @Composable
        get() = LocalShikidroidColors.current

    val typography: ShikidroidTypography
        @Composable
        get() = LocalShikidroidTypography.current

    val shapes: ShikidroidShapes
        @Composable
        get() = LocalShikidroidShapes.current

    val elevation: Dp
        @Composable
        get() = LocalShikidroidElevation.current
}

val LocalShikidroidColors = compositionLocalOf<ShikidroidColors> {
    error("No colors provided")
}

val LocalShikidroidTypography = staticCompositionLocalOf<ShikidroidTypography> {
    error("No typography provided")
}

val LocalShikidroidShapes = staticCompositionLocalOf<ShikidroidShapes> {
    error("No shape provided")
}

val LocalShikidroidElevation = staticCompositionLocalOf<Dp> {
    error("No elevation provided")
}