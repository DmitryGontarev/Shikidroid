package com.shikidroid.domain.models.video

/**
 *
 */
data class DownloadVideoModel(
    val animeId : Long,
    val animeName : String,
    val episodeIndex : Int,
    val link : String?,
    val requestHeaders : Map<String, String>
)
