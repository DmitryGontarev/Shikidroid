package com.shikidroid.data.network.entity.anime

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.GenreResponse
import com.shikidroid.data.network.entity.common.ImageResponse
import com.shikidroid.data.network.entity.rates.UserRateResponse
import com.shikidroid.data.network.entity.studio.StudioResponse
import com.shikidroid.data.network.entity.user.StatisticResponse

/**
 * Сущность с детальной информацией по выбранному аниме
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
data class AnimeDetailsResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("russian") val nameRu: String?,
    @SerializedName("image") val image: ImageResponse?,
    @SerializedName("url") val url: String?,
    @SerializedName("kind") val type: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("status") val status: String?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("episodes_aired") val episodesAired: Int?,
    @SerializedName("aired_on") val dateAired: String?,
    @SerializedName("released_on") val dateReleased: String?,
    @SerializedName("rating") val ageRating: String?,
    @SerializedName("english") val namesEnglish: List<String?>?,
    @SerializedName("japanese") val namesJapanese: List<String?>?,
    @SerializedName("synonyms") val synonyms: List<String>?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("description_html") val descriptionHtml: String?,
    @SerializedName("description_source") val descriptionSource: String?,
    @SerializedName("franchise") val franchise: String?,
    @SerializedName("favoured") val favoured: Boolean?,
    @SerializedName("anons") val anons: Boolean?,
    @SerializedName("ongoing") val ongoing: Boolean?,
    @SerializedName("thread_id") val threadId: Long?,
    @SerializedName("topic_id") val topicId: Long?,
    @SerializedName("myanimelist_id") val myAnimeListId: Long?,
    @SerializedName("rates_scores_stats") val rateScoresStats: List<StatisticResponse>?,
    @SerializedName("rates_statuses_stats") val rateStatusesStats: List<StatisticResponse>?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("next_episode_at") val nextEpisodeDate: String?,
    @SerializedName("genres") val genres: List<GenreResponse>?,
    @SerializedName("studios") val studios: List<StudioResponse>?,
    @SerializedName("videos") val videos: List<AnimeVideoResponse>?,
    @SerializedName("screenshots") val screenshots: List<ScreenshotResponse>?,
    @SerializedName("user_rate") val userRate: UserRateResponse?
)

