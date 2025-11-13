package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.video.ShimoriEpisodeResponse
import com.shikidroid.data.network.entity.video.ShimoriTranslationResponse
import com.shikidroid.data.network.entity.video.VideoResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Интерфейс API для получения видео
 */
internal interface ShimoriVideoApi {

    /**
     * Получить количество вышедших эпизодов
     *
     * @param malId идентификационный номер аниме с сайта MyAnimeList
     * @param name название на английском
     */
    @GET("/api/anime/episodes")
    fun getEpisodes(
        @Query("id") malId: Long,
        @Query("name") name: String
    ) : Single<Int>

    /**
     * Получить информацию по каждому эпизоду
     *
     * @param malId идентификационный номер аниме с сайта MyAnimeList
     * @param name название на английском
     */
    @GET("/api/anime/series")
    fun getSeries(
        @Query("id") malId: Long,
        @Query("name") name: String
    ) : Single<List<ShimoriEpisodeResponse>>

    /**
     * Получить информацию по трансляции конкретноого эпизода
     *
     * @param malId идентификационный номер аниме с сайта MyAnimeList
     * @param name название на английском
     * @param episode номер эпизода
     * @param hostingId идентификационный номер хостинга
     * @param translationType тип трансляции (оригинал, субтитры, озвучка)
     */
    @GET("/api/anime/query")
    fun getTranslations(
        @Query("id") malId: Long,
        @Query("name") name: String,
        @Query("episode") episode: Int,
        @Query("hostingId") hostingId : Int,
        @Query("kind") translationType: String?
    ): Single<List<ShimoriTranslationResponse>>

    /**
     * Получить информацию для потоковой загрузки эпизода
     *
     * @param malId идентификационный номер аниме с сайта MyAnimeList
     * @param episode номер эпизода
     * @param translationType тип трансляции (оригинал, субтитры, озвучка)
     * @param author автор загруженного эпизода
     * @param hosting название хостинга
     * @param hostingId идентификационный номер хостинга
     * @param videoId идентификационный номер видео на хостинге
     * @param url ссылка на видео
     * @param accessToken токен для загрузки, если нужен
     */
    @GET("/api/anime/video")
    fun getVideo(
        @Query("id") malId: Long,
        @Query("episode") episode: Int,
        @Query("kind") translationType: String?,
        @Query("author") author: String?,
        @Query("hosting") hosting : String,
        @Query("hostingId") hostingId : Int,
        @Query("videoId") videoId : Long?,
        @Query("url") url : String?,
        @Query("accessToken") accessToken : String?
    ) : Single<VideoResponse>
}