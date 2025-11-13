package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.common.FranchiseResponse
import com.shikidroid.data.network.entity.common.LinkResponse
import com.shikidroid.data.network.entity.common.RelatedResponse
import com.shikidroid.data.network.entity.common.RolesResponse
import com.shikidroid.data.network.entity.manga.MangaDetailsResponse
import com.shikidroid.data.network.entity.manga.MangaResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс API для получения данных о ранобэ
 */
internal interface RanobeApi {

    /**
     * Получение списка ранобэ по указанным параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, volumes, chapters, status, random, created_at, created_at_desc) (необязательно)
     * @param status тип релиза (anons, ongoing, released, paused, discontinued) (необязательно)
     * @param season сезон выхода манги (summer_2017, spring_2016,fall_2016, 2016,!winter_2016, 2016, 2014_2016, 199x) (необязательно)
     * @param score минимальная оценка манги (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param publisher список и издателями манги
     * @param franchise список с названиями франшиз манги (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param ids список id номеров манги (необязательно)
     * @param excludeIds список id номеров манги (необязательно)
     * @param search поисковая фраза для фильтрации манги по имени (name) (необязательно)
     */
    @GET("/api/ranobe")
    fun getRanobeListByParameters(
        @Query("page")
        page: Int?,
        @Query("limit")
        limit: Int?,
        @Query("order")
        order: String?,
        @Query("status")
        status: String?,
        @Query("season")
        season: String?,
        @Query("score")
        score: Double?,
        @Query("genre")
        genre: String?,
        @Query("publisher")
        publisher: List<String>?,
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
    ): Single<List<MangaResponse>>

    /**
     * Получить информацию о ранобэ по ID
     *
     * @param id id номер ранобэ
     */
    @GET("/api/ranobe/{id}")
    fun getRanobeDetailsById(@Path("id") id: Long): Single<MangaDetailsResponse>

    /**
     * Получить информацию о людях, принимавших участие в создании ранобэ по id
     *
     * @param id id ранобэ
     */
    @GET("/api/ranobe/{id}/roles")
    fun getRanobeRolesById(@Path("id") id: Long): Single<List<RolesResponse>>

    /**
     * Получить список похожих ранобэ по ID
     *
     * @param id id ранобэ
     */
    @GET("/api/ranobe/{id}/similar")
    fun getSimilarRanobe(@Path("id") id: Long): Single<List<MangaResponse>>

    /**
     * Получить список ранобэ, связанным с текущим
     *
     * @param id id ранобэ
     */
    @GET("/api/ranobe/{id}/related")
    fun getRelatedRanobe(@Path("id") id: Long): Single<List<RelatedResponse>>

    /**
     * Получить список франшизы
     *
     * @param id id ранобэ
     */
    @GET("/api/ranobe/{id}/franchise")
    fun getRanobeFranchise(@Path("id") id: Long): Single<FranchiseResponse>

    /**
     * Получить внешние ссылки на произведение
     *
     * @param id id ранобэ
     */
    @GET("/api/ranobe/{id}/external_links")
    fun getRanobeExternalLinksById(@Path("id") id: Long): Single<List<LinkResponse>>
}