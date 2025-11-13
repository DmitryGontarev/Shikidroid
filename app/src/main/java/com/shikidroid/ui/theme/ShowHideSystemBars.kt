package com.shikidroid.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.shikidroid.hideSystemBars
import com.shikidroid.hideSystemUi
import com.shikidroid.showSystemBars
import com.shikidroid.showSystemUi

@Composable
internal fun ShowHideSystemBars() {
    /** контекст */
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.hideSystemBars()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            context.showSystemBars()
        }
    }
}

@Composable
internal fun ShowHideSystemUi() {
    /** контекст */
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.hideSystemUi()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            context.showSystemUi()
        }
    }
}