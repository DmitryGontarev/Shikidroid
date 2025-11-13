package com.shikidroid.uikit

import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Форма с закруглёнными углами в 7 пикселей
 */
val RoundedCorner7dp = RoundedCornerShape(7.dp)

/**
 * Форма с закруглёнными углами в 30 пикселей
 */
val RoundedCorner30dp = RoundedCornerShape(30.dp)

/**
 * Форма с закруглённым левым верхним и правым нижним углами в 7 пикселей
 */
val RoundedCornerTopStartBottomEnd7dp = RoundedCornerShape(topStart = 7.dp, bottomEnd = 7.dp)

/**
 * Форма с закруглённым правым верхним и правым нижним углами в 30 пикселей
 */
val RoundedCornerTopEndBottomEnd30dp = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 30.dp,
    bottomStart = 0.dp,
    bottomEnd = 30.dp
)

/**
 * Форма с закруглённым правым верхним и правым нижним углами в 30 пикселей
 */
val RoundedCornerTopStartBottomStart30dp = RoundedCornerShape(
    topStart = 30.dp,
    topEnd = 0.dp,
    bottomStart = 30.dp,
    bottomEnd = 0.dp
)

/**
 * Форма с закруглённым левым верхним и правым верхним углами в 30 пикселей
 */
val RoundedCornerTopStartTopEnd14dp = RoundedCornerShape(
    topStart = 14.dp,
    topEnd = 14.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/**
 * Форма с закруглённым левым верхним и правым верхним углами в 30 пикселей
 */
val RoundedCornerTopStartTopEnd30dp = RoundedCornerShape(
    topStart = 30.dp,
    topEnd = 30.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/**
 * Форма круга
 */
val AbsoluteRounded50dp = AbsoluteRoundedCornerShape(50.dp)