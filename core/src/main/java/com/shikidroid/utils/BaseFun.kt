package com.shikidroid.utils

import android.util.Log

/**
 * Функция для показа логов
 */
fun logD(message: String?) {
    Log.d("ABRA", message.orEmpty())
}