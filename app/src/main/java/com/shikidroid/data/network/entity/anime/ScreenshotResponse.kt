package com.shikidroid.data.network.entity.anime

import com.google.gson.annotations.SerializedName

/**
 * Сущность с сылками на скриншоты из аниме
 *
 * @property [original] оригинальный размер
 * @property [preview] размер для превью
 */
data class ScreenshotResponse(
    @SerializedName("original")
    val original: String?,
    @SerializedName("preview")
    val preview: String?
)
