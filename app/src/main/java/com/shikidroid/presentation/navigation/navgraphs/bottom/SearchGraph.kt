package com.shikidroid.presentation.navigation.navgraphs.bottom

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.AppComponent
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.presentation.CharacterPeopleScreenType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.SearchScreens
import com.shikidroid.presentation.screens.CharacterPeopleScreen
import com.shikidroid.presentation.screens.DetailsScreen
import com.shikidroid.presentation.screens.EpisodeScreen
import com.shikidroid.presentation.screens.SearchScreen
import com.shikidroid.presentation.screens.WebViewScreen
import com.shikidroid.presentation.viewmodels.CharacterPeopleScreenViewModel
import com.shikidroid.presentation.viewmodels.DetailsScreenViewModel
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.uikit.getData
import com.shikidroid.uikit.viewmodel.viewModelFactory

/**
 * Граф навигации для экрана "Поиск"
 *
 * @param component компонент доставки зависимостей
 * @param rateViewModel общая вью модель экранов, хранящая пользовательский список
 */
@Composable
internal fun SearchGraph(
    component: AppComponent,
    rateViewModel: RateScreenViewModel?
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SearchScreens.Search.route
    ) {

        composable(route = SearchScreens.Search.route) {

            val viewModel: SearchScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    SearchScreenViewModel(
                        screenSearchType = navController.getData(key = AppKeys.SEARCH_SCREEN_TYPE) as SearchType?,
                        genreId = navController.getData(key = AppKeys.SEARCH_SCREEN_GENRE_ID) as Long?,
                        studioId = navController.getData(key = AppKeys.SEARCH_SCREEN_STUDIO_ID) as Long?,
                        animeInteractor = component.getAnimeInteractor(),
                        mangaInteractor = component.getMangaInteractor(),
                        ranobeInteractor = component.getRanobeInteractor(),
                        characterInteractor = component.getCharacterInteractor(),
                        peopleInteractor = component.getPeopleInteractor(),
                        prefs = component.getSharedPreferencesProvider()
                    )
                }
            )

            SearchScreen(
                prefs = component.getSharedPreferencesProvider(),
                viewModel = viewModel,
                navigator = navController
            )
        }

        composable(route = SearchScreens.Details.route) {

            val viewModel: DetailsScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    DetailsScreenViewModel(
                        id = navController.getData(key = AppKeys.DETAILS_SCREEN_ID) as? Long,
                        screenType = navController.getData(key = AppKeys.DETAILS_SCREEN_TYPE) as? DetailsScreenType,
                        animeInteractor = component.getAnimeInteractor(),
                        mangaInteractor = component.getMangaInteractor(),
                        ranobeInteractor = component.getRanobeInteractor(),
                        commentsInteractor = component.getCommentsInteractor()
                    )
                }
            )

            DetailsScreen(
                screenType = navController.getData(key = AppKeys.DETAILS_SCREEN_TYPE) as? DetailsScreenType,
                prefs = component.getSharedPreferencesProvider(),
                navigator = navController,
                viewModel = viewModel,
                rateHolder = rateViewModel
            )
        }

        composable(route = SearchScreens.CharacterPeople.route) {

            val viewModel: CharacterPeopleScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    CharacterPeopleScreenViewModel(
                        id = navController.getData(key = AppKeys.CHARACTER_PEOPLE_SCREEN_ID) as? Long,
                        screenType = navController.getData(key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE) as? CharacterPeopleScreenType,
                        characterInteractor = component.getCharacterInteractor(),
                        peopleInteractor = component.getPeopleInteractor()
                    )
                }
            )

            CharacterPeopleScreen(
                screenType = navController.getData(key = AppKeys.CHARACTER_PEOPLE_SCREEN_TYPE) as? CharacterPeopleScreenType,
                navigator = navController,
                viewModel = viewModel
            )
        }

        composable(route = SearchScreens.WebView.route) {

            WebViewScreen(
                url = navController.getData(key = AppKeys.WEBVIEW_SCREEN_URL) as? String,
                navigator = navController
            )
        }

        composable(route = SearchScreens.Episode.route) {

            val viewModel: EpisodeScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    EpisodeScreenViewModel(
                        animeId = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_ID) as? Long,
                        animeUserId = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_ID_USER_RATE) as? Long,
                        animeNameEng = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_TITLE) as? String,
                        userInteractor = component.getUserInteractor(),
                        shimoriVideoInteractor = component.getShimoriVideoInteractor(),
                        downloadVideoInteractor = component.getDownloadVideoInteractor()
                    )
                }
            )

            EpisodeScreen(
                animeNameRu = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_RU_TITLE) as? String,
                animeImageUrl = navController.getData(AppKeys.EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE_URL) as? String,
                navigator = navController,
                viewModel = viewModel
            )
        }
    }
}