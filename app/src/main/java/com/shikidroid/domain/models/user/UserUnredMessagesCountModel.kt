package com.shikidroid.domain.models.user

/**
 * Модель с информацией о количестве непрочитанных сообщений
 *
 * @property messages количесвто непрочитанных сообщений
 * @property news количесвто непрочитанных новостей
 * @property notifications количество непрочитанных уведомлений
 */
data class UserUnreadMessagesCountModel(
    val messages : Int,
    val news : Int,
    val notifications : Int
)
