package com.shikidroid.domain.models.anime

/**
 * Тип видеоматериалов к аниме
 */
enum class AnimeVideoType {

    /** Опенинг (начальная заставка) */
    OPENING,

    /** Эндинг (конечная заставка) */
    ENDING,

    /** Промо видео для рекламы */
    PROMO,

    /** Коммерческая реклама */
    COMMERCIAL,

    /** Превью эпизода */
    EPISODE_PREVIEW,

    /** Клип вступления или окончания серии */
    OP_ED_CLIP,

    /** Видео о персонаже */
    CHARACTER_TRAILER,

    /** Другое */
    OTHER,

    /** неизвестный тип (если придёт что-то не из списка) */
    UNKNOWN
}