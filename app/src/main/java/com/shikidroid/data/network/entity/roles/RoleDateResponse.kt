package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о дате, связанной с человеком
 *
 * @property day день
 * @property year год
 * @property month месяц
 */
data class RoleDateResponse(
    @SerializedName("day")
    val day: Long?,
    @SerializedName("year")
    val year: Long?,
    @SerializedName("month")
    val month: Long?
)