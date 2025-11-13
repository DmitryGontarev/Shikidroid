package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RankingType
import com.shikidroid.domain.repository.MyAnimeListRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора для получения информации с MyAnimeList
 */
interface MyAnimeListInteractor {
    /**
     * Получение списка аниме через Поиск
     *
     * @param search название аниме
     * @param limit количество аниме для показа (максимум 100)
     */
    fun getAnimeListByParameters(
        search: String?,
        limit: Int? = 100,
        offset: Int? = null,
        fields: String?,
        nsfw: Boolean = false
    ): Single<List<AnimeMalModel>>

    /**
     * Получение списка аниме по параметру ранжирования
     *
     * @param rankingType тип ранжирования аниме
     * @param limit количество аниме для показа (максимум 500)
     */
    fun getAnimeRankingList(
        rankingType: RankingType?,
        limit: Int? = 500,
        offset: Int? = null,
        fields: String?,
        nsfw: Boolean = false
    ): Single<List<AnimeMalModel>>


    fun getAnimeDetailsById(
        id: Long?,
        fields: String?,
    ): Single<AnimeMalModel>
}

/**
 * Реализация интерактора [MyAnimeListInteractor]
 *
 * @property malRepository репозиторий для получения данных с MyAnimeList
 */
internal class MyAnimeListInteractorImpl @Inject constructor(
    private val malRepository: MyAnimeListRepository
) : MyAnimeListInteractor {
    override fun getAnimeListByParameters(
        search: String?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>> {
        return malRepository.getAnimeListByParameters(
            search = search,
            limit = limit,
            offset = offset,
            fields = fields,
            nsfw = nsfw
        )
    }

    override fun getAnimeRankingList(
        rankingType: RankingType?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>> {
        return malRepository.getAnimeRankingList(
            rankingType = rankingType,
            limit = limit,
            offset = offset,
            fields = fields,
            nsfw = nsfw
        )
    }

    override fun getAnimeDetailsById(id: Long?, fields: String?): Single<AnimeMalModel> {
        return malRepository.getAnimeDetailsById(id = id, fields = fields)
    }
}