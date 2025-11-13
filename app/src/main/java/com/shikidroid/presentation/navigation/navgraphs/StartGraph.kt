package com.shikidroid.presentation.navigation.navgraphs

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.BaseUrl
import com.shikidroid.di.AppComponent
import com.shikidroid.presentation.navigation.navgraphs.bottom.BottomBarGraph
import com.shikidroid.presentation.navigation.navgraphs.bottom.BottomBarGuestGraph
import com.shikidroid.presentation.navigation.navgraphs.bottom.BottomMalGraph
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.presentation.screens.SplashScreen
import com.shikidroid.presentation.screens.WebViewAuthScreen
import com.shikidroid.presentation.navigation.navgraphs.tv.TvRailGraph
import com.shikidroid.presentation.navigation.navgraphs.tv.TvRailGuestGraph
import com.shikidroid.presentation.navigation.navgraphs.tv.TvRailMalGraph
import com.shikidroid.presentation.screens.EnterScreen
import com.shikidroid.presentation.screens.WebViewScreen
import com.shikidroid.presentation.screens.items.NoInternetConnectionMessage
import com.shikidroid.presentation.screens.tv.EnterTvScreen
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.presentation.viewmodels.SplashScreenViewModel
import com.shikidroid.presentation.viewmodels.WebViewAuthViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.viewmodel.viewModelFactory

@Composable
internal fun StartScreens(component: AppComponent) {

    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    /** состояние подключения к интернету */
    val isNetworkConnect = component.getNetworkConnectionObserver().observeAsState(initial = true)

    NavHost(
        navController = navController,
        startDestination = StartScreens.Splash.route,
        modifier = Modifier
            .background(color = ShikidroidTheme.colors.background)
    ) {

        composable(route = StartScreens.Splash.route) {

            val viewModel: SplashScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    SplashScreenViewModel(
                        authInteractor = component.getAuthInteractor(),
                        tokenLocalInteractor = component.getTokenInteractor(),
                        userInteractor = component.getUserInteractor(),
                        userLocalInteractor = component.getUserProfileStorageInteractor(),
                    )
                }
            )

            SplashScreen(navigator = navController, viewModel = viewModel)
        }

        composable(route = StartScreens.Enter.route) {

            EnterScreen(navigator = navController)
        }

        composable(route = StartScreens.WebViewAuth.route) {

            val viewModel: WebViewAuthViewModel by it.viewModelFactory(
                viewModelCreator = {
                    WebViewAuthViewModel(
                        authInteractor = component.getAuthInteractor(),
                        tokenLocalInteractor = component.getTokenInteractor(),
                        userInteractor = component.getUserInteractor(),
                        userLocalInteractor = component.getUserProfileStorageInteractor()
                    )
                }
            )

            WebViewAuthScreen(
                navigator = navController,
                url = BaseUrl.AUTH_URL,
                viewModel = viewModel
            )
        }

        composable(route = StartScreens.WebView.route) {

            WebViewScreen(
                url = BaseUrl.SHIKIMORI_BASE_URL,
                navigator = navController
            )
        }

        composable(route = StartScreens.Bottom.route) {

            val rateViewModel: RateScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    RateScreenViewModel(
                        userInteractor = component.getUserInteractor(),
                        userLocalInteractor = component.getUserProfileStorageInteractor(),
                        prefs = component.getSharedPreferencesProvider()
                    )
                }
            )

            BottomBarGraph(
                component = component,
                viewModel = rateViewModel
            )
        }

        composable(route = StartScreens.BottomGuest.route) {

            BottomBarGuestGraph(component = component)
        }

        ///////////////////////////////////////////////////////////////////////////
        // Shikidroid TV
        ///////////////////////////////////////////////////////////////////////////

        composable(route = StartScreens.EnterTv.route) {

            EnterTvScreen(navigator = navController)
        }

        composable(route = StartScreens.TvRail.route) {

            val rateViewModel: RateScreenViewModel by it.viewModelFactory(
                viewModelCreator = {
                    RateScreenViewModel(
                        userInteractor = component.getUserInteractor(),
                        userLocalInteractor = component.getUserProfileStorageInteractor(),
                        prefs = component.getSharedPreferencesProvider()
                    )
                }
            )

            TvRailGraph(
                component = component,
                rateViewModel = rateViewModel
            )
        }

        composable(route = StartScreens.TvRailGuest.route) {

            TvRailGuestGraph(component = component)
        }

        ///////////////////////////////////////////////////////////////////////////
        // My Anime List
        ///////////////////////////////////////////////////////////////////////////

        composable(route = StartScreens.BottomMal.route) {

            BottomMalGraph(component = component)
        }

        ///////////////////////////////////////////////////////////////////////////
        // My Anime List TV
        ///////////////////////////////////////////////////////////////////////////

        composable(route = StartScreens.TvRailMal.route) {

            TvRailMalGraph(component = component)
        }

    }

    NoInternetConnectionMessage(showMessage = !isNetworkConnect.value)
}