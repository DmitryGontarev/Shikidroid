package com.shikidroid.domain.repository

import com.shikidroid.domain.models.video.DownloadVideoModel
import io.reactivex.rxjava3.core.Completable

/**
 * Интерфейс для загрузки видеофайла
 */
internal interface DownloadVideoRepository {

    /**
     * скачать видеофайл
     *
     * @param data данные видео для загрузки
     */
    fun downloadVideo(data: DownloadVideoModel) : Completable
}