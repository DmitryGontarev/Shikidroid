package com.shikidroid.data.network.entity.anime

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность с информацией об аниме
 *
 * @property id id номер аниме
 * @property name название аниме
 * @property nameRu название аниме на русском
 * @property image ссылка на постер аниме
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48)
 * @property score оцена аниме по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property episodes общее количество эпизодов
 * @property episodesAired количество вышедших эпизодов
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 */
data class AnimeResponse(
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
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("episodes_aired")
    val episodesAired: Int?,
    @SerializedName("aired_on")
    val dateAired: String?,
    @SerializedName("released_on")
    val dateReleased: String?
)