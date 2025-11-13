package com.shikidroid.domain.models.anime

/**
 * Тип аниме
 */
enum class AnimeType {

    /**
     * ТВ-сериал
     */
    TV,

    /**
     * Полнометражный фильм
     */
    MOVIE,

    /**
     * Специальная серия (спэшл)
     */
    SPECIAL,

    /**
     * Музыкальное видео
     */
    MUSIC,

    /**
     * Original Video Animation
     */
    OVA,

    /**
     * Original Net Animation
     */
    ONA,

    /**
     * 13-серийное аниме
     */
    TV_13,

    /**
     * 24-серийное аниме
     */
    TV_24,

    /**
     * 48-серийное аниме
     */
    TV_48,

    /**
     * Пока нет типа
     */
    NONE,

    /**
     * Неизвестный тип (если придёт что-то не из списка)
     */
    UNKNOWN
}