package com.shikidroid.presentation.navigation.navgraphs.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shikidroid.di.AppComponent
import com.shikidroid.presentation.navigation.navgraphs.tvmal.CalendarTvMalGraph
import com.shikidroid.presentation.navigation.navgraphs.tvmal.SearchTvMalGraph
import com.shikidroid.presentation.navigation.sealedscreens.tv.TvRailScreens
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.TvRailLayout
import com.shikidroid.uikit.components.TvSelectable

@Composable
internal fun TvRailGraph(
    component: AppComponent,
    rateViewModel: RateScreenViewModel
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    /** список экранов */
    val screens = listOf(
        TvRailScreens.RatesTv,
        TvRailScreens.CalendarTv,
        TvRailScreens.SearchTv
    )

    /** стэк навигации */
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    /** текущий экран стэка */
    val currentDestination = navBackStackEntry?.destination

    /** флаг фокуса на боковой навигации */
    var railFocus by remember {
        mutableStateOf(false)
    }

    TvRailLayout(
        isShift = railFocus,
        shiftOpen = oneHundredTwentyDP,
        backgroundColor = ShikidroidTheme.colors.background,
        tvRail = { width ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = width)
                    .background(color = ShikidroidTheme.colors.background)
                    .onFocusChanged { focusState ->
                        railFocus = focusState.isFocused || focusState.hasFocus
                    },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                screens.forEach { screen ->
                    AddItem(
                        screen = screen,
                        currentDestination = currentDestination,
                        railFocus = railFocus,
                        navigator = navController
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = TvRailScreens.RatesTv.route,
            modifier = Modifier
                .background(color = ShikidroidTheme.colors.background)
        ) {

            composable(route = TvRailScreens.RatesTv.route) {

                RateTvGraph(
                    component = component,
                    rateViewModel = rateViewModel
                )
            }

            composable(route = TvRailScreens.CalendarTv.route) {

                CalendarTvGraph(
                    component = component,
                    rateViewModel = rateViewModel
                )
            }

            composable(route = TvRailScreens.SearchTv.route) {

                SearchTvGraph(
                    component = component,
                    rateViewModel = rateViewModel
                )
            }
        }
    }
}

@Composable
internal fun TvRailGuestGraph(
    component: AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    /** список экранов */
    val screens = listOf(
        TvRailScreens.CalendarTv,
        TvRailScreens.SearchTv
    )

    /** стэк навигации */
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    /** текущий экран стэка */
    val currentDestination = navBackStackEntry?.destination

    /** флаг фокуса на боковой навигации */
    var railFocus by remember {
        mutableStateOf(false)
    }

    TvRailLayout(
        isShift = railFocus,
        backgroundColor = Color.Transparent,
        tvRail = { width ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = width)
                    .background(color = ShikidroidTheme.colors.background)
                    .onFocusChanged { focusState ->
                        railFocus = focusState.isFocused || focusState.hasFocus
                    },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                screens.forEach { screen ->
                    AddItem(
                        screen = screen,
                        currentDestination = currentDestination,
                        railFocus = railFocus,
                        navigator = navController
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = TvRailScreens.CalendarTv.route,
            modifier = Modifier
                .background(color = ShikidroidTheme.colors.background)
        ) {

            composable(route = TvRailScreens.CalendarTv.route) {

                CalendarTvGraph(
                    component = component,
                    rateViewModel = null
                )
            }

            composable(route = TvRailScreens.SearchTv.route) {

                SearchTvGraph(
                    component = component,
                    rateViewModel = null
                )
            }
        }
    }
}

@Composable
internal fun TvRailMalGraph(
    component: AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    /** список экранов */
    val screens = listOf(
        TvRailScreens.CalendarTv,
        TvRailScreens.SearchTv
    )

    /** стэк навигации */
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    /** текущий экран стэка */
    val currentDestination = navBackStackEntry?.destination

    /** флаг фокуса на боковой навигации */
    var railFocus by remember {
        mutableStateOf(false)
    }

    TvRailLayout(
        isShift = railFocus,
        backgroundColor = Color.Transparent,
        tvRail = { width ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width = width)
                    .background(color = ShikidroidTheme.colors.background)
                    .onFocusChanged { focusState ->
                        railFocus = focusState.isFocused || focusState.hasFocus
                    },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                screens.forEach { screen ->
                    AddItem(
                        screen = screen,
                        currentDestination = currentDestination,
                        railFocus = railFocus,
                        navigator = navController
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = TvRailScreens.CalendarTv.route,
            modifier = Modifier
                .background(color = ShikidroidTheme.colors.background)
        ) {

            composable(route = TvRailScreens.CalendarTv.route) {

                CalendarTvMalGraph(
                    component = component
                )
            }

            composable(route = TvRailScreens.SearchTv.route) {

                SearchTvMalGraph(
                    component = component
                )
            }
        }
    }
}

@Composable
internal fun ColumnScope.AddItem(
    screen: TvRailScreens,
    currentDestination: NavDestination?,
    railFocus: Boolean,
    navigator: NavHostController
) {

    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val backgroundColor = if (selected) Color.Transparent else Color.Transparent

    TvSelectable { interactionSource, isFocused, scale ->

        val contentColor =
            if (selected) ShikidroidTheme.colors.secondary else ShikidroidTheme.colors.onSurface

        Box(
            modifier = Modifier
                .wrapContentSize()
                .focusable(true, interactionSource)
                .scale(scale = scale.value)
                .clip(CircleShape)
                .background(
                    when {
                        isFocused -> ShikidroidTheme.colors.secondaryVariant
                        else -> Color.Transparent
                    }
                )
                .clickable(
                    onClick = {
                        navigator.navigate(screen.route) {
                            popUpTo(navigator.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(end = threeDP),
                    painter = painterResource(id = if (selected) screen.iconFocused else screen.icon),
                    contentDescription = "Иконка",
                    tint = contentColor
                )
                if (railFocus) {
                    Text(
                        text = screen.title,
                        color = contentColor
                    )
                }
            }
        }
    }
}