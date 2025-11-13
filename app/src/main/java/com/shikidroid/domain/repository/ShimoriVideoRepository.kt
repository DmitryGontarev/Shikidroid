package com.shikidroid.domain.repository

import com.shikidroid.domain.models.video.ShimoriEpisodeModel
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.domain.models.video.VideoModel
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения данных видео
 */
internal interface ShimoriVideoRepository {

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