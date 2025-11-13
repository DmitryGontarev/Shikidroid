package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.manga.MangaResponse

/**
 * Сущность с информацией об аниме/манги, в создании которого принимал участие человек
 *
 * @param anime аниме
 * @param manga манга
 * @param role роль в создании
 */
data class WorkResponse(
    @SerializedName("anime") val anime : AnimeResponse?,
    @SerializedName("manga") val manga : MangaResponse?,
    @SerializedName("role") val role : String?
)
