package com.shikidroid.presentation.screens

import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.shikidroid.findMainActivity
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.presentation.viewmodels.WebViewAuthViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.ui.theme.ShowHideSystemBars
import com.shikidroid.uikit.*
import kotlin.math.roundToInt

@Composable
internal fun WebViewAuthScreen(
    url: String?,
    navigator: NavHostController,
    viewModel: WebViewAuthViewModel
) {
    /** флаг показа прогресса загрузки страницы */
    val progressVisibility = remember { mutableStateOf(false) }

    /** числовое значение прогресса загрузки страниццы */
    val progress = remember { mutableStateOf(0.0F) }

    /** флаг получения токена  */
    val isTokenReceived by viewModel.isTokenReceived.observeAsState(false)

    /** флаг запуска приложения на Android TV */
    val isAndroidTv = LocalContext.current.findMainActivity()?.isAndroidTv ?: false

    ShowHideSystemBars()

    LaunchedEffect(key1 = isTokenReceived) {
        if (isTokenReceived) {
            if (isAndroidTv) {
                navigator.navigate(StartScreens.TvRail.route) {
                    popUpTo(StartScreens.WebViewAuth.route) {
                        inclusive = true
                    }
                }
            } else {
                navigator.navigate(route = StartScreens.Bottom.route) {
                    popUpTo(StartScreens.WebViewAuth.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    /** WebView экрана */
    var localWebView: WebView? by remember {
        mutableStateOf(null)
    }

    /** пакет данных состояние WebView */
    var savedBundle: Bundle? by rememberSaveable {
        mutableStateOf(null)
    }

    /** флаг фокуса курсора  */
    var isCursorFocused by remember {
        mutableStateOf(false)
    }

    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                val bundle = Bundle()
                localWebView?.saveState(bundle)
                savedBundle = bundle
            }

            else -> Unit
        }
    }

    BackHandler {
        if (isAndroidTv) {
            if (localWebView?.isFocused == true) {
                localWebView?.clearFocus()
                focusRequester.requestFocus()
            } else {
                navigator.popBackStack()
            }
        } else {
            navigator.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester = focusRequester)
            .onFocusChanged {
                isCursorFocused = it.hasFocus || it.isFocused
            }
    ) {
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                WebView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.setSupportMultipleWindows(true)
                    settings.setSupportZoom(true)
                    settings.loadWithOverviewMode = true

                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            progressVisibility.value = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            progressVisibility.value = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            viewModel.interceptUrlCode(request?.url.toString())
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress.value = newProgress.toFloat()
                        }
                    }

                    val bundle: Bundle? = savedBundle
                    if (bundle != null) {
                        restoreState(bundle)
                    } else {
                        url?.let {
                            loadUrl(it)
                        }
                    }
                }
            },
            update = { webView ->
                if (localWebView == null) {
                    localWebView = webView
                }
            }
        )

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Указатель AndroidTv для взаимодействия со страницей
        ////////////////////////////////////////////////////////////////////////////////////////////

        if (isAndroidTv) {
            TvMouseCursor(
                webView = localWebView,
                webViewFocusRequester = focusRequester,
                isCursorFocused = isCursorFocused,
            )
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Индикатор прогресса загрузки страницы
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (progressVisibility.value) {
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .height(seventyDP)
                    .padding(sevenDP)
                    .background(
                        color = Color.Black,
                        shape = RoundedCorner7dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(sevenDP),
                    color = ShikidroidTheme.colors.secondary,
                    progress = progress.value / 100
                )
                Text(
                    modifier = Modifier
                        .padding(sevenDP),
                    text = "${progress.value.roundToInt()}%",
                    color = Color.White,
                    style = ShikidroidTheme.typography.bodySemiBold16sp
                )
            }
        }
    }
}