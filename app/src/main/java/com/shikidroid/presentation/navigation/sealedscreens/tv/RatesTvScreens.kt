package com.shikidroid.presentation.navigation.sealedscreens.tv

sealed class RatesTvScreens(
    val route: String,
    val title: String
) {

    object RateTv: RatesTvScreens(
        route = "rateTv",
        title = "RATE_TV"
    )

    object EpisodeTv: RatesTvScreens(
        route = "episodeTv",
        title = "EPISODE_TV"
    )

    object DetailsTv: RatesTvScreens(
        route = "detailsTv",
        title = "DETAILS_TV"
    )

    object SearchTv: RatesTvScreens(
        route = "searchTv",
        title = "SEARCH_TV"
    )
}
