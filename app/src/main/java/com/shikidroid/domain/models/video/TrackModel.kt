package com.shikidroid.domain.models.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackModel(
    val quality : String?,
    val url : String?
) : Parcelable
