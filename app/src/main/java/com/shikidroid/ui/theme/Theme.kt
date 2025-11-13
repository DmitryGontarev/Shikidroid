package com.shikidroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val LightTheme = lightColors(
    primary = DefaultColorPrimary,
    primaryVariant = DefaultColorPrimaryVariant,
    secondary = DefaultColorSecondary,
    secondaryVariant = DefaultColorSecondaryVariant,
    background = DefaultColorBackground,
    surface = DefaultColorSurface,
    onPrimary = DefaultColorOnPrimary,
    onSecondary = DefaultColorOnSecondary,
    onBackground = DefaultColorOnBackground,
    onSurface = DefaultColorOnSurface
)

private val DarkTheme = darkColors(
    primary = DarkColorPrimary,
    primaryVariant = DarkColorPrimaryVariant,
    secondary = DarkColorSecondary,
    secondaryVariant = DarkColorSecondaryVariant,
    background = DarkColorBackground,
    surface = DarkColorSurface,
    onPrimary = DarkColorOnPrimary,
    onSecondary = DarkColorOnSecondary,
    onBackground = DarkColorOnBackground,
    onSurface = DarkColorOnSurface
)

@Composable
fun ShikimoriTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkTheme
    } else {
        LightTheme
    }

    val systemUiController = rememberSystemUiController()
    if (darkTheme) {
        systemUiController.setStatusBarColor(
            color = DarkColorStatusBar
        )
    } else {
        systemUiController.setStatusBarColor(
            color = DefaultColorStatusBar
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}