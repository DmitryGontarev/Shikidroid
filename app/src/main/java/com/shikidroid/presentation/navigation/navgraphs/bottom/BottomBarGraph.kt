package com.shikidroid.presentation.navigation.navgraphs.bottom

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shikidroid.R
import com.shikidroid.di.AppComponent
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.findMainActivity
import com.shikidroid.presentation.BottomSheetType
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.navgraphs.mal.CalendarMalGraph
import com.shikidroid.presentation.navigation.navgraphs.mal.SearchMalGraph
import com.shikidroid.presentation.navigation.sealedscreens.BottomBarScreens
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.ui.ALL_READED
import com.shikidroid.ui.ALL_WATCHED
import com.shikidroid.ui.NOT_YET_RELEASED_TEXT
import com.shikidroid.ui.theme.BackgroundUnselectedColor
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.containsOnlyZero
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.StringUtils.firstNotZeroIndex

@Composable
internal fun BottomBarGraph(
    component: AppComponent,
    viewModel: RateScreenViewModel
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    /** тип текущего списка - аниме/манга */
    val listType by viewModel.listType.observeAsState(initial = SectionType.ANIME)

    /** состояние показа нижней шторки */
    val isBottomSheetVisible by viewModel.isBottomSheetVisible.observeAsState(initial = false)

    /** тип нижней шторки */
    val bottomSheetType by viewModel.bottomSheetType.observeAsState(initial = BottomSheetType.RATE_LIST_EDIT)

    /** флаг показа тост сообщения */
    val showToast by viewModel.showToast.observeAsState(initial = false)

    if (showToast) {
        Toast(
            text = viewModel.toastText.value,
            callback = {
                viewModel.resetToast()
            }
        )
    }

    /** контекст */
    val context = LocalContext.current

    BackHandler() {
        if (isBottomSheetVisible) {
            viewModel.hideBottomSheet()
            return@BackHandler
        } else {
            context.findMainActivity()?.finish()
        }
    }

    BoxWithSlideBottomSheet(
        isBottomSheetOpen = isBottomSheetVisible,
        sheetElevation = oneDP,
        sheetBackgroundColor = ShikidroidTheme.colors.background,
        sheetBorderStrokeColor = ShikidroidTheme.colors.primaryBorderVariant,
        sheetShape = RoundedCorner30dp,
        sheetContent = {
            if (bottomSheetType == BottomSheetType.RATE_LIST_EDIT) {
                BottomSheetRateList(
                    viewModel = viewModel
                )
            } else {
                BottomSheetEdit(
                    listType = listType,
                    viewModel = viewModel
                )
            }
            SpacerNavigationBar()
        },
        onDismiss = {
            viewModel.hideBottomSheet()
        }
    ) {
        BoxWithSlideBottomBar(
            backgroundColor = ShikidroidTheme.colors.background,
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomBarScreens.Rates.route
            ) {

                composable(route = BottomBarScreens.Rates.route) {
                    RateGraph(
                        component = component,
                        rateViewModel = viewModel
                    )
                }

                composable(route = BottomBarScreens.Calendar.route) {
                    CalendarGraph(
                        component = component,
                        rateViewModel = viewModel
                    )
                }

                composable(route = BottomBarScreens.Search.route) {
                    SearchGraph(
                        component = component,
                        rateViewModel = viewModel
                    )
                }

                composable(route = BottomBarScreens.Main.route) {
                    MainGraph()
                }

                composable(route = BottomBarScreens.Profile.route) {
                    ProfileGraph(
                        component = component
                    )
                }
            }
        }
    }
}

@Composable
internal fun BottomBarGuestGraph(
    component: AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    BoxWithSlideBottomBar(
        backgroundColor = ShikidroidTheme.colors.background,
        bottomBar = {
            BottomBar(
                authorizationStatus = UserAuthStatus.GUEST,
                navController = navController
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomBarScreens.Calendar.route
        ) {

            composable(route = BottomBarScreens.Calendar.route) {
                CalendarGraph(
                    component = component,
                    rateViewModel = null
                )
            }

            composable(route = BottomBarScreens.Search.route) {
                SearchGraph(
                    component = component,
                    rateViewModel = null
                )
            }
        }
    }
}

@Composable
internal fun BottomMalGraph(
    component: AppComponent
) {
    /** контроллер навигации по экранам */
    val navController = rememberNavController()

    BoxWithSlideBottomBar(
        backgroundColor = ShikidroidTheme.colors.background,
        bottomBar = {
            BottomBar(
                authorizationStatus = UserAuthStatus.GUEST,
                navController = navController
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomBarScreens.Calendar.route
        ) {

            composable(route = BottomBarScreens.Calendar.route) {
                CalendarMalGraph(
                    component = component
                )
            }

            composable(route = BottomBarScreens.Search.route) {
                SearchMalGraph(
                    component = component
                )
            }
        }
    }
}

@Composable
internal fun BottomBar(
    authorizationStatus: UserAuthStatus = UserAuthStatus.AUTHORIZED,
    navController: NavHostController
) {

    /** список экранов для бара нижней навигации */
    val bottomScreens =
        if (authorizationStatus == UserAuthStatus.AUTHORIZED) {
            listOf(
                BottomBarScreens.Rates,
                BottomBarScreens.Calendar,
                BottomBarScreens.Search,
                BottomBarScreens.Main,
                BottomBarScreens.Profile
            )
        } else {
            listOf(
                BottomBarScreens.Calendar,
                BottomBarScreens.Search
            )
        }

    /** стэк навигации */
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    /** текущий экран стэка */
    val currentDestination = navBackStackEntry?.destination

    BottomNavigationBar(
        modifier = Modifier
            .wrapContentSize()
            .padding(sevenDP),
        backgroundColor = ShikidroidTheme.colors.surface,
        shape = ShikidroidTheme.shapes.roundedCorner30dp,
        border = BorderStroke(
            width = oneDP,
            color = ShikidroidTheme.colors.primaryBorderVariant
        ),
        elevation = ShikidroidTheme.elevation
    ) { rowScope ->
        bottomScreens.forEach { screen ->
            rowScope.AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navigator = navController
            )
        }
    }
}

@Composable
internal fun RowScope.AddItem(
    screen: BottomBarScreens,
    currentDestination: NavDestination?,
    navigator: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val backgroundColor = if (selected) Color.Transparent else Color.Transparent

    val contentColor =
        if (selected) ShikidroidTheme.colors.secondary else ShikidroidTheme.colors.onSurface

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(backgroundColor)
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
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = if (selected) screen.iconFocused else screen.icon),
                contentDescription = "Иконка",
                tint = contentColor
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Нижняя шторка со списком статусов пользовательского рейтинга
 *
 * используется для перемещения элементов между списками
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun BottomSheetRateList(
    viewModel: RateScreenViewModel
) {

    /** статусы списка для показа в шторке */
    val bottomItems = remember {
        RateStatus.values().filter {
            it != RateStatus.UNKNOWN
        }
    }

    /** статус выбранного элемента в пользовательском списке */
    val currentItemStatus by viewModel.currentItemStatus.observeAsState(initial = RateStatus.PLANNED)

    /** текущий выбранный элемент для передачи в шторку */
    val currentItem by viewModel.currentItem.observeAsState(
        initial = RateModel(
            id = 1,
            score = 0,
            status = RateStatus.PLANNED,
            text = "",
            episodes = 0,
            chapters = 0,
            volumes = 0,
            textHtml = "",
            rewatches = 0,
            createdDateTime = null,
            updatedDateTime = null,
            user = null,
            anime = null,
            manga = null
        )
    )

    Text(
        modifier = Modifier
            .padding(fourteenDP),
        text = (currentItem?.anime?.nameRu ?: currentItem?.manga?.nameRu).orEmpty(),
        textAlign = TextAlign.Left,
        color = ShikidroidTheme.colors.onPrimary
    )

    Divider(
        color = ShikidroidTheme.colors.primaryBorderVariant
    )

    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {

        items(
            items = bottomItems,
            key = { it.ordinal }
        ) { item ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fiftyDP)
                    .padding(start = fourteenDP, top = threeDP, bottom = threeDP)
                    .background(
                        color =
                        if (currentItemStatus == item) {
                            item.toBackgroundColor()
                        } else {
                            Color.Transparent
                        },
                        shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomStart30dp,
                    )
                    .clickable {
                        viewModel.currentItemStatus.value = item
                        if (currentItem?.status != null) {
                            currentItem?.let {
                                viewModel.changeRateStatus(
                                    rateModel = it,
                                    newStatus = item
                                )
                            }
                        } else {
                            currentItem?.let {
                                viewModel.createRateStatus(it)
                            }
                        }
                        viewModel.hideBottomSheet()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = fourteenDP),
                    painter = painterResource(id = item.toDrawable()),
                    contentDescription = null,
                    tint =
                    if (currentItemStatus == item) {
                        item.toColor()
                    } else {
                        ShikidroidTheme.colors.onPrimary
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(start = sevenDP),
                    text =
                    if (currentItem?.anime != null) {
                        item.toAnimePresentationString()
                    } else {
                        item.toMangaPresentationString()
                    },
                    textAlign = TextAlign.Center,
                    color =
                    if (currentItemStatus == item) {
                        item.toColor()
                    } else {
                        ShikidroidTheme.colors.onPrimary
                    }
                )
            }
        }
    }
}

/**
 * Нижняя шторка с параметрами редактирования элемента списка аниме - манги/ранобэ
 *
 * используется для изменения элемента списка
 *
 * @param listType тип текущего списка - аниме/манга
 * @param viewModel вью модель экрана
 */
@Composable
internal fun BottomSheetEdit(
    listType: SectionType,
    viewModel: RateScreenViewModel
) {

    /** статусы списка для показа в шторке */
    val bottomItems = remember {
        RateStatus.values().filter {
            it != RateStatus.UNKNOWN
        }
    }

    /** статус выбранного элемента в пользовательском списке */
    val currentItemStatus by viewModel.currentItemStatus.observeAsState(initial = RateStatus.PLANNED)

    /** текущий выбранный элемент для передачи в шторку */
    val currentItem by viewModel.currentItem.observeAsState(
        initial = RateModel(
            id = 1,
            score = 0,
            status = RateStatus.PLANNED,
            text = "",
            episodes = 0,
            chapters = 0,
            volumes = 0,
            textHtml = "",
            rewatches = 0,
            createdDateTime = null,
            updatedDateTime = null,
            user = null,
            anime = null,
            manga = null
        )
    )

    /** оценка аниме/манги */
    val ratingBarScore by viewModel.ratingBarScore.observeAsState(initial = 0f)

    /** количество просмотренных или прочитанных глав/эпизодов */
    val episodeChapterCount by viewModel.episodeChapterCount.observeAsState(initial = "")

    /** количество повторных просмотров/прочитываний глав/эпизодов */
    val reWatchReReadCount by viewModel.reWatchReReadCount.observeAsState(initial = "")

    /** флаг показа ошибки ввода просмотренных/прочитанных эпизодов/глав */
    val isEpisodeChapterError by viewModel.isEpisodeChapterError.observeAsState(initial = false)

    /** флаг показа ошибки ввода количества пересмотров/перечитываний */
    val isReWatchesError by viewModel.isReWatchesError.observeAsState(initial = false)

    /** комментарий к списку */
    val itemListComment by viewModel.itemListComment.observeAsState(initial = "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(fourteenDP)
    ) {

        Text(
            modifier = Modifier
                .weight(0.95f),
            text = (currentItem?.anime?.nameRu ?: currentItem?.manga?.nameRu).orEmpty(),
            textAlign = TextAlign.Start,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Row(
            modifier = Modifier
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = fourteenDP)
                    .clickable {
                        currentItem?.let { viewModel.deleteRateModel(it) }
                        viewModel.hideBottomSheet()
                    },
                painter = painterResource(id = R.drawable.ic_delete),
                tint = ShikidroidTheme.colors.onPrimary,
                contentDescription = null
            )
            Icon(
                modifier = Modifier
                    .padding(end = sevenDP)
                    .clickable {
                        if (currentItem?.status != null) {
                            currentItem?.let { viewModel.changeRateModel(it) }
                        } else {
                            currentItem?.let { viewModel.createRateStatus(it) }
                        }
                        viewModel.hideBottomSheet()
                    },
                painter = painterResource(id = R.drawable.ic_check),
                tint = ShikidroidTheme.colors.onPrimary,
                contentDescription = null
            )
        }

    }


    Divider(
        color = ShikidroidTheme.colors.primaryBorderVariant
    )

    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = fourteenDP),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        gridItems(
            data = bottomItems,
            columnCount = 3,
            modifier = Modifier
        ) { item ->

            RoundedIconButton(
                modifier = Modifier
                    .padding(sevenDP),
                onClick = {
                    viewModel.currentItemStatus.value = item
                },
                backgroundColor =
                if (currentItemStatus == item) {
                    item.toBackgroundColor()
                } else {
                    BackgroundUnselectedColor
                },
                icon = item.toDrawable(),
                tint =
                if (currentItemStatus == item) {
                    item.toColor()
                } else {
                    ShikidroidTheme.colors.onPrimary
                },
                text =
                if (currentItem?.anime != null) {
                    item.toAnimePresentationString()
                } else {
                    item.toMangaPresentationString()
                },
                textStyle = ShikidroidTheme.typography.body12sp,
                textColor = ShikidroidTheme.colors.onPrimary
            )
        }

        item {
            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )
        }

        item {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(fourteenDP),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .padding(sevenDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${ratingBarScore?.toInt() ?: 0.0}",
                        color = ShikidroidTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = ratingBarScore.toScoreString(),
                        style = ShikidroidTheme.typography.body12sp,
                        color = ShikidroidTheme.colors.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
                ShikiRatingBar(
                    value = ratingBarScore ?: 0.0f,
                    numStars = 10,
                    activeColor = ShikidroidTheme.colors.secondary,
                    inactiveColor = ShikidroidTheme.colors.onBackground,
                    onValueChange = {
                        viewModel.ratingBarScore.value = it
                    },
                    onRatingChanged = {}
                )
            }
        }

        item {
            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )
        }

        item {
            Row(
                modifier = Modifier
                    .padding(sevenDP)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                FilteredTextField(
                    modifier = Modifier
                        .width(oneHundredNinetyDP),
                    value = episodeChapterCount,
                    onValueChange = {
                        viewModel.isEpisodeChapterError.value = false
                        if (it.contains(StringUtils.DIGITS_REGEX)) {
                            when {
                                it.containsOnlyZero() -> {
                                    viewModel.episodeChapterCount.value = "0"
                                }
                                it.length > 1 && it.first() == '0' -> {
                                    viewModel.episodeChapterCount.value =
                                        it.substring(it.firstNotZeroIndex()).deleteEmptySpaces()
                                }
                                it.deleteEmptySpaces().toInt() > (
                                        if (listType == SectionType.ANIME) {
                                            when (currentItem?.anime?.status) {
                                                AiredStatus.ANONS -> 0
                                                AiredStatus.ONGOING -> currentItem?.anime?.episodesAired
                                                    ?: 0
                                                AiredStatus.RELEASED -> currentItem?.anime?.episodes
                                                    ?: 0
                                                else -> currentItem?.anime?.episodes ?: 0
                                            }
                                        } else {
                                            when (currentItem?.manga?.status) {
                                                AiredStatus.ANONS -> 0
                                                AiredStatus.ONGOING -> Int.MAX_VALUE
                                                AiredStatus.RELEASED -> currentItem?.manga?.chapters
                                                    ?: 0
                                                else -> Int.MAX_VALUE
                                            }
                                        }) -> {
                                    viewModel.isEpisodeChapterError.value = true
                                    viewModel.episodeChapterCount.value =
                                        if (listType == SectionType.ANIME) {
                                            when (currentItem?.anime?.status) {
                                                AiredStatus.ANONS, AiredStatus.ONGOING -> {
                                                    currentItem?.anime?.episodesAired.toString().deleteEmptySpaces()
                                                }
                                                else -> currentItem?.anime?.episodes.toString().deleteEmptySpaces()
                                            }
                                        } else {
                                            currentItem?.manga?.chapters.toString().deleteEmptySpaces()
                                        }
                                }
                                else -> {
                                    viewModel.episodeChapterCount.value = it.deleteEmptySpaces()
                                }
                            }
                        } else {
                            viewModel.episodeChapterCount.value = it.deleteEmptySpaces()
                        }
                    },
                    textStyle = ShikidroidTheme.typography.body16sp,
                    trailingIcon = {
                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(end = sevenDP),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {

                            RoundedIconButton(
                                onClick = {
                                    if (episodeChapterCount.contains(StringUtils.DIGITS_REGEX)) {
                                        viewModel.isEpisodeChapterError.value = false
                                        if (episodeChapterCount.toInt() < 1) {
                                            viewModel.episodeChapterCount.value = "0"
                                        } else {
                                            viewModel.episodeChapterCount.value =
                                                viewModel.episodeChapterCount.value?.toInt()
                                                    ?.minus(1).toString()
                                        }
                                    }
                                },
                                icon = R.drawable.ic_minus,
                                isIcon = true,
                                tint = ShikidroidTheme.colors.onPrimary
                            )

                            RoundedIconButton(
                                onClick = {
                                    if (episodeChapterCount == "") {
                                        viewModel.episodeChapterCount.value = "0"
                                    }
                                    if (episodeChapterCount.contains(StringUtils.DIGITS_REGEX)) {
                                        when {
                                            episodeChapterCount.toInt() >= (
                                                    if (listType == SectionType.ANIME) {
                                                        when (currentItem?.anime?.status) {
                                                            AiredStatus.ANONS, AiredStatus.ONGOING -> {
                                                                viewModel.isEpisodeChapterError.value = true
                                                                currentItem?.anime?.episodesAired
                                                                    ?: 0
                                                            }
                                                            else -> {
                                                                viewModel.isEpisodeChapterError.value = true
                                                                currentItem?.anime?.episodes ?: 0
                                                            }
                                                        }
                                                    } else {
                                                        when (currentItem?.manga?.status) {
                                                            AiredStatus.ANONS, AiredStatus.RELEASED -> {
                                                                viewModel.isEpisodeChapterError.value = true
                                                                currentItem?.manga?.chapters ?: 0
                                                            }
                                                            else -> {
                                                                viewModel.isEpisodeChapterError.value = false
                                                                Int.MAX_VALUE
                                                            }
                                                        }
                                                    }) -> {
                                                return@RoundedIconButton
                                            }
                                            else -> {
                                                viewModel.isEpisodeChapterError.value = false
                                                viewModel.episodeChapterCount.value =
                                                    viewModel.episodeChapterCount.value?.toInt()
                                                        ?.plus(1).toString()
                                            }
                                        }
                                    }
                                },
                                icon = R.drawable.ic_plus,
                                isIcon = true,
                                tint = ShikidroidTheme.colors.onPrimary
                            )
                        }
                    },
                    label = {
                        Text(
                            text =
                            if (listType == SectionType.ANIME) {
                                if (isEpisodeChapterError == true) {
                                    when (currentItem?.anime?.status) {
                                        AiredStatus.ANONS -> NOT_YET_RELEASED_TEXT
                                        AiredStatus.ONGOING -> "Вышло ${currentItem?.anime?.episodesAired} эп."
                                        AiredStatus.RELEASED -> "Всего ${currentItem?.anime?.episodes} эп."
                                        else -> ""
                                    }
                                } else {
                                    ALL_WATCHED
                                }
                            } else {
                                if (isEpisodeChapterError == true) {
                                    when (currentItem?.manga?.status) {
                                        AiredStatus.ANONS -> NOT_YET_RELEASED_TEXT
                                        AiredStatus.RELEASED -> "Всего ${currentItem?.manga?.chapters} гл."
                                        else -> ""
                                    }
                                } else {
                                    ALL_READED
                                }
                            },
                            style = ShikidroidTheme.typography.body12sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ShikidroidTheme.colors.onPrimary,
                        backgroundColor = Color.Transparent,
                        cursorColor = ShikidroidTheme.colors.secondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorLabelColor = ShikidroidTheme.colors.secondary,
                        unfocusedLabelColor = ShikidroidTheme.colors.onBackground,
                        focusedLabelColor = ShikidroidTheme.colors.onBackground
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isRegexEnabled = true,
                    regex = StringUtils.EMPTY_STRING_DIGITS_REGEX,
                    isError = isEpisodeChapterError,
                    handleColor = ShikidroidTheme.colors.secondary
                )

                VerticalDivider(
                    modifier = Modifier.padding(sevenDP),
                    color = ShikidroidTheme.colors.primaryBorderVariant
                )

                FilteredTextField(
                    value = reWatchReReadCount,
                    onValueChange = {
                        if (it.contains(StringUtils.DIGITS_REGEX)) {
                            when {
                                it.containsOnlyZero() -> {
                                    viewModel.isReWatchesError.value = false
                                    viewModel.reWatchReReadCount.value = "0"
                                }
                                it.length > 1 && it.first() == '0' -> {
                                    viewModel.isReWatchesError.value = false
                                    viewModel.reWatchReReadCount.value =
                                        it.substring(it.firstNotZeroIndex()).deleteEmptySpaces()
                                }
                                currentItem?.anime?.status == AiredStatus.ANONS && currentItem?.manga == null -> {
                                    viewModel.isReWatchesError.value = true
                                    viewModel.reWatchReReadCount.value = "0"
                                }
                                currentItem?.manga?.status == AiredStatus.ANONS && currentItem?.anime == null -> {
                                    viewModel.isReWatchesError.value = true
                                    viewModel.reWatchReReadCount.value = "0"
                                }
                                else -> {
                                    viewModel.isReWatchesError.value = false
                                    viewModel.reWatchReReadCount.value = it.deleteEmptySpaces()
                                }
                            }
                        } else {
                            viewModel.isReWatchesError.value = false
                            viewModel.reWatchReReadCount.value = it.deleteEmptySpaces()
                        }
                    },
                    textStyle = ShikidroidTheme.typography.body16sp,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ShikidroidTheme.colors.onPrimary,
                        backgroundColor = Color.Transparent,
                        cursorColor = Orange,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    label = {
                        Text(
                            text =
                            if (isReWatchesError == true) {
                                "Ещё не вышло"
                            } else {
                                when (listType) {
                                    SectionType.ANIME -> "Просмотров"
                                    else -> "Перечитываний"
                                }
                            },
                            style = ShikidroidTheme.typography.body12sp,
                            color = ShikidroidTheme.colors.onBackground
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isRegexEnabled = true,
                    regex = StringUtils.EMPTY_STRING_DIGITS_REGEX,
                    isError = isReWatchesError,
                    handleColor = ShikidroidTheme.colors.secondary
                )
            }
        }

        item {
            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )
        }

        item {
            FilteredTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sevenDP),
                value = itemListComment,
                onValueChange = {
                    viewModel.itemListComment.value = it
                },
                placeholder = {
                    Text(
                        text = "Добавить комментарий к списку",
                        color = ShikidroidTheme.colors.onBackground
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ShikidroidTheme.colors.onPrimary,
                    backgroundColor = Color.Transparent,
                    cursorColor = ShikidroidTheme.colors.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                isRegexEnabled = false,
                handleColor = ShikidroidTheme.colors.secondary
            )
        }

    }
}