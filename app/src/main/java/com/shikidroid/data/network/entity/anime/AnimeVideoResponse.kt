package com.shikidroid.data.network.entity.anime

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о видеоматериалах к аниме
 *
 * @property id номер в списке
 * @property url ссылка на видео
 * @property imageUrl ссылка на картинку превью
 * @property playerUrl ссылка на плеер
 * @property name название видео
 * @property type тип видео
 * @property hosting название видеохостинга
 */
data class AnimeVideoResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("player_url")
    val playerUrl: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("kind")
    val type: String?,
    @SerializedName("hosting")
    val hosting: String?
)
