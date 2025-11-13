package com.shikidroid.presentation.screens

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.shikidroid.domain.models.video.checkNeedIFrame
import com.shikidroid.findWebViewActivity
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.ui.theme.ShowHideSystemBars
import com.shikidroid.uikit.ComposableLifecycle
import com.shikidroid.uikit.fourteenDP
import com.shikidroid.uikit.sevenDP
import kotlin.math.roundToInt

/**
 * @param url ссылка на веб страницу
 * @param useIFrame флаг, обернуть ли страницу в HTML-тег <iframe>
 * @param getHtmlBodyString обратный вызов для возврата текста веб страницы
 * @param navigator контроллер навигации
 */
@Composable
internal fun WebViewScreen(
    url: String?,
    useIFrame: Boolean = false,
    getHtmlBodyString: ((String) -> Unit)? = null,
    navigator: NavHostController
) {
    ShowHideSystemBars()

    /** контектс приложения  */
    val context = LocalContext.current

    /** флаг запуска приложения на Android TV */
    val isAndroidTv = context.findWebViewActivity()?.isAndroidTv ?: false

    /** флаг показа прогресса загрузки страницы */
    val progressVisibility = remember { mutableStateOf(false) }

    /** значение прогресса загрузки */
    val progress = remember { mutableStateOf(0.0F) }

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
                if (isCursorFocused) {
                    if (localWebView?.canGoBack() == true) {
                        localWebView?.goBack()
                    } else {
                        context.findWebViewActivity()?.finish()
                    }
                }
            }
        } else {
            if (localWebView?.canGoBack() == true) {
                localWebView?.goBack()
            } else {
                navigator.popBackStack()
                context.findWebViewActivity()?.finish()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester = focusRequester)
            .onFocusChanged {
                isCursorFocused = it.hasFocus || it.isFocused
            },
        contentAlignment = Alignment.TopStart
    ) {
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                WebView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )

                    setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                    settings.apply {
                        javaScriptCanOpenWindowsAutomatically = true
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportMultipleWindows(true)
                        setSupportZoom(true)
                        loadWithOverviewMode = true
                        allowContentAccess = true
                        allowUniversalAccessFromFileURLs = true
//                        userAgentString = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (HTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36"

                        if (getHtmlBodyString != null) {
                            val a = GetBodyJavaScriptInterface()
                            a.getHtmlBody = { getHtmlBodyString?.invoke(it) }
                            addJavascriptInterface(a, "HtmlBody")
                        }
                    }

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

                            if (getHtmlBodyString != null) {
//                                view?.loadUrl("javascript:window.HtmlBody.setHtmlBodyString(document.getElementsByTagName('html')[0].innerHTML);")
                                view?.loadUrl("javascript:window.HtmlBody.setHtmlBodyString(document.body.getElementsByTagName('pre')[0].innerHTML);")
                            }
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
                            if (useIFrame) {
                                val needIFrame = it.checkNeedIFrame()
                                if (needIFrame) {
                                    val iframe =
                                        "<html><body style='margin:0;padding:0;'><iframe src='$it' width='100%' height='100%'  frameborder='0' allowfullscreen></iframe></body></html>"
                                    loadData(iframe, "text/html", "utf-8")
                                } else {
                                    loadUrl(it)
                                }
                            } else {
                                loadUrl(it)
                            }
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
                isCursorFocused = isCursorFocused
            )
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Индикатор прогресса загрузки страницы
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (progressVisibility.value) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = ShikidroidTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = sevenDP),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentSize()
                            .wrapContentHeight()
                    ) {
                        val (indicator, text) = createRefs()

                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(start = fourteenDP)
                                .constrainAs(indicator) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(text.start)
                                    bottom.linkTo(parent.bottom)
                                },
                            color = ShikidroidTheme.colors.secondary,
                            progress = progress.value / 100
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = sevenDP, end = fourteenDP)
                                .constrainAs(text) {
                                    top.linkTo(parent.top)
                                    start.linkTo(indicator.end)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                },
                            text = "${progress.value.roundToInt()}%",
                            color = ShikidroidTheme.colors.onPrimary,
                            style = ShikidroidTheme.typography.body16sp
                        )
                    }
                }

            }
        }
    }
}

class GetBodyJavaScriptInterface(var getHtmlBody: ((String) -> Unit)? = null) {
    @JavascriptInterface
    fun setHtmlBodyString(string: String) {
        getHtmlBody?.invoke(string)
    }
}