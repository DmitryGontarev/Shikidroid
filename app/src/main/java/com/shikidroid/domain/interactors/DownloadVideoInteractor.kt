package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.video.DownloadVideoModel
import com.shikidroid.domain.repository.DownloadVideoRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

/**
 * Интерфейс интерактора для загрузки видео
 */
internal interface DownloadVideoInteractor {

    fun downloadVideo(data: DownloadVideoModel): Completable
}

/**
 * Реализация интерфейса [DownloadVideoInteractor]
 */
internal class DownloadVideoInteractorImpl @Inject constructor(
    private val downloadVideoRepository: DownloadVideoRepository
) : DownloadVideoInteractor {

    override fun downloadVideo(data: DownloadVideoModel): Completable {
        return downloadVideoRepository.downloadVideo(
            data = data
        )
    }
}