package com.shikidroid.uikit.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

/**
 * Функция, которая делает экран постоянно включенным
 *
 * @param activity активити экрана
 */
@Composable
fun KeepScreenOn(activity: Activity?) {

    DisposableEffect(key1 = Unit) {
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}