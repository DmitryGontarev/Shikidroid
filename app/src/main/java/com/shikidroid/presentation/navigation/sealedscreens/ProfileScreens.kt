package com.shikidroid.presentation.navigation.sealedscreens

sealed class ProfileScreens(
    val route: String,
    val title: String
) {

    object Profile : ProfileScreens(
        route = "profile",
        title = "PROFILE"
    )

    object Messages : ProfileScreens(
        route = "messages",
        title = "MESSAGES"
    )
}
