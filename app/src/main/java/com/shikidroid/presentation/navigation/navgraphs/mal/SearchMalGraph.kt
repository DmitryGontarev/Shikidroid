package com.shikidroid.presentation.navigation.navgraphs.mal

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.AppComponent
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.SearchScreens
import com.shikidroid.presentation.screens.EpisodeScreen
import com.shikidroid.presentation.screens.SearchScreen
import com.shikidroid.presentation.screens.mal.DetailsMalScreen
import com.shikidroid.presentation.screens.mal.SearchMalScreen
import com.shikidroid.presentation.viewmodels.mal.DetailsScreenMalViewModel
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.presentation.viewmodels.mal.SearchScreenMalViewModel
import com.shikidroid.uikit.getData
import com.shikidroid.uikit.viewmodel.viewModelFactory

/**
 * Граф навигации для экрана "Поиск" MyAnimeList
 *
 * @param component компонент доставки зависимостей
 * @param rateViewModel общая вью модель экранов, хранящая пользовательский список
 */
@Composable
internal fun SearchMalGraph(
    component: AppComponent,
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SearchScreens.Search.route
    ) {

        composable(route = SearchScreens.Search.route) {

            val viewModel: SearchScreenMalViewModel by it.viewModelFactory(
                viewModelCreator = {
                    SearchScreenMalViewModel(
                        malInteractor = component.getMyAnimeListInteractor()
                    )
                }
            )

            SearchMalScreen(
                viewModel = viewModel,
                navigator = navController
            )
        }

        composable(route = SearchScreens.Details.route) {

            val viewModel: DetailsScreenMalViewModel by it.viewModelFactory(
                viewModelCreator = {
                    DetailsScreenMalViewModel(
                        id = navController.getData(key = AppKeys.DETAILS_SCREEN_ID) as? Long,
                        screenType = navController.getData(key = AppKeys.DETAILS_SCREEN_TYPE) as? DetailsScreenType,
                        malInteractor = component.getMyAnimeListInteractor()
                    )
                }
            )

            DetailsMalScreen(
                screenType = navController.getData(key = AppKeys.DETAILS_SCREEN_TYPE) as? DetailsScreenType,
                prefs = component.getSharedPreferencesProvider(),
                navigator = navController,
                viewModel = viewModel
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