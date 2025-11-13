package com.shikidroid.presentation.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.grid.*
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeDurationType
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeSearchType
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreAlphabet
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.search.SeasonType
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.presentation.converters.toAnimePresentationString
import com.shikidroid.presentation.converters.toScreenFilterString
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.navigateDetailsTvAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.OverPictureTwo
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.ui.*
import com.shikidroid.ui.APPLY_BUTTON_TITLE
import com.shikidroid.ui.CLEAR_BUTTON_TITLE
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.GENRE_TITLE
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран "Поиск" для AndroidTV
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации
 */
@Composable
internal fun SearchTvScreen(
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    BackHandler() {
        if (isFilterOpen) {
            viewModel.isFilterOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    TvBoxWithVerticalDrawer(
        isDrawerOpen = isFilterOpen,
        drawerContent = {
            FilterTvDrawer(viewModel = viewModel)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = ShikidroidTheme.colors.background
                )
        ) {
            SearchTvBar(viewModel = viewModel)
            SearchTvAnimeGrid(
                viewModel = viewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
internal fun SearchTvBar(
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг пустых фильтров */
    val isFiltersCLear by viewModel.isFiltersClears.observeAsState(initial = true)

    /** строка для поиска аниме*/
    val searchValue by viewModel.searchValue.observeAsState(initial = "")

    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    /** функционал управления фокусом */
    val focusManager = LocalFocusManager.current

    /** флаг фокуса на поле ввода */
    val searchFocus = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(weight = 0.95f)
                .fillMaxWidth()
                .height(seventyDP)
                .padding(top = sevenDP, bottom = sevenDP, start = fourteenDP, end = sevenDP),
            backgroundColor = ShikidroidTheme.colors.background,
            elevation = ShikidroidTheme.elevation,
            border = BorderStroke(
                width = oneDP,
                color = if (searchFocus.value) {
                    ShikidroidTheme.colors.secondary
                } else {
                    ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                }
            ),
            shape = ShikidroidTheme.shapes.roundedCorner7dp
        ) {
            TvSelectable { interactionSource, isFocused, scale ->
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(interactionSource = interactionSource)
                        .focusRequester(focusRequester = focusRequester)
                        .onFocusChanged {
                            searchFocus.value = it.isFocused || it.hasFocus
                        }
                        .onKeyEvent { key ->
                            if (
                                (key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT ||
                                        key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_DOWN ||
                                        key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT) &&
                                searchFocus.value
                            ) {
                                focusManager.clearFocus()
                            }
                            false
                        }
                        .background(
                            color = if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                Color.Transparent
                            }
                        ),
                    value = searchValue,
                    onValueChange = {
                        viewModel.searchValue.value = it.deleteEmptySpaces()
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .scale(scale = scale.value),
                            text = INPUT_TITLE_TEXT,
                            color = ShikidroidTheme.colors.onBackground
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ShikidroidTheme.colors.onPrimary,
                        backgroundColor = Color.Transparent,
                        cursorColor = ShikidroidTheme.colors.secondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        if (searchValue.isNotEmpty()) {
            TvSelectable { interactionSource, isFocused, scale ->
                RoundedIconButton(
                    iconBoxModifier = Modifier
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    icon = R.drawable.ic_close,
                    backgroundColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        ShikidroidTheme.colors.tvSelectable
                    },
                    tint =
                    if (isFocused) {
                        ShikidroidTheme.colors.secondary
                    } else {
                        ShikidroidTheme.colors.onBackground
                    },
                    isIcon = true,
                    onClick = {
                        viewModel.searchValue.value = ""
                    }
                )
            }
        }

        TvSelectable { interactionSource, isFocused, scale ->
            RoundedIconButton(
                iconBoxModifier = Modifier
                    .focusable(interactionSource = interactionSource)
                    .scale(scale = scale.value),
                icon =
                if (isFiltersCLear) {
                    R.drawable.ic_filter
                } else {
                    R.drawable.ic_filter_edit
                },
                backgroundColor =
                if (isFocused) {
                    ShikidroidTheme.colors.secondaryVariant
                } else {
                    ShikidroidTheme.colors.tvSelectable
                },
                tint =
                if (isFocused) {
                    ShikidroidTheme.colors.secondary
                } else {
                    ShikidroidTheme.colors.onBackground
                },
                isIcon = true,
                onClick = {
                    viewModel.isFilterOpen.value =
                        viewModel.isFilterOpen.value != true
                }
            )
        }

        TvSelectable { interactionSource, isFocused, scale ->
            RoundedIconButton(
                iconBoxModifier = Modifier
                    .focusable(interactionSource = interactionSource)
                    .scale(scale = scale.value),
                iconModifier = Modifier
                    .size(
                        size = twentyFourDP
                    ),
                icon = R.drawable.ic_refresh,
                backgroundColor =
                if (isFocused) {
                    ShikidroidTheme.colors.secondaryVariant
                } else {
                    ShikidroidTheme.colors.tvSelectable
                },
                tint =
                if (isFocused) {
                    ShikidroidTheme.colors.secondary
                } else {
                    ShikidroidTheme.colors.onBackground
                },
                isIcon = true,
                onClick = {
                    viewModel.fullReset()
                }
            )
        }
    }
}

@Composable
internal fun TvLazyGridForPage(
    tvLazyGridState: TvLazyGridState,
    viewModel: SearchScreenViewModel,
    items: (TvLazyGridScope) -> Unit
) {
    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    TvLazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        state = tvLazyGridState,
        columns = TvGridCells.Fixed(count = 5)
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

        for (i in 0 until 5) {
            item {
                SpacerForList()
            }
        }
    }
}

@Composable
internal fun SearchTvAnimeGrid(
    viewModel: SearchScreenViewModel,
    navigator: NavHostController
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** индикатор загрузки без полной блокировки экрана  */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список аниме из поиска */
    val animeSearch by viewModel.animeSearch.observeAsState(listOf())

    /** флаг загрузки полного списка аниме */
    val endReached by viewModel.endReached.observeAsState(false)

    /** состояния для списка сетки */
    val tvLazyGridState = rememberTvLazyGridState()

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
            TvLazyGridForPage(
                tvLazyGridState = tvLazyGridState,
                viewModel = viewModel
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
                        TvSelectable() { interactionSource, isFocused, scale ->
                            SelectableCard(
                                modifier = Modifier
                                    .padding(sevenDP),
                                interactionSource = interactionSource,
                                isFocused = isFocused,
                                scale = scale.value,
                                unselectColor = Color.Transparent
                            ) {
                                SearchTvAnimeCard(
                                    animeModel = anime,
                                    navigator = navigator
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SearchTvAnimeCard(
    animeModel: AnimeModel,
    navigator: NavHostController
) {
    ImageWithOverPicture(
        url = animeModel.image?.original,
        height = 242.dp,
        width = 174.dp,
        overPicture = {
            OverPictureTwo(
                expanded = false,
                textLeftTopCorner = animeModel.type?.toScreenString(),
                textBottomCenter = StringUtils.getEmptyIfBothNull(
                    one = animeModel.nameRu,
                    two = animeModel.name
                ),
                showIcon = false,
                onClick = {
                    navigateDetailsTvAnimeScreen(
                        id = animeModel.id,
                        navigator = navigator
                    )
                }
            )
        }
    )
}

@Composable
internal fun FilterTvDrawer(
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** состояние прокрутки */
    val scrollState = rememberScrollState(0)

    /** флаг открытия выпадающего меню фильтров поиска */
    val isSearchTypeOpen by viewModel.isSearchTypeOpen.observeAsState(initial = false)

    /** порядок сортировки аниме */
    val orderAnime by viewModel.orderAnime.observeAsState(initial = AnimeSearchType.RANKED)

    /** списки выбранных фильтров поиска */
    var genres = listOf<Long>()
    var airedStatus = listOf<AiredStatus>()
    var rateLists = listOf<RateStatus>()
    var animeTypes = listOf<AnimeType>()
    var animeDurations = listOf<AnimeDurationType>()
    var animeAgeRating = listOf<AgeRatingType>()
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
            viewModel.orderAnime.value = AnimeSearchType.RANKED
            viewModel.studioAnime.value = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            TvSelectable { interactionSource, isFocused, scale ->
                MyButton(
                    modifier = Modifier
                        .padding(threeDP)
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    enabledColor = Color.Transparent,
                    borderColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.secondary
                    } else {
                        ShikidroidTheme.colors.onBackground
                    },
                    onClick = {
                        viewModel.isSearchTypeOpen.value =
                            viewModel.isSearchTypeOpen.value != true
                    },
                ) {
                    Text13SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = orderAnime.toScreenString()
                    )
                    IconRotation(
                        modifier = Modifier
                            .padding(threeDP),
                        icon = R.drawable.ic_chevron_down,
                        tint = ShikidroidTheme.colors.onPrimary,
                        isIconRotate = isSearchTypeOpen,
                        rotateValue = 180f
                    )
                    SearchTypeTvDropdown(viewModel = viewModel)
                }
            }

            TvSelectable() { interactionSource, isFocused, scale ->
                MyButton(
                    modifier = Modifier
                        .padding(threeDP)
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    enabled = clearButtonActive,
                    enabledColor = ShikidroidTheme.colors.secondary,
                    disabledColor = ShikidroidTheme.colors.tvSelectable,
                    borderColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.onPrimary
                    } else {
                        Color.Transparent
                    },
                    onClick = {
                        coroutineScope.launch {
                            viewModel.clearFilters.value = true
                            delay(500)
                            viewModel.clearFilters.value = false
                        }
                    }
                ) {
                    Text13SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = CLEAR_BUTTON_TITLE
                    )
                }
            }

            TvSelectable() { interactionSource, isFocused, scale ->
                MyButton(
                    modifier = Modifier
                        .padding(threeDP)
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    enabledColor = ShikidroidTheme.colors.secondary,
                    disabledColor = ShikidroidTheme.colors.tvSelectable,
                    borderColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.onPrimary
                    } else {
                        Color.Transparent
                    },
                    onClick = {
                        viewModel.genre.value = genres
                        viewModel.airedStatus.value = airedStatus
                        viewModel.myList.value = rateLists
                        viewModel.kindAnime.value = animeTypes
                        viewModel.durationAnime.value = animeDurations
                        viewModel.ageRating.value = animeAgeRating
                        viewModel.seasonTypes.value = selectedSeasons
                        viewModel.yearSeason.value = yearSeason
                        viewModel.yearStart.value = yearStart
                        viewModel.yearEnd.value = yearEnd
                        viewModel.isFilterOpen.value = false

                        viewModel.isFiltersClears.value = !clearButtonActive

                        viewModel.reset()
                    }
                ) {
                    Text13SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = APPLY_BUTTON_TITLE
                    )
                }
            }
        }

        GenreTvDrawer(
            getGenres = {
                viewModel.genresNotEmpty.value = it.isNotEmpty()
                genres = it.toList()
            },
            viewModel = viewModel
        )

        SeasonTvDrawer(
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

        AiredStatusTvDrawer(
            getAiredStatus = {
                viewModel.airedStatusNotEmpty.value = it.isNotEmpty()
                airedStatus = it.toList()
            },
            viewModel = viewModel
        )

        if (userAuthorizationStatus == UserAuthStatus.AUTHORIZED.name) {
            RateListTvDrawer(
                getRateStatus = {
                    viewModel.rateListsNotEmpty.value = it.isNotEmpty()
                    rateLists = it.toList()
                },
                viewModel = viewModel
            )
        }
        AnimeTypeTvDrawer(
            getAnimeType = {
                viewModel.animeTypesNotEmpty.value = it.isNotEmpty()
                animeTypes = it.toList()
            },
            viewModel = viewModel
        )
        AnimeDurationTvDrawer(
            getAnimeDurations = {
                viewModel.animeDurationsNotEmpty.value = it.isNotEmpty()
                animeDurations = it.toList()
            },
            viewModel = viewModel
        )
        AnimeAgeRatingTvDrawer(
            getAnimeAgeRatings = {
                viewModel.animeAgeRatingNotEmpty.value = it.isNotEmpty()
                animeAgeRating = it.toList()
            },
            viewModel = viewModel
        )
        SpacerForList()
    }
}

@Composable
internal fun SearchTypeTvDropdown(
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия выпадающего меню фильтров поиска */
    val isSearchTypeOpen by viewModel.isSearchTypeOpen.observeAsState(initial = false)

    /** порядок сортировки аниме */
    val orderAnime by viewModel.orderAnime.observeAsState(initial = AnimeSearchType.RANKED)

    val animeOrderTypes = AnimeSearchType.values()

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
        animeOrderTypes.map { animeOrderType ->
            TvSelectable() { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value)
                        .background(
                            color =
                            when {
                                isFocused -> ShikidroidTheme.colors.secondaryVariant
                                else -> Color.Transparent
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
        }
    }
}

@Composable
internal fun GenreTvDrawer(
    getGenres: (List<Long>) -> Unit,
    viewModel: SearchScreenViewModel
) {
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
            viewModel.genre.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = expanded,
        titleText = GENRE_TITLE,
        onClick = { expanded = !expanded },
        endIcons = { }
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            if (isCensored) {
                GenreAlphabet.genreAnimeCensoredMap
            } else {
                GenreAlphabet.genreAnimeMap
            }.map {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = sevenDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    TvSelectable { interactionSource, isFocused, scale ->
                        RoundedTextButton(
                            modifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            textModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                Color.Transparent
                            },
                            iconBoxPadding = zeroDP,
                            text = it.key,
                            textColor = ShikidroidTheme.colors.onSurface,
                            textStyle = ShikidroidTheme.typography.bodySemiBold16sp,
                            onClick = {
                                val genres = it.value.map {
                                    it.animeId
                                }
                                if (genreIds.containsAll(genres)) {
                                    genreIds.removeAll(genres)
                                } else {
                                    genres.forEach {
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
                                key = { it.animeId }
                            ) { genre ->
                                TvSelectable { interactionSource, isFocused, scale ->
                                    SelectableGenreCard(
                                        modifier = Modifier
                                            .padding(horizontal = fiveDP)
                                            .focusable(interactionSource = interactionSource)
                                            .scale(scale = scale.value)
                                            .clickable {
                                                if (genreIds.contains(genre.animeId)) {
                                                    genreIds.remove(genre.animeId)
                                                } else {
                                                    genreIds.add(genre.animeId)
                                                }
                                            },
                                        isSelect =
                                        genreIds.contains(genre.animeId),
                                        text = genre.nameRu,
                                        backgroundColor =
                                        if (isFocused) {
                                            ShikidroidTheme.colors.secondaryVariant
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SeasonTvDrawer(
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
            viewModel.seasonTypes.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        useCard = false,
        useIcon = false,
        titleText = RELEASED_DATE_TITLE,
        focusable = false,
        onClick = { },
        endIcons = { }
    ) {
        BoxWithConstraints(
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
                    selectedTabIndex = selectedTabIndex.value
                ) {
                    SeasonTvTab(
                        isSelected = selectedTabIndex.value == 0,
                        onClick = {
                            selectedTabIndex.value = 0
                            viewModel.seasonTab.value = 0
                        },
                        title = SEASON_TITLE
                    )
                    SeasonTvTab(
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
                                    TvSelectable() { interactionSource, isFocused, scale ->
                                        SelectableGenreCard(
                                            isSelect = selectedSeasons.contains(season),
                                            modifier = Modifier
                                                .padding(threeDP)
                                                .focusable(interactionSource = interactionSource)
                                                .scale(scale = scale.value)
                                                .clickable {
                                                    if (selectedSeasons.contains(season)) {
                                                        selectedSeasons.remove(season)
                                                    } else {
                                                        selectedSeasons.add(season)
                                                    }
                                                },
                                            text = season.toScreenString(),
                                            backgroundColor =
                                            if (isFocused) {
                                                ShikidroidTheme.colors.secondaryVariant
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                    }
                                }
                            }
                            InputTvYear(
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
                            InputTvYear(
                                modifier = Modifier
                                    .weight(1f),
                                state = yearStart,
                                labelText = YEAR_RELEASED_TITLE
                            )
                            InputTvYear(
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
internal fun SeasonTvTab(
    isSelected: Boolean,
    onClick: () -> Unit,
    title: String
) {
    TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
        SelectableCard(
            interactionSource = interactionSource,
            isFocused = isSelected,
            scale = scale.value,
            backgroundColor = ShikidroidTheme.colors.background
        ) {
            Tab(
                modifier = Modifier
                    .height(thirtyFiveDP)
                    .padding(threeDP)
                    .background(
                        color =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            ShikidroidTheme.colors.surface
                        },
                        shape = RoundedCorner30dp
                    ),
                selected = isSelected,
                onClick = { onClick() },
                selectedContentColor = ShikidroidTheme.colors.secondaryVariant,
                unselectedContentColor = ShikidroidTheme.colors.surface
            ) {
                Text13SemiBold(text = title)
            }
        }
    }
}

@Composable
internal fun InputTvYear(
    modifier: Modifier = Modifier,
    state: MutableState<String>,
    labelText: String
) {
    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    /** функционал управления фокусом */
    val focusManager = LocalFocusManager.current

    /** флаг фокуса на поле ввода */
    val searchFocus = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchFocus) {
        if (!searchFocus.value) {
            focusManager.clearFocus()
        }
    }

    TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
        Card(
            modifier = modifier,
            backgroundColor = ShikidroidTheme.colors.background,
            elevation = ShikidroidTheme.elevation,
            border = BorderStroke(
                width = oneDP,
                color = if (searchFocus.value) {
                    ShikidroidTheme.colors.secondary
                } else {
                    ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                }
            ),
            shape = ShikidroidTheme.shapes.roundedCorner7dp
        ) {
            TextField(
                modifier = Modifier
                    .focusable(interactionSource = interactionSource)
                    .focusRequester(focusRequester = focusRequester)
                    .scale(scale = scale.value)
                    .onFocusChanged {
                        searchFocus.value = it.isFocused || it.hasFocus
                    }
                    .onKeyEvent { key ->
                        when {
                            (key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT ||
                                    key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_UP ||
                                    key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_DOWN ||
                                    key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT
                                    ) && searchFocus.value -> {
                                focusManager.clearFocus()
                            }
                            key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_CENTER -> {
                                focusRequester.requestFocus()
                            }
                        }
                        false
                    }
                    .background(
                        color = if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    ),
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
            )
        }
    }
}

@Composable
internal fun AiredStatusTvDrawer(
    getAiredStatus: (List<AiredStatus>) -> Unit,
    viewModel: SearchScreenViewModel
) {
    /** флаг открытия фильтров поиска */
    val isFilterOpen by viewModel.isFilterOpen.observeAsState(initial = false)

    /** флаг очистки фильтров поиска */
    val clearFilters by viewModel.clearFilters.observeAsState(initial = false)

    /** список статусов аниме */
    val selectedAiredStatus = remember { mutableStateListOf<AiredStatus>() }

    /** список статусов релиза */
    val airedStatuses =
        AiredStatus.values().filter {
            it != AiredStatus.NONE && it != AiredStatus.UNKNOWN && it != AiredStatus.PAUSED
                    && it != AiredStatus.DISCONTINUED
        }

    getAiredStatus(selectedAiredStatus)

    LaunchedEffect(key1 = isFilterOpen) {
        if (isFilterOpen) {
            viewModel.airedStatus.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        useIcon = false,
        titleText = AIRED_STATUS_TITLE,
        focusable = false,
        onClick = { },
        endIcons = { }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = airedStatuses
            ) { status ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    SelectableGenreCard(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
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
                        isSelect = selectedAiredStatus.contains(status),
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun RateListTvDrawer(
    getRateStatus: (List<RateStatus>) -> Unit,
    viewModel: SearchScreenViewModel
) {
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
            viewModel.myList.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        useIcon = false,
        focusable = false,
        titleText = LIST_TITLE,
        onClick = { },
        endIcons = { }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = rateStatuses
            ) { status ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    SelectableGenreCard(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
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
                        text = status.toAnimePresentationString(),
                        isSelect = selectedRateStatus.contains(status),
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnimeTypeTvDrawer(
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
            viewModel.kindAnime.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        titleText = TYPE_TITLE,
        useIcon = false,
        focusable = false,
        onClick = { },
        endIcons = { }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = types
            ) { type ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    SelectableGenreCard(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
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
                        isSelect = animeTypes.contains(type),
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnimeDurationTvDrawer(
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
            viewModel.durationAnime.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        titleText = DURATION_TITLE,
        useIcon = false,
        focusable = false,
        onClick = { },
        endIcons = { }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = durations
            ) { duration ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    SelectableGenreCard(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
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
                        isSelect = animeDurations.contains(duration),
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnimeAgeRatingTvDrawer(
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
            viewModel.ageRating.value?.forEach {
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

    ExpandableTvFilterItem(
        expanded = true,
        titleText = AGE_RATING_TITLE,
        useIcon = false,
        focusable = false,
        onClick = { },
        endIcons = { }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = ageRatingTypes
            ) { age ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    SelectableGenreCard(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
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
                        isSelect = ageRatings.contains(age),
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ExpandableTvFilterItem(
    expanded: Boolean,
    useCard: Boolean = false,
    titleText: String,
    onClick: () -> Unit,
    useIcon: Boolean = true,
    focusable: Boolean = true,
    endIcons: @Composable () -> Unit,
    expandableContent: @Composable () -> Unit
) {
    ExpandableCard(
        expand = expanded,
        useCard = useCard,
        mainContent = {
            TvSelectable(scaleAnimation = 1f) { interactionSource, isFocused, scale ->
                SelectableCard(
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value,
                    focusable = focusable,
                    unselectColor = Color.Transparent
                ) {
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
            }
        },
    ) {
        expandableContent()
    }
}