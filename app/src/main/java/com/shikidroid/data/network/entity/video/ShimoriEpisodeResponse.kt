package com.shikidroid.data.network.entity.video

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией об эпизоде аниме
 *
 * @param id идентификационный номер
 * @param index порядковый номер
 * @param animeId идентификационный номер аниме с сайта MyAnimeList
 */
data class ShimoriEpisodeResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("index") val index: Long?,
    @SerializedName("animeId") val animeId: Long?
)
