package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.anime.AnimeDetailsResponse
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.anime.AnimeVideoResponse
import com.shikidroid.data.network.entity.anime.ScreenshotResponse
import com.shikidroid.data.network.entity.common.FranchiseResponse
import com.shikidroid.data.network.entity.common.LinkResponse
import com.shikidroid.data.network.entity.common.RelatedResponse
import com.shikidroid.data.network.entity.common.RolesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс API для получения данных об аниме
 */
internal interface AnimeApi {

    /**
     * Получение списка аниме по указанным параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, episodes, status, random) (необязательно)
     * @param kind тип аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48) (необязательно)
     * @param status тип релиза (anons, ongoing, released) (необязательно)
     * @param season сезон выхода аниме (summer_2017, 2016, 2014_2016) (необязательно)
     * @param score оценка аниме (необязательно)
     * @param duration длительность аниме (S, D, F) (необязательно)
     * @param rating возрастной рейтинг (none, g, pg, pg_13, r, r_plus, rx) (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param studio список студий, работавших над аниме
     * @param franchise список с названиями франшиз аниме (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус аниме в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param ids список id номеров аниме (необязательно)
     * @param excludeIds список id номеров аниме (необязательно)
     * @param search поисковая фраза для фильтрации аниме по имени (name) (необязательно)
     */
    @GET("/api/animes")
    fun getAnimeListByParameters(
        @Query("page")
        page: Int?,
        @Query("limit")
        limit: Int?,
        @Query("order")
        order: String?,
        @Query("kind")
        kind: String?,
        @Query("status")
        status: String?,
        @Query("season")
        season: String?,
        @Query("score")
        score: Double?,
        @Query("duration")
        duration: String?,
        @Query("rating")
        rating: String?,
        @Query("genre")
        genre: String?,
        @Query("studio")
        studio: String?,
        @Query("franchise")
        franchise: List<String>?,
        @Query("censored")
        censored: Boolean?,
        @Query("mylist")
        myList: String?,
        @Query("ids")
        ids: List<String>?,
        @Query("exclude_ids")
        excludeIds: List<String>?,
        @Query("search")
        search: String?
    ): Single<MutableList<AnimeResponse>>

    /**
     * Получить информацию об аниме по ID
     *
     * @param id id номер аниме
     */
    @GET("/api/animes/{id}")
    fun getAnimeDetailsById(@Path("id") id: Long): Single<AnimeDetailsResponse>

    /**
     * Получить информацию о людях, принимавших участие в создании аниме по id
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/roles")
    fun getAnimeRolesById(@Path("id") id: Long): Single<List<RolesResponse>>

    /**
     * Получить список похожих аниме по ID
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/similar")
    fun getSimilarAnime(@Path("id") id: Long): Single<List<AnimeResponse>>

    /**
     * Получить список аниме, связанных с текущим
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/related")
    fun getRelatedAnime(@Path("id") id: Long): Single<List<RelatedResponse>>

    /**
     * Получить скриншоты текущего аниме по id
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/screenshots")
    fun getAnimeScreenshotsById(@Path("id") id: Long): Single<List<ScreenshotResponse>>

    /**
     * Получить список франшизы
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/franchise")
    fun getAnimeFranchise(@Path("id") id: Long): Single<FranchiseResponse>

    /**
     * Получить внешние ссылки на произведение
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/external_links")
    fun getAnimeExternalLinksById(@Path("id") id: Long): Single<List<LinkResponse>>

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Videos API
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Получить список видео относящихся к аниме
     *
     * @param id id аниме
     */
    @GET("/api/animes/{id}/videos")
    fun getAnimeVideos(@Path("id") id: Long): Single<List<AnimeVideoResponse>>
}