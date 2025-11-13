package com.shikidroid.domain.models.anime

/**
 * Модель с ссылками на скриншоты из аниме
 *
 * [original] оригинальный размер
 * [preview] размер для превью
 */
data class ScreenshotModel(
    val original: String?,
    val preview: String?
)
