package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность с информацией о персонаже
 *
 * @property id номер персонажа
 * @property name имя персонажа
 * @property nameRu имя персонажа на русском
 * @property image ссылки на картинки с персонажем
 * @property url ссылка на персонажа
 */
data class CharacterResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String,
    @SerializedName("russian") val nameRu: String?,
    @SerializedName("image") val image: ImageResponse?,
    @SerializedName("url") val url: String?
)
