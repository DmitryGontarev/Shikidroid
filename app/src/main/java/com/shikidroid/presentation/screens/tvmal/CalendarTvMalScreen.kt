package com.shikidroid.presentation.screens.tvmal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import com.shikidroid.R
import com.shikidroid.domain.models.calendar.CalendarModel
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.presentation.navigation.navigateDetailsTvAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.OverPictureOne
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.CalendarViewModel
import com.shikidroid.presentation.viewmodels.mal.CalendarMalViewModel
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.RELEASED_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.toHourMinutes
import java.util.*

/**
 * Экран графика выхода серий аниме для AndroidTV
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации
 */
@Composable
internal fun CalendarTvMalScreen(
    viewModel: CalendarMalViewModel,
    navigator: NavHostController,
) {
    /** флаг состояния нижнего меню - открыто/закрыто */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(false)

    BackHandler() {
        if (isDrawerOpen) {
            viewModel.isDrawerOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    TvBoxWithVerticalDrawer(
        isDrawerOpen = isDrawerOpen,
        drawerContent = {
            CalendarTvMalScreenDrawer(
                viewModel = viewModel,
                navigator = navigator
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = ShikidroidTheme.colors.background
                )
        ) {
            CalendarTvMalSearchBar(viewModel = viewModel)
            CalendarTvMalLazyList(viewModel = viewModel, navigator = navigator)
        }
    }
}

/**
 * Поиск по списку с графиком выхода серий
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun CalendarTvMalSearchBar(
    viewModel: CalendarMalViewModel
) {
    /** строка для поиска аниме*/
    val searchValue by viewModel.searchValue.observeAsState(initial = "")

    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    /** функционал управления фокусом */
    val focusManager = LocalFocusManager.current

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
                .padding(horizontal = fourteenDP, vertical = sevenDP),
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
                textModifier = Modifier
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
                    viewModel.reset()
                }
            )
        }
    }
}

/**
 * Список с датами выхода серий и горизонтальным списком для каждой даты
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации экрана
 */
@Composable
internal fun CalendarTvMalLazyList(
    viewModel: CalendarMalViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** список уже вышедших эпизодов */
    val releasedToday by viewModel.releasedToday.observeAsState()

    /** словарь дата выхода - список аниме */
    val dateAnimeMap by viewModel.dateAnimeMap.observeAsState()

    when {
        isLoading -> {
            Loader()
        }

        dateAnimeMap.isNullOrEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }

        else -> {
            /** состояние для вертикально расположенного списка */
            val tvLazyListState = rememberTvLazyListState()

            TvLazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = tvLazyListState,
                verticalArrangement = Arrangement.spacedBy(threeDP)
            ) {

                if (releasedToday?.isNotEmpty() == true) {
                    releasedToday?.let {
                        item {
                            CalendarTvMalItemRow(
                                rowTitle = RELEASED_TITLE,
                                list = it,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                    }
                }

                dateAnimeMap?.keys?.forEach { date ->
                    dateAnimeMap?.get(key = date)?.let { list ->
                        item {
                            CalendarTvMalItemRow(
                                rowTitle = date,
                                list = list,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                        }
                    }
                }

                item {
                    SpacerForList()
                }
            }
        }
    }
}

/**
 * Элемент списка графика выхода серий для AndroidTV вида "дата - горизонтальный список с аниме"
 *
 * @param rowTitle заголовок строки с датой
 * @param list список аниме, выходящих в этот день
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации по экранам
 */
@Composable
internal fun CalendarTvMalItemRow(
    rowTitle: String,
    list: List<AnimeMalModel>,
    viewModel: CalendarMalViewModel,
    navigator: NavHostController
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
            SelectableCard(
                interactionSource = interactionSource,
                isFocused = isFocused,
                scale = scale.value,
                unselectColor = Color.Transparent
            ) {
                RowTitleText(
                    modifier = Modifier
                        .clickable {
                            viewModel.currentDateString.value = rowTitle
                            viewModel.currentCalendar.value = list
                            viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                        },
                    text = rowTitle
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = sevenDP)
        ) {
            items(
                items = list,
                key = { it.id ?: 1 }
            ) { anime ->
                AnimeCalendarTvMalCard(
                    modifier = Modifier
                        .padding(
                            start = if (anime == list.first()) fourteenDP else fiveDP,
                            end = if (anime == list.last()) fourteenDP else fiveDP
                        ),
                    calendarModel = anime,
                    navigator = navigator
                )
            }
        }
    }
}

/**
 * Карточка элемента списка экрана "Календарь" для AndroidTV
 *
 * @param modifier модификатор
 * @param calendarModel модель данных элемента списка
 * @param navigator контроллер навигации
 */
@Composable
internal fun AnimeCalendarTvMalCard(
    modifier: Modifier = Modifier,
    calendarModel: AnimeMalModel,
    navigator: NavHostController
) {
    TvSelectable { interactionSource, isFocused, scale ->
        SelectableCard(
            modifier = modifier,
            interactionSource = interactionSource,
            isFocused = isFocused,
            scale = scale.value,
            unselectColor = Color.Transparent
        ) {
            ImageTextCard(
                modifier = Modifier
                    .clickable {
                        navigateDetailsTvAnimeScreen(
                            id = calendarModel.id,
                            navigator = navigator
                        )
                    },
                url = calendarModel.image?.large,
                overPicture = {
                    OverPictureOne(
                        textLeftTopCorner = "${calendarModel.broadcast?.startTime}"
                    )
                },
                firstText = StringUtils.getEmptyIfBothNull(
                    one = calendarModel.title,
                    two = calendarModel.alternativeTitles?.en
                ),
                secondTextTwo = "${calendarModel.episodes} эп."
            )
        }
    }
}

@Composable
internal fun CalendarTvMalScreenDrawer(
    viewModel: CalendarMalViewModel,
    navigator: NavHostController
) {
    /** выбранная дата в виде строки */
    val currentDateString by viewModel.currentDateString.observeAsState()

    /** список выбранного расписания выхода эпизодов */
    val currentCalendar by viewModel.currentCalendar.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RowTitleText(text = currentDateString.orEmpty())
        TvLazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = TvGridCells.Fixed(count = 5)
        ) {
            currentCalendar?.let { calendar ->
                items(
                    items = calendar,
                    key = { it.id ?: 1 }
                ) { anime ->
                    AnimeCalendarTvMalCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        calendarModel = anime,
                        navigator = navigator
                    )
                }
                item {
                    SpacerForList()
                }
            }
        }
    }
}