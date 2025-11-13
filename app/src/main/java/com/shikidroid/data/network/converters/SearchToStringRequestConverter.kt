package com.shikidroid.data.network.converters

import com.shikidroid.domain.models.anime.AnimeDurationType
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus

/**
 * Возвращает строку из элементов, разделённых запятой, или [null], если список пустой
 */
internal fun <T> List<T>.toStringOrNull() : String? {
    return if (this.isNotEmpty()) {
        this.joinToString(",")
    } else {
        null
    }
}

/**
 * Возвращает список, если он не пустой, или [null]
 */
internal fun <T> List<T>.toNullIfEmpty(): List<T>? {
    return this.ifEmpty { null }
}

/**
 * Конвертация списка [AnimeType] в строку запроса
 */
internal fun List<AnimeType>.toStringRequestAnimeType(): String? {
    val animeTypes = mutableListOf<String>()
    this.map { animeType ->
        animeTypes.add(element = animeType.toStringRequest())
    }
    return animeTypes.toStringOrNull()
}

/**
 * Конвертация списка [AiredStatus] в строку запроса
 */
internal fun List<AiredStatus>.toStringRequestAiredStatuses(): String? {
    val airedStatuses = mutableListOf<String>()
    this.map { status ->
        airedStatuses.add(element = status.toStringRequest())
    }
    return airedStatuses.toStringOrNull()
}

/**
 * Конвертация сезонов выхода аниме в строку запроса
 */
internal fun List<Pair<String?, String?>>.toStringRequestSeason(): String? {
    val seasons = mutableListOf<String>()
    this.map { season ->
        when {
            season.first.isNullOrEmpty().not() && season.second.isNullOrEmpty() -> {
                season.first?.let { seasons.add(it) }
            }
            season.first.isNullOrEmpty() && season.second.isNullOrEmpty().not() -> {
                season.second?.let { seasons.add(it) }
            }
            season.first.isNullOrEmpty().not() && season.second.isNullOrEmpty().not() -> {
                seasons.add(
                    season.first + "_" + season.second
                )
            }
            else -> {}
        }
    }
    return seasons.toStringOrNull()
}

/**
 * Конвертация [AnimeDurationType] в строку запроса
 */
internal fun List<AnimeDurationType>.toStringRequestDuration(): String? {
    val durations = mutableListOf<String>()
    this.map { duration ->
        durations.add(duration.toStringRequest())
    }
    return durations.toStringOrNull()
}

/**
 * Конвертация [AgeRatingType] в строку запроса
 */
internal fun List<AgeRatingType>.toStringRequestAgeRatingType(): String? {
    val ageType = mutableListOf<String>()
    this.map { age ->
        ageType.add(element = age.toStringRequest())
    }
    return ageType.toStringOrNull()
}

/**
 * Конвертация пользовательского статуса аниме в строку запроса
 */
internal fun List<RateStatus>.toStringRequestRateStatus(): String? {
    val rateStatuses = mutableListOf<String>()
    this.map { rate ->
        rateStatuses.add(element = rate.toStringRequest())
    }
    return rateStatuses.toStringOrNull()
}

/**
 * Конвертация списка типов манги в строку запроса
 */
internal fun List<MangaType>.toStringRequestMangaType(): String? {
    val mangaTypes = mutableListOf<String>()
    this.map { mangaType ->
        mangaTypes.add(element = mangaType.toStringRequest())
    }
    return mangaTypes.toStringOrNull()
}