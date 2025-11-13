package com.shikidroid.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Метод получения жизненного цикла composable-функции
 *
 * @param lifeCycleOwner жизненный цикл текущей composable-функции
 * @param onEvent событие жизненного цикла
 */
@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Функция для получения жизненного цикла composable-функции
 *
 * @param lifeCycleOwner жизненный цикл текущей composable-функции
 * @param onCreate обратный вызов при создании функции
 * @param onStart обратный вызов при старте функции
 * @param onResume обратный вызов при возобновлении работы функции
 * @param onPause обратный вызов при паузе функции
 * @param onStop обратный вызов при остановке функции
 * @param onDestroy обратный вызов при удалении функции из памяти
 * @param onAny обратный вызов для любого типа события жизненного цикла
 *
 */
@Composable
fun ComposableLifecycleOnEvents(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onCreate: ((LifecycleOwner) -> Unit)? = null,
    onStart: ((LifecycleOwner) -> Unit)? = null,
    onResume: ((LifecycleOwner) -> Unit)? = null,
    onPause: ((LifecycleOwner) -> Unit)? = null,
    onStop: ((LifecycleOwner) -> Unit)? = null,
    onDestroy: ((LifecycleOwner) -> Unit)? = null,
    onAny: ((LifecycleOwner) -> Unit)? = null,
) {

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate?.invoke(source)
                Lifecycle.Event.ON_START -> onStart?.invoke(source)
                Lifecycle.Event.ON_RESUME -> onResume?.invoke(source)
                Lifecycle.Event.ON_PAUSE -> onPause?.invoke(source)
                Lifecycle.Event.ON_STOP -> onStop?.invoke(source)
                Lifecycle.Event.ON_DESTROY -> onDestroy?.invoke(source)
                Lifecycle.Event.ON_ANY -> onAny?.invoke(source)
            }
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}