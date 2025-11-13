package com.shikidroid.di

import android.content.Context
import android.os.Bundle
import com.shikidroid.eventprovider.EventProvider
import com.shikidroid.eventprovider.EventProviderImpl
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.sharedprefs.SharedPreferencesProviderImpl
import com.shikidroid.utils.NetworkConnectionObserver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Модуль, предоставляющий общие зависимости
 */
@Module
internal class CommonModule(val context: Context) {

    /**
     * Предоставляет контекст приложения
     */
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    /**
     * Предоставляет [SharedPreferencesProvider]
     */
    @Provides
    @Singleton
    fun provideSharedPrefs(): SharedPreferencesProvider {
        return SharedPreferencesProviderImpl(context = context)
    }

    /**
     * Предоставляет [EventProvider]
     */
    @Provides
    @Singleton
    fun provideEventProvider(): EventProvider<Bundle> {
        return EventProviderImpl<Bundle>()
    }

    /**
     * Предоставляет [NetworkConnectionObserver]
     */
    @Provides
    @Singleton
    fun provideNetworkConnectionObserver(): NetworkConnectionObserver {
        return NetworkConnectionObserver(context = context)
    }
}