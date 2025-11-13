package com.shikidroid.presentation.navigation.sealedscreens.tv

sealed class SearchTvScreens(
    val route: String,
    val title: String
) {
    object SearchTv: SearchTvScreens(
        route = "searchTv",
        title = "SEARCH_TV"
    )

    object DetailsTv: SearchTvScreens(
        route = "detailsTv",
        title = "DETAILS_TV"
    )

    object EpisodeTv: SearchTvScreens(
        route = "episodeTv",
        title = "EPISODE_TV"
    )
}
