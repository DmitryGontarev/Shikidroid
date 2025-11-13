package com.shikidroid.presentation.navigation.sealedscreens.tv

sealed class CalendarTvScreens(
    val route: String,
    val title: String
) {

    object CalendarTv: CalendarTvScreens(
        route = "calendarTv",
        title = "CALENDAR_TV"
    )

    object EpisodeTv: CalendarTvScreens(
        route = "episodeTv",
        title = "EPISODE_TV"
    )

    object DetailsTv: CalendarTvScreens(
        route = "detailsTv",
        title = "DETAILS_TV"
    )

    object SearchTv: CalendarTvScreens(
        route = "searchTv",
        title = "SEARCH_TV"
    )
}
