package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.common.ImageResponse
import com.shikidroid.data.network.entity.manga.MangaResponse

/**
 * Сущность с детельной информацией о персонаже
 *
 * @property id номер персонажа
 * @property name имя персонажа
 * @property nameRu имя персонажа на русском
 * @property image ссылки на картинки с персонажем
 * @property url ссылка на персонажа
 * @property nameAlt альтернативное имя
 * @property nameJp имя на японском
 * @property description описание
 * @property descriptionSource источник описания
 * @property favoured флаг есть ли в списке избранного
 * @property threadId идентификацонный номер треда
 * @property topicId идентификацонный номер топика
 * @property dateUpdated дата обновления
 * @property seyu акётр озвучивания
 * @property animes список аниме с персонажем
 * @property mangas список манги с персонажем
 */
data class CharacterDetailsResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("russian")
    val nameRu: String?,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("altname")
    val nameAlt: String?,
    @SerializedName("japanese")
    val nameJp: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("description_html")
    val descriptionHtml: String?,
    @SerializedName("description_source")
    val descriptionSource: String?,
    @SerializedName("favoured")
    val favoured: Boolean?,
    @SerializedName("thread_id")
    val threadId: Long?,
    @SerializedName("topic_id")
    val topicId: Long?,
    @SerializedName("updated_at")
    val dateUpdated: String?,
    @SerializedName("seyu")
    val seyu: List<PersonResponse>?,
    @SerializedName("animes")
    val animes: List<AnimeResponse>?,
    @SerializedName("mangas")
    val mangas: List<MangaResponse>?
)
