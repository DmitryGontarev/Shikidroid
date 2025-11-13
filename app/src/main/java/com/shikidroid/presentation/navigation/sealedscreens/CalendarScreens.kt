package com.shikidroid.presentation.navigation.sealedscreens

sealed class CalendarScreens(
    val route: String,
    val title: String
) {

    object Calendar : CalendarScreens(
        route = "calendar",
        title = "CALENDAR"
    )

    object Details : CalendarScreens(
        route = "details",
        title = "DETAILS"
    )

    object CharacterPeople : CalendarScreens(
        route = "character",
        title = "CHARACTER"
    )

    object WebView : CalendarScreens(
        route = "webview",
        title = "WEBVIEW"
    )

    object Episode : CalendarScreens(
        route = "episode",
        title = "EPISODE"
    )

    object Search : CalendarScreens(
        route = "search",
        title = "SEARCH"
    )
}