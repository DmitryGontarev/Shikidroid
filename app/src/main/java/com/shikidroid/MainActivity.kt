package com.shikidroid

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.presentation.navigation.navgraphs.StartScreens
import com.shikidroid.ui.theme.ShikidroidTheme

/**
 * Основная Activity приложения
 */
internal class MainActivity : ComponentActivity() {

    /** флаг запуска приложения на Android TV */
    var isAndroidTv = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // запись значения флага AndroidTV
        isAndroidTv =
            packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
                    packageManager.hasSystemFeature(PackageManager.FEATURE_TELEVISION)

        // сохраняем флаг типа экранов Android или AndroidTV
        appComponent.getSharedPreferencesProvider().putBoolean(
            key = AppKeys.IS_ANDROID_TV,
            boolean = isAndroidTv
        )

        // если влючен режим Edge-to-Edge, то отключаем системные отступы для приложения
        if (getApp.isEdgeToEdge.value) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            setDisplayCutoutMode()
        }

        if (getApp.isSystemBarsHide.value) {
            hideSystemBars()
        }

        setContent {
            ShikidroidTheme(
                isEdgeToEdge = getApp.isEdgeToEdge.value,
                darkTheme = getApp.isDarkTheme.value
            ) {
                StartScreens(component = appComponent)
            }
        }
    }
}

/** Функция, которая вытаскивает сслыку на [MainActivity] из контекста */
internal fun Context.findMainActivity(): MainActivity? =
    when (this) {
        is MainActivity -> this
        is ContextWrapper -> baseContext.findMainActivity()
        else -> null
    }

/** Функция получения флага есть ли нужное приложение для запуска интента */
internal fun Context.isIntentAvailable(intent: Intent): Boolean {
    return intent.resolveActivity(packageManager) != null
}

/**
 * Функция отправки ссылки через меню системы Android
 *
 * @param link ссылка
 * @param title заголовок в шторке системы
 */
internal fun Context.shareLink(link: String?, title: String?) {
    link?.let { url ->
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        if (isIntentAvailable(intent = intent)) {
            startActivity(
                Intent.createChooser(
                    intent,
                    title.orEmpty()
                )
            )
        } else {
            return
        }
    }
}

/**
 * Функция для открытия ссылки через подходящее приложение, если оно доступно
 *
 * @param link ссылка
 */
internal fun Context.openLink(link: String?) {
    link?.let { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (isIntentAvailable(intent = intent)) {
            startActivity(intent)
        } else {
            return
        }
    }
}

/** Функция, которая вытаскивает сслыку на активити из контекста */
internal fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

/** Функция для скрытия системных UI компонентов и отключения системных оступов */
internal fun Context.hideSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/** Функция для показа системных UI компонентов и включения системных отступов */
internal fun Context.showSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

/** Функция для скрытия системных UI компонентов */
internal fun Context.hideSystemBars() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/** Функция для показа системных UI компонентов */
internal fun Context.showSystemBars() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

/** Функция для установки аттрибутов отображения приложения под вырезами экрана */
internal fun Activity.setDisplayCutoutMode() {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
    }
}

/**
 * Функция для установки горизонтальной/вертиакальной ориентиации Activity
 * также скрывает системный UI в горизонтальном режиме
 */
internal fun Context.setScreenOrientation(orientation: Int) {
    val activity = this.findActivity() ?: return
    activity.requestedOrientation = orientation
    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        hideSystemUi()
    } else {
        showSystemUi()
    }
}

/**
 * Функция для проверки положения устройства
 * возвращает true, если горизонтальное положение
 */
internal fun Context.isHorizontalOrientation(): Boolean {
    return this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}