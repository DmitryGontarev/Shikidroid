package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.shikidroid.R
import com.shikidroid.ui.INTERNET_CONNECTION_OFF_TEXT
import com.shikidroid.ui.INTERNET_CONNECTION_ON_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.LightGreen
import com.shikidroid.uikit.components.BoxWithMessage
import com.shikidroid.uikit.fourteenDP
import com.shikidroid.uikit.oneDP
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.thirtyFiveDP
import com.shikidroid.uikit.threeDP
import kotlinx.coroutines.delay

@Composable
internal fun NoInternetConnectionMessage(
    showMessage: Boolean
) {
    BoxWithMessage(
        showMessage = showMessage
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = fourteenDP, vertical = threeDP),
            backgroundColor = ShikidroidTheme.colors.background,
            elevation = ShikidroidTheme.elevation,
            border = BorderStroke(
                width = oneDP,
                color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
            ),
            shape = ShikidroidTheme.shapes.roundedCorner7dp
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(all = sevenDP),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(size = thirtyFiveDP)
                        .padding(threeDP),
                    painter = painterResource(
                        id =
                        if (showMessage) {
                            R.drawable.ic_wifi_off
                        } else {
                            R.drawable.ic_wifi_on
                        }
                    ),
                    tint =
                    if (showMessage) {
                        ShikidroidTheme.colors.secondary
                    } else {
                        LightGreen
                    },
                    contentDescription = null
                )
                Text16SemiBold(
                    modifier = Modifier
                        .padding(threeDP),
                    text =
                    if (showMessage) {
                        INTERNET_CONNECTION_OFF_TEXT
                    } else {
                        INTERNET_CONNECTION_ON_TEXT
                    },
                    color = ShikidroidTheme.colors.onPrimary
                )
            }
        }
    }
}