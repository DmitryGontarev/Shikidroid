package com.shikidroid.presentation.navigation.sealedscreens

sealed class StartScreens(
    val route: String,
    val title: String
) {

    object Splash: StartScreens(
        route = "splash",
        title = "SPLASH"
    )

    object Enter: StartScreens(
        route = "enter",
        title = "ENTER"
    )

    object WebViewAuth: StartScreens(
        route = "webViewAuth",
        title = "WEB_VIEW_AUTH"
    )

    object WebView: StartScreens(
        route = "webView",
        title = "WEB_VIEW"
    )

    object Bottom: StartScreens(
        route = "bottom",
        title = "BOTTOM"
    )

    object BottomGuest: StartScreens(
        route = "bottomGuest",
        title = "BOTTOM_GUEST"
    )

    ///////////////////////////////////////////////////////////////////////////
    // Shikidroid TV
    ///////////////////////////////////////////////////////////////////////////

    object EnterTv: StartScreens(
        route = "enterTvRail",
        title = "ENTER_TV_RAIL"
    )

    object TvRail: StartScreens(
        route = "tvRail",
        title = "TV_RAIL"
    )

    object TvRailGuest: StartScreens(
        route = "tvRailGuest",
        title = "TV_RAIL_GUEST"
    )

    ///////////////////////////////////////////////////////////////////////////
    // My Anime List
    ///////////////////////////////////////////////////////////////////////////

    object BottomMal: StartScreens(
        route = "bottomMal",
        title = "BOTTOM_MAL"
    )

    ///////////////////////////////////////////////////////////////////////////
    // My Anime List TV
    ///////////////////////////////////////////////////////////////////////////

    object TvRailMal: StartScreens(
        route = "tvRailMal",
        title = "TV_RAIL_MAL"
    )
}