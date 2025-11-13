package com.shikidroid.domain.repository

import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RankingType
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория для получения информации с MyAnimeList
 */
interface MyAnimeListRepository {
    /**
     * Получение списка аниме через Поиск
     *
     * @param search название аниме
     * @param limit количество аниме для показа (максимум 100)
     */
    fun getAnimeListByParameters(
        search: String?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>>

    /**
     * Получение списка аниме по параметру ранжирования
     *
     * @param rankingType тип ранжирования аниме
     * @param limit количество аниме для показа (максимум 500)
     */
    fun getAnimeRankingList(
        rankingType: RankingType?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>>


    fun getAnimeDetailsById(
        id: Long?,
        fields: String?
    ): Single<AnimeMalModel>
}