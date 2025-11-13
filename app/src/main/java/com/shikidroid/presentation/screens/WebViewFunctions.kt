package com.shikidroid.presentation.screens

import android.os.SystemClock
import android.view.MotionEvent
import android.webkit.WebView
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shikidroid.R
import com.shikidroid.uikit.components.TvSelectable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Курсор для WebView на AndroidTv
 *
 * @param webView экземпляр WebView
 * @param webViewFocusRequester управляюший фокусом WebView
 * @param isCursorFocused флаг в фокусе ли курсор
 */
@Composable
internal fun TvMouseCursor(
    webView: WebView?,
    webViewFocusRequester: FocusRequester,
    isCursorFocused: Boolean
) {
    /** значение для сдвига курсора */
    val cursorOffsetValue = 25f

    /** область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** разрешение экрана */
    val localDensity = LocalDensity.current

    /** высота экрана */
    var fullHeight by remember {
        mutableStateOf(0f)
    }

    /** ширина экрана */
    var fullWidth by remember {
        mutableStateOf(0f)
    }

    /** функционал фокуса */
    val cursorFocusRequester = remember { FocusRequester() }

    /** сдвиг по оси X */
    var offsetX by remember { mutableStateOf(0f) }

    /** сдвиг по оси X */
    var offsetY by remember { mutableStateOf(0f) }

    /** флаг нажатия на центральную кнопку пульта */
    var isCenterBtnPressed by remember { mutableStateOf(false) }

    /** последнее время нажатия центральной кнопки пульта */
    var lastKeyDownTime by remember { mutableStateOf(0L) }

    /** величина задержки для регистрации долгого нажатия центральной кнопки пульта */
    val pressedDelta = 700

    /**
     * Метод для эмуляции нажатия на WebView
     *
     * @param x значение по оси X
     * @param y значение по оси Y
     * @param webViewFocus флаг установки фокуса на WebView
     */
    fun emulateWebViewTouch(x: Float, y: Float, webViewFocus: Boolean = false) {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis() + 100
        val metaState = 0

        /** событие нажатия кнопки */
        val motionEventDown = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
            x,
            y,
            metaState
        )

        /** событие отпускания кнопки */
        val motionEventUp = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_UP,
            x,
            y,
            metaState
        )

        webView?.dispatchTouchEvent(motionEventDown)
        webView?.dispatchTouchEvent(motionEventUp)

        if (webViewFocus) {
            webViewFocusRequester.requestFocus()
        } else {
            webViewFocusRequester.freeFocus()
        }
    }

    /** Метод для прокрутки WebView вверх при помощи курсора */
    fun emulateWebViewScrollUp() {
        if (offsetY <= 0f + cursorOffsetValue * 2) {
            webView?.scrollTo(0, webView.scrollY - cursorOffsetValue.toInt())
        }
    }

    /** Метод для прокрутки WebView вниз при помощи курсора */
    fun emulateWebViewScrollDown() {
        if (offsetY >= fullHeight - cursorOffsetValue * 2) {
            webView?.scrollTo(0, webView.scrollY + cursorOffsetValue.toInt())
        }
    }

    TvSelectable { interactionSource, isFocused, scale ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester = cursorFocusRequester)
                .onSizeChanged {
                    fullHeight = it.height.toFloat()
                    fullWidth = it.width.toFloat()
                    offsetX = (it.width / 2).toFloat()
                    offsetY = (it.height / 2).toFloat()
                }
                .focusable(
                    enabled = true,
                    interactionSource = interactionSource
                )
                .onKeyEvent {
                    when {
                        it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_UP &&
                                it.type == KeyEventType.KeyDown -> {
                            offsetY = (offsetY - cursorOffsetValue).coerceAtLeast(minimumValue = 0f)
                            emulateWebViewScrollUp()
                        }

                        it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_DOWN &&
                                it.type == KeyEventType.KeyDown -> {
                            offsetY =
                                (offsetY + cursorOffsetValue).coerceAtMost(maximumValue = fullHeight - cursorOffsetValue)
                            emulateWebViewScrollDown()
                        }

                        it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT &&
                                it.type == KeyEventType.KeyDown -> {
                            offsetX = (offsetX - cursorOffsetValue).coerceAtLeast(minimumValue = 0f)
                        }

                        it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT &&
                                it.type == KeyEventType.KeyDown -> {
                            offsetX =
                                (offsetX + cursorOffsetValue).coerceAtMost(maximumValue = fullWidth - cursorOffsetValue)
                        }

                        it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_CENTER -> {
                            val time = SystemClock.uptimeMillis()
                            if (lastKeyDownTime + pressedDelta <= time) {
                                /** Долгое нажатие */
                                if (it.type == KeyEventType.KeyUp) {
                                    coroutineScope.launch {
                                        isCenterBtnPressed = true
                                        emulateWebViewTouch(x = offsetX, y = offsetY, webViewFocus = true)
                                        delay(500)
                                        isCenterBtnPressed = false
                                        cursorFocusRequester.freeFocus()
                                    }
                                }
                            } else {
                                /** Короткое нажатие */
                                if (it.type == KeyEventType.KeyUp) {
                                    coroutineScope.launch {
                                        isCenterBtnPressed = true
                                        emulateWebViewTouch(x = offsetX, y = offsetY, webViewFocus = false)
                                        delay(500)
                                        isCenterBtnPressed = false
                                        cursorFocusRequester.requestFocus()
                                    }
                                }
                            }
                            lastKeyDownTime = it.nativeKeyEvent.downTime
                        }
                    }
                    false
                }
        ) {
            Icon(
                modifier = Modifier
                    .size(
                        size = 27.dp
                    )
                    .offset(
                        x = (offsetX / localDensity.density).dp,
                        y = (offsetY / localDensity.density).dp
                    ),
                painter = painterResource(
                    id =
                    if (isCenterBtnPressed) {
                        R.drawable.ic_arrow_left_click
                    } else {
                        R.drawable.ic_arrow_selector
                    }
                ),
                tint = if (isCursorFocused) {
                    Color.Black
                } else {
                    Color.Transparent
                },
                contentDescription = null
            )
            Icon(
                modifier = Modifier
                    .size(
                        size = 24.dp
                    )
                    .offset(
                        x = (offsetX / localDensity.density).dp,
                        y = (offsetY / localDensity.density).dp
                    ),
                painter = painterResource(
                    id =
                    if (isCenterBtnPressed) {
                        R.drawable.ic_arrow_left_click
                    } else {
                        R.drawable.ic_arrow_selector
                    }
                ),
                tint = if (isCursorFocused) {
                    Color.White
                } else {
                    Color.Transparent
                },
                contentDescription = null
            )
        }
    }
}