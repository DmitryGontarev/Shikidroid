package com.shikidroid.domain.models.manga

/**
 * Тип манги
 */
enum class MangaType {

    /**
     * Японская манга
     */
    MANGA,

    /**
     * Корейская манхва
     */
    MANHWA,

    /**
     * Китайская маньхуа
     */
    MANHUA,

    /**
     * Новелла
     */
    LIGHT_NOVEL,

    /**
     * Новелла
     */
    NOVEL,

    /**
     * Ваншот
     */
    ONE_SHOT,

    /**
     * Додзинси
     */
    DOUJIN,

    /**
     * Неизвестный тип (если придёт что-то не из списка)
     */
    UNKNOWN
}