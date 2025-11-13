package com.shikidroid.presentation.screens.items

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.shikidroid.ui.theme.ShikidroidTheme

@Composable
internal fun Text12Sp(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.body12sp,
        textAlign = textAlign
    )
}

@Composable
internal fun Text13Sp(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.body13sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text13SemiBold(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.bodySemiBold13sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text14Sp(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.body14sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text16Sp(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.body16sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text16SemiBold(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.bodySemiBold16sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text17SemiBold(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.bodySemiBold17sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Text18SemiBold(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = ShikidroidTheme.colors.onPrimary
) {
    Text(
        modifier = modifier,
        text = text.orEmpty(),
        color = color,
        style = ShikidroidTheme.typography.bodySemiBold18sp,
        textAlign = TextAlign.Center
    )
}