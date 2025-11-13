package com.shikidroid.presentation.navigation

import androidx.navigation.NavHostController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.presentation.CharacterPeopleScreenType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.RatesScreens
import com.shikidroid.uikit.setData
import com.shikidroid.utils.StringUtils.toOnlyDigits

/**
 * Метод для навигации на следующий экран в зависимости от типа ссылки
 *
 * используется для клика по аннотации в описании
 *
 * @param linkType тип ссылки (anime, manga, ranobe, character, url)
 * @param link ссылка
 * @param navigator фунцкионал навигации
 */
internal fun navigateByLinkType(
    linkType: String,
    link: String,
    navigator: NavHostController
) {
    when (linkType) {
        "anime" -> {
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_TYPE,
                value = DetailsScreenType.ANIME
            )
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_ID,
                value = link.toOnlyDigits().toLong()
            )
            navigator.navigate(RatesScreens.Details.route)
        }
        "manga" -> {
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_TYPE,
                value = DetailsScreenType.MANGA
            )
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_ID,
                value = link.toOnlyDigits().toLong()
            )
            navigator.navigate(RatesScreens.Details.route)
        }
        "ranobe" -> {
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_TYPE,
                value = DetailsScreenType.RANOBE
            )
            navigator.setData(
                key = AppKeys.DETAILS_SCREEN_ID,
                value = link.toOnlyDigits().toLong()
            )
            navigator.navigate(RatesScreens.Details.route)
        }
        "character" -> {
            navigator.setData(
                key = AppKeys.CHARACTER_PEOPLE_SCREEN_ID,
                value = link.toOnlyDigits().toLong()
            )
            navigator.setData(
                key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE,
                value = CharacterPeopleScreenType.CHARACTER
            )
            navigator.navigate(RatesScreens.CharacterPeople.route)
        }
        "person" -> {
            navigator.setData(
                key = AppKeys.CHARACTER_PEOPLE_SCREEN_ID,
                value = link.toOnlyDigits().toLong()
            )
            navigator.setData(
                key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE,
                value = CharacterPeopleScreenType.PEOPLE
            )
            navigator.navigate(RatesScreens.CharacterPeople.route)
        }
        "url" -> {
            navigator.setData(
                key = AppKeys.WEBVIEW_SCREEN_URL,
                value = link
            )
            navigator.navigate(RatesScreens.WebView.route)
        }
    }
}