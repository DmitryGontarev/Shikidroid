package com.shikidroid.presentation.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.grid.*
import com.shikidroid.R
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.rates.SortBy
import com.shikidroid.getApp
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.sealedscreens.tv.RatesTvScreens
import com.shikidroid.presentation.screens.items.AnimeRateCard
import com.shikidroid.presentation.screens.items.RateCardLoader
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.RateScreenViewModel
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import kotlinx.coroutines.launch

@Composable
internal fun RateTvScreen(
    navigator: NavHostController,
    viewModel: RateScreenViewModel
) {
    /** список для показа */
    val showList by viewModel.showList.observeAsState(listOf())

    /** количества аниме в каждой категории */
    val animeRateSize by viewModel.animeRateSize.observeAsState()

    /** флаг открыто ли боковое меню */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(initial = false)

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

    val switchTheme by viewModel.switchTheme.observeAsState(initial = false)
    val app = LocalContext.current.getApp
    LaunchedEffect(key1 = switchTheme) {
        app.switchTheme()
    }

    BackHandler() {
        if (isDrawerOpen) {
            viewModel.isDrawerOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    TvBoxWithLeftHorizontalDrawer(
        isAlwaysDrawer = true,
        isDrawerOpen = isDrawerOpen && showList.isNotEmpty() && animeRateSize?.isNotEmpty() == true,
        drawerContent = {
            DrawerTvList(viewModel = viewModel)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = ShikidroidTheme.colors.surface
                )
        ) {
            SearchRateTvBar(viewModel = viewModel)
            SortTvBar(viewModel = viewModel)
            AnimeGrid(
                showList = showList,
                navigator = navigator,
                viewModel = viewModel
            )
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
internal fun DrawerTvList(
    viewModel: RateScreenViewModel
) {
    /** статус аниме в списке пользователя */
    val animeRateStatus by viewModel.animeRateStatus.observeAsState(initial = RateStatus.PLANNED)

    /** количества аниме в каждой категории */
    val animeRateSize by viewModel.animeRateSize.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        animeRateSize?.map {

            if (it.value > 0) {

                item {

                    TvSelectable { interactionSource, isFocused, scale ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onKeyEvent { key ->
                                    if (
                                        key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT &&
                                        key.type == KeyEventType.KeyDown
                                    ) {
                                        viewModel.isDrawerOpen.value = false
                                    }
                                    false
                                }
                                .focusable(true, interactionSource)
                                .height(seventyDP)
                                .scale(scale = scale.value)
                                .padding(top = sevenDP, bottom = sevenDP, end = tenDP)
                                .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                                .background(
                                    color =
                                    when {
                                        it.key == animeRateStatus -> it.key.toBackgroundColor()
                                        it.key != animeRateStatus && isFocused -> ShikidroidTheme.colors.secondaryVariant
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    viewModel.animeRateStatus.value = it.key
                                    viewModel.searchTitleName.value =
                                        viewModel.searchTitleName.value
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
                                    text = it.key.toAnimePresentationString(),
                                    color =
                                    if (it.key == animeRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    },
                                    textAlign = TextAlign.Center,
                                    style =
                                    if (it.key == animeRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
                                    }
                                )
                                Text(
                                    text = "${it.value}",
                                    color =
                                    if (it.key == animeRateStatus) {
                                        it.key.toColor()
                                    } else {
                                        ShikidroidTheme.colors.onPrimary
                                    },
                                    textAlign = TextAlign.Center,
                                    style =
                                    if (it.key == animeRateStatus) {
                                        ShikidroidTheme.typography.bodySemiBold13sp
                                    } else {
                                        ShikidroidTheme.typography.body13sp
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

/**
 * Поиск по списку с переключателем списков аниме - манга/ранобэ
 *
 * находится в самом верху экрана
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SearchRateTvBar(
    viewModel: RateScreenViewModel
) {

    /** строка для поиска названия аниме/манги в списке */
    val searchValue by viewModel.searchTitleName.observeAsState(initial = "")

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
                        .focusable(true, interactionSource)
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
                        viewModel.searchTitleName.value = it.deleteEmptySpaces()
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
                        .focusable(true, interactionSource)
                        .scale(scale = scale.value),
                    textModifier = Modifier
                        .focusable(true, interactionSource)
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
                        viewModel.searchTitleName.value = ""
                    }
                )
            }
        }

        TvSelectable { interactionSource, isFocused, scale ->
            RoundedIconButton(
                iconBoxModifier = Modifier
                    .focusable(true, interactionSource)
                    .scale(scale = scale.value),
                textModifier = Modifier
                    .focusable(true, interactionSource)
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
                    viewModel.reset()
                }
            )
        }
    }
}

/**
 * Бар сортировки списка
 *
 * находится под поиском
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun SortTvBar(
    viewModel: RateScreenViewModel
) {

    /** флаг по возрастания или убыванию отсортирован список */
    val isSortAscend by viewModel.isSortAscend.observeAsState(initial = true)

    /** тип сортировки списка */
    val sortBy by viewModel.sortBy.observeAsState(initial = SortBy.BY_NAME)

    /** статус аниме в списке пользователя */
    val animeRateStatus by viewModel.animeRateStatus.observeAsState(initial = RateStatus.PLANNED)

    val rotationState by animateFloatAsState(
        targetValue = if (isSortAscend) 180f else 0f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = fourteenDP, vertical = oneDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        TvSelectable { interactionSource, isFocused, scale ->
            SelectableCard(
                interactionSource = interactionSource,
                isFocused = isFocused,
                scale = scale.value,
                modifier = Modifier
                    .onKeyEvent { key ->
                        if (
                            key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT &&
                            key.type == KeyEventType.KeyDown
                        ) {
                            viewModel.isDrawerOpen.value = true
                        }
                        if (key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT) {
                            viewModel.isDrawerOpen.value = false
                        }
                        false
                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(threeDP)
                        .focusable(true, interactionSource)
                        .scale(scale = scale.value)
                        .clickable {
                            viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                        },
                    text = animeRateStatus.toAnimePresentationString(),
                    style = ShikidroidTheme.typography.body12sp,
                    color = ShikidroidTheme.colors.onPrimary
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            TvSelectable { interactionSource, isFocused, scale ->
                SelectableCard(
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value
                ) {
                    Text(
                        modifier = Modifier
                            .padding(threeDP)
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value)
                            .clickable {
                                viewModel.showSortByMenu.value =
                                    viewModel.showSortByMenu.value != true
                            },
                        text = sortBy.toScreenString(),
                        style = ShikidroidTheme.typography.body12sp,
                        color = ShikidroidTheme.colors.onPrimary
                    )
                }
            }

            SortByTvMenu(viewModel = viewModel)

            TvSelectable { interactionSource, isFocused, scale ->
                SelectableCard(
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value,
                    modifier = Modifier
                        .padding(start = threeDP),
                    shape = ShikidroidTheme.shapes.absoluteRounded50dp
                ) {
                    Icon(
                        modifier = Modifier
                            .height(twentyDP)
                            .padding(threeDP)
                            .rotate(rotationState)
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value)
                            .clickable {
                                viewModel.isSortAscend.value =
                                    viewModel.isSortAscend.value != true
                            },
                        painter = painterResource(
                            id = R.drawable.ic_sort_descending
                        ),
                        contentDescription = null,
                        tint = ShikidroidTheme.colors.onPrimary
                    )
                }
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
internal fun SortByTvMenu(viewModel: RateScreenViewModel) {

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

        sortByTitles.forEach { sortedBy ->

            TvSelectable { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .focusable(true, interactionSource)
                        .scale(scale = scale.value)
                        .background(
                            color =
                            when {
                                isFocused -> ShikidroidTheme.colors.secondaryVariant
                                else -> Color.Transparent
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
}

@Composable
internal fun AnimeGrid(
    showList: List<RateModel>,
    navigator: NavHostController,
    viewModel: RateScreenViewModel
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** состояния для списка сетки */
    val tvLazyGridState = rememberTvLazyGridState()

    /** позиция текущего скролла списка */
    val currentListPosition by viewModel.currentListPosition.observeAsState(initial = 0)

    /** флаг полной загрузки списка аниме */
    val animeEndReached by viewModel.animeEndReached.observeAsState(initial = false)

    /** область действия сопрограммы (корутины) */
    val coroutineScope = rememberCoroutineScope()

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                viewModel.currentListPosition.value = tvLazyGridState.firstVisibleItemIndex
            }

            Lifecycle.Event.ON_RESUME -> {
                coroutineScope.launch {
                    tvLazyGridState.scrollToItem(currentListPosition)
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(key1 = showList) {
        tvLazyGridState.scrollToItem(0)
    }

    TvLazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        state = tvLazyGridState,
        columns = TvGridCells.Fixed(2)
    ) {

        items(
            items = showList,
            key = { it.id ?: 1 }
        ) { rateModel ->

            if (showList[showList.size - 1] == rateModel && !isLoading && animeEndReached == false) {
                viewModel.nextAnimePage.value =
                    viewModel.nextAnimePage.value?.plus(1)
            }

            RateAnimeTvCard(
                showList = showList,
                rateModel = rateModel,
                navigator = navigator,
                viewModel = viewModel
            )
        }

        // если идёт загрузка, то последним элементом показывать прогресс
        if (isLoading) {
            item {
                RateCardLoader()
            }
        }
    }
}

/**
 * Элемент списка пользовательского рейтинга
 *
 * @param rateModel модель данных элемента
 * @param viewModel вью модель экрана
 * @param navigator навигация
 */
@Composable
internal fun RateAnimeTvCard(
    showList: List<RateModel>,
    rateModel: RateModel,
    viewModel: RateScreenViewModel,
    navigator: NavHostController
) {
    TvSelectable { interactionSource, isFocused, scale ->
        rateModel.anime?.let {
            AnimeRateCard(
                modifier = Modifier
                    .focusable(
                        enabled = true,
                        interactionSource = interactionSource
                    )
                    .scale(scale = scale.value)
                    .onKeyEvent { key ->
                        if (showList.indexOf(rateModel) % 2 == 0 &&
                            key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT &&
                            key.type == KeyEventType.KeyDown
                        ) {
                            viewModel.isDrawerOpen.value = true
                        }
                        if (key.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT) {
                            viewModel.isDrawerOpen.value = false
                        }
                        false
                    }
                    .clickable {
                        if (viewModel.isDrawerOpen.value == true) {
                            viewModel.isDrawerOpen.value = false
                        }
                        navigator.setData(
                            key = AppKeys.DETAILS_SCREEN_ID,
                            value = it.id
                        )
                        navigator.setData(
                            key = AppKeys.DETAILS_SCREEN_TYPE,
                            value = DetailsScreenType.ANIME
                        )
                        viewModel.currentItem.value = rateModel
                        navigator.navigate(RatesTvScreens.DetailsTv.route)
                    },
                userScore = rateModel.score,
                userEpisodes = rateModel.episodes,
                anime = it,
                borderColor = if (isFocused) {
                    ShikidroidTheme.colors.secondary
                } else {
                    ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                }
            )
        }
    }
}