package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.LinkedContentResponse

/**
 * Сущность с информацией о пользовательской истории действий
 *
 * @property id номера списка действий
 * @property dateCreated дата совершённого действия
 * @property description описание совершённого действия
 * @property target изменённый контент
 */
data class UserHistoryResponse(
    @SerializedName("id")
    val id : Long?,
    @SerializedName("created_at")
    val dateCreated : String?,
    @SerializedName("description")
    val description : String?,
    @SerializedName("target")
    val target : LinkedContentResponse?
)
