package com.shikidroid.presentation.navigation.sealedscreens

sealed class RatesScreens(
    val route: String,
    val title: String
) {

    object Rate : RatesScreens(
        route = "rate",
        title = "RATE"
    )

    object Details : RatesScreens(
        route = "details",
        title = "DETAILS"
    )

    object CharacterPeople : RatesScreens(
        route = "character",
        title = "CHARACTER"
    )

    object WebView : RatesScreens(
        route = "webview",
        title = "WEBVIEW"
    )

    object Episode : RatesScreens(
        route = "episode",
        title = "EPISODE"
    )

    object Search: RatesScreens(
        route = "search",
        title = "SEARCH"
    )
}