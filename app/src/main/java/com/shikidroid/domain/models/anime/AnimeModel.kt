package com.shikidroid.domain.models.anime

import com.shikidroid.domain.models.common.ImageModel
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.ImageJsonModel

/**
 * Модель с информацией об аниме
 *
 * @property id id номер аниме
 * @property name название аниме
 * @property nameRu название аниме на русском
 * @property image ссылка на постер аниме
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48)
 * @property score оцена аниме по 10-тибалльной шкале
 * @property status статус трансляции релиза (anons, ongoing, released)
 * @property episodes общее количество эпизодов
 * @property episodesAired количество вышедших эпизодов
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 */
data class AnimeModel(
    val id: Long? = null,
    val name: String? = null,
    val nameRu: String? = null,
    val image: ImageModel? = null,
    val url: String? = null,
    val type: AnimeType? = null,
    val score : Double? = null,
    val status: AiredStatus? = null,
    val episodes: Int? = null,
    val episodesAired: Int? = null,
    val dateAired: String? = null,
    val dateReleased: String? = null
)

data class AnimeJsonModel(
    val id: Long? = null,
    val name: String? = null,
    val russian: String? = null,
    val image: ImageJsonModel? = null,
    val url: String? = null,
    val kind: String? = null,
    val score : Double? = null,
    val status: String? = null,
    val episodes: Int? = null,
    val episodes_aired: Int? = null,
    val aired_on: String? = null,
    val released_on: String? = null
)