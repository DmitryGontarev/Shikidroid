package com.shikidroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ShikidroidTheme(
    isEdgeToEdge: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    if (isEdgeToEdge) {
        TransparentSystemBars(useDarkIcons = !darkTheme)
    } else {
        val systemUiController = rememberSystemUiController()
        systemUiController.setStatusBarColor(
            color = when(darkTheme) {
                true -> AmoledColorStatusBar
                false -> DefaultColorStatusBar
            }
        )
    }

    val colors = when (darkTheme) {
        true -> ShikidroidAmoledTheme
        false -> ShikidroidLightTheme
    }

    val typography = ShikidroidTypography(
        header24sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp
        ),
        body12sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        ),
        body13sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        ),
        body14sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        body16sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodySemiBold13sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        ),
        bodySemiBold16sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        bodySemiBold17sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp
        ),
        bodySemiBold18sp = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    )

    val shapes = ShikidroidShapes(
        roundedCorner7dp = RoundedCornerShape(7.dp),
        roundedCorner30dp = RoundedCornerShape(30.dp),
        roundedCornerTopStartBottomEnd7dp = RoundedCornerShape(
            topStart = 7.dp,
            bottomEnd = 7.dp
        ),
        roundedCornerTopEndBottomEnd30dp = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 30.dp,
            bottomStart = 0.dp,
            bottomEnd = 30.dp
        ),
        roundedCornerTopStartBottomStart30dp = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 0.dp,
            bottomStart = 30.dp,
            bottomEnd = 0.dp
        ),
        roundedCornerTopStartTopEnd30dp = RoundedCornerShape(
            topStart = 30.dp,
            topEnd = 30.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        absoluteRounded50dp = AbsoluteRoundedCornerShape(50.dp)
    )

    val elevation = when (darkTheme) {
        true -> 3.dp
        false -> 7.dp
    }

    CompositionLocalProvider(
        LocalShikidroidColors provides colors,
        LocalShikidroidTypography provides typography,
        LocalShikidroidShapes provides shapes,
        LocalShikidroidElevation provides elevation,
        content = content
    )
}