package com.shikidroid.data.network.entity.manga

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность с информацией о манге
 *
 * @property id id номер манги
 * @property name название манги
 * @property nameRu название аниме на русском
 * @property image ссылка на обложку манги
 * @property url ссылка на страницу сайта манги
 * @property type тип манги (manga, manwha, manhua, novel, oneshot, doujin)
 * @property score оценка манги по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property volumes количество томов
 * @property chapters количество глав
 * @property dateAired дата начала выпуска
 * @property dateReleased дата окончания выпуска
 */
data class MangaResponse(
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
    @SerializedName("kind")
    val type: String?,
    @SerializedName("score")
    val score : Double?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("aired_on")
    val dateAired: String?,
    @SerializedName("released_on")
    val dateReleased: String?
)
