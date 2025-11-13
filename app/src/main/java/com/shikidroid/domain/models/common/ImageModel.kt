package com.shikidroid.domain.models.common

/**
 * Модель постера аниме
 *
 * @property original ссылка на оригинальный размер картинки
 * @property preview ссылка на картинку для превью
 * @property x96 ссылка на картинку размером 96 пикселей
 * @property x48 ссылка на картинку размером 48 пикселей
 */
data class ImageModel(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?,
)

data class ImageJsonModel(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?,
)