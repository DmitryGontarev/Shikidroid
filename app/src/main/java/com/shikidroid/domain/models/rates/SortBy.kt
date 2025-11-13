package com.shikidroid.domain.models.rates

/**
 * Тип сортировки для списка
 */
enum class SortBy {

    /**
     * По названию
     */
    BY_NAME,

    /**
     * По прогрессу просмотра/чтения
     */
    BY_PROGRESS,

    /**
     * По дате выхода
     */
    BY_RELEASE_DATE,

    /**
     * По дате добавления
     */
    BY_ADD_DATE,

    /**
     * По дате обновления в списке
     */
    BY_REFRESH_DATE,

    /**
     * По оценке
     */
    BY_SCORE,

    /**
     * По количеству эпизодов
     */
    BY_EPISODES,

    /**
     * По количеству глав
     */
    BY_CHAPTERS

}