package com.shikidroid.presentation.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shikidroid.presentation.screens.items.Text13SemiBold
import com.shikidroid.ui.GO_BACK_TITLE
import com.shikidroid.ui.ONE_MORE_TRY_TITLE
import com.shikidroid.ui.REQUEST_ERROR_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.MyButton
import com.shikidroid.uikit.components.TvSelectable

@Composable
internal fun ErrorTvScreen(
    screenText: String = REQUEST_ERROR_TITLE,
    screenIcon: Int? = null,
    mainBtnText: String = ONE_MORE_TRY_TITLE,
    altBtnText: String = GO_BACK_TITLE,
    mainClick: () -> Unit,
    altClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ShikidroidTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            screenIcon?.let {
                Icon(
                    modifier = Modifier
                        .height(fortyDP)
                        .width(fortyDP)
                        .padding(bottom = fourteenDP),
                    painter = painterResource(id = it),
                    tint = ShikidroidTheme.colors.onPrimary,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .padding(bottom = fourteenDP),
                text = screenText,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 25.sp
                ),
                color = ShikidroidTheme.colors.onPrimary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = twentyDP),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TvSelectable() { interactionSource, isFocused, scale ->
                    MyButton(
                        modifier = Modifier
                            .padding(threeDP)
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        onClick = {
                            altClick()
                        },
                        enabledColor = Color.Transparent,
                        borderColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        }
                    ) {
                        Text13SemiBold(
                            modifier = Modifier
                                .padding(horizontal = sevenDP),
                            text = altBtnText
                        )
                    }
                }
                TvSelectable() { interactionSource, isFocused, scale ->
                    MyButton(
                        modifier = Modifier
                            .padding(threeDP)
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        onClick = {
                            mainClick()
                        },
                        enabledColor = ShikidroidTheme.colors.secondary,
                        borderColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.onPrimary
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Text13SemiBold(
                            modifier = Modifier
                                .padding(horizontal = fiveDP),
                            text = mainBtnText
                        )
                    }
                }
            }
        }
    }
}