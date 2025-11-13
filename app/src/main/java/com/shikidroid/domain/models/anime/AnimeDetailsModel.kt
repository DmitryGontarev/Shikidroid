package com.shikidroid.domain.models.anime

import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.common.ImageModel
import com.shikidroid.domain.models.rates.UserRateModel
import com.shikidroid.domain.models.studio.StudioModel
import com.shikidroid.domain.models.user.StatisticModel

/**
 * Модель domain слоя с детальной информацие об аниме
 *
 * @property id id номер аниме
 * @property name название аниме
 * @property nameRu название аниме на русском
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
data class AnimeDetailsModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?,
    val type: AnimeType?,
    val score: Double?,
    val status: AiredStatus?,
    val episodes: Int?,
    val episodesAired: Int?,
    val dateAired: String?,
    val dateReleased: String?,
    val ageRating: AgeRatingType?,
    val namesEnglish: List<String?>?,
    val namesJapanese: List<String?>?,
    val synonyms: List<String>?,
    val duration: Int?,
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
    val updatedAt: String?,
    val nextEpisodeDate: String?,
    val genres: List<GenreModel>?,
    val studios: List<StudioModel>?,
    val videos: List<AnimeVideoModel>?,
    val screenshots: List<ScreenshotModel>?,
    val userRate: UserRateModel?
)
