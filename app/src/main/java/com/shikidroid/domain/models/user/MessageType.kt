package com.shikidroid.domain.models.user

/**
 * Тип сообщений
 */
enum class MessageType {

    /**
     * Входящие
     */
    INBOX,

    /**
     * Личные
     */
    PRIVATE,

    /**
     * Исходящие
     */
    SENT,

    /**
     * Новости
     */
    NEWS,

    /**
     * Уведомления
     */
    NOTIFICATIONS,

    /**
     * Эпизоды
     */
    EPISODE,

    /**
     * Релизы
     */
    RELEASED,

    /**
     * Анонсы
     */
    ANONS,

    /**
     * Онгоинги
     */
    ONGOING,

    /**
     * Неизвестный тип (если придёт что-то не из списка)
     */
    UNKNOWN
}