package com.shikidroid.data.local

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.shikidroid.domain.models.video.DownloadVideoModel
import com.shikidroid.domain.repository.DownloadVideoRepository
import com.shikidroid.domain.repository.SettingsRepository
import com.shikidroid.utils.downloadManager
import io.reactivex.rxjava3.core.Completable
import java.io.File
import javax.inject.Inject

/**
 * Реализация [DownloadVideoRepository]
 *
 * @param context контекст приложения
 * @param settings настройки приложения
 */
internal class DownloadVideoRepositoryImpl @Inject constructor(
    private val context: Context,
    private val settings: SettingsRepository
): DownloadVideoRepository {

    override fun downloadVideo(data: DownloadVideoModel): Completable {
        return if (data.link.isNullOrBlank()) {
            Completable.error(NoSuchElementException())
        } else {
            Completable.fromAction {
                val title = "${data.episodeIndex} эпизод ${data.animeName}"

                val file = File(settings.downloadFolder)
                val path = Uri.withAppendedPath(
                    Uri.fromFile(file), "anime/${data.animeName}/$title.mp4"
                )

                val downloadManager = context.downloadManager()
                val uri = Uri.parse(data.link)
                val request = DownloadManager.Request(uri)
                    .setTitle(title)
                    .setDescription("Shikidroid")
                    .setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                    )
                    .apply {
                        allowScanningByMediaScanner()
                        setDestinationUri(path)
                        data.requestHeaders.entries.forEach {
                            addRequestHeader(
                                it.key,
                                it.value
                            )
                        }
                    }
                downloadManager?.enqueue(request)
            }
        }
    }
}