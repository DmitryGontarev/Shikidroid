package com.shikidroid.presentation.navigation.navgraphs.tvmal

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.AppComponent
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.navigation.sealedscreens.tv.CalendarTvScreens
import com.shikidroid.presentation.screens.tv.CalendarTvScreen
import com.shikidroid.presentation.screens.tv.DetailsTvScreen
import com.shikidroid.presentation.screens.tv.EpisodesTvScreen
import com.shikidroid.presentation.screens.tvmal.CalendarTvMalScreen
import com.shikidroid.presentation.screens.tvmal.DetailsTvMalScreen
import com.shikidroid.presentation.viewmodels.CalendarViewModel
import com.shikidroid.presentation.viewmodels.DetailsScreenViewModel
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.presentation.viewmodels.mal.CalendarMalViewModel
import com.shikidroid.presentation.viewmodels.mal.DetailsScreenMalViewModel
import com.shikidroid.uikit.getData
import com.shikidroid.uikit.viewmodel.viewModelFactory

/**
 * Граф навигации для экрана "Календарь" AndroidTV
 *
 * @param component компонент доставки зависимостей
 * @param rateViewModel общая вью модель экранов, хранящая пользовательский список
 */
@Composable
internal fun CalendarTvMalGraph(
    component: AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CalendarTvScreens.CalendarTv.route
    ) {

        composable(route = CalendarTvScreens.CalendarTv.route) {

            val viewModel: CalendarMalViewModel by it.viewModelFactory(
                viewModelCreator = {
                    CalendarMalViewModel(
                        malInteractor = component.getMyAnimeListInteractor()
                    )
                }
            )

            CalendarTvMalScreen(
                navigator = navController,
                viewModel = viewModel
            )
        }

        composable(route = CalendarTvScreens.DetailsTv.route) {

            val viewModel: DetailsScreenMalViewModel by it.viewModelFactory(
                viewModelCreator = {
                    DetailsScreenMalViewModel(
                        id = navController.getData(key = AppKeys.DETAILS_SCREEN_ID) as? Long,
                        screenType = navController.getData(key = AppKeys.DETAILS_SCREEN_TYPE) as? DetailsScreenType,
                        malInteractor = component.getMyAnimeListInteractor()
                    )
                }
            )

            DetailsTvMalScreen(
                viewModel = viewModel,
                navigator = navController
            )
        }

        composable(route = CalendarTvScreens.EpisodeTv.route) {

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
    }
}