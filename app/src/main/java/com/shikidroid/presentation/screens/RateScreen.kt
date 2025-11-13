package com.shikidroid.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.shikidroid.*
import com.shikidroid.R
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.rates.SortBy
import com.shikidroid.presentation.BottomSheetType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.navigateDetailsScreen
import com.shikidroid.presentation.navigation.navigateEpisodeScreen
import com.shikidroid.presentation.screens.items.AnimeRateCard
import com.shikidroid.presentation.screens.items.ListIsEmpty
import com.shikidroid.presentation.screens.items.MangaRateCard
import com.shikidroid.presentation.screens.items.RateCardLoader
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import kotlinx.coroutines.launch

/**
 * Экран со списком рейтинга пользователя аниме, манги/ранобэ
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun RateScreen(
    viewModel: RateScreenViewModel,
    navigator: NavHostController
) {

    val switchTheme by viewModel.switchTheme.observeAsState(initial = false)

    val app = LocalContext.current.getApp

    LaunchedEffect(key1 = switchTheme) {
        if (switchTheme) {
            app.switchTheme()
        }
    }

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** тип текущего списка - аниме/манга */
    val listType by viewModel.listType.observeAsState(initial = SectionType.ANIME)

    /** список для показа */
    val showList by viewModel.showList.observeAsState(listOf())

    /** если ли строка поиска в списке */
    val isSearchInList by viewModel.isSearchInList.observeAsState(initial = true)

    /** флаг открыто ли боковое меню */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(initial = false)

    /** состояние обновления по свайпу */
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.reset() }
    )

    BackHandler() {
        if (isDrawerOpen) {
            viewModel.isDrawerOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    BoxWithHorizontalDrawer(
        mainModifier = Modifier
            .pullRefresh(state = pullRefreshState),
        isDrawerOpen = isDrawerOpen,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(180.dp)
            ) {
                DrawerList(
                    viewModel = viewModel
                )
            }
        }
    ) {
        BoxWithSlideTopBar(
            toolbar = {
                SearchBar(viewModel = viewModel)
            },
            pullRefresh = {
                it.apply {
                    PullRefreshIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = fiftyDP),
                        refreshing = isLoading,
                        state = pullRefreshState,
                        contentColor = ShikidroidTheme.colors.secondary,
                        backgroundColor = ShikidroidTheme.colors.surface
                    )
                }
            }
        ) { spacer ->
            when {
                showList.isNotEmpty() || isLoading -> {
                    if (listType == SectionType.ANIME) {
                        AnimeRateList(
                            spacer = spacer,
                            navigator = navigator,
                            viewModel = viewModel
                        )
                    } else {
                        MangaRateList(
                            spacer = spacer,
                            navigator = navigator,
                            viewModel = viewModel
                        )
                    }
                }

                showList.isEmpty() && isSearchInList == true && isLoading == false -> {
                    ListIsEmpty()
                }

                showList.isEmpty() && isSearchInList == false && isLoading == false -> {
                    ListIsEmpty(text = EMPTY_SEARCH_TITLE)
                }
            }
        }
    }
}

/**
 * Поиск по списку с переключателем списков аниме - манга/ранобэ
 *
 * находится в самом верху экрана
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SearchBar(
    viewModel: RateScreenViewModel
) {
    /** строка для поиска названия аниме/манги в списке */
    val searchValue by viewModel.searchTitleName.observeAsState(initial = "")

    /** тип списка аниме/манга */
    val listType by viewModel.listType.observeAsState(initial = SectionType.ANIME)

    /** флаг открыто ли боковое меню */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(seventyDP)
                .padding(horizontal = fourteenDP, vertical = sevenDP),
            backgroundColor = ShikidroidTheme.colors.surface,
            elevation = ShikidroidTheme.elevation,
            border = BorderStroke(
                width = oneDP,
                color = ShikidroidTheme.colors.primaryBorderVariant
            ),
            shape = ShikidroidTheme.shapes.roundedCorner7dp
        ) {
            FilteredTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = searchValue,
                onValueChange = {
                    viewModel.searchTitleName.value = it.deleteEmptySpaces()
                },
                placeholder = {
                    Text(
                        text = listType.toScreenString(),
                        color = ShikidroidTheme.colors.onBackground
                    )
                },
                singleLine = true,
                leadingIcon = {
                    RoundedIconButton(
                        isIconRotate = isDrawerOpen,
                        rotateValue = 360f,
                        icon =
                        if (isDrawerOpen && viewModel.showList.value?.isNotEmpty() == true) {
                            R.drawable.ic_arrow_back
                        } else {
                            R.drawable.ic_menu
                        },
                        backgroundColor = Color.Transparent,
                        onClick = {
                            viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                        },
                        tint = ShikidroidTheme.colors.onPrimary
                    )
                },
                trailingIcon = {
                    RoundedIconButton(
                        icon = if (viewModel.searchTitleName.value.isNullOrEmpty().not()) {
                            R.drawable.ic_close
                        } else {
                            R.drawable.ic_switch_list
                        },
                        backgroundColor = Color.Transparent,
                        onClick = {
                            if (viewModel.searchTitleName.value.isNullOrEmpty().not()) {
                                viewModel.searchTitleName.value = ""
                            } else {
                                when (viewModel.listType.value) {
                                    SectionType.ANIME -> viewModel.listType.value =
                                        SectionType.MANGA

                                    SectionType.MANGA -> viewModel.listType.value =
                                        SectionType.ANIME

                                    else -> viewModel.listType.value = SectionType.ANIME
                                }
                            }
                        },
                        tint = ShikidroidTheme.colors.onPrimary
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ShikidroidTheme.colors.onPrimary,
                    backgroundColor = Color.Transparent,
                    cursorColor = ShikidroidTheme.colors.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                isRegexEnabled = false,
                handleColor = ShikidroidTheme.colors.secondary
            )
        }
        SortBar(listType = listType, viewModel = viewModel)
    }
}

/**
 * Бар сортировки списка
 *
 * находится под поиском
 *
 * @param listType тип текущего списка - аниме или манга
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SortBar(
    listType: SectionType,
    viewModel: RateScreenViewModel
) {
    /** флаг по возрастания или убыванию отсортирован список */
    val isSortAscend by viewModel.isSortAscend.observeAsState(initial = true)

    /** тип сортировки списка */
    val sortBy by viewModel.sortBy.observeAsState(initial = SortBy.BY_NAME)

    /** статус аниме в списке пользователя */
    val animeRateStatus by viewModel.animeRateStatus.observeAsState(initial = RateStatus.PLANNED)

    /** статус манги в списке пользователя */
    val mangaRateStatus by viewModel.mangaRateStatus.observeAsState(initial = RateStatus.PLANNED)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = fourteenDP, vertical = oneDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SelectableCard(isFocused = false, scale = 1f) {
            Text(
                modifier = Modifier
                    .padding(threeDP)
                    .clickable {
                        viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                    },
                text =
                if (listType == SectionType.ANIME) {
                    animeRateStatus.toAnimePresentationString()
                } else {
                    mangaRateStatus.toMangaPresentationString()
                },
                style = ShikidroidTheme.typography.body12sp,
                color = ShikidroidTheme.colors.onPrimary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SelectableCard(isFocused = false, scale = 1f) {
                Text(
                    modifier = Modifier
                        .padding(threeDP)
                        .clickable {
                            viewModel.showSortByMenu.value =
                                viewModel.showSortByMenu.value != true
                        },
                    text = sortBy.toScreenString(),
                    style = ShikidroidTheme.typography.body12sp,
                    color = ShikidroidTheme.colors.onPrimary
                )
            }
            SortByMenu(viewModel = viewModel)
            SelectableCard(
                isFocused = false,
                scale = 1f,
                modifier = Modifier
                    .padding(start = threeDP),
                shape = ShikidroidTheme.shapes.absoluteRounded50dp
            ) {
                IconRotation(
                    modifier = Modifier
                        .height(twentyDP)
                        .padding(horizontal = threeDP)
                        .clip(shape = AbsoluteRounded50dp)
                        .clickable {
                            viewModel.isSortAscend.value =
                                viewModel.isSortAscend.value != true
                        },
                    icon = R.drawable.ic_sort_descending,
                    tint = ShikidroidTheme.colors.onPrimary,
                    isIconRotate = isSortAscend,
                    rotateValue = 180f
                )
            }
        }
    }
}

/**
 * Меню сортировки
 *
 * вызывается по клику кнопки бара сортировки
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SortByMenu(viewModel: RateScreenViewModel) {

    /** флаг показа выпадающего меню */
    val showMenu by viewModel.showSortByMenu.observeAsState(initial = false)

    /** названия типов сортировки для показа в выпадающем меню */
    val sortByTitles by viewModel.sortByTitles.observeAsState(initial = listOf())

    /** тип выбранной сортировки */
    val sortBy by viewModel.sortBy.observeAsState(initial = SortBy.BY_NAME)

    DropdownMenu(
        modifier = Modifier
            .background(
                color = ShikidroidTheme.colors.surface
            ),
        expanded = showMenu,
        onDismissRequest = { viewModel.showSortByMenu.value = false }
    ) {
        sortByTitles.map { sortedBy ->
            Box(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                    .background(
                        color =
                        if (sortedBy == sortBy) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        viewModel.sortBy.value = sortedBy
                        viewModel.showSortByMenu.value =
                            viewModel.showSortByMenu.value != true
                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(fourteenDP),
                    text = sortedBy.toScreenString(),
                    style = ShikidroidTheme.typography.body12sp,
                    textAlign = TextAlign.Center,
                    color =
                    if (sortedBy == sortBy) {
                        ShikidroidTheme.colors.secondary
                    } else {
                        ShikidroidTheme.colors.onPrimary
                    }
                )
            }
        }
    }
}

/**
 * Список с карточками аниме для показа в центре экрана
 *
 * @param spacer отступ списка
 * @param navigator навигация
 * @param viewModel вью модель экрана
 */
@Composable
internal fun AnimeRateList(
    spacer: Dp,
    navigator: NavHostController,
    viewModel: RateScreenViewModel
) {
    /** список для показа */
    val showList by viewModel.showList.observeAsState(listOf())

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    /** позиция текущего скролла списка */
    val currentListPosition by viewModel.currentListPosition.observeAsState(initial = 0)

    /** флаг полной загрузки списка аниме */
    val animeEndReached by viewModel.animeEndReached.observeAsState(initial = false)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    val isHorizontal = isComposableHorizontalOrientation()

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                viewModel.currentListPosition.value = lazyGridState.firstVisibleItemIndex
            }

            Lifecycle.Event.ON_RESUME -> {
                coroutineScope.launch {
                    lazyGridState.scrollToItem(currentListPosition)
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(key1 = showList) {
        lazyGridState.scrollToItem(index = 0)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = if (isHorizontal) 2 else 1),
        state = lazyGridState,
        contentPadding = PaddingValues(top = spacer, bottom = spacer)
    ) {
        items(
            items = showList,
            key = { it.id ?: 1 }
        ) { rateModel ->
            if (rateModel == showList[showList.size - 1] &&
                !isLoading &&
                animeEndReached == false
            ) {
                viewModel.nextAnimePage.value =
                    viewModel.nextAnimePage.value?.plus(1)
            }
            AnimeSwipeableCard(
                rateModel = rateModel,
                viewModel = viewModel,
                navigator = navigator
            )
        }
        if (isLoading) {
            item {
                RateCardLoader()
            }
        }
    }
}

/**
 * Список с карточками манги/ранобэ для показа в центре экрана
 *
 * @param spacer отступ списка
 * @param navigator навигация
 * @param viewModel вью модель экрана
 */
@Composable
internal fun MangaRateList(
    spacer: Dp,
    navigator: NavHostController,
    viewModel: RateScreenViewModel
) {
    /** список для показа */
    val showList by viewModel.showList.observeAsState(listOf())

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** состояние для вертикально расположенного списка */
    val lazyListState = rememberLazyListState()

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    /** позиция текущего скролла списка */
    val currentListPosition by viewModel.currentListPosition.observeAsState(initial = 0)

    /** флаг полной загрузки списка манги */
    val mangaEndReached by viewModel.mangaEndReached.observeAsState(initial = false)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    val isHorizontal = isComposableHorizontalOrientation()

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                viewModel.currentListPosition.value = lazyGridState.firstVisibleItemIndex
            }

            Lifecycle.Event.ON_RESUME -> {
                coroutineScope.launch {
                    lazyGridState.scrollToItem(currentListPosition)
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(key1 = showList) {
        lazyGridState.scrollToItem(index = 0)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = if (isHorizontal) 2 else 1),
        state = lazyGridState,
        contentPadding = PaddingValues(top = spacer, bottom = spacer)
    ) {
        items(
            items = showList,
            key = { it.id ?: 1 }
        ) { rateModel ->
            if (rateModel == showList[showList.size - 1] &&
                !isLoading &&
                mangaEndReached == false
            ) {
                viewModel.nextMangaPage.value =
                    viewModel.nextMangaPage.value?.plus(1)
            }
            MangaSwipeableCard(
                rateModel = rateModel,
                viewModel = viewModel,
                navigator = navigator
            )
        }
        if (isLoading) {
            item {
                RateCardLoader()
            }
        }
    }
}

/**
 * Карточка аниме пользовательского рейтинга
 *
 * @param rateModel модель данных элемента
 * @param viewModel вью модель экрана
 * @param navigator навигация
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AnimeSwipeableCard(
    rateModel: RateModel,
    viewModel: RateScreenViewModel,
    navigator: NavHostController
) {
    /** состояние свайпа */
    val swipeableState = rememberSwipeableState(initialValue = 0)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            viewModel.currentItem.value = rateModel
            viewModel.showBottomSheet(
                bottomType = BottomSheetType.RATE_LIST_EDIT
            )
            swipeableState.animateTo(
                targetValue = 0,
                tween(400, 0)
            )
        }
    }

    SwipeableBox(
        swipeableState = swipeableState,
        rightToLeftSize = 180f,
        backgroundItem = {
            SwipeItem(
                rateModel = rateModel,
                swipeableState = swipeableState,
                viewModel = viewModel,
            )
        }
    ) {
        rateModel.anime?.let {
            AnimeRateCard(
                userScore = rateModel.score,
                userEpisodes = rateModel.episodes,
                anime = it,
                onTap = {
                    if (viewModel.isDrawerOpen.value == true) {
                        viewModel.isDrawerOpen.value = false
                        return@AnimeRateCard
                    }
                    navigateDetailsScreen(
                        id = it.id,
                        detailsType = DetailsScreenType.ANIME,
                        navigator = navigator
                    )
                    viewModel.currentItem.value = rateModel
                },
                onDoubleTap = {
                    viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                },
                onEditClick = {
                    coroutineScope.launch {
                        viewModel.currentItem.value = rateModel
                        viewModel.showBottomSheet(
                            bottomType = BottomSheetType.RATE_MODEL_EDIT
                        )
                    }
                },
                onPlayClick = {
                    viewModel.switchTheme.value =
                        viewModel.switchTheme.value != true
                    navigateEpisodeScreen(
                        id = it.id,
                        userRateId = rateModel.id,
                        animeName = it.name.orEmpty(),
                        animeNameRu = it.nameRu.orEmpty(),
                        animeImageUrl = it.image?.original,
                        navigator = navigator
                    )
                }
            )
        }
    }
}

/**
 * Карточка манги/ранобэ пользовательского рейтинга
 *
 * @param rateModel модель данных элемента
 * @param viewModel вью модель экрана
 * @param navigator навигация
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun MangaSwipeableCard(
    rateModel: RateModel,
    viewModel: RateScreenViewModel,
    navigator: NavHostController
) {
    /** состояние свайпа */
    val swipeableState = rememberSwipeableState(initialValue = 0)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            viewModel.currentItem.value = rateModel
            viewModel.showBottomSheet(
                bottomType = BottomSheetType.RATE_LIST_EDIT
            )
            swipeableState.animateTo(
                targetValue = 0,
                tween(400, 0)
            )
        }
    }

    SwipeableBox(
        swipeableState = swipeableState,
        rightToLeftSize = 180f,
        backgroundItem = {
            SwipeItem(
                rateModel = rateModel,
                swipeableState = swipeableState,
                viewModel = viewModel
            )
        }
    ) {
        rateModel.manga?.let {
            MangaRateCard(
                userScore = rateModel.score,
                userChapters = rateModel.chapters,
                manga = it,
                onTap = {
                    if (viewModel.isDrawerOpen.value == true) {
                        viewModel.isDrawerOpen.value = false
                        return@MangaRateCard
                    }
                    navigateDetailsScreen(
                        id = it.id,
                        detailsType =
                        when (it.type) {
                            MangaType.LIGHT_NOVEL, MangaType.NOVEL -> DetailsScreenType.RANOBE
                            else -> DetailsScreenType.MANGA
                        },
                        navigator = navigator
                    )
                    viewModel.currentItem.value = rateModel
                },
                onDoubleTap = {
                    viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                },
                onEditClick = {
                    coroutineScope.launch {
                        viewModel.currentItem.value = rateModel
                        viewModel.showBottomSheet(
                            bottomType = BottomSheetType.RATE_MODEL_EDIT
                        )
                    }
                }
            )
        }
    }
}

/**
 * Подложка под элемент пользовательского списка для показа при свайпе в левую/правую сторону
 *
 * @param rateModel модель данных элемента
 * @param listType тип списка
 * @param swipeableState состояние свайпа
 * @param viewModel вью модель экрана
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SwipeItem(
    rateModel: RateModel,
    swipeableState: SwipeableState<Int>,
    viewModel: RateScreenViewModel
) {
    /** тип текущего списка - аниме/манга */
    val listType by viewModel.listType.observeAsState(initial = SectionType.ANIME)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(horizontal = fourteenDP, vertical = threeDP)
            .fillMaxWidth()
            .height(oneHundredFiftyDP),
        backgroundColor = ShikidroidTheme.colors.surface,
        elevation = ShikidroidTheme.elevation,
        shape = ShikidroidTheme.shapes.roundedCorner7dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ShikidroidTheme.colors.secondaryVariant),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RoundedIconButton(
                modifier = Modifier
                    .padding(twentyDP),
                icon = R.drawable.ic_list_change,
                backgroundColor = Color.Transparent,
                text = "В другой\nсписок",
                textMaxLines = 2,
                isIcon = true,
                tint = ShikidroidTheme.colors.secondary,
                textStyle = ShikidroidTheme.typography.body12sp,
                textColor = ShikidroidTheme.colors.onPrimary
            )
            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                RoundedIconButton(
                    modifier = Modifier
                        .padding(twentyDP),
                    icon = R.drawable.ic_plus_one_filled,
                    text =
                    if (listType == SectionType.ANIME) {
                        "эпизод"
                    } else {
                        "глава"
                    },
                    isIcon = true,
                    tint = ShikidroidTheme.colors.secondary,
                    textStyle = ShikidroidTheme.typography.body12sp,
                    textColor = ShikidroidTheme.colors.onPrimary,
                    onClick = {
                        viewModel.plusOneEpisodeChapter(rateModel = rateModel)
                        coroutineScope.launch {
                            swipeableState.animateTo(
                                0,
                                tween(400, 0)
                            )
                        }
                    }
                )
                VerticalDivider(color = ShikidroidTheme.colors.primaryBorderVariant)
                RoundedIconButton(
                    modifier = Modifier
                        .padding(twentyDP),
                    icon = R.drawable.ic_delete,
                    text = "удалить",
                    isIcon = true,
                    tint = ShikidroidTheme.colors.secondary,
                    textStyle = ShikidroidTheme.typography.body12sp,
                    textColor = ShikidroidTheme.colors.onPrimary,
                    onClick = {
                        viewModel.deleteRateModel(rateModel = rateModel)
                        coroutineScope.launch {
                            swipeableState.animateTo(
                                0,
                                tween(400, 0)
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Выдвижное боковое меню
 *
 * вызывается по клику кнопик бара поиска и свайпу справа налево
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun DrawerList(
    viewModel: RateScreenViewModel
) {
    /** статус аниме в списке пользователя */
    val animeRateStatus by viewModel.animeRateStatus.observeAsState(initial = RateStatus.PLANNED)

    /** статус манги в списке пользователя */
    val mangaRateStatus by viewModel.mangaRateStatus.observeAsState(initial = RateStatus.PLANNED)

    /** количества аниме в каждой категории */
    val animeRateSize by viewModel.animeRateSize.observeAsState()

    /** количества манги в каждой категории */
    val mangaRateSize by viewModel.mangaRateSize.observeAsState()

    /** тип текущего списка - аниме/манга */
    val listType by viewModel.listType.observeAsState(initial = SectionType.ANIME)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SpacerStatusBar()
        }
        (if (listType == SectionType.ANIME) animeRateSize else mangaRateSize)?.map {
            if (it.value > 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(seventyDP)
                            .padding(top = sevenDP, bottom = sevenDP, end = tenDP)
                            .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                            .background(
                                color =
                                if (listType == SectionType.ANIME) {
                                    if (it.key == animeRateStatus) {
                                        it.key.toBackgroundColor()
                                    } else {
                                        Color.Transparent
                                    }
                                } else {
                                    if (it.key == mangaRateStatus) {
                                        it.key.toBackgroundColor()
                                    } else {
                                        Color.Transparent
                                    }
                                }
                            )
                            .clickable {
                                if (listType == SectionType.ANIME) {
                                    viewModel.animeRateStatus.value = it.key
                                } else {
                                    viewModel.mangaRateStatus.value = it.key
                                }
                                viewModel.searchTitleName.value = viewModel.searchTitleName.value
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .width(oneHundredFiftyDP),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text =
                                if (listType == SectionType.ANIME) {
                                    it.key.toAnimePresentationString()
                                } else {
                                    it.key.toMangaPresentationString()
                                },
                                color =
                                if (listType == SectionType.ANIME) {
                                    if (it.key == animeRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    }
                                } else {
                                    if (it.key == mangaRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    }
                                },
                                textAlign = TextAlign.Center,
                                style =
                                if (listType == SectionType.ANIME) {
                                    if (it.key == animeRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
                                    }
                                } else {
                                    if (it.key == mangaRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
                                    }
                                }
                            )
                            Text(
                                text = "${it.value}",
                                color =
                                if (listType == SectionType.ANIME) {
                                    if (it.key == animeRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    }
                                } else {
                                    if (it.key == mangaRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    }
                                },
                                textAlign = TextAlign.Center,
                                style =
                                if (listType == SectionType.ANIME) {
                                    if (it.key == animeRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
                                    }
                                } else {
                                    if (it.key == mangaRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}