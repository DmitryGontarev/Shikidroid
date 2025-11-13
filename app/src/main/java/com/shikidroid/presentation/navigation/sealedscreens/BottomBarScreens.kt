package com.shikidroid.presentation.navigation.sealedscreens

import com.shikidroid.R

/**
 * Данные для нижней панели навигации
 *
 * @param route ссылка экрана
 * @param title название экрана
 * @param icon иконка панели навигации
 * @param iconFocused иконка панели навигации, если она выбрана
 */
sealed class BottomBarScreens(
    val route: String,
    val title: String,
    val icon: Int,
    val iconFocused: Int
) {
    object Rates : BottomBarScreens(
        route = "rates",
        title = "Списки",
        icon = R.drawable.ic_bookmark,
        iconFocused = R.drawable.ic_bookmark_filled
    )

    object Calendar : BottomBarScreens(
        route = "calendar",
        title = "Календарь",
        icon = R.drawable.ic_calendar,
        iconFocused = R.drawable.ic_calendar_filled
    )

    object Search : BottomBarScreens(
        route = "search",
        title = "Поиск",
        icon = R.drawable.ic_search,
        iconFocused = R.drawable.ic_search_filled
    )

    object Main : BottomBarScreens(
        route = "main",
        title = "Главная",
        icon = R.drawable.ic_group,
        iconFocused = R.drawable.ic_group_filled
    )

    object Profile : BottomBarScreens(
        route = "profile",
        title = "Профиль",
        icon = R.drawable.ic_profile,
        iconFocused = R.drawable.ic_profile_selected
    )
}
