package com.shikidroid.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeDurationType
import com.shikidroid.domain.models.anime.AnimeSearchType
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreAlphabet
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.domain.models.search.SeasonType
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.*
import com.shikidroid.presentation.navigation.navigateCharacterScreen
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.presentation.navigation.navigateDetailsMangaScreen
import com.shikidroid.presentation.navigation.navigatePeopleScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.OverPictureTwo
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.ui.*
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран "Поиск"
 *
 * @param prefs доступ к локальному хранилищу системы Android
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SearchScreen(
    prefs: SharedPreferencesProvider,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** флаг открыто ли боковое меню */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(initial = false)

    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг пустых фильтров */
    val isFiltersCLear by viewModel.isFiltersClears.observeAsState(initial = true)

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** состояние обновления по свайпу */
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.fullReset() }
    )

    BackHandler() {
        if (isDrawerOpen || isFilterOpen) {
            viewModel.isDrawerOpen.value = false
            viewModel.isFilterOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    BoxWithVerticalDrawer(
        isDrawerOpen = isFilterOpen,
        drawerContent = {
            FilterDrawer(viewModel = viewModel)
        }
    ) {
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
                    SearchTypeDrawer(viewModel = viewModel)
                }
            }
        ) {
            BoxWithSlideFloatingButton(
//                prefs = prefs,
//                xAxisKey = AppKeys.SEARCH_SCREEN_FLOATING_BUTTON_X,
//                yAxisKey = AppKeys.SEARCH_SCREEN_FLOATING_BUTTON_Y,
                showButton = true,
                floatingButton = {
                    if (searchType == SearchType.ANIME || searchType == SearchType.MANGA || searchType == SearchType.RANOBE) {
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(fourteenDP),
                            backgroundColor = ShikidroidTheme.colors.secondary,
                            onClick = {
                                viewModel.isFilterOpen.value =
                                    viewModel.isFilterOpen.value != true
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id =
                                    if (isFiltersCLear) {
                                        R.drawable.ic_filter
                                    } else {
                                        R.drawable.ic_filter_edit
                                    }
                                ),
                                contentDescription = null
                            )
                        }
                    }
                }
            ) {
                BoxWithSlideTopBar(
                    toolbar = {
                        SearchScreenSearchBar(viewModel = viewModel)
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
                    when (searchType) {
                        SearchType.ANIME -> {
                            AnimeSearch(
                                spacer = spacer,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                        SearchType.MANGA -> {
                            MangaSearch(
                                spacer = spacer,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                        SearchType.RANOBE -> {
                            RanobeSearch(
                                spacer = spacer,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                        SearchType.CHARACTER -> {
                            CharacterSearch(
                                spacer = spacer,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                        SearchType.PEOPLE -> {
                            PeopleSearch(
                                spacer = spacer,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

/**
 * Верхняя панель экрана для поиска по списку
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SearchScreenSearchBar(
    viewModel: SearchScreenViewModel
) {
    /** флаг открыто ли боковое меню */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(initial = false)

    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** строка для поиска аниме*/
    val searchValue by viewModel.searchValue.observeAsState(initial = "")

    val rotationState by animateFloatAsState(
        targetValue = if (isDrawerOpen) 360f else 0f
    )

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
                viewModel.searchValue.value = it.deleteEmptySpaces()
            },
            placeholder = {
                Text(
                    text = searchType.toScreenString(),
                    color = ShikidroidTheme.colors.onBackground
                )
            },
            singleLine = true,
            leadingIcon = {
                RoundedIconButton(
                    modifier = Modifier
                        .rotate(rotationState),
                    icon =
                    if (isDrawerOpen) {
                        R.drawable.ic_arrow_back
                    } else {
                        R.drawable.ic_menu
                    },
                    tint = ShikidroidTheme.colors.onPrimary,
                    onClick = {
                        viewModel.isDrawerOpen.value =
                            viewModel.isDrawerOpen.value != true
                    }
                )
            },
            trailingIcon = {
                RoundedIconButton(
                    icon =
                    if (searchValue.isEmpty()) {
                        R.drawable.ic_search
                    } else {
                        R.drawable.ic_close
                    },
                    tint = ShikidroidTheme.colors.onPrimary,
                    onClick = {
                        viewModel.searchValue.value = ""
                    }
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
}

@Composable
internal fun SearchTypeDrawer(
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** список типов поиска */
    val types = SearchType.values()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SpacerStatusBar()
        }
        items(
            items = types,
            key = { it.ordinal }
        ) { type ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(seventyDP)
                    .padding(top = sevenDP, bottom = sevenDP, end = tenDP)
                    .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                    .background(
                        color =
                        if (type == searchType) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        viewModel.isSearchTypeChangesByUI.value = true
                        viewModel.searchType.value = type
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = type.toScreenString(),
                    color =
                    if (type == searchType) {
                        ShikidroidTheme.colors.secondary
                    } else {
                        ShikidroidTheme.colors.onPrimary
                    },
                    textAlign = TextAlign.Center,
                    style =
                    if (type == searchType) {
                        ShikidroidTheme.typography.bodySemiBold13sp
                    } else {
                        ShikidroidTheme.typography.body13sp
                    }
                )
            }
        }
    }
}

@Composable
internal fun LazyGridForPage(
    spacer: Dp,
    lazyGridState: LazyGridState,
    viewModel: SearchScreenViewModel,
    items: (LazyGridScope) -> Unit
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyGridState,
        columns = GridCells.Fixed(if (!isScreenHorizontal) 2 else 4),
        contentPadding = PaddingValues(top = spacer, bottom = spacer)
    ) {

        items(this)

        if (isLoadingWithoutBlocking) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(oneHundredDP),
                    color = ShikidroidTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
internal fun AnimeSearch(
    spacer: Dp,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список аниме из поиска */
    val animeSearch by viewModel.animeSearch.observeAsState(listOf())

    /** флаг загрузки полного списка аниме */
    val endReached by viewModel.endReached.observeAsState(false)

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    if (animeSearch.isEmpty()) {
        viewModel.nextPage.value = 1
    }

    when {
        isLoading -> {
            Loader()
        }
        animeSearch.isEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }
        else -> {
            LazyGridForPage(
                spacer = spacer,
                lazyGridState = lazyGridState,
                viewModel = viewModel,
            ) {
                it.apply {
                    items(
                        items = animeSearch,
                        key = { it.id ?: 0 }
                    ) { anime ->
                        if (anime == animeSearch[animeSearch.size - 1] && !isLoadingWithoutBlocking && !endReached) {
                            viewModel.nextPage.value =
                                viewModel.nextPage.value?.plus(1)
                        }
                        SearchCard(
                            onClick = {
                                navigateDetailsAnimeScreen(
                                    id = anime.id,
                                    navigator = navigator
                                )
                            },
                            url = anime.image?.original,
                            textLeftTop = anime.type?.toScreenString(),
                            textBottom =
                            StringUtils.getEmptyIfBothNull(
                                one = anime.nameRu,
                                two = anime.name
                            ),
                            status = anime.status,
                            score = anime.score,
                            episodes = anime.episodes,
                            episodesAired = anime.episodesAired,
                            dateAired = anime.dateAired,
                            dateReleased = anime.dateReleased
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun MangaSearch(
    spacer: Dp,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список манги/ранобэ из поиска */
    val mangaSearch by viewModel.mangaSearch.observeAsState(listOf())

    /** флаг загрузки полного списка манги */
    val endReached by viewModel.endReached.observeAsState(false)

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    if (mangaSearch.isEmpty()) {
        viewModel.nextPage.value = 1
    }

    when {
        isLoading -> {
            Loader()
        }
        mangaSearch.isEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }
        else -> {
            LazyGridForPage(
                spacer = spacer,
                lazyGridState = lazyGridState,
                viewModel = viewModel
            ) {
                it.apply {
                    items(
                        items = mangaSearch,
                        key = { it.id ?: 0 }
                    ) { manga ->
                        if (manga == mangaSearch[mangaSearch.size - 1] && !isLoadingWithoutBlocking && !endReached) {
                            viewModel.nextPage.value =
                                viewModel.nextPage.value?.plus(1)
                        }
                        SearchCard(
                            onClick = {
                                navigateDetailsMangaScreen(
                                    id = manga.id,
                                    navigator = navigator
                                )
                            },
                            url = manga.image?.original,
                            textLeftTop = manga.type?.toScreenString(),
                            textBottom = manga.nameRu,
                            status = manga.status,
                            score = manga.score,
                            chapters = manga.chapters,
                            volumes = manga.volumes,
                            dateAired = manga.dateAired,
                            dateReleased = manga.dateReleased
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun RanobeSearch(
    spacer: Dp,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список манги/ранобэ из поиска */
    val ranobeSearch by viewModel.ranobeSearch.observeAsState(listOf())

    /** флаг загрузки полного списка манги */
    val endReached by viewModel.endReached.observeAsState(false)

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    if (ranobeSearch.isEmpty()) {
        viewModel.nextPage.value = 1
    }

    when {
        isLoading -> {
            Loader()
        }
        ranobeSearch.isEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }
        else -> {
            LazyGridForPage(
                spacer = spacer,
                lazyGridState = lazyGridState,
                viewModel = viewModel
            ) {
                it.apply {
                    items(
                        items = ranobeSearch,
                        key = { it.id ?: 0 }
                    ) { ranobe ->
                        if (ranobe == ranobeSearch[ranobeSearch.size - 1] && !isLoadingWithoutBlocking && !endReached) {
                            viewModel.nextPage.value =
                                viewModel.nextPage.value?.plus(1)
                        }
                        SearchCard(
                            onClick = {
                                navigateDetailsRanobeScreen(
                                    id = ranobe.id,
                                    navigator = navigator
                                )
                            },
                            url = ranobe.image?.original,
                            textLeftTop = ranobe.type?.toScreenString(),
                            textBottom = ranobe.nameRu,
                            status = ranobe.status,
                            score = ranobe.score,
                            chapters = ranobe.chapters,
                            volumes = ranobe.volumes,
                            dateAired = ranobe.dateAired,
                            dateReleased = ranobe.dateReleased
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CharacterSearch(
    spacer: Dp,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** список манги из поиска */
    val characterSearch by viewModel.characterSearch.observeAsState(listOf())

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    if (characterSearch.isEmpty()) {
        ListIsEmpty(text = ENTER_CHARACTER_NAME)
    } else {
        LazyGridForPage(
            spacer = spacer,
            lazyGridState = lazyGridState,
            viewModel = viewModel
        ) {
            it.apply {
                items(
                    items = characterSearch,
                    key = { it.id ?: 0 }
                ) { character ->
                    SearchCard(
                        onClick = {
                            navigateCharacterScreen(
                                id = character.id,
                                navigator = navigator
                            )
                        },
                        url = character.image?.original,
                        textLeftTop = null,
                        textBottom = character.nameRu ?: character.name,
                        showIcon = false,
                        status = null,
                        score = null,
                        dateAired = null,
                        dateReleased = null
                    )
                }
            }
        }
    }
}

@Composable
internal fun PeopleSearch(
    spacer: Dp,
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** список манги из поиска */
    val peopleSearch by viewModel.peopleSearch.observeAsState(listOf())

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    if (peopleSearch.isEmpty()) {
        ListIsEmpty(text = ENTER_CHARACTER_NAME)
    } else {
        LazyGridForPage(
            spacer = spacer,
            lazyGridState = lazyGridState,
            viewModel = viewModel
        ) { scope ->
            scope.apply {
                items(
                    items = peopleSearch,
                    key = { it.id ?: 0 }
                ) { people ->
                    SearchCard(
                        onClick = {
                            navigatePeopleScreen(
                                id = people.id,
                                navigator = navigator
                            )
                        },
                        url = people.image?.original,
                        textLeftTop = null,
                        textBottom = people.nameRu ?: people.name,
                        showIcon = false,
                        status = null,
                        score = null,
                        dateAired = null,
                        dateReleased = null
                    )
                }
            }
        }
    }
}

@Composable
internal fun SearchCard(
    onClick: () -> Unit,
    url: String?,
    textLeftTop: String?,
    textBottom: String?,
    showIcon: Boolean = true,
    status: AiredStatus?,
    score: Double?,
    episodes: Int? = null,
    episodesAired: Int? = null,
    chapters: Int? = null,
    volumes: Int? = null,
    dateAired: String?,
    dateReleased: String?
) {
    /** флаг раскрытия карточки */
    var expanded by remember {
        mutableStateOf(false)
    }

    DefaultExpandCard(
        modifier = Modifier
            .wrapContentSize()
            .padding(threeDP)
            .clickable {
                onClick()
            },
        expanded = expanded,
        mainContent = {
            ImageWithOverPicture(
                modifier = Modifier,
                height = 242.dp,
                width = 174.dp,
                url = url,
                overPicture = {
                    OverPictureTwo(
                        expanded = expanded,
                        textLeftTopCorner = textLeftTop,
                        textBottomCenter = textBottom,
                        showIcon = showIcon,
                        onClick = {
                            expanded = !expanded
                        }
                    )
                }
            )
        },
        status = status,
        score = score,
        episodes = episodes,
        episodesAired = episodesAired,
        chapters = chapters,
        volumes = volumes,
        dateAired = dateAired,
        dateReleased = dateReleased
    )
}

@Composable
internal fun FilterDrawer(
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** состояние прокрутки */
    val scrollState = rememberScrollState(0)

    /** флаг открытия выпадающего меню фильтров поиска */
    val isSearchTypeOpen by viewModel.isSearchTypeOpen.observeAsState(initial = false)

    /** порядок сортировки аниме */
    val orderAnime by viewModel.orderAnime.observeAsState(initial = AnimeSearchType.RANKED)

    /** порядок сортировки манги */
    val orderMangaRanobe by viewModel.orderManga.observeAsState(initial = MangaSearchType.RANKED)

    /** списки выбранных фильтров поиска */
    var genres = listOf<Long>()
    var airedStatus = listOf<AiredStatus>()
    var rateLists = listOf<RateStatus>()
    var animeTypes = listOf<AnimeType>()
    var animeDurations = listOf<AnimeDurationType>()
    var animeAgeRating = listOf<AgeRatingType>()
    var mangaTypes = listOf<MangaType>()
    var selectedSeasons = listOf<SeasonType>()
    var yearSeason = ""
    var yearStart = ""
    var yearEnd = ""

    /** активность кнопкис Сбросить в фильтрах поиска */
    val clearButtonActive by viewModel.clearButtonActive.observeAsState(initial = false)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    /** статус авторизации пользователя */
    val userAuthorizationStatus by viewModel.userAuthorizationStatus.observeAsState()

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    LaunchedEffect(key1 = clearFilters){
        if (clearFilters) {
            if (searchType == SearchType.ANIME) {
                viewModel.orderAnime.value = AnimeSearchType.RANKED
                viewModel.studioAnime.value = null
            } else {
                viewModel.orderManga.value = MangaSearchType.RANKED
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        SpacerStatusBar()
        RowTitleText(
            text = FILTERS_TITLE
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MyButton(
                modifier = Modifier
                    .padding(threeDP),
                enabledColor = Color.Transparent,
                borderColor = ShikidroidTheme.colors.onBackground,
                onClick = {
                    viewModel.isSearchTypeOpen.value =
                        viewModel.isSearchTypeOpen.value != true
                },
            ) {
                Text13SemiBold(
                    modifier = Modifier
                        .padding(sevenDP),
                    text =
                    if (searchType == SearchType.ANIME) {
                        orderAnime.toScreenString()
                    } else {
                        orderMangaRanobe.toScreenString()
                    }
                )
                IconRotation(
                    modifier = Modifier
                        .padding(threeDP),
                    icon = R.drawable.ic_chevron_down,
                    tint = ShikidroidTheme.colors.onPrimary,
                    isIconRotate = isSearchTypeOpen,
                    rotateValue = 180f
                )
                SearchTypeDropdown(viewModel = viewModel)
            }
            MyButton(
                modifier = Modifier
                    .padding(threeDP),
                enabled = clearButtonActive,
                enabledColor = ShikidroidTheme.colors.secondary,
                disabledColor = ShikidroidTheme.colors.tvSelectable,
                onClick = {
                    coroutineScope.launch {
                        viewModel.clearFilters.value = true
                        delay(500)
                        viewModel.clearFilters.value = false
                    }
                }
            ) {
                Text13SemiBold(text = CLEAR_BUTTON_TITLE)
            }
            MyButton(
                modifier = Modifier
                    .padding(threeDP),
                enabledColor = ShikidroidTheme.colors.secondary,
                disabledColor = ShikidroidTheme.colors.tvSelectable,
                onClick = {
                    viewModel.genre.value = genres
                    viewModel.airedStatus.value = airedStatus
                    viewModel.myList.value = rateLists
                    viewModel.kindAnime.value = animeTypes
                    viewModel.durationAnime.value = animeDurations
                    viewModel.ageRating.value = animeAgeRating
                    viewModel.kindManga.value = mangaTypes
                    viewModel.seasonTypes.value = selectedSeasons
                    viewModel.yearSeason.value = yearSeason
                    viewModel.yearStart.value = yearStart
                    viewModel.yearEnd.value = yearEnd
                    viewModel.isFilterOpen.value = false

                    viewModel.isFiltersClears.value = !clearButtonActive

                    viewModel.reset()
                }
            ) {
                Text13SemiBold(text = APPLY_BUTTON_TITLE)
            }
        }
        GenreDrawer(
            getGenres = {
                viewModel.genresNotEmpty.value = it.isNotEmpty()
                genres = it.toList()
            },
            viewModel = viewModel
        )
        SeasonDrawer(
            getSelectedSeasons = {
                viewModel.seasonsNotEmpty.value = it.isNotEmpty()
                selectedSeasons = it.toList()
            },
            getYearSeason = {
                viewModel.yearSeasonNotEmpty.value = it.isNotEmpty()
                yearSeason = it
            },
            getYearStart = {
                viewModel.yearStartNotEmpty.value = it.isNotEmpty()
                yearStart = it
            },
            getYearEnd = {
                viewModel.yearEndNotEmpty.value = it.isNotEmpty()
                yearEnd = it
            },
            viewModel = viewModel
        )
        AiredStatusDrawer(
            getAiredStatus = {
                viewModel.airedStatusNotEmpty.value = it.isNotEmpty()
                airedStatus = it.toList()
            },
            viewModel = viewModel
        )
        if (userAuthorizationStatus == UserAuthStatus.AUTHORIZED.name) {
            RateListDrawer(
                getRateStatus = {
                    viewModel.rateListsNotEmpty.value = it.isNotEmpty()
                    rateLists = it.toList()
                },
                viewModel = viewModel
            )
        }
        when (searchType) {
            SearchType.ANIME -> {
                AnimeTypeDrawer(
                    getAnimeType = {
                        viewModel.animeTypesNotEmpty.value = it.isNotEmpty()
                        animeTypes = it.toList()
                    },
                    viewModel = viewModel
                )
                AnimeDurationDrawer(
                    getAnimeDurations = {
                        viewModel.animeDurationsNotEmpty.value = it.isNotEmpty()
                        animeDurations = it.toList()
                    },
                    viewModel = viewModel
                )
            }
            SearchType.MANGA -> {
                MangaTypeDrawer(
                    getMangaType = {
                        viewModel.mangaTypesNotEmpty.value = it.isNotEmpty()
                        mangaTypes = it.toList()
                    },
                    viewModel = viewModel
                )
            }
            else -> Unit
        }
        if (searchType == SearchType.ANIME) {
            AnimeAgeRatingDrawer(
                getAnimeAgeRatings = {
                    viewModel.animeAgeRatingNotEmpty.value = it.isNotEmpty()
                    animeAgeRating = it.toList()
                },
                viewModel = viewModel
            )
        }
        SpacerForList()
    }
}

@Composable
internal fun SearchTypeDropdown(
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** флаг открытия выпадающего меню фильтров поиска */
    val isSearchTypeOpen by viewModel.isSearchTypeOpen.observeAsState(initial = false)

    /** порядок сортировки аниме */
    val orderAnime by viewModel.orderAnime.observeAsState(initial = AnimeSearchType.RANKED)

    /** порядок сортировки манги */
    val orderMangaRanobe by viewModel.orderManga.observeAsState(initial = MangaSearchType.RANKED)

    val animeOrderTypes = AnimeSearchType.values()
    val mangaOrderTypes = MangaSearchType.values()

    DropdownMenu(
        modifier = Modifier
            .background(
                color = ShikidroidTheme.colors.surface
            ),
        expanded = isSearchTypeOpen,
        onDismissRequest = {
            viewModel.isSearchTypeOpen.value = false
        }
    ) {
        if (searchType == SearchType.ANIME) {
            animeOrderTypes.map { animeOrderType ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .background(
                            color =
                            if (animeOrderType == orderAnime) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            viewModel.orderAnime.value = animeOrderType
                            viewModel.isSearchTypeOpen.value =
                                viewModel.isSearchTypeOpen.value != true
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(fourteenDP),
                        text = animeOrderType.toScreenString(),
                        style = ShikidroidTheme.typography.body12sp,
                        textAlign = TextAlign.Center,
                        color =
                        if (animeOrderType == orderAnime) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onPrimary
                        }
                    )
                }
            }
        } else {
            mangaOrderTypes.map { mangaOrderType ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .background(
                            color =
                            if (mangaOrderType == orderMangaRanobe) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            viewModel.orderManga.value = mangaOrderType
                            viewModel.isSearchTypeOpen.value =
                                viewModel.isSearchTypeOpen.value != true
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(fourteenDP),
                        text = mangaOrderType.toScreenString(),
                        style = ShikidroidTheme.typography.body12sp,
                        textAlign = TextAlign.Center,
                        color =
                        if (mangaOrderType == orderMangaRanobe) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onPrimary
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun GenreDrawer(
    getGenres: (List<Long>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** флаг раскрытия карточки */
    var expanded by remember {
        mutableStateOf(false)
    }

    /** флаг включена ли цензура */
    val isCensored by viewModel.isCensored.observeAsState(initial = true)

    /** список c id жанров */
    val genreIds = remember { mutableStateListOf<Long>() }

    getGenres(genreIds.toList())

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.genre.value?.map {
                genreIds.add(it)
            }
        } else {
            genreIds.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            genreIds.clear()
        }
    }

    ExpandableFilterItem(
        expanded = expanded,
        titleText = GENRE_TITLE,
        onClick = { expanded = !expanded },
        endIcons = {
            AnimatedVisibility(visible = genreIds.isNotEmpty()) {
                FilterEndIconsRow(
                    text = genreIds.size.toString(),
                    onClick = { genreIds.clear() }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            if (searchType == SearchType.ANIME) {
                if (isCensored) GenreAlphabet.genreAnimeCensoredMap else GenreAlphabet.genreAnimeMap
            } else {
                if (isCensored) GenreAlphabet.genreMangaRanobeCensoredMap else GenreAlphabet.genreMangaRanobeMap
            }
                .map {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = sevenDP),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RoundedTextButton(
                            iconBoxPadding = zeroDP,
                            text = it.key,
                            textColor = ShikidroidTheme.colors.onSurface,
                            textStyle = ShikidroidTheme.typography.bodySemiBold16sp,
                            onClick = {
                                val genres = it.value.map {
                                    if (searchType == SearchType.ANIME) {
                                        it.animeId
                                    } else {
                                        it.mangaId
                                    }
                                }
                                if (genreIds.containsAll(genres)) {
                                    genreIds.removeAll(genres)
                                } else {
                                    genres.map {
                                        if (!genreIds.contains(it)) {
                                            genreIds.add(it)
                                        }
                                    }
                                }
                            }
                        )
                        LazyRow(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {
                            items(
                                items = it.value,
                                key = {
                                    if (searchType == SearchType.ANIME) {
                                        it.animeId
                                    } else {
                                        it.mangaId
                                    }
                                }
                            ) { genre ->
                                SelectableGenreCard(
                                    modifier = Modifier
                                        .padding(horizontal = fiveDP)
                                        .clickable {
                                            if (searchType == SearchType.ANIME) {
                                                if (genreIds.contains(genre.animeId)) {
                                                    genreIds.remove(genre.animeId)
                                                } else {
                                                    genreIds.add(genre.animeId)
                                                }
                                            } else {
                                                if (genreIds.contains(genre.mangaId)) {
                                                    genreIds.remove(genre.mangaId)
                                                } else {
                                                    genreIds.add(genre.mangaId)
                                                }
                                            }
                                        },
                                    isSelect =
                                    genreIds.contains(
                                        if (searchType == SearchType.ANIME) {
                                            genre.animeId
                                        } else {
                                            genre.mangaId
                                        }
                                    ),
                                    text = genre.nameRu
                                )
                            }
                        }
                    }
                }
        }
    }
}

/**
 *
 */
@Composable
internal fun SeasonDrawer(
    getSelectedSeasons: (List<SeasonType>) -> Unit,
    getYearSeason: (String) -> Unit,
    getYearStart: (String) -> Unit,
    getYearEnd: (String) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список сезонов */
    val seasons = SeasonType.values().toList()

    /** список выбранных сезонов */
    val selectedSeasons = remember { mutableStateListOf<SeasonType>() }

    /** год сезона */
    val yearSeason = remember { mutableStateOf("") }

    /** первый год диапазона поиска */
    val yearStart = remember { mutableStateOf("") }

    /** год выхода или второй год диапазона поиска */
    val yearEnd = remember { mutableStateOf("") }

    /** индекс выбранного таба */
    val selectedTabIndex = remember { mutableStateOf(0) }

    getSelectedSeasons(selectedSeasons)
    getYearSeason(yearSeason.value)
    getYearStart(yearStart.value)
    getYearEnd(yearEnd.value)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.seasonTypes.value?.map {
                selectedSeasons.add(it)
            }
            yearSeason.value = viewModel.yearSeason.value ?: ""
            yearStart.value = viewModel.yearStart.value ?: ""
            yearEnd.value = viewModel.yearEnd.value ?: ""
            selectedTabIndex.value = viewModel.seasonTab.value ?: 0
        } else {
            selectedSeasons.clear()
            yearSeason.value = ""
            yearStart.value = ""
            yearEnd.value = ""
            selectedTabIndex.value = 0
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            selectedSeasons.clear()
            yearSeason.value = ""
            yearStart.value = ""
            yearEnd.value = ""
            selectedTabIndex.value = 0
        }
    }

    ExpandableFilterItem(
        expanded = true,
        useCard = false,
        useIcon = false,
        titleText = RELEASED_DATE_TITLE,
        onClick = { },
        endIcons = { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = fourteenDP)
            ) {
                TabRow(
                    modifier = Modifier
                        .height(fiftyDP),
                    backgroundColor = ShikidroidTheme.colors.background,
                    contentColor = ShikidroidTheme.colors.secondaryVariant,
                    selectedTabIndex = selectedTabIndex.value
                ) {
                    SeasonTab(
                        isSelected = selectedTabIndex.value == 0,
                        onClick = {
                            selectedTabIndex.value = 0
                            viewModel.seasonTab.value = 0
                        },
                        title = SEASON_TITLE
                    )
                    SeasonTab(
                        isSelected = selectedTabIndex.value == 1,
                        onClick = {
                            selectedTabIndex.value = 1
                            viewModel.seasonTab.value = 1
                        },
                        title = YEAR_TITLE
                    )
                }

                when (selectedTabIndex.value) {
                    0 -> {
                        Row(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .weight(1f)
                            ) {
                                RowForColumnScope(
                                    columnScope = this,
                                    data = seasons,
                                    columnCount = 2
                                ) { season ->
                                    SelectableGenreCard(
                                        isSelect = selectedSeasons.contains(season),
                                        modifier = Modifier
                                            .padding(threeDP)
                                            .clickable {
                                                if (selectedSeasons.contains(season)) {
                                                    selectedSeasons.remove(season)
                                                } else {
                                                    selectedSeasons.add(season)
                                                }
                                            },
                                        text = season.toScreenString(),
                                    )
                                }
                            }
                            InputYear(
                                modifier = Modifier
                                    .weight(1f),
                                state = yearSeason,
                                labelText = YEAR_SEASON_TITLE
                            )
                        }
                    }
                    1 -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            InputYear(
                                modifier = Modifier
                                    .weight(1f),
                                state = yearStart,
                                labelText = YEAR_RELEASED_TITLE
                            )
                            InputYear(
                                modifier = Modifier
                                    .weight(1f),
                                state = yearEnd,
                                labelText = YEAR_RELEASED_TITLE
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
internal fun SeasonTab(
    isSelected: Boolean,
    onClick: () -> Unit,
    title: String
) {
    SelectableCard(isFocused = isSelected, scale = 1f) {
        Tab(
            modifier = Modifier
                .height(thirtyFiveDP)
                .padding(threeDP)
                .background(
                    color = ShikidroidTheme.colors.surface,
                    shape = RoundedCorner30dp
                ),
            selected = isSelected,
            onClick = {
                onClick()
            },
            selectedContentColor = ShikidroidTheme.colors.secondaryVariant,
            unselectedContentColor = ShikidroidTheme.colors.surface
        ) {
            Text13SemiBold(text = title)
        }
    }
}

@Composable
internal fun InputYear(
    modifier: Modifier = Modifier,
    state: MutableState<String>,
    labelText: String
) {
    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    /** функционал управления фокусом */
    val focusManager = LocalFocusManager.current

    val searchFocus = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchFocus) {
        if (!searchFocus.value) {
            focusManager.clearFocus()
        }
    }

    SelectableCard(
        modifier = modifier,
        isFocused = searchFocus.value,
        scale = 1f
    ) {
        FilteredTextField(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged {
                    searchFocus.value = it.isFocused || it.hasFocus
                },
            value = state.value,
            onValueChange = {
                if (it.length <= 4) {
                    state.value = it.deleteEmptySpaces()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = {
                Text12Sp(
                    text = labelText,
                    color = ShikidroidTheme.colors.onBackground
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
            regex = StringUtils.EMPTY_STRING_DIGITS_REGEX,
            isRegexEnabled = true,
            handleColor = ShikidroidTheme.colors.secondary
        )
    }
}

/**
 * Статус выхода произведения
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun AiredStatusDrawer(
    getAiredStatus: (List<AiredStatus>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список статусов аниме */
    val selectedAiredStatus = remember { mutableStateListOf<AiredStatus>() }

    /** список статусов релиза */
    val airedStatuses =
        AiredStatus.values().filter {
            if (searchType == SearchType.ANIME) {
                it != AiredStatus.NONE && it != AiredStatus.UNKNOWN && it != AiredStatus.PAUSED
                        && it != AiredStatus.DISCONTINUED
            } else {
                it != AiredStatus.NONE && it != AiredStatus.UNKNOWN
            }
        }

    getAiredStatus(selectedAiredStatus)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.airedStatus.value?.map {
                selectedAiredStatus.add(it)
            }
        } else {
            selectedAiredStatus.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            selectedAiredStatus.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        useIcon = false,
        titleText = AIRED_STATUS_TITLE,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = selectedAiredStatus.isNotEmpty()) {
                FilterEndIconsRow(
                    text = selectedAiredStatus.size.toString(),
                    onClick = { selectedAiredStatus.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = airedStatuses
            ) { status ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = airedStatuses,
                            item = status
                        )
                        .clickable {
                            if (selectedAiredStatus.contains(status)) {
                                selectedAiredStatus.remove(status)
                            } else {
                                selectedAiredStatus.add(status)
                            }
                        },
                    text = status.toScreenString(),
                    isSelect = selectedAiredStatus.contains(status)
                )
            }
        }
    }
}

/**
 * Фильтр по пользовательскому списку
 */
@Composable
internal fun RateListDrawer(
    getRateStatus: (List<RateStatus>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** тип поиска */
    val searchType by viewModel.searchType.observeAsState(initial = SearchType.ANIME)

    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** статусы списка пользовательского рейтинга */
    val rateStatuses = RateStatus.values().filter { it != RateStatus.UNKNOWN }

    /** статусы пользовательского рейтинга аниме */
    val selectedRateStatus = remember { mutableStateListOf<RateStatus>() }

    getRateStatus(selectedRateStatus)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.myList.value?.map {
                selectedRateStatus.add(it)
            }
        } else {
            selectedRateStatus.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            selectedRateStatus.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        useIcon = false,
        titleText = LIST_TITLE,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = selectedRateStatus.isNotEmpty()) {
                FilterEndIconsRow(
                    text = selectedRateStatus.size.toString(),
                    onClick = { selectedRateStatus.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = rateStatuses
            ) { status ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = rateStatuses,
                            item = status
                        )
                        .clickable {
                            if (selectedRateStatus.contains(status)) {
                                selectedRateStatus.remove(status)
                            } else {
                                selectedRateStatus.add(status)
                            }
                        },
                    text =
                    if (searchType == SearchType.ANIME) {
                        status.toAnimePresentationString()
                    } else {
                        status.toMangaPresentationString()
                    },
                    isSelect = selectedRateStatus.contains(status)
                )
            }
        }
    }
}

/**
 * Фильтр по типу аниме
 */
@Composable
internal fun AnimeTypeDrawer(
    getAnimeType: (List<AnimeType>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список типов */
    val types = AnimeType.values().filter { it != AnimeType.NONE && it != AnimeType.UNKNOWN }

    /** список выбранных типов аниме */
    val animeTypes = remember { mutableStateListOf<AnimeType>() }

    getAnimeType(animeTypes)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.kindAnime.value?.map {
                animeTypes.add(it)
            }
        } else {
            animeTypes.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            animeTypes.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        titleText = TYPE_TITLE,
        useIcon = false,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = animeTypes.isNotEmpty()) {
                FilterEndIconsRow(
                    text = animeTypes.size.toString(),
                    onClick = { animeTypes.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = types
            ) { type ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = types,
                            item = type
                        )
                        .clickable {
                            if (animeTypes.contains(type)) {
                                animeTypes.remove(type)
                            } else {
                                animeTypes.add(type)
                            }
                        },
                    text = type.toScreenFilterString(),
                    isSelect = animeTypes.contains(type)
                )
            }
        }
    }
}

/**
 * Фильтр по типу манги
 */
@Composable
internal fun MangaTypeDrawer(
    getMangaType: (List<MangaType>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список типов */
    val types = MangaType.values().filter { it != MangaType.UNKNOWN }

    /** список выбранных типов аниме */
    val mangaTypes = remember { mutableStateListOf<MangaType>() }

    getMangaType(mangaTypes)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.kindManga.value?.map {
                mangaTypes.add(it)
            }
        } else {
            mangaTypes.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            mangaTypes.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        titleText = TYPE_TITLE,
        useIcon = false,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = mangaTypes.isNotEmpty()) {
                FilterEndIconsRow(
                    text = mangaTypes.size.toString(),
                    onClick = { mangaTypes.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = types
            ) { type ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = types,
                            item = type
                        )
                        .clickable {
                            if (mangaTypes.contains(type)) {
                                mangaTypes.remove(type)
                            } else {
                                mangaTypes.add(type)
                            }
                        },
                    text = type.toScreenString(),
                    isSelect = mangaTypes.contains(type)
                )
            }
        }
    }
}

/**
 * Фильтр по длительности аниме
 */
@Composable
internal fun AnimeDurationDrawer(
    getAnimeDurations: (List<AnimeDurationType>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список с длительностью аниме */
    val durations = AnimeDurationType.values().toList()

    /** список с выбранными длительностями */
    val animeDurations = remember { mutableStateListOf<AnimeDurationType>() }

    getAnimeDurations(animeDurations)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.durationAnime.value?.map {
                animeDurations.add(it)
            }
        } else {
            animeDurations.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            animeDurations.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        titleText = DURATION_TITLE,
        useIcon = false,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = animeDurations.isNotEmpty()) {
                FilterEndIconsRow(
                    text = animeDurations.size.toString(),
                    onClick = { animeDurations.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = durations
            ) { duration ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = durations,
                            item = duration
                        )
                        .clickable {
                            if (animeDurations.contains(duration)) {
                                animeDurations.remove(duration)
                            } else {
                                animeDurations.add(duration)
                            }
                        },
                    text = duration.toScreenString(),
                    isSelect = animeDurations.contains(duration)
                )
            }
        }
    }
}

/**
 * Фильтр по возрастному рейтингу аниме
 */
@Composable
internal fun AnimeAgeRatingDrawer(
    getAnimeAgeRatings: (List<AgeRatingType>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** флаг включена ли цензура */
    val isCensored by viewModel.isCensored.observeAsState(initial = true)

    /** список с возрастным рейтингом аниме */
    val ageRatingTypes =
        AgeRatingType.values().filter {
            if (isCensored) {
                it != AgeRatingType.NONE && it != AgeRatingType.UNKNOWN && it != AgeRatingType.RX
            } else {
                it != AgeRatingType.NONE && it != AgeRatingType.UNKNOWN
            }
        }

    /** список с выбранными длительностями */
    val ageRatings = remember { mutableStateListOf<AgeRatingType>() }

    getAnimeAgeRatings(ageRatings)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.ageRating.value?.map {
                ageRatings.add(it)
            }
        } else {
            ageRatings.clear()
        }
    }

    LaunchedEffect(key1 = clearFilters) {
        if (clearFilters) {
            ageRatings.clear()
        }
    }

    ExpandableFilterItem(
        expanded = true,
        titleText = AGE_RATING_TITLE,
        useIcon = false,
        onClick = { },
        endIcons = {
            AnimatedVisibility(visible = ageRatings.isNotEmpty()) {
                FilterEndIconsRow(
                    text = ageRatings.size.toString(),
                    onClick = { ageRatings.clear() }
                )
            }
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = ageRatingTypes
            ) { age ->
                SelectableGenreCard(
                    modifier = Modifier
                        .paddingByList(
                            items = ageRatingTypes,
                            item = age
                        )
                        .clickable {
                            if (ageRatings.contains(age)) {
                                ageRatings.remove(age)
                            } else {
                                ageRatings.add(age)
                            }
                        },
                    text = age.toScreenString(),
                    isSelect = ageRatings.contains(age)
                )
            }
        }
    }
}

@Composable
internal fun ExpandableFilterItem(
    expanded: Boolean,
    useCard: Boolean = false,
    titleText: String,
    onClick: () -> Unit,
    useIcon: Boolean = true,
    endIcons: @Composable () -> Unit,
    expandableContent: @Composable () -> Unit
) {
    ExpandableCard(
        expand = expanded,
        useCard = useCard,
        mainContent = {
            RowTitleText(
                modifier = Modifier
                    .wrapContentHeight()
                    .clickable {
                        onClick()
                    },
                text = titleText,
                textIcon = {
                    if (useIcon) {
                        IconRotation(
                            modifier = Modifier
                                .padding(start = threeDP),
                            icon = R.drawable.ic_chevron_down,
                            tint = ShikidroidTheme.colors.onPrimary,
                            isIconRotate = expanded,
                            rotateValue = 180f
                        )
                    }
                },
                endIcons = {
                    endIcons()
                }
            )
        }
    ) {
        expandableContent()
    }
}

@Composable
internal fun FilterEndIconsRow(
    text: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
    ) {
        RoundedTextButton(
            text = text,
            textColor = ShikidroidTheme.colors.secondary,
            backgroundColor = ShikidroidTheme.colors.secondaryVariant
        )
        RoundedIconButton(
            icon = R.drawable.ic_delete,
            tint = ShikidroidTheme.colors.onPrimary,
            onClick = {
                onClick()
            }
        )
    }
}