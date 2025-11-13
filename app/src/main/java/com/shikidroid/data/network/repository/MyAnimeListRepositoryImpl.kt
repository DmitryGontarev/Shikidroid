package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.MyAnimeListApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.data.network.converters.toStringRequest
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RankingType
import com.shikidroid.domain.repository.MyAnimeListRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [MyAnimeListRepository]
 *
 * @property api - для получения данных об аниме из сети
 */
class MyAnimeListRepositoryImpl @Inject constructor(private val api: MyAnimeListApi) :
    MyAnimeListRepository {

    override fun getAnimeListByParameters(
        search: String?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>> {
        return api.getAnimeListByParameters(
            search = search,
            limit = limit,
            offset = offset,
            fields = fields,
            nsfw = nsfw
        ).map { data ->
            data.data.map { node ->
                node.anime.toDomainModel()
            }
        }
    }

    override fun getAnimeRankingList(
        rankingType: RankingType?,
        limit: Int?,
        offset: Int?,
        fields: String?,
        nsfw: Boolean
    ): Single<List<AnimeMalModel>> {
        return api.getAnimeRankingList(
            rankingType = rankingType?.toStringRequest(),
            limit = limit,
            offset = offset,
            fields = fields,
            nsfw = nsfw
        ).map { data ->
            data.data.map { node ->
                node.anime.toDomainModel()
            }
        }
    }

    override fun getAnimeDetailsById(id: Long?, fields: String?): Single<AnimeMalModel> {
        return api.getAnimeDetailsById(id = id, fields = fields)
            .map { it.toDomainModel() }
    }
}