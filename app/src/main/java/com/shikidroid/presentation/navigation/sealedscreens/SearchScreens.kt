package com.shikidroid.presentation.navigation.sealedscreens

sealed class SearchScreens(
    val route: String,
    val title: String
) {

    object Search : SearchScreens(
        route = "search",
        title = "SEARCH"
    )

    object Details : SearchScreens(
        route = "details",
        title = "DETAILS"
    )

    object CharacterPeople : SearchScreens(
        route = "character",
        title = "CHARACTER"
    )

    object WebView : SearchScreens(
        route = "webview",
        title = "WEBVIEW"
    )

    object Episode : SearchScreens(
        route = "episode",
        title = "EPISODE"
    )
}
