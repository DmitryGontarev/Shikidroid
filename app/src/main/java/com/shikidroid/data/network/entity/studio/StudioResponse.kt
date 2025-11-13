package com.shikidroid.data.network.entity.studio

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о студии аниме
 *
 * @property id номер студии
 * @property name название
 * @property nameFiltered
 * @property isReal
 * @property imageUrl ссылка на логотип студии
 */
data class StudioResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("filtered_name")
    val nameFiltered: String?,
    @SerializedName("real")
    val isReal: Boolean?,
    @SerializedName("image")
    val imageUrl: String?
)
