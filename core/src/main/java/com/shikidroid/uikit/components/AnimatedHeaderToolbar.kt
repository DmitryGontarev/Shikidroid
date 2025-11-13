package com.shikidroid.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Контейнер с анимированной верхней панелью инструментов
 *
 * @param toolbarHeight высота тулбара
 * @param toolbarColor цвет тулбара
 * @param headerHeight предполагаемя высота заголовка под тулбаром
 * @param headerWrapColors цвет поверх контента под тулбаром
 * @param hideToolbarByScroll скрывать ли тулбар
 * @param toolbarContent контент тулбара
 * @param headerContent контент под тулбаром
 * @param bodyContent основной контент
 */
@Composable
fun AnimatedHeaderToolbar(
    toolbarHeight: Dp = 56.dp,
    toolbarColor: Color = MaterialTheme.colors.primary,
    headerHeight: Dp = 350.dp,
    headerWrapColors: Color = MaterialTheme.colors.primary,
    hideToolbarByScroll: Boolean = false,
    toolbarContent: @Composable (Boolean) -> Unit,
    headerContent: @Composable () -> Unit,
    bodyContent: @Composable (ColumnScope) -> Unit,
) {
    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** состояние прокрутки */
    val scrollState = rememberScrollState(0)

    /** высота контента под тулбаром */
    val headerHeightDp = remember { mutableStateOf(headerHeight) }

    /** высота контента под тулбаром в пикселях */
    val headerHeightPx = with(LocalDensity.current) { headerHeightDp.value.toPx() }

    /** высота тулбара в пикселях */
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    /** вычисляемое свойство, скрывать ли тулбар при прокрутке */
    val showToolbar = derivedStateOf {
        scrollState.value >= headerHeightPx - toolbarHeightPx
    }

    /** отступ статус бара */
    val topInsetPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //////////////////////// BODY ////////////////////////

        Column(
            modifier = Modifier
                .verticalScroll(
                    state = scrollState
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 700,
                        easing = LinearOutSlowInEasing
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(headerHeightDp.value + toolbarHeight + topInsetPadding))

            bodyContent(this)
        }

        //////////////////////// TOOLBAR ////////////////////////

        if (hideToolbarByScroll) {
            AnimatedVisibility(
                visible = !showToolbar.value,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(toolbarHeight)
                        .fillMaxWidth()
//                        .background(
//                            color = toolbarColor.copy(
//                                alpha = when {
//                                    ((1f / (headerHeightPx / 2)) * scrollState.value).isNaN() -> 0f
//                                    (1f / (headerHeightPx / 2)) * scrollState.value - 1 <= 0 -> 0f
//                                    (1f / (headerHeightPx / 2)) * scrollState.value - 1 >= 1 -> 1f
//                                    else -> (1f / (headerHeightPx / 2)) * scrollState.value - 1
//                                }
//                            )
//                        )
                ) {
                    toolbarContent(showToolbar.value)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .height(toolbarHeight)
                    .fillMaxWidth()
                    .background(
                        color = toolbarColor.copy(
                            alpha = when {
                                ((1f / (headerHeightPx / 2)) * scrollState.value).isNaN() -> 0f
                                (1f / (headerHeightPx / 2)) * scrollState.value - 1 <= 0 -> 0f
                                (1f / (headerHeightPx / 2)) * scrollState.value - 1 >= 1 -> 1f
                                else -> (1f / (headerHeightPx / 2)) * scrollState.value - 1
                            }
                        )
                    )
            ) {
                toolbarContent(showToolbar.value)
            }
        }

        //////////////////////// HEADER ////////////////////////

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = toolbarHeight)
                .graphicsLayer {
                    alpha = (-1f / headerHeightPx) * scrollState.value + 1
                    translationY = -scrollState.value.toFloat() / 2f
                }
        ) {

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        headerHeightDp.value = with(localDensity) {
                            it.size.height.toDp()
                        }
                    }
            ) {
                headerContent()
            }

            Box(
                modifier = Modifier
                    .height(headerHeightDp.value)
                    .fillMaxWidth()
                    .background(
                        color = headerWrapColors.copy(
                            alpha = when {
                                ((1f / (headerHeightPx / 2)) * scrollState.value).isNaN() -> 0f
                                (1f / (headerHeightPx / 2)) * scrollState.value - 1 <= 0 -> 0f
                                (1f / (headerHeightPx / 2)) * scrollState.value - 1 >= 1 -> 1f
                                else -> (1f / (headerHeightPx / 2)) * scrollState.value - 1
                            }
                        )
                    )
            )
        }
    }
}