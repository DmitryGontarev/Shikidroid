package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность с информацией о человеке, принимавшем участиве в создании аниме
 *
 * @property id номер
 * @property name имя
 * @property nameRu имя на русском
 * @property image ссылки на фото
 * @property url ссылка
 */
data class PersonResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("russian")
    val nameRu: String?,
    @SerializedName("image")
    val image: ImageResponse?,
    @SerializedName("url")
    val url: String?
)
