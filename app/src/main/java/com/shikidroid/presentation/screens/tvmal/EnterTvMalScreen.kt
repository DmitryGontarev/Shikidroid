package com.shikidroid.presentation.screens.tvmal

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.presentation.screens.items.Text13SemiBold
import com.shikidroid.presentation.screens.items.Text13Sp
import com.shikidroid.ui.CONTINUE_TITLE
import com.shikidroid.ui.ONE_MORE_TRY_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.MyButton
import com.shikidroid.uikit.components.TvSelectable
import com.shikidroid.uikit.fiftyDP
import com.shikidroid.uikit.fiveDP
import com.shikidroid.uikit.fourteenDP
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.threeDP
import com.shikidroid.uikit.twentyDP

@Composable
internal fun EnterTvMalScreen(
    altClick: () -> Unit,
    navigator: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ShikidroidTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = fourteenDP),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .height(fiftyDP)
                    .width(fiftyDP),
                painter = painterResource(id = R.drawable.ic_my_anime_list),
                tint = ShikidroidTheme.colors.onPrimary,
                contentDescription = null
            )
            Text13Sp(
                modifier = Modifier.padding(vertical = threeDP),
                text = "Сервер Shikimori не отвечает",
                color = ShikidroidTheme.colors.onPrimary
            )
            Text13Sp(
                modifier = Modifier.padding(vertical = threeDP),
                text = "Вы можете повторить запрос или использовать сервер MyAnimeList, но часть текста будет на английском",
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
                            text = ONE_MORE_TRY_TITLE
                        )
                    }
                }
                TvSelectable() { interactionSource, isFocused, scale ->
                    MyButton(
                        modifier = Modifier
                            .padding(threeDP)
                            .focusable(enabled = true, interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        onClick = {
                            navigator.navigate(StartScreens.TvRailMal.route) {
                                popUpTo(route = StartScreens.Splash.route) {
                                    inclusive = true
                                }
                            }
                        },
                        enabledColor = ShikidroidTheme.colors.secondary,
                        borderColor = if (isFocused) {
                            ShikidroidTheme.colors.onPrimary
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Text13SemiBold(
                            modifier = Modifier
                                .padding(horizontal = fiveDP),
                            text = CONTINUE_TITLE
                        )
                    }
                }
            }
        }
    }
}