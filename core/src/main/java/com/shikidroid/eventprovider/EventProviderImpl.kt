package com.shikidroid.eventprovider

import androidx.annotation.CallSuper
import java.util.concurrent.ConcurrentHashMap

/**
 * Реализация шины данных для общения вью моделей
 * Представляет собой классическую реализацию паттерна observer
 * Все подписчики добавляются в мапу по классу, в котором производится подписка
 * Во время отписки отписываются все подписчики класса
 */
open class EventProviderImpl<E> : EventProvider<E> {

    /**
     * используется ConcurrentHashMap вместо HashMap
     * так как вызовы EventProvider могут осуществляться из разных потоков
     */
    private val subscribers = ConcurrentHashMap<Any, MutableList<(event: E) -> Unit>>()

    override fun send(event: E) {
        subscribers.values.forEach { subscribers ->
            subscribers.forEach { it.invoke(event) }
        }
    }

    override fun subscribe(owner: Any, subscriber: (event: E) -> Unit) {
        (subscribers[owner] ?: ArrayList<(event: E) -> Unit>().also { subscribers[owner] = it }).run {
            if (!contains(subscriber)) {
                add(subscriber)
            }
        }
    }

    @CallSuper
    override fun unSubscribe(owner: Any) {
        subscribers.remove(owner)
    }

    @CallSuper
    override fun clear() {
        subscribers.clear()
    }
}