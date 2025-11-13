package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.calendar.CalendarResponse
import com.shikidroid.domain.models.calendar.CalendarJsonModel
import com.shikidroid.domain.models.calendar.CalendarModel

/**
 * Конвертация [CalendarResponse] в модель domain слоя
 *
 * @return [CalendarModel]
 */
fun CalendarResponse.toDomainModel() =
    CalendarModel(
        nextEpisode = nextEpisode,
        nextEpisodeDate = nextEpisodeDate,
        duration = duration,
        anime = anime?.toDomainModel()
    )

fun CalendarJsonModel.toDomainModel() =
    CalendarModel(
        nextEpisode = next_episode,
        nextEpisodeDate = next_episode_at,
        duration = duration,
        anime = anime?.toDomainModel()
    )