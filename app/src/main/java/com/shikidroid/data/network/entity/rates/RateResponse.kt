package com.shikidroid.data.network.entity.rates

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.manga.MangaResponse
import com.shikidroid.data.network.entity.user.UserBriefResponse

/**
 * Сущность элемента пользовательского списка рейтинга аниме
 *
 * @property id id элемента списка
 * @property score оценка пользователя
 * @property status статус чтения или просмотра
 * @property text комментарий
 * @property episodes количество просмотренных эпизодов аниме
 * @property chapters количество прочитанных глав манги
 * @property volumes количество прочитанных томов
 * @property textHtml
 * @property rewatches количество повторных просмотров
 * @property createdDateTime дата добавления в пользовательский список
 * @property updatedDateTime дата обновления в пользовательском списке
 * @property user информация о пользователе
 * @property anime информация о аниме
 * @property manga информация о манге
 */
data class RateResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("score")
    val score: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("text_html")
    val textHtml: String?,
    @SerializedName("rewatches")
    val rewatches: Int?,
    @SerializedName("created_at")
    val createdDateTime : String?,
    @SerializedName("updated_at")
    val updatedDateTime : String?,
    @SerializedName("user")
    val user: UserBriefResponse?,
    @SerializedName("anime")
    val anime: AnimeResponse?,
    @SerializedName("manga")
    val manga: MangaResponse?
)