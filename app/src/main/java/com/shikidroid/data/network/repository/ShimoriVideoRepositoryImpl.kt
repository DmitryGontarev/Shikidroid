package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.ShimoriVideoApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.data.network.converters.toStringRequest
import com.shikidroid.domain.models.video.ShimoriEpisodeModel
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.domain.models.video.VideoModel
import com.shikidroid.domain.repository.ShimoriVideoRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [ShimoriVideoRepository]
 *
 * @property api для получения данных видое из сети
 */
internal class ShimoriVideoRepositoryImpl @Inject constructor(
    private val api: ShimoriVideoApi
) : ShimoriVideoRepository {

    override fun getEpisodes(malId: Long, name: String): Single<Int> {
        return api.getEpisodes(
            malId = malId,
            name = name
        )
    }

    override fun getSeries(malId: Long, name: String): Single<List<ShimoriEpisodeModel>> {
        return api.getSeries(
            malId = malId,
            name = name
        ).map { listShimoriVideos ->
            listShimoriVideos.map { shimoriVideo ->
                shimoriVideo.toDomainModel()
            }
        }
    }

    override fun getTranslations(
        malId: Long,
        name: String,
        episode: Int,
        hostingId: Int,
        translationType: TranslationType?
    ): Single<List<ShimoriTranslationModel>> {
        return api.getTranslations(
            malId = malId,
            name = name,
            episode = episode,
            hostingId = hostingId,
            translationType = translationType?.toStringRequest()
        ).map { listShimoriTranslation ->
            listShimoriTranslation.map { shimoriTranslation ->
                shimoriTranslation.toDomainModel()
            }
        }
    }

    override fun getVideo(
        malId: Long,
        episode: Int,
        translationType: TranslationType?,
        author: String?,
        hosting: String,
        hostingId: Int,
        videoId: Long?,
        url: String?,
        accessToken: String?
    ): Single<VideoModel> {
        return api.getVideo(
            malId = malId,
            episode = episode,
            translationType = translationType?.toStringRequest(),
            author = author,
            hosting = hosting,
            hostingId = hostingId,
            videoId = videoId,
            url = url,
            accessToken = accessToken
        ).map { video ->
            video.toDomainModel()
        }
    }
}