package com.shikidroid.domain.models.calendar

import com.shikidroid.domain.models.anime.AnimeJsonModel
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.rates.RateStatus

/**
 * Сущность с данными о дате выхода аниме
 *
 * @property nextEpisode номер следующего эпизода
 * @property nextEpisodeDate дата выхода следующего эпизода
 * @property duration длительность эпизода
 * @property anime сущность с данными об аниме
 */
data class CalendarModel(
    val nextEpisode : Int?,
    val nextEpisodeDate: String?,
    val duration : String?,
    val anime : AnimeModel?
) {

    var status: RateStatus? = null
}

data class CalendarJsonModel(
    val next_episode : Int?,
    val next_episode_at: String?,
    val duration : String?,
    val anime : AnimeJsonModel?
)