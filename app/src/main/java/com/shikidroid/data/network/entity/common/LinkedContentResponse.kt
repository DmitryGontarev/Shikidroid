package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName

/**
 * Сущность с прикреплённом контенте об аниме, манге, ранобэ для уведомления в сообщениях
 *
 * @property id id номер аниме
 * @property topicUrl ссылка на топик
 * @property threadId id треда
 * @property topicId id топика
 * @property productType тип произведения (аниме, манга, ранобэ)
 * @property name название аниме
 * @property nameRu название аниме на русском
 * @property image ссылка на постер аниме
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48)
 * @property score оцена аниме по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property episodes общее количество эпизодов
 * @property episodesAired количество вышедших эпизодов
 * @property volumes количество томов
 * @property chapters количество глав
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 */
data class LinkedContentResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("topic_url")
    val topicUrl: String?,
    @SerializedName("thread_id")
    val threadId: Long?,
    @SerializedName("topic_id")
    val topicId: Long?,
    @SerializedName("type")
    val productType: String?,
    @SerializedName("name")
    val name: String,
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
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("aired_on")
    val dateAired: String?,
    @SerializedName("released_on")
    val dateReleased: String?
)
