package com.shikidroid.paginator

/**
 * Интерфейс кастомной пагинации
 */
interface Paginator<Key, T> {

    /**
     * Загрузить следующие элементы
     */
    fun loadNextItems()

    /**
     * Сбросить состояние
     */
    fun reset()
}