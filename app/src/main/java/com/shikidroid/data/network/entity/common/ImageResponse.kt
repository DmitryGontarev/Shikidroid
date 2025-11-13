package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName

/**
 * Сущность постера аниме
 *
 * @property original ссылка на оригинальный размер картинки
 * @property preview ссылка на картинку для превью
 * @property x96 ссылка на картинку размером 96 пикселей
 * @property x48 ссылка на картинку размером 48 пикселей
 */
data class ImageResponse(
    @SerializedName("original")
    val original: String?,
    @SerializedName("preview")
    val preview: String?,
    @SerializedName("x96")
    val x96: String?,
    @SerializedName("x48")
    val x48: String?,
)