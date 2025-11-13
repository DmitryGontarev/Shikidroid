package com.shikidroid.di.annotations

import javax.inject.Qualifier

/**
 * Аннотация используется для OkHttp и Retrofit,
 * которые используются для вызовов VideoApi
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Video
