package com.shikidroid.uikit.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shikidroid.asynchelper.AsyncHelper
import com.shikidroid.asynchelper.AsyncHelperImpl
import com.shikidroid.eventprovider.EventProvider
import com.shikidroid.eventprovider.EventProviderImpl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Базовая вью модель с индикатором загрузки и шиной данных
 *
 * @param eventProvider шина данных
 */
abstract class BaseViewModel(
    open val eventProvider: EventProvider<Bundle> = EventProviderImpl()
) : ViewModel(),
    AsyncHelper by AsyncHelperImpl()
{

    /**
     * Индикатор загрузки
     */
    val isLoading = MutableLiveData<Boolean>(false)

    /**
     * Индикатор загрузки без полной блокировки экрана
     */
    val isLoadingWithoutBlocking = MutableLiveData<Boolean>(false)

    /**
     * Индикатор загрузки элемента
     */
    val isLoadingWithItemProgress = MutableLiveData<Boolean>(false)

    /**
     * Сообщение об ошибке
     */
    val error = SingleLiveEvent<String>()

    /**
     * Флаг показа экрана с ошибкой
     */
    val showErrorScreen = MutableLiveData<Boolean>(false)

    ///////////////////////////////////////
    // Live Data-ы тост сообщения
    //////////////////////////////////////

    /**
     * Live Data флага показа тост сообщения
     */
    val showToast = MutableLiveData<Boolean>(false)

    /**
     * Live Data текста сообщенния тоста
     */
    val toastText = MutableLiveData<String>("")

    /**
     * Показать индикатор загрузки
     */
    fun showLoading() {
        isLoading.postValue(true)
    }

    /**
     * Скрыть идникатор разгрузки
     */
    fun hideLoading() {
        isLoading.postValue(false)
    }

    /**
     * Показать индиакатор загрузки без полной блокировки экрана
     */
    fun showLoadingWithoutBlocking() {
        isLoadingWithoutBlocking.postValue(true)
    }

    /**
     * Скрыть индиакатор загрузки без полной блокировки экрана
     */
    fun hideLoadingWithoutBlocking() {
        isLoadingWithoutBlocking.postValue(false)
    }

    /**
     * Показать индиакатор загрузки элемента
     */
    fun showLoadingWithItemProgress() {
        isLoadingWithItemProgress.postValue(true)
    }

    /**
     * Скрыть индиакатор загрузки элемента
     */
    fun hideLoadingWithItemProgress() {
        isLoadingWithItemProgress.postValue(false)
    }

    /**
     * Показать ошибку
     *
     * @param error текст ошибки
     */
    fun showError(error: String?) {
        hideLoading()
        this.error.postValue(error)
    }

    /**
     * Показать тост сообщение
     */
    fun showToast(message: String?) {
        toastText.value = message.orEmpty()
        showToast.value = true
    }

    /**
     * Сброс параметров тоста
     */
    fun resetToast() {
        showToast.value = false
        toastText.value = ""
    }

    /**
     * Показать экран с ошибкой
     */
    fun showErrorScreen() {
        showErrorScreen.value = true
    }

    /**
     * Скрыть экран с ошибкой
     */
    fun hideErrorScreen() {
        showErrorScreen.value = false
    }

    /**
     * Запускает функцию через заданное время
     *
     * @param millisec время в миллисекундах
     * @param callback функция вызова
     */
    fun delayLambda(millisec: Long = 1000L, callback: () -> Unit) {
        Completable
            .timer(millisec, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback()
            }, {

            }).addToDisposable()
    }

    /**
     * Для Observable
     * выполнение в io потоке, результат отправить в main поток
     */
    fun <T : Any> doAsync() = ObservableTransformer { upstream: Observable<T> ->
        upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Single
     * выполнение в io потоке, результат отправить в main поток
     */
    fun <T : Any> doAsyncSingle() = SingleTransformer { upstream: Single<T> ->
        upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Single
     *
     * выполняет в io потоке, показывает прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun <T : Any> doAsyncSingleWithProgress() = SingleTransformer { upstream: Single<T> ->
        upstream
            .doOnSubscribe { showLoading() }
            .doFinally { hideLoading() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Single
     *
     * выполняет в io потоке, показывает неблокирующий прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun <T : Any> doAsyncSingleWithoutBlocking() = SingleTransformer { upstream: Single<T> ->
        upstream
            .doOnSubscribe { showLoadingWithoutBlocking() }
            .doFinally { hideLoadingWithoutBlocking() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Single
     *
     * выполняет в io потоке, показывает неблокирующий прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun <T : Any> doAsyncSingleWithItemProgress() = SingleTransformer { upstream: Single<T> ->
        upstream
            .doOnSubscribe { showLoadingWithItemProgress() }
            .doFinally { hideLoadingWithItemProgress() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Completable
     * выполнение в io потоке, результат отправить в main поток
     */
    fun doAsyncCompletable() = CompletableTransformer { upstream: Completable ->
        upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Completable
     *
     * выполняет в io потоке, показывает прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun doAsyncCompletableWithProgress() = CompletableTransformer { upstream: Completable ->
        upstream
            .doOnSubscribe { showLoading() }
            .doFinally { hideLoading() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Completable
     *
     * выполняет в io потоке, показывает прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun doAsyncCompletableWithoutBlocking() = CompletableTransformer { upstream: Completable ->
        upstream
            .doOnSubscribe { showLoadingWithoutBlocking() }
            .doFinally { hideLoadingWithoutBlocking() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Observable
     *
     * выполняет в io потоке, показывает прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun <T : Any> doAsyncObservableWithProgress() = ObservableTransformer { upstream: Observable<T> ->
        upstream
            .doOnSubscribe { showLoading() }
            .doFinally { hideLoading() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Для Observable
     *
     * выполняет в io потоке, показывает прогресс
     * после завершения скрывает прогресс,
     * результат отправляет в main поток
     */
    fun <T : Any> doAsyncObservableWithoutBlocking() = ObservableTransformer { upstream: Observable<T> ->
        upstream
            .doOnSubscribe { showLoadingWithoutBlocking() }
            .doFinally { hideLoadingWithoutBlocking() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun onCleared() {
        super.onCleared()
        clearDisposables()
        eventProvider.clear()
    }

    companion object {

        private const val TAG = "BaseViewModel"
    }
}