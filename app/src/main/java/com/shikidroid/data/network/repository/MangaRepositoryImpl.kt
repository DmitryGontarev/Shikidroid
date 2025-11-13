package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.MangaApi
import com.shikidroid.data.network.converters.*
import com.shikidroid.data.network.converters.toStringRequestAiredStatuses
import com.shikidroid.data.network.converters.toStringRequestMangaType
import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.repository.MangaRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


/**
 * Реализация репозитория [MangaRepository]
 *
 * @property api для получения данных об аниме из сети
 */
internal class MangaRepositoryImpl @Inject constructor(
    private val api: MangaApi
) : MangaRepository {

    override fun getMangaListByParameters(
        page: Int?,
        limit: Int?,
        order: MangaSearchType?,
        kind: List<MangaType>?,
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
        return api.getMangaListByParameters(
            page = page,
            limit = limit,
            order = order?.toStringRequest(),
            kind = kind?.toStringRequestMangaType(),
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

    override fun getMangaDetailsById(id: Long): Single<MangaDetailsModel> {
        return api.getMangaDetailsById(id = id)
            .map {
                it.toDomainModel()
            }
    }

    override fun getMangaRolesById(id: Long): Single<List<RolesModel>> {
        return api.getMangaRolesById(id = id)
            .map { listRolesResponse ->
                listRolesResponse.map { rolesResponse ->
                    rolesResponse.toDomainModel()
                }
            }
    }

    override fun getSimilarManga(id: Long): Single<List<MangaModel>> {
        return api.getSimilarManga(id = id)
            .map { listMangaResponse ->
                listMangaResponse.map { mangaResponse ->
                    mangaResponse.toDomainModel()
                }
            }
    }

    override fun getRelatedManga(id: Long): Single<List<RelatedModel>> {
        return api.getRelatedManga(id = id)
            .map { listRelatedResponse ->
                listRelatedResponse.map { relatedResponse ->
                    relatedResponse.toDomainModel()
                }
            }
    }

    override fun getMangaFranchise(id: Long): Single<FranchiseModel> {
        return api.getMangaFranchise(id = id)
            .map { franchiseResponse ->
                franchiseResponse.toDomainModel()
            }
    }

    override fun getMangaExternalLinksById(id: Long): Single<List<LinkModel>> {
        return api.getMangaExternalLinksById(id = id)
            .map { listLinkResponse ->
                listLinkResponse.map { linkResponse ->
                    linkResponse.toDomainModel()
                }
            }
    }
}