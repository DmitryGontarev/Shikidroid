package com.shikidroid.di

import com.shikidroid.data.network.api.*
import com.shikidroid.di.annotations.Base
import com.shikidroid.di.annotations.Mal
import com.shikidroid.di.annotations.Video
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/**
 * Модуль, предоставляющий зависимости для запросов Api
 */
@Module
internal class ApiModule {

    /**
     * Предоставить Api для авторизации
     */
    @Singleton
    @Provides
    fun provideAuthApi(@Base retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    /**
     * Предоставить Api для получения информации профиля пользователя
     */
    @Singleton
    @Provides
    fun provideUserApi(@Base retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    /**
     * Предоставить Api для получения информации об аниме
     */
    @Singleton
    @Provides
    fun provideAnimeApi(@Base retrofit: Retrofit): AnimeApi {
        return retrofit.create(AnimeApi::class.java)
    }

    /**
     * Предоставить Api для получения информации о манге
     */
    @Singleton
    @Provides
    fun provideMangaApi(@Base retrofit: Retrofit): MangaApi {
        return retrofit.create(MangaApi::class.java)
    }

    /**
     * Предоставить Api для получения информации о ранобе
     */
    @Singleton
    @Provides
    fun provideRanobeApi(@Base retrofit: Retrofit): RanobeApi {
        return retrofit.create(RanobeApi::class.java)
    }

    /**
     * Предоставить Api для получения информации о персонаже
     */
    @Singleton
    @Provides
    fun provideCharactersApi(@Base retrofit: Retrofit): CharactersApi {
        return retrofit.create(CharactersApi::class.java)
    }

    /**
     * Предоставить Api для получения информации о роли человека
     */
    @Singleton
    @Provides
    fun providePeopleApi(@Base retrofit: Retrofit): PeopleApi {
        return retrofit.create(PeopleApi::class.java)
    }

    /**
     * Предоставить Api для получения видео
     */
    @Singleton
    @Provides
    fun provideShimoriVideoApi(@Video retrofit: Retrofit): ShimoriVideoApi {
        return retrofit.create(ShimoriVideoApi::class.java)
    }

    /**
     * Предоставить Api для получения графика выхода эпизодов
     */
    @Singleton
    @Provides
    fun provideCalendarApi(@Base retrofit: Retrofit): CalendarApi {
        return retrofit.create(CalendarApi::class.java)
    }

    /**
     * Предоставить Api для получения комментариев
     */
    @Singleton
    @Provides
    fun provideCommentsApi(@Base retrofit: Retrofit): CommentsApi {
        return retrofit.create(CommentsApi::class.java)
    }

    /**
     * Предоставить Api для MyAnimeList
     */
    @Singleton
    @Provides
    fun provideMyAnimeListApi(@Mal retrofit: Retrofit): MyAnimeListApi {
        return retrofit.create(MyAnimeListApi::class.java)
    }
}