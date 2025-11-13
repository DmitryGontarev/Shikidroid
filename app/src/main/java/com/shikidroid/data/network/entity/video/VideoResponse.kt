package com.shikidroid.data.network.entity.video

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о видео
 *
 * @param animeId идентификационный номер аниме
 * @param episodeId идентификационный номер эпизода
 * @param player название плеера
 * @param hosting название хостинга
 * @param tracks список доступных разрешений видео
 * @param subAss
 * @param subVtt
 */
data class VideoResponse(
    @SerializedName("animeId") val animeId: Long?,
    @SerializedName("episodeId") val episodeId: Long?,
    @SerializedName("player") val player: String?,
    @SerializedName("hosting") val hosting: String?,
    @SerializedName("tracks") val tracks: List<TrackResponse?>?,
    @SerializedName("subtitlesUrl") val subAss : String?,
    @SerializedName("subtitlesVttUrl") val subVtt : String?
)
