package com.shikidroid.uikit.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Наблюдаемый объект с учётом жизненного цикла,
 * который отправляет только новые данные подписчику после подписки
 *
 */
class SingleLiveEvent<T>() : MutableLiveData<T>() {

    private val mPending: AtomicBoolean = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    /**
     * Утсановить новое значение на главном потоке
     *
     * @param t значение
     */
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Установить новое значение на любом потоке
     *
     * @param t значение
     */
    @MainThread
    override fun postValue(t: T?) {
        mPending.set(true)
        super.postValue(value)
    }

    /**
     * Вызвать событие с null
     *
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {

        const val TAG = "SingleLiveEvent"
    }
}