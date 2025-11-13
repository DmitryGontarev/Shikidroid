package com.shikidroid.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.appComponent
import com.shikidroid.findMainActivity
import com.shikidroid.presentation.navigation.sealedscreens.RatesScreens
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.presentation.screens.mal.EnterMalScreen
import com.shikidroid.presentation.screens.tv.ErrorTvScreen
import com.shikidroid.presentation.screens.tvmal.EnterTvMalScreen
import com.shikidroid.presentation.viewmodels.SplashScreenViewModel
import com.shikidroid.ui.EXIT_TITLE
import com.shikidroid.ui.SHIKIDROID_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.Toast
import kotlinx.coroutines.delay

/**
 * Стартовый Экран приложения
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации экранов
 */
@Composable
internal fun SplashScreen(
    navigator: NavHostController,
    viewModel: SplashScreenViewModel
) {
    /** контекст */
    val context = LocalContext.current

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** флаг показа экрана с ошибкой */
    val showErrorScreen by viewModel.showErrorScreen.observeAsState(initial = false)

    /** флаг показа экрана входа с использование сервера MyAnimeList */
    val showEnterMalScreen by viewModel.showEnterMalScreen.observeAsState(initial = false)

    /** флаг запуска приложения на Android TV */
    val isAndroidTv = LocalContext.current.findMainActivity()?.isAndroidTv ?: false

    /** состояние подключения к интернету */
    val isNetworkConnect =
        context.appComponent.getNetworkConnectionObserver().observeAsState(initial = true)

    /** строка с названием приложения для показа на экране */
    var title by remember {
        mutableStateOf("")
    }

    /** флаг показа тост сообщения */
    val showToast by viewModel.showToast.observeAsState(initial = false)

    LaunchedEffect(key1 = isNetworkConnect.value) {
        if (!isNetworkConnect.value) {
            viewModel.showErrorScreen()
        }
    }

    LaunchedEffect(key1 = Unit) {
        SHIKIDROID_TITLE.forEach {
            title += it
            delay(33)
        }
        if (isNetworkConnect.value) {
            viewModel.loadData()
        }
    }

    /** ключ экрана для следующей навигации */
    val navigateScreen by viewModel.navigateScreen.observeAsState("")

    LaunchedEffect(key1 = navigateScreen) {
        when (navigateScreen) {
            StartScreens.Bottom.route -> {
                if (isAndroidTv) {
                    navigator.navigate(StartScreens.TvRail.route) {
                        popUpTo(StartScreens.Splash.route) {
                            inclusive = true
                        }
                    }
                } else {
                    navigator.navigate(StartScreens.Bottom.route) {
                        popUpTo(StartScreens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            }

            StartScreens.WebViewAuth.route -> {
                navigator.navigate(StartScreens.WebViewAuth.route) {
                    popUpTo(StartScreens.Splash.route) {
                        inclusive = true
                    }
                }
            }

            StartScreens.WebView.route -> {
                navigator.navigate(RatesScreens.WebView.route) {
                    popUpTo(StartScreens.Splash.route) {
                        inclusive = true
                    }
                }
            }

            StartScreens.Enter.route -> {
                if (isAndroidTv) {
                    navigator.navigate(StartScreens.EnterTv.route) {
                        popUpTo(StartScreens.Splash.route) {
                            inclusive = true
                        }
                    }
                } else {
                    navigator.navigate(StartScreens.Enter.route) {
                        popUpTo(StartScreens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
        viewModel.navigateScreen.value = ""
    }

    if (showToast) {
        Toast(
            text = viewModel.toastText.value,
            callback = {
                viewModel.resetToast()
            }
        )
    }

    when {
        showErrorScreen -> {
            if (isAndroidTv) {
                ErrorTvScreen(
                    mainClick = {
                        if (isNetworkConnect.value) {
                            viewModel.hideErrorScreen()
                            viewModel.loadData()
                        }
                    },
                    altBtnText = EXIT_TITLE,
                    altClick = {
                        context.findMainActivity()?.finish()
                    }
                )
            } else {
                ErrorScreen(
                    mainClick = {
                        if (isNetworkConnect.value) {
                            viewModel.hideErrorScreen()
                            viewModel.loadData()
                        }
                    },
                    altBtnText = EXIT_TITLE,
                    altClick = {
                        context.findMainActivity()?.finish()
                    }
                )
            }
        }

        showEnterMalScreen -> {
            if (isAndroidTv) {
                EnterTvMalScreen(
                    altClick = {
                        viewModel.loadData()
                        viewModel.showEnterMalScreen.value = false
                    },
                    navigator = navigator
                )
            } else {
                EnterMalScreen(
                    altClick = {
                        viewModel.loadData()
                        viewModel.showEnterMalScreen.value = false
                    },
                    navigator = navigator
                )
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = ShikidroidTheme.colors.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row() {
                        AnimatedVisibility(
                            modifier = Modifier.padding(threeDP),
                            visible = title.isEmpty().not(),
                            enter = fadeIn(animationSpec = tween(durationMillis = animationSevenHundred)),
                            exit = fadeOut(animationSpec = tween(durationMillis = animationSevenHundred))
                        ) {
                            Text(
                                text = title,
                                style = SplashScreenTitle,
                                color = ShikidroidTheme.colors.onPrimary
                            )
                        }

                        Icon(
                            modifier = Modifier
                                .height(fiftyDP)
                                .width(fiftyDP),
                            painter = painterResource(id = R.drawable.ic_shikimori),
                            contentDescription = "Splash Image",
                            tint = ShikidroidTheme.colors.onPrimary
                        )
                    }

                    if (isLoading) {
                        LinearProgressIndicator(
                            color = Orange,
                            modifier = Modifier.padding(vertical = tenDP)
                        )
                    }
                }
            }
        }
    }
}