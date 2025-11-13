package com.shikidroid.presentation.navigation.navgraphs.tv

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.AppComponent
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.tv.RatesTvScreens
import com.shikidroid.presentation.screens.tv.DetailsTvScreen
import com.shikidroid.presentation.screens.tv.EpisodesTvScreen
import com.shikidroid.presentation.screens.tv.RateTvScreen
import com.shikidroid.presentation.screens.tv.SearchTvScreen
import com.shikidroid.presentation.viewmodels.DetailsScreenViewModel
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.uikit.getData
import com.shikidroid.uikit.viewmodel.viewModelFactory

/**
 * Граф навигации для экрана "Списки" AndroidTV
 *
 * @param component компонент доставки зависимостей
 * @param rateViewModel общая вью модель экранов, хранящая пользовательский список
 */
@Composable
internal fun RateTvGraph(
    component: AppComponent,
    rateViewModel: RateScreenViewModel
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RatesTvScreens.RateTv.route
    ) {

        composable(route = RatesTvScreens.RateTv.route) {

            RateTvScreen(
                navigator = navController,
                viewModel = rateViewModel
            )
        }

        composable(route = RatesTvScreens.DetailsTv.route) {

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

            DetailsTvScreen(
                viewModel = viewModel,
                rateHolder = rateViewModel,
                navigator = navController
            )
        }

        composable(route = RatesTvScreens.EpisodeTv.route) {

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

            EpisodesTvScreen(
                animeNameRu = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_RU_TITLE) as? String,
                animeImageUrl = navController.getData(key = AppKeys.EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE_URL) as? String,
                navigator = navController,
                viewModel = viewModel
            )
        }

        composable(route = RatesTvScreens.SearchTv.route) {

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

            SearchTvScreen(viewModel = viewModel, navigator = navController)
        }
    }
}