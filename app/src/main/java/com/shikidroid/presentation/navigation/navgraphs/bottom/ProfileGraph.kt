package com.shikidroid.presentation.navigation.navgraphs.bottom

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.di.AppComponent
import com.shikidroid.presentation.navigation.sealedscreens.ProfileScreens
import com.shikidroid.presentation.screens.ProfileScreen
import com.shikidroid.presentation.viewmodels.ProfileScreenViewModel
import com.shikidroid.uikit.getData
import com.shikidroid.uikit.viewmodel.viewModelFactory

/**
 * Граф навигации для экрана "Профиль"
 *
 * @param component компонент доставки зависимостей
 */
@Composable
internal fun ProfileGraph(
    component:AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProfileScreens.Profile.route
    ) {

        composable(route = ProfileScreens.Profile.route) {

            val viewModel by it.viewModelFactory(
                viewModelCreator = {
                    ProfileScreenViewModel(
                        userId = navController.getData(key = AppKeys.USER_ID_PROFILE_KEY) as? Long,
                        userInteractor = component.getUserInteractor(),
                        profileStorageInteractor = component.getUserProfileStorageInteractor(),
                        prefs = component.getSharedPreferencesProvider()
                    )
                }
            )

            ProfileScreen(
                viewModel = viewModel,
                navigator = navController
            )
        }

        composable(route = ProfileScreens.Messages.route) {


        }
    }
}