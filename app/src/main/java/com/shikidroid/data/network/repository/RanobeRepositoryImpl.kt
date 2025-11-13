package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.RanobeApi
import com.shikidroid.data.network.converters.*
import com.shikidroid.data.network.converters.toStringRequestAiredStatuses
import com.shikidroid.data.network.converters.toStringRequestSeason
import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.repository.RanobeRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [RanobeRepository]
 *
 * @property api для получения данных о ранобэ из сети
 */
internal class RanobeRepositoryImpl @Inject constructor(
    private val api: RanobeApi
) : RanobeRepository {

    override fun getRanobeListByParameters(
        page: Int?,
        limit: Int?,
        order: MangaSearchType?,
        status: List<AiredStatus>?,
        season: List<Pair<String?, String?>>?,
        score: Double?,
        genre: List<Long>?,
        publisher: List<String>?,
        franchise: List<String>?,
        censored: Boolean?,
        myList: List<RateStatus>?,
        ids: List<String>?,
        excludeIds: List<String>?,
        search: String?
    ): Single<List<MangaModel>> {
        return api.getRanobeListByParameters(
            page = page,
            limit = limit,
            order = order?.toStringRequest(),
            status = status?.toStringRequestAiredStatuses(),
            season = season?.toStringRequestSeason(),
            score = score,
            genre = genre?.toStringOrNull(),
            publisher = publisher?.toNullIfEmpty(),
            franchise = franchise?.toNullIfEmpty(),
            censored = censored,
            myList = myList?.toStringRequestRateStatus(),
            ids = ids?.toNullIfEmpty(),
            excludeIds = excludeIds?.toNullIfEmpty(),
            search = search
        ).map { listMangaResponse ->
            listMangaResponse.map { mangaResponse ->
                mangaResponse.toDomainModel()
            }
        }
    }

    override fun getRanobeDetailsById(id: Long): Single<MangaDetailsModel> {
        return api.getRanobeDetailsById(id = id)
            .map {
                it.toDomainModel()
            }
    }

    override fun getRanobeRolesById(id: Long): Single<List<RolesModel>> {
        return api.getRanobeRolesById(id = id)
            .map { listRolesResponse ->
                listRolesResponse.map { rolesResponse ->
                    rolesResponse.toDomainModel()
                }
            }
    }

    override fun getSimilarRanobe(id: Long): Single<List<MangaModel>> {
        return api.getSimilarRanobe(id = id)
            .map { listMangaResponse ->
                listMangaResponse.map { mangaResponse ->
                    mangaResponse.toDomainModel()
                }
            }
    }

    override fun getRelatedRanobe(id: Long): Single<List<RelatedModel>> {
        return api.getRelatedRanobe(id = id)
            .map { listRelatedResponse ->
                listRelatedResponse.map { relatedResponse ->
                    relatedResponse.toDomainModel()
                }
            }
    }

    override fun getRanobeFranchise(id: Long): Single<FranchiseModel> {
        return api.getRanobeFranchise(id = id)
            .map { franchiseResponse ->
                franchiseResponse.toDomainModel()
            }
    }

    override fun getRanobeExternalLinksById(id: Long): Single<List<LinkModel>> {
        return api.getRanobeExternalLinksById(id = id)
            .map { listLinkResponse ->
                listLinkResponse.map { linkResponse ->
                    linkResponse.toDomainModel()
                }
            }
    }

}