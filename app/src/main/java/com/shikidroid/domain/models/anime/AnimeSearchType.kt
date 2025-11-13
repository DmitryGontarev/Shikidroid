package com.shikidroid.domain.models.anime

/**
 * Параметры, по которым нужно получить список аниме
 */
enum class AnimeSearchType {

    /**
     * по id
     */
    ID,

    /**
     * по убыванию id
     */
    ID_DESC,

    /**
     * ранжировать
     */
    RANKED,

    /**
     * по типу (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48)
     */
    KIND,

    /**
     * по популярности
     */
    POPULARITY,

    /**
     * по порядку алфавита
     */
    NAME,

    /**
     * по дате релиза
     */
    AIRED_ON,

    /**
     * по количеству эпизодов
     */
    EPISODES,

    /**
     * по статусу выхода (anons, ongoing, released)
     */
    STATUS,

    /**
     * рандомно
     */
    RANDOM
}

/**
 * Параметры длительности аниме
 */
enum class AnimeDurationType {

    /**
     * меньше 10 минут
     */
    S,

    /**
     * меньше 30 минут
     */
    D,

    /**
     * больше 30 минут
     */
    F
}