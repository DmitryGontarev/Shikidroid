package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName

/**
 * Сущность с жанрами
 *
 * @property id жанра
 * @property name название жанра
 * @property nameRu название жанра на рускком
 * @property type тип жанра
 */
data class GenreResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("russian") val nameRu: String?,
    @SerializedName("kind") val type: String?
)
