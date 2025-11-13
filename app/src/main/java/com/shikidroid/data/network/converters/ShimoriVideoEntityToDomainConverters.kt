package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.video.ShimoriEpisodeResponse
import com.shikidroid.data.network.entity.video.ShimoriTranslationResponse
import com.shikidroid.data.network.entity.video.TrackResponse
import com.shikidroid.data.network.entity.video.VideoResponse
import com.shikidroid.domain.models.video.ShimoriEpisodeModel
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.domain.models.video.TrackModel
import com.shikidroid.domain.models.video.VideoModel

/**
 * конвертация [ShimoriEpisodeResponse] в модель domain слоя
 *
 * @return [ShimoriEpisodeModel]
 */
fun ShimoriEpisodeResponse.toDomainModel(): ShimoriEpisodeModel =
    ShimoriEpisodeModel(
        id = id,
        index = index,
        animeId = animeId
    )

/**
 * конвертация [ShimoriTranslationResponse] в модель domain слоя
 *
 * @return [ShimoriTranslationModel]
 */
fun ShimoriTranslationResponse.toDomainModel(): ShimoriTranslationModel =
    ShimoriTranslationModel(
        id = id,
        kind = kind?.toDomainTranslationType(),
        targetId = targetId,
        episode = episode,
        url = url,
        hosting = hosting,
        language = language,
        author = author,
        quality = quality,
        episodesTotal = episodesTotal
    )

/**
 * конвертация [VideoResponse] в модель domain слоя
 *
 * @return [VideoModel]
 */
fun VideoResponse.toDomainModel(): VideoModel =
    VideoModel(
        animeId = animeId,
        episodeId = episodeId,
        player = player,
        hosting = hosting,
        tracks = tracks?.map { it?.toDomainModel() },
        subAss = subAss,
        subVtt = subVtt
    )

/**
 * конвертация [TrackResponse] в модель domain слоя
 *
 * @return [TrackModel]
 */
fun TrackResponse.toDomainModel(): TrackModel =
    TrackModel(
        quality = quality,
        url = url
    )
