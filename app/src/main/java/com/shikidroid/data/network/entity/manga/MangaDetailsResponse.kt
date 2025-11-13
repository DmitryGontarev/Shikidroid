package com.shikidroid.data.network.entity.manga

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.GenreResponse
import com.shikidroid.data.network.entity.common.ImageResponse
import com.shikidroid.data.network.entity.rates.UserRateResponse
import com.shikidroid.data.network.entity.user.StatisticResponse

/**
 * Сущность с детальной информацией по выбранной манге
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
 * @property publisher название издателей
 * @property userRate пользовательский рейтинг манги
 */
data class MangaDetailsResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("russian") val nameRu: String?,
    @SerializedName("image") val image: ImageResponse?,
    @SerializedName("url") val url: String?,
    @SerializedName("kind") val type: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("status") val status: String?,
    @SerializedName("volumes") val volumes: Int?,
    @SerializedName("chapters") val chapters: Int?,
    @SerializedName("aired_on") val dateAired: String?,
    @SerializedName("released_on") val dateReleased: String?,
    @SerializedName("english") val namesEnglish: List<String>?,
    @SerializedName("japanese") val namesJapanese: List<String>?,
    @SerializedName("synonyms") val synonyms: List<String>?,
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
    @SerializedName("genres") val genres: List<GenreResponse>?,
    @SerializedName("user_rate") val userRate: UserRateResponse?,
)
