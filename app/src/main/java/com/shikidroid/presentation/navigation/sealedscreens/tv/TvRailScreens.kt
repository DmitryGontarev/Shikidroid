package com.shikidroid.presentation.navigation.sealedscreens.tv

import com.shikidroid.R

/**
 * Данные для боковой панели навигации AndroidTV
 *
 * @param route ссылка экрана
 * @param title название экрана
 * @param icon иконка панели навигации
 * @param iconFocused иконка панели навигации, если она выбрана
 */
sealed class TvRailScreens(
    val route: String,
    val title: String,
    val icon: Int,
    val iconFocused: Int
) {

    object RatesTv : TvRailScreens(
        route = "ratesTv",
        title = "Списки",
        icon = R.drawable.ic_bookmark,
        iconFocused = R.drawable.ic_bookmark_filled
    )

    object CalendarTv : TvRailScreens(
        route = "calendarTv",
        title = "Календарь",
        icon = R.drawable.ic_calendar,
        iconFocused = R.drawable.ic_calendar_filled
    )

    object SearchTv : TvRailScreens(
        route = "searchTv",
        title = "Поиск",
        icon = R.drawable.ic_search,
        iconFocused = R.drawable.ic_search_filled
    )
}
