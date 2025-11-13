package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName

/**
 * Сущность со ссылками на ресурс произведения
 *
 * @property id id ссылки
 * @property name название ресурса (например myanimelist, wikipedia)
 * @property url ссылка на ресурс
 */
data class LinkResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("kind")
    val name: String?,
    @SerializedName("url")
    val url: String
)
