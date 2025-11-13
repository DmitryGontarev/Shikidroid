package com.shikidroid.data.network.entity.video

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией типа трансляции эипозда аниме (оригинал, субтитры, озвучка)
 *
 * @param id идентификационный номер эпизода
 * @param kind тип трансляции
 * @param targetId идентификационный номер
 * @param episode порядковый номер эпизода
 * @param url ссылка на эпизод
 * @param hosting навзание хостинга
 * @param language язык трансляции
 * @param author автор загрузки
 * @param quality качество видео
 * @param episodesTotal количество эпизодов
 */
data class ShimoriTranslationResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("kind") val kind: String?,
    @SerializedName("targetId") val targetId: Long?,
    @SerializedName("episode") val episode: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("hosting") val hosting: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("quality") val quality: String?,
    @SerializedName("episodesTotal") val episodesTotal: Int?
)
