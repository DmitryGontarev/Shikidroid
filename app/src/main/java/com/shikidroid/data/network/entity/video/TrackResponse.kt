package com.shikidroid.data.network.entity.video

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о качестве видео
 *
 * @param quality разрешение видеофайла
 * @param url ссылка на видеофайл
 */
data class TrackResponse(
    @SerializedName("quality") val quality : String?,
    @SerializedName("url") val url : String?
)
