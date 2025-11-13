package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность со статистикой
 *
 * @property name название
 * @property value значение
 */
data class StatisticResponse(
    @SerializedName("name") val name : String?,
    @SerializedName("value") val value : Int?
)