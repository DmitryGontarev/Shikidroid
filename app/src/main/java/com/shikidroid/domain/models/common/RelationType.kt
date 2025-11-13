package com.shikidroid.domain.models.common

/**
 * Тип связи между произведениями
 */
enum class RelationType {

    /**
     * Адаптация
     */
    ADAPTATION,

    /**
     * Сиквел (вторая часть)
     */
    SEQUEL,

    /**
     * Приквел (предистория)
     */
    PREQUEL,

    /**
     * Короткий пересказ
     */
    SUMMARY,

    /**
     * Полная история
     */
    FULL_STORY,

    /**
     * Стороння история
     */
    SIDE_STORY,

    /**
     * Основная история
     */
    PARENT_STORY,

    /**
     * Альтернативный сеттинг
     */
    ALTERNATIVE_SETTING,

    /**
     * Альтернативная версия
     */
    ALTERNATIVE_VERSION,

    /**
     * Другое
     */
    OTHER,

    /**
     * Нет информации
     */
    NONE,

    /**
     * неизвестный тип (если придёт что-то не из списка)
     */
    UNKNOWN
}