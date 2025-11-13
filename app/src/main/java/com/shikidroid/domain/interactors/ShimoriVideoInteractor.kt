package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.video.ShimoriEpisodeModel
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.domain.models.video.VideoModel
import com.shikidroid.domain.repository.ShimoriVideoRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения видео
 */
internal interface ShimoriVideoInteractor {

    fun getEpisodes(
        malId: Long,
        name: String
    ): Single<Int>

    fun getSeries(
        malId: Long,
        name: String
    ): Single<List<ShimoriEpisodeModel>>

    fun getTranslations(
        malId: Long,
        name: String,
        episode: Int,
        hostingId: Int,
        translationType: TranslationType?
    ): Single<List<ShimoriTranslationModel>>

    fun getVideo(
        malId: Long,
        episode: Int,
        translationType: TranslationType?,
        author: String?,
        hosting: String,
        hostingId: Int,
        videoId: Long?,
        url: String?,
        accessToken: String?
    ): Single<VideoModel>
}

internal class ShimoriVideoInteractorImpl @Inject constructor(
    private val shimoriVideoRepository: ShimoriVideoRepository
) : ShimoriVideoInteractor {

    override fun getEpisodes(malId: Long, name: String): Single<Int> {
        return shimoriVideoRepository.getEpisodes(
            malId = malId,
            name = name
        )
    }

    override fun getSeries(malId: Long, name: String): Single<List<ShimoriEpisodeModel>> {
        return shimoriVideoRepository.getSeries(
            malId = malId,
            name = name
        )
    }

    override fun getTranslations(
        malId: Long,
        name: String,
        episode: Int,
        hostingId: Int,
        translationType: TranslationType?
    ): Single<List<ShimoriTranslationModel>> {
        return shimoriVideoRepository.getTranslations(
            malId = malId,
            name = name,
            episode = episode,
            hostingId = hostingId,
            translationType = translationType
        )
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
        return shimoriVideoRepository.getVideo(
            malId = malId,
            episode = episode,
            translationType = translationType,
            author = author,
            hosting = hosting,
            hostingId = hostingId,
            videoId = videoId,
            url = url,
            accessToken = accessToken
        )
    }
}