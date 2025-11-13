package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.myanimelist.AnimeMalEntity
import com.shikidroid.data.network.entity.myanimelist.DataMalEntity
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Api для получения информации с MyAnimeList
 */
interface MyAnimeListApi {

    /**
     * Получение списка аниме через Поиск
     *
     * @param search название аниме
     * @param limit количество аниме для показа (максимум 100)
     */
    @GET("/v2/anime")
    fun getAnimeListByParameters(
        @Query("q")
        search: String?,
        @Query("limit")
        limit: Int?,
        @Query("offset")
        offset: Int?,
        @Query("fields")
        fields: String?,
        @Query("nsfw")
        nsfw: Boolean
    ): Single<DataMalEntity>

    /**
     * Получение списка аниме по параметру ранжирования
     *
     * @param rankingType тип ранжирования аниме
     * @param limit количество аниме для показа (максимум 500)
     */
    @GET("/v2/anime/ranking")
    fun getAnimeRankingList(
        @Query("ranking_type")
        rankingType: String?,
        @Query("limit")
        limit: Int?,
        @Query("offset")
        offset: Int?,
        @Query("fields")
        fields: String?,
        @Query("nsfw")
        nsfw: Boolean
    ): Single<DataMalEntity>

    /**
     * Получить информацию об аниме по ID
     *
     * @param id id номер аниме
     */
    @GET("/v2/anime/{id}")
    fun getAnimeDetailsById(
        @Path("id")
        id: Long?,
        @Query("fields")
        fields: String?
    ): Single<AnimeMalEntity>
}