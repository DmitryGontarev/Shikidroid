package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.shikidroid.R
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.sevenDP

@Composable
fun ListIsEmpty(text: String = "Ваш список пуст") {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        item {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .padding(sevenDP),
                    painter = painterResource(id = R.drawable.ic_dono),
                    tint = ShikidroidTheme.colors.onBackground,
                    contentDescription = null
                )
                Text(
                    text = text,
                    style = ShikidroidTheme.typography.body12sp,
                    color = ShikidroidTheme.colors.onBackground
                )
            }
        }
    }
}