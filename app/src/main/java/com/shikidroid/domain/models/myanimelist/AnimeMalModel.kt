package com.shikidroid.domain.models.myanimelist

import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.studio.StudioModel


/**
 * Сущность с детальной информацией по выбранному аниме
 *
 * @property id id номер аниме
 * @property title название аниме
 * @property image ссылка на постер аниме
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48)
 * @property score оценка аниме по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property episodes общее количество эпизодов
 * @property episodesAired количество вышедших эпизодов
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 * @property ageRating возрастной рейтинг
 * @property namesEnglish название на английском
 * @property namesJapanese название на японском
 * @property synonyms синонимы
 * @property duration длительность аниме
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
 * @property updatedAt дата обновления
 * @property nextEpisodeDate дата трансляции следующего эпизода
 * @property genres список жанров
 * @property studios список студий, работавших над аниме
 * @property videos список видео
 * @property screenshots список скриншотов из аниме
 * @property userRate пользовательский рейтинг аниме
 */
data class AnimeMalModel(
    val id: Long?,
    val title: String?,
    val synopsys: String?,
    val image: MainPictureModel?,
    val alternativeTitles: AlternativeTitlesModel?,
    val dateAired: String?,
    val dateReleased: String?,
    val genres: List<GenreModel>?,
    val type: AnimeType?,
    val status: AiredStatus?,
    val episodes: Int?,
    val broadcast: BroadcastModel?,
    val episodeDuration: Int?,
    val ageRating: AgeRatingType?,
    val pictures: List<MainPictureModel>?,
    val backgroundInfo: String?,
    val relatedAnime: List<RelatedAnimeModel>?,
    val recommendations: List<AnimeMalModel>?,
    val studios: List<StudioModel>?,
    val score: Double?
)

/**
 * Сущность с картинкой аниме
 *
 * @property medium средняя картинка
 * @property large большая картинка
 */
data class MainPictureModel(
    val medium: String?,
    val large: String?
)

/**
 * Сущность с альтернативными названиями
 *
 * @property synonyms список синонимов
 * @property en название на английском
 * @property ja название на японском
 */
data class AlternativeTitlesModel(
    val synonyms: List<String>?,
    val en: String?,
    val ja: String?
)

/**
 * Сущность с данными о времени трансляции
 *
 * @property dayOfTheWeek день недель трансляции
 * @property startTime время трансляции (Японское время)
 */
data class BroadcastModel(
    val dayOfTheWeek: String?,
    val startTime: String?
)

/**
 * Сущность с информацией о связанном аниме
 *
 * @property anime данные аниме
 * @property relationType тип связи
 * @property relationTypeFormatted готовая строка типа связи для показа на экране
 */
data class RelatedAnimeModel(
    val anime: AnimeMalModel?,
    val relationType: String?,
    val relationTypeFormatted: String?
)