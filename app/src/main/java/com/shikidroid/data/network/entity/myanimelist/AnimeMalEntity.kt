package com.shikidroid.data.network.entity.myanimelist

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.GenreResponse
import com.shikidroid.data.network.entity.studio.StudioResponse

/**
 * Сущность с данным списка узлов аниме
 *
 * [data] список с узлами списка
 */
data class DataMalEntity(
    @SerializedName("data") val data: List<NodeMalEntity>
)

/**
 * Сущность узла списка с информацией об аниме
 *
 * [anime] сущность данных аниме
 */
data class NodeMalEntity(
    @SerializedName("node") val anime: AnimeMalEntity
)

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
data class AnimeMalEntity(
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("synopsis") val synopsys: String,
    @SerializedName("main_picture") val image: MainPictureEntity?,
    @SerializedName("alternative_titles") val alternativeTitles: AlternativeTitlesEntity?,
    @SerializedName("start_date") val dateAired: String?,
    @SerializedName("end_date") val dateReleased: String?,
    @SerializedName("genres") val genres: List<GenreResponse>?,
    @SerializedName("media_type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("num_episodes") val episodes: Int?,
    @SerializedName("broadcast") val broadcast: BroadcastEntity?,
    @SerializedName("average_episode_duration") val episodeDuration: Int?,
    @SerializedName("rating") val ageRating: String?,
    @SerializedName("pictures") val pictures: List<MainPictureEntity>?,
    @SerializedName("background") val backgroundInfo: String?,
    @SerializedName("related_anime") val relatedAnime: List<RelatedAnimeEntity>?,
    @SerializedName("recommendations") val recommendations: List<NodeMalEntity>?,
    @SerializedName("studios") val studios: List<StudioResponse>?,
    @SerializedName("mean") val score: Double?
)

/**
 * Сущность с картинкой аниме
 *
 * @property medium средняя картинка
 * @property large большая картинка
 */
data class MainPictureEntity(
    @SerializedName("medium") val medium: String?,
    @SerializedName("large") val large: String?
)

/**
 * Сущность с альтернативными названиями
 *
 * @property synonyms список синонимов
 * @property en название на английском
 * @property ja название на японском
 */
data class AlternativeTitlesEntity(
    @SerializedName("synonyms") val synonyms: List<String>?,
    @SerializedName("en") val en: String?,
    @SerializedName("ja") val ja: String?
)

/**
 * Сущность с данными о времени трансляции
 *
 * @property dayOfTheWeek день недель трансляции
 * @property startTime время трансляции (Японское время)
 */
data class BroadcastEntity(
    @SerializedName("day_of_the_week") val dayOfTheWeek: String?,
    @SerializedName("start_time") val startTime: String?
)

/**
 * Сущность с информацией о связанном аниме
 *
 * @property anime данные аниме
 * @property relationType тип связи
 * @property relationTypeFormatted готовая строка типа связи для показа на экране
 */
data class RelatedAnimeEntity(
    @SerializedName("node") val anime: AnimeMalEntity?,
    @SerializedName("relation_type") val relationType: String?,
    @SerializedName("relation_type_formatted") val relationTypeFormatted: String?
)