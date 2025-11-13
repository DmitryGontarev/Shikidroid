package com.shikidroid.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.presentation.viewmodels.ProfileScreenViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.AsyncImageLoader
import com.shikidroid.uikit.components.MyMotionLayout
import com.shikidroid.uikit.components.RoundedIconButton
import com.shikidroid.uikit.fiftyDP
import com.shikidroid.uikit.oneHundredDP

@Composable
internal fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** детальная информация о пользователе */
    val userDetails by viewModel.userDetails.observeAsState()

    MyMotionLayout(
        image = {
            Column {
                Box(modifier = Modifier
                    .size(oneHundredDP)
                ) {
                    AsyncImageLoader(url = userDetails?.image?.x160)
                }
            }
        },
        text = userDetails?.nickname.orEmpty(),
        underImage = {
            RoundedIconButton(
                icon = R.drawable.ic_mail,
                tint = ShikidroidTheme.colors.onPrimary
            )
        }
    )
}