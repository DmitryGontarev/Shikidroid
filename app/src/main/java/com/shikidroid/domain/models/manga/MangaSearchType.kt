package com.shikidroid.domain.models.manga

/**
 * Параметры, по которым нужно получить список манги
 */
enum class MangaSearchType {

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
     * по количеству томов
     */
    VOLUMES,

    /**
     * по количеству глав
     */
    CHAPTERS,

    /**
     * по статусу выхода (anons, ongoing, released, paused, discontinued)
     */
    STATUS,

    /**
     * рандомно
     */
    RANDOM
}