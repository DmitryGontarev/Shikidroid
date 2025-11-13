package com.shikidroid.domain.models.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель данных для воспроизведения видео
 *
 * @param animeId
 * @param episodeId
 * @param player
 * @param hosting
 * @param tracks
 * @param subAss
 * @param subVtt
 */
@Parcelize
data class VideoModel(
    val animeId: Long?,
    val episodeId: Long?,
    val player: String?,
    val hosting: String?,
    val tracks: List<TrackModel?>?,
    val subAss: String?,
    val subVtt: String?
) : Parcelable
