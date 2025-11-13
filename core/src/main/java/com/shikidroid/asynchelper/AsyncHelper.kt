package com.shikidroid.asynchelper

import io.reactivex.rxjava3.disposables.Disposable

/**
 * Интерфейс для упрощения очистки подписок rx, основное применение в качестве делегата
 */
interface AsyncHelper {

    /**
     * Добавить подписку в список для последующей очистки
     *
     * @param disposable подписка, может быть null
     */
    fun withDisposable(disposable: Disposable?): Disposable?

    /**
     * Добавить подписку в список для последующей очистки
     * @receiver подписка, которую необходимо утилизировать при очистке
     * @return подписка переданная в ресивер
     */
    fun Disposable?.addToDisposable(): Disposable? = withDisposable(this)

    /**
     * Очистить все подписки
     */
    fun clearDisposables()
}