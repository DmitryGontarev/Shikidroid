package com.shikidroid.domain.models.manga

import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.common.ImageModel
import com.shikidroid.domain.models.rates.UserRateModel
import com.shikidroid.domain.models.user.StatisticModel

/**
 * Модель с детальной информацией по выбранной манге
 *
 * @property id id номер манги
 * @property name название манги
 * @property nameRu название манги на русском
 * @property image ссылка на постер манги
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза манги (manga, manhwa, manhua, light_novel, novel, one_shot, doujin)
 * @property score оценка манги по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property volumes количество томов
 * @property chapters количество глав
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 * @property namesEnglish название на английском
 * @property namesJapanese название на японском
 * @property synonyms синонимы
 * @property description описание
 * @property descriptionHtml описание в HTML
 * @property descriptionSource название источника описания
 * @property franchise название франшизы
 * @property favoured в избранном (true or false)
 * @property anons в статусе анонса (true or false)
 * @property ongoing в статусе онгоинга (true or false)
 * @property threadId номер треда
 * @property topicId номер топика
 * @property myAnimeListId номер в списке пользователя, если есть
 * @property rateScoresStats статистика по оценкам
 * @property rateStatusesStats статистика по статусам
 * @property genres список жанров
 * @property userRate пользовательский рейтинг манги
 */
data class MangaDetailsModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?,
    val type: MangaType?,
    val score: Double?,
    val status: AiredStatus?,
    val volumes: Int?,
    val chapters: Int?,
    val dateAired: String?,
    val dateReleased: String?,
    val namesEnglish: List<String>?,
    val namesJapanese: List<String>?,
    val synonyms: List<String>?,
    val description: String?,
    val descriptionHtml: String?,
    val descriptionSource: String?,
    val franchise: String?,
    val favoured: Boolean?,
    val anons: Boolean?,
    val ongoing: Boolean?,
    val threadId: Long?,
    val topicId: Long?,
    val myAnimeListId: Long?,
    val rateScoresStats: List<StatisticModel>?,
    val rateStatusesStats: List<StatisticModel>?,
    val genres: List<GenreModel>?,
    val userRate: UserRateModel?,
)
