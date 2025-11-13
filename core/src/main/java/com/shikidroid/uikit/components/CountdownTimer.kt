package com.shikidroid.uikit.components

import androidx.compose.runtime.*
import kotlinx.coroutines.delay

/**
 * Функция, вызывающая коллбэк по достижению заданного времени
 */
@Composable
fun CountdownTimer(
    targetTime: Long,
    timerState: MutableState<Long>? = null,
    callback: () -> Unit
) {
    /** текущее время таймера */
    val currentTime = remember {
        mutableStateOf(0L)
    }

    if (timerState != null) {
        LaunchedEffect(key1 = timerState.value) {
            delay(1000L)
            timerState.value += 1L
            if (timerState.value == targetTime) {
                callback.invoke()
                return@LaunchedEffect
            }
        }
    } else {
        LaunchedEffect(key1 = currentTime.value) {
            delay(1000L)
            currentTime.value += 1L
            if (currentTime.value == targetTime) {
                callback.invoke()
                return@LaunchedEffect
            }
        }
    }
}