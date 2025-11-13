package com.shikidroid.data.network.converters

import com.shikidroid.domain.models.myanimelist.RankingType

/**
 * Конвертация [RankingType] в строку для передачи в запрос
 */
fun RankingType.toStringRequest(): String {
    return when(this) {
        RankingType.ALL -> "all"
        RankingType.AIRING -> "airing"
        RankingType.UPCOMING -> "upcoming"
        RankingType.TV -> "tv"
        RankingType.OVA -> "ova"
        RankingType.MOVIE -> "movie"
        RankingType.SPECIAL -> "special"
        RankingType.BY_POPULARITY -> "bypopularity"
        RankingType.FAVORITE -> "favorite"
    }
}
