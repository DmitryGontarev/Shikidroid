package com.shikidroid.data.network.entity.calendar

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse

/**
 * Сущность с данными о дате выхода аниме
 *
 * @property nextEpisode номер следующего эпизода
 * @property nextEpisodeDate дата выхода следующего эпизода
 * @property duration длительность эпизода
 * @property anime сущность с данными об аниме
 */
data class CalendarResponse(
    @SerializedName("next_episode") val nextEpisode : Int?,
    @SerializedName("next_episode_at") val nextEpisodeDate: String?,
    @SerializedName("duration") val duration : String?,
    @SerializedName("anime") val anime : AnimeResponse?
)
