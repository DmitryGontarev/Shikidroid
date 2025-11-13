package com.shikidroid.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.presentation.screens.items.Text13SemiBold
import com.shikidroid.ui.ENTER_LIKE_GUEST_TITLE
import com.shikidroid.ui.ENTER_TITLE
import com.shikidroid.ui.SHIKIDROID_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.MyButton

@Composable
internal fun EnterScreen(
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
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .height(fortyDP)
                    .width(fortyDP),
                painter = painterResource(id = R.drawable.ic_shikimori),
                tint = ShikidroidTheme.colors.onPrimary,
                contentDescription = null
            )
            Text(
                text = SHIKIDROID_TITLE,
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
                MyButton(
                    modifier = Modifier
                        .padding(threeDP),
                    onClick = {
                        navigator.navigate(StartScreens.BottomGuest.route) {
                            popUpTo(route = StartScreens.Enter.route) {
                                inclusive = true
                            }
                        }
                    },
                    enabledColor = Color.Transparent,
                    borderColor = ShikidroidTheme.colors.onBackground
                ) {
                    Text13SemiBold(
                        modifier = Modifier
                            .padding(horizontal = sevenDP),
                        text = ENTER_LIKE_GUEST_TITLE
                    )
                }
                MyButton(
                    modifier = Modifier
                        .padding(threeDP),
                    onClick = {
                        navigator.navigate(StartScreens.WebViewAuth.route) {
                            popUpTo(route = StartScreens.Enter.route) {
                                inclusive = true
                            }
                        }
                    },
                    enabledColor = ShikidroidTheme.colors.secondary,
                    borderColor = Color.Transparent
                ) {
                    Text13SemiBold(
                        modifier = Modifier
                            .padding(horizontal = fiveDP),
                        text = ENTER_TITLE
                    )
                }
            }
        }
    }
}