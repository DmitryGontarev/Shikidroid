package com.shikidroid.uikit.components

import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Toast(
    text: String?,
    callback: (() -> Unit)? = null
) {
    text?.let {
        val toast =
            Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.AXIS_X_SHIFT, 0, 700)
        toast.show()
        callback?.let {
            it()
        }
    }
}