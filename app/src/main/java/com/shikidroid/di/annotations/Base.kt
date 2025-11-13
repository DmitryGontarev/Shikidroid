package com.shikidroid.di.annotations

import javax.inject.Qualifier

/**
 * Аннотация используется для OkHttp и Retrofit,
 * которые используются во всех стандартных вызовах серверов Shikimori
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Base
