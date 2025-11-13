package com.shikidroid.asynchelper

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Реализация [AsyncHelper]
 *
 * Для добавления подписки используется [withDisposable], для очистки [clearDisposables]
 */
class AsyncHelperImpl : AsyncHelper {

    private val disposable = CompositeDisposable()

    override fun withDisposable(disposable: Disposable?): Disposable? {
        return disposable?.also { d -> this.disposable.add(d) }
    }

    override fun clearDisposables() {
        disposable.clear()
    }
}