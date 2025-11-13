package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.AnimeApi
import com.shikidroid.data.network.converters.*
import com.shikidroid.domain.models.anime.*
import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.repository.AnimeRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [AnimeRepository]
 *
 * @property api для получения данных об аниме из сети
 */
internal class AnimeRepositoryImpl @Inject constructor(
    private val api: AnimeApi
) : AnimeRepository {

    override fun getAnimeListByParameters(
        page: Int?,
        limit: Int?,
        order: AnimeSearchType?,
        kind: List<AnimeType>?,
        status: List<AiredStatus>?,
        season: List<Pair<String?, String?>>?,
        score: Double?,
        duration: List<AnimeDurationType>?,
        rating: List<AgeRatingType>?,
        genre: List<Long>?,
        studio: List<Long>?,
        franchise: List<String>?,
        censored: Boolean?,
        myList: List<RateStatus>?,
        ids: List<String>?,
        excludeIds: List<String>?,
        search: String?
    ): Single<MutableList<AnimeModel>> {
        return api.getAnimeListByParameters(
            page = page,
            limit = limit,
            order = order?.toStringRequest(),
            kind = kind?.toStringRequestAnimeType(),
            status = status?.toStringRequestAiredStatuses(),
            season = season?.toStringRequestSeason(),
            score = score,
            duration = duration?.toStringRequestDuration(),
            rating = rating?.toStringRequestAgeRatingType(),
            genre = genre?.toStringOrNull(),
            studio = studio?.toStringOrNull(),
            franchise = franchise?.toNullIfEmpty(),
            censored = censored,
            myList = myList?.toStringRequestRateStatus(),
            ids = ids?.toNullIfEmpty(),
            excludeIds = excludeIds?.toNullIfEmpty(),
            search = search
        )
            .map { listAnimeResponse ->
                listAnimeResponse.map { animeResponse ->
                    animeResponse.toDomainModel()
                }.toMutableList()
            }
    }

    override fun getAnimeDetailsById(id: Long): Single<AnimeDetailsModel> {
        return api.getAnimeDetailsById(id = id)
            .map {
                it.toDomainModel()
            }
    }

    override fun getAnimeRolesById(id: Long): Single<List<RolesModel>> {
        return api.getAnimeRolesById(id = id)
            .map { listRolesResponse ->
                listRolesResponse.map { rolesResponse ->
                    rolesResponse.toDomainModel()
                }
            }
    }

    override fun getSimilarAnime(id: Long): Single<List<AnimeModel>> {
        return api.getSimilarAnime(id = id)
            .map { listAnimeResponse ->
                listAnimeResponse.map { animeResponse ->
                    animeResponse.toDomainModel()
                }
            }
    }

    override fun getRelatedAnime(id: Long): Single<List<RelatedModel>> {
        return api.getRelatedAnime(id = id)
            .map { listRelatedResponse ->
                listRelatedResponse.map { relatedResponse ->
                    relatedResponse.toDomainModel()
                }
            }
    }

    override fun getAnimeScreenshotsById(id: Long): Single<List<ScreenshotModel>> {
        return api.getAnimeScreenshotsById(id = id)
            .map { listScreenshotResponse ->
                listScreenshotResponse.map { screenshotResponse ->
                    screenshotResponse.toDomainModel()
                }
            }
    }

    override fun getAnimeFranchise(id: Long): Single<FranchiseModel> {
        return api.getAnimeFranchise(id = id)
            .map { franchiseResponse ->
                franchiseResponse.toDomainModel()
            }
    }

    override fun getAnimeExternalLinksById(id: Long): Single<List<LinkModel>> {
        return api.getAnimeExternalLinksById(id = id)
            .map { listLinkResponse ->
                listLinkResponse.map { linkResponse ->
                    linkResponse.toDomainModel()
                }
            }
    }

    override fun getAnimeVideos(id: Long): Single<List<AnimeVideoModel>> {
        return api.getAnimeVideos(id = id)
            .map { listAnimeVideos ->
                listAnimeVideos.map { animeVideoResponse ->
                    animeVideoResponse.toDomainModel()
                }
            }
    }
}