package com.shikidroid.domain.models.rates

/**
 * Тип статуса в котором находится аниме или манга/ранобэ
 */
enum class RateStatus {

    /**
     * Смотрю
     */
    WATCHING,

    /**
     * Запланировано
     */
    PLANNED,

    /**
     * Пересматриваю
     */
    REWATCHING,

    /**
     * Просмотрено
     */
    COMPLETED,

    /**
     * Отложено
     */
    ON_HOLD,

    /**
     * Брошено
     */
    DROPPED,

    /**
     * Неизвестный статус (если придёт что-то не из списка)
     */
    UNKNOWN
}