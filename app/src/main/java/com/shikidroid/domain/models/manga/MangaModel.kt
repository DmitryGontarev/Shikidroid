package com.shikidroid.domain.models.manga

import com.shikidroid.domain.models.common.ImageModel
import com.shikidroid.domain.models.common.AiredStatus

/**
 * Сущность с информацией о манге
 *
 * @property id id номер манги
 * @property name название манги
 * @property nameRu название аниме на русском
 * @property image ссылка на обложку манги
 * @property url ссылка на страницу сайта манги
 * @property type тип манги (manga, manwha, manhua, novel, oneshot, doujin)
 * @property score оцена манги по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property volumes количество томов
 * @property chapters количество глав
 * @property dateAired дата начала выпуска
 * @property dateReleased дата окончания выпуска
 */
data class MangaModel(
    val id: Long? = null,
    val name: String? = null,
    val nameRu: String? = null,
    val image: ImageModel? = null,
    val url: String? = null,
    val type: MangaType? = null,
    val score : Double? = null,
    val status: AiredStatus? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    val dateAired: String? = null,
    val dateReleased: String? = null
)
