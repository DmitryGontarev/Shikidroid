package com.shikidroid.di

import com.google.gson.Gson
import com.shikidroid.di.getinterface.GetInteractors
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.utils.NetworkConnectionObserver
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger компонент приложения для предоставления зависимостей
 */
@Singleton
@Component(modules = [
    ApiModule::class,
    RetrofitModule::class,
    RepositoryModule::class,
    InteractorsModule::class,
    CommonModule::class,
])
internal interface AppComponent: GetInteractors {

    /**
     * Получить хранилище [SharedPreferencesProvider]
     */
    fun getSharedPreferencesProvider(): SharedPreferencesProvider

    /** Получить [Gson] */
    fun getGson(): Gson

    /** Получить [NetworkConnectionObserver] */
    fun getNetworkConnectionObserver(): NetworkConnectionObserver
}