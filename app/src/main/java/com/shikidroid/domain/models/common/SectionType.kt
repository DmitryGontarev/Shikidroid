package com.shikidroid.domain.models.common

/**
 * Типы разделов
 */
enum class SectionType {

    /**
     * Аниме
     */
    ANIME,

    /**
     * Манга
     */
    MANGA,

    /**
     * Ранобэ
     */
    RANOBE,

    /**
     * Персонаж
     */
    CHARACTER,

    /**
     * Реальный человек (автор, режиссёр, мангака, сценарит)
     */
    PERSON,

    /**
     * Пользователь
     */
    USER,

    /**
     * Клуб
     */
    CLUB,

    /**
     * Страница клуба
     */
    CLUB_PAGE,

    /**
     * Коллекция
     */
    COLLECTION,

    /**
     * Рецензия
     */
    REVIEW,

    /**
     * Косплэй
     */
    COSPLAY,

    /**
     * Конкурс
     */
    CONTEST,

    // Those last used only in App

    /**
     * Топик обсуждения
     */
    TOPIC,

    /**
     * Комментарии
     */
    COMMENT,

    /**
     * Неизвестный тип (если придёт что-то не из списка)
     */
    UNKNOWN
}