package com.shikidroid.appconstants

/**
 * Константы приложения
 */
object NetworkConstants {

    /**
     * Стандартное время ожидания ответа
     */
    const val DEFAULT_TIMEOUT = 15L

    /**
     * Долгое время ожидания ответа
     */
    const val LONG_TIMEOUT = 30L

    /**
     * Стандартный лимит элементов для отображения для запросов
     */
    const val DEFAULT_LIMIT = 12

    /**
     * Большой лимит элементов для отображения для запросов
     */
    const val BIG_LIMIT = 30

    /**
     * Стандартное значение, если не найдено id пользователя
     */
    const val NO_ID = -1L

    /**
     * Максимальное количество данных от сервера на одной странице
     */
    const val MAX_LIMIT = 5000

    const val EXIT_TIMEOUT = 3L * 1000

    const val DEFAULT_DEBOUNCE_INTERVAL = 300L

    const val BIG_DEBOUNCE_INTERVAL = 750L

    const val TASK_LONG_DELAY = 3500L

    const val MAX_PINNED_RATES = 3

    const val BACKUP_FILE_NAME= "shimori-backup.json"
}