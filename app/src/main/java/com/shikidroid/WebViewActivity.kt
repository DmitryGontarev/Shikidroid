package com.shikidroid

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.presentation.screens.WebViewScreen
import com.shikidroid.ui.theme.ShikidroidTheme

/** Активити для открытия WebView на AndroidTV */
class WebViewActivity: ComponentActivity() {

    /** флаг запуска приложения на Android TV */
    var isAndroidTv = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUi()
        setDisplayCutoutMode()

        // запись значения флага AndroidTV
        isAndroidTv =
            packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
                    packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION)

        val url = intent.getStringExtra(AppKeys.WEBVIEW_SCREEN_URL)

        val useIFrame = intent.getBooleanExtra(AppKeys.WEBVIEW_SCREEN_I_FRAME, false)

        val isGetHtmlBodyForTv = intent.getBooleanExtra(AppKeys.WEBVIEW_SCREEN_IS_GET_HTML_BODY_FOR_TV, false)

        setContent {

            /** контроллер навигации по экранам */
            val navController = rememberNavController()

            ShikidroidTheme(
                isEdgeToEdge = getApp.isEdgeToEdge.value,
                darkTheme = getApp.isDarkTheme.value
            ) {
                WebViewScreen(
                    url = url,
                    useIFrame = useIFrame,
                    getHtmlBodyString = if (isGetHtmlBodyForTv) {
                        { htmlBody ->
                            val intent = Intent()
                            intent.putExtra(AppKeys.WEBVIEW_SCREEN_GET_HTML_BODY_FOR_TV, htmlBody)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    } else {
                        null
                    },
                    navigator = navController
                )
            }
        }
    }
}

/** Функция, которая вытаскивает сслыку на [WebViewActivity] из контекста */
internal fun Context.findWebViewActivity(): WebViewActivity? =
    when (this) {
        is WebViewActivity -> this
        is ContextWrapper -> baseContext.findWebViewActivity()
        else -> null
    }