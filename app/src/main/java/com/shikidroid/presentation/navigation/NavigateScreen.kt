package com.shikidroid.presentation.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavHostController
import com.shikidroid.VideoActivity
import com.shikidroid.WebViewActivity
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.appconstants.AppKeys.SEARCH_SCREEN_GENRE_ID
import com.shikidroid.appconstants.AppKeys.SEARCH_SCREEN_STUDIO_ID
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.findMainActivity
import com.shikidroid.presentation.CharacterPeopleScreenType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.RatesScreens
import com.shikidroid.presentation.navigation.sealedscreens.tv.CalendarTvScreens
import com.shikidroid.presentation.navigation.sealedscreens.tv.RatesTvScreens
import com.shikidroid.uikit.removeData
import com.shikidroid.uikit.setData

/**
 * Функция перехода на экран с WebView
 *
 * @param url ссылка на сайт
 * @param navigator контроллер навигации
 */
internal fun navigateWebViewScreen(
    url: String?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.WEBVIEW_SCREEN_URL,
        value = url
    )
    navigator.navigate(RatesScreens.WebView.route)
}

/**
 * Функция перехода на экран с WebView для AndroidTV
 *
 * @param url ссылка на сайт
 * @param context контекст приложения
 */
internal fun navigateWebViewTvScreen(
    url: String?,
    useIFrame: Boolean = false,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null,
    context: Context
) {
    val intent = Intent(context, WebViewActivity::class.java)
    intent.putExtra(AppKeys.WEBVIEW_SCREEN_URL, url)
    intent.putExtra(AppKeys.WEBVIEW_SCREEN_I_FRAME, useIFrame)
    intent.putExtra(AppKeys.WEBVIEW_SCREEN_IS_GET_HTML_BODY_FOR_TV, launcher != null)

    if (launcher != null) {
        launcher.launch(intent)
    } else {
        context.startActivity(intent)
    }
}

/**
 * Функция перехода на экран детальной информации
 *
 * @param id идентификационный номер произведения
 * @param detailsType тип экрана детальной информации - Аниме/Манга/Ранобэ
 * @param navigator контроллер навигации
 */
internal fun navigateDetailsScreen(
    id: Long?,
    detailsType: DetailsScreenType,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_TYPE,
        value = detailsType
    )
    navigator.navigate(RatesScreens.Details.route)
}

/**
 * Функция перехода на экран детальной информации с Аниме
 *
 * @param id идентификационный номер произведения
 * @param navigator контроллер навигации
 */
internal fun navigateDetailsAnimeScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_TYPE,
        value = DetailsScreenType.ANIME
    )
    navigator.navigate(RatesScreens.Details.route)
}

/**
 * Функция перехода на экран детальной информации с Аниме для AndroidTV
 *
 * @param id идентификационный номер произведения
 * @param navigator контроллер навигации
 */
internal fun navigateDetailsTvAnimeScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_TYPE,
        value = DetailsScreenType.ANIME
    )
    navigator.navigate(CalendarTvScreens.DetailsTv.route)
}

/**
 * Функция перехода на экран детальной информации с Мангой
 *
 * @param id идентификационный номер произведения
 * @param navigator контроллер навигации
 */
internal fun navigateDetailsMangaScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_TYPE,
        value = DetailsScreenType.MANGA
    )
    navigator.navigate(RatesScreens.Details.route)
}

/**
 * Функция перехода на экран детальной информации с Ранобэ
 *
 * @param id идентификационный номер произведения
 * @param navigator контроллер навигации
 */
internal fun navigateDetailsRanobeScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.DETAILS_SCREEN_TYPE,
        value = DetailsScreenType.RANOBE
    )
    navigator.navigate(RatesScreens.Details.route)
}

/**
 * Функция перехода на экран с эпизодами аниме
 *
 * @param id идентификационный номер произведения
 * @param userRateId идентификационный номер элемента в списке пользователя
 * @param animeName название аниме на английском
 * @param animeNameRu название аниме на русском
 * @param animeImageUrl ссылка на картинку аниме
 * @param navigator контроллер навигации
 */
internal fun navigateEpisodeScreen(
    id: Long?,
    userRateId: Long?,
    animeName: String?,
    animeNameRu: String?,
    animeImageUrl: String?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_ID_USER_RATE,
        value = userRateId
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_TITLE,
        value = animeName
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_RU_TITLE,
        value = animeNameRu
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE_URL,
        value = animeImageUrl
    )
    navigator.navigate(RatesScreens.Episode.route)
}

/**
 * Функция перехода на экран с эпизодами аниме
 *
 * @param id идентификационный номер произведения
 * @param userRateId идентификационный номер элемента в списке пользователя
 * @param animeName название аниме на английском
 * @param animeNameRu название аниме на русском
 * @param animeImageUrl ссылка на картинку аниме
 * @param navigator контроллер навигации
 */
internal fun navigateEpisodeTvScreen(
    id: Long?,
    userRateId: Long?,
    animeName: String?,
    animeNameRu: String?,
    animeImageUrl: String?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_ID_USER_RATE,
        value = userRateId
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_TITLE,
        value = animeName
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_RU_TITLE,
        value = animeNameRu
    )
    navigator.setData(
        key = AppKeys.EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE_URL,
        value = animeImageUrl
    )
    navigator.navigate(RatesTvScreens.EpisodeTv.route)
}

/**
 * Функция перехода на экран с информацией о человеке
 *
 * @param id идентификационный номер человека
 * @param navigator контроллер навигации
 */
internal fun navigatePeopleScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.CHARACTER_PEOPLE_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE,
        value = CharacterPeopleScreenType.PEOPLE
    )
    navigator.navigate(RatesScreens.CharacterPeople.route)
}

/**
 * Функция перехода на экран с информацией о человеке
 *
 * @param id идентификационный номер человека
 * @param navigator контроллер навигации
 */
internal fun navigateCharacterScreen(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.CHARACTER_PEOPLE_SCREEN_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE,
        value = CharacterPeopleScreenType.CHARACTER
    )
    navigator.navigate(RatesScreens.CharacterPeople.route)
}

/**
 * Функция перехода на экран с поиском по жанру аниме
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchScreenByAnimeGenreId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_GENRE_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.ANIME
    )
    // удаляем из стэка данные о студии аниме, так как они там могут сохраниться
    navigator.removeData<Long>(key = SEARCH_SCREEN_STUDIO_ID)

    navigator.navigate(RatesScreens.Search.route)
}

/**
 * Функция перехода на экран с поиском по жанру аниме для AndroidTV
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchTvScreenByAnimeGenreId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_GENRE_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.ANIME
    )
    // удаляем из стэка данные о студии аниме, так как они там могут сохраниться
    navigator.removeData<Long>(key = SEARCH_SCREEN_STUDIO_ID)

    navigator.navigate(CalendarTvScreens.SearchTv.route)
}

/**
 * Функция перехода на экран с поиском по жанру манги
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchScreenByMangaGenreId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_GENRE_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.MANGA
    )
    navigator.navigate(RatesScreens.Search.route)
}

/**
 * Функция перехода на экран с поиском по жанру ранобэ
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchScreenByRanobeGenreId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_GENRE_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.RANOBE
    )
    navigator.navigate(RatesScreens.Search.route)
}

/**
 * Функция перехода на экран с поиском по студии аниме
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchScreenByAnimeStudioId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_STUDIO_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.ANIME
    )
    // удаляем из стэка данные жанра, так как они там могут сохраниться
    navigator.removeData<Long>(key = SEARCH_SCREEN_GENRE_ID)

    navigator.navigate(RatesScreens.Search.route)
}

/**
 * Функция перехода на экран с поиском по студии аниме для AndroidTV
 *
 * @param id идентикационный номер жанра аниме
 * @param navigator контроллер навигации
 */
internal fun navigateSearchTvScreenByAnimeStudioId(
    id: Long?,
    navigator: NavHostController
) {
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_STUDIO_ID,
        value = id
    )
    navigator.setData(
        key = AppKeys.SEARCH_SCREEN_TYPE,
        value = SearchType.ANIME
    )
    // удаляем из стэка данные жанра, так как они там могут сохраниться
    navigator.removeData<Long>(key = SEARCH_SCREEN_GENRE_ID)

    navigator.navigate(RatesTvScreens.SearchTv.route)
}

/**
 * Функция перехода на экран с видеоплеера
 *
 * @param animeId идентикационный номер аниме
 * @param animeNameEng название аниме на английском
 * @param animeNameRu название аниме на русском
 * @param translation модель данных для загрузки эпизода
 * @param context контекст приложения для запуска Активити видеоплеера
 */
internal fun navigateVideoPlayerScreen(
    animeId: Long?,
    animeNameEng: String?,
    animeNameRu: String?,
    translation: ShimoriTranslationModel,
    context: Context
) {
    val intent = Intent(context, VideoActivity::class.java)
    intent.putExtra(AppKeys.VIDEOS_SCREEN_ANIME_ID, animeId)
    intent.putExtra(AppKeys.VIDEO_SCREEN_ANIME_ENG_TITLE, animeNameEng)
    intent.putExtra(AppKeys.VIDEO_SCREEN_ANIME_RU_TITLE, animeNameRu)
    intent.putExtra(AppKeys.VIDEO_SCREEN_TRANSLATION_MODEL, translation)
    context.startActivity(intent)
}