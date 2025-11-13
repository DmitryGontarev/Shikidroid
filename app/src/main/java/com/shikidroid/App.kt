package com.shikidroid

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.CommonModule
import com.shikidroid.di.AppComponent
import com.shikidroid.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

/**
 * Класс приложения, содержит компонент Dagger
 */
internal class App : Application() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Создание компонента Dagger
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Dagger компонент для получения зависимостей
     */
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent =
            DaggerAppComponent
                .builder()
                .commonModule(CommonModule(context = this))
                .build()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Переключение темы
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Включена ли тёмная тема
     */
    var isDarkTheme = mutableStateOf(
        true
//        value = appComponent.getSharedPreferencesProvider().getBoolean(
//            key = AppExtras.IS_DARK_THEME,
//            default = false
//        )
    )

    /** Флаг включения отображения без границ системных компонентов */
    var isEdgeToEdge = mutableStateOf(
        true
    )

    /** Флаг скрытия системных UI компонентов */
    var isSystemBarsHide = mutableStateOf(
        false
    )

    /** Переключить тему и сохранить значение */
    fun switchTheme() {
        if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            isDarkTheme.value = true
        } else {
            isDarkTheme.value = !isDarkTheme.value
        }
//        appComponent.getSharedPreferencesProvider().putBoolean(
//            key = AppExtras.IS_DARK_THEME,
//            boolean = !isDarkTheme.value
//        )
    }

    private fun periodicWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val messageWorker =
            PeriodicWorkRequestBuilder<NotificationWorker>(
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
            )
                .setConstraints(constraints)
                .addTag(AppKeys.MESSAGE_NOTIFICATION)
                .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                AppKeys.MESSAGE_NOTIFICATION,
                ExistingPeriodicWorkPolicy.KEEP,
                messageWorker
            )
    }
}

/** Получить Dagger компонент приложения из контекста */
internal val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }

/** Получить класс [App] из контекста */
internal val Context.getApp: App
    get() = when (this) {
        is App -> this
        else -> this.applicationContext.getApp
    }