package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацие о количестве непрочитанных сообщений
 *
 * @property messages количесвто непрочитанных сообщений
 * @property news количесвто непрочитанных новостей
 * @property notifications количесвто непрочитанных уведомлений
 */
data class UserUnreadMessagesCountResponse(
    @SerializedName("messages")
    val messages : Int,
    @SerializedName("news")
    val news : Int,
    @SerializedName("notifications")
    val notifications : Int
)
