package com.shikidroid.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shikidroid.BuildConfig
import com.shikidroid.appconstants.BaseUrl
import com.shikidroid.appconstants.NetworkConstants
import com.shikidroid.data.network.TokenInterceptor
import com.shikidroid.data.network.UserAgentInterceptor
import com.shikidroid.data.network.repository.MalAgentInterceptor
import com.shikidroid.di.annotations.Base
import com.shikidroid.di.annotations.Mal
import com.shikidroid.di.annotations.Video
import com.shikidroid.domain.repository.TokenLocalRepository
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger модуль для Retrofit
 */
@Module
internal class RetrofitModule {

    /** Предоставить [Gson] */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    /** Предоставить [HttpLoggingInterceptor] */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        } else {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /** Предоставить [UserAgentInterceptor] */
    @Provides
    @Singleton
    fun provideUserAgentInterceptor(): UserAgentInterceptor {
        return UserAgentInterceptor()
    }

    /** Предоставить [TokenInterceptor] */
    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenLocalRepository: TokenLocalRepository): TokenInterceptor {
        return TokenInterceptor(tokenLocalRepository)
    }

    ///////////////////////////////////////////////////////////////////////////
    // OkHttp и Retrofit для всего приложения и авторизации
    ///////////////////////////////////////////////////////////////////////////
    /** Предоставить [OkHttpClient] */
    @Provides
    @Singleton
    @Base
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(tokenInterceptor)
            .connectTimeout(NetworkConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /** Предоставить [Retrofit] */
    @Provides
    @Singleton
    @Base
    fun provideRetrofit(
        @Base authOkHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseUrl.SHIKIMORI_BASE_URL)
            .client(authOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // OkHttp и Retrofit для видео
    ///////////////////////////////////////////////////////////////////////////
    /** Предоставить [OkHttpClient] для загрузки видео */
    @Provides
    @Singleton
    @Video
    fun provideVideoOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(userAgentInterceptor)
            .connectTimeout(NetworkConstants.LONG_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.LONG_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /** Предоставить [Retrofit] для загрузки видео */
    @Provides
    @Singleton
    @Video
    fun provideVideoRetrofit(
        @Video videoOkHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseUrl.SHIMORI_VIDEO_URL)
            .client(videoOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // OkHttp и Retrofit для MyAnimeList
    ///////////////////////////////////////////////////////////////////////////
    /** Предоставить [MalAgentInterceptor] */
    @Provides
    @Singleton
    fun provideMalAgentInterceptor(): MalAgentInterceptor {
        return MalAgentInterceptor()
    }

    /** Предоставить [OkHttpClient] для MyAnimeList */
    @Provides
    @Singleton
    @Mal
    fun provideMalOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        malAgentInterceptor: MalAgentInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(malAgentInterceptor)
            .connectTimeout(NetworkConstants.LONG_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.LONG_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /** Предоставить [Retrofit] для MyAnimeList */
    @Provides
    @Singleton
    @Mal
    fun provideMalRetrofit(
        @Mal malOkHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseUrl.MY_ANIME_LIST_BASE_URL)
            .client(malOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }
}