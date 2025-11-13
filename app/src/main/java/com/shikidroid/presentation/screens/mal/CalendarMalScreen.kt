package com.shikidroid.presentation.screens.mal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.DefaultExpandCard
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.OverPictureOne
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.viewmodels.mal.CalendarMalViewModel
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.RELEASED_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.StringUtils.getEmptyIfBothNull
import java.util.*

/**
 * Экран графика выхода серий аниме
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CalendarMalScreen(
    viewModel: CalendarMalViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** список уже вышедших эпизодов */
    val releasedToday by viewModel.releasedToday.observeAsState()

    /** словарь дата выхода - список аниме */
    val dateAnimeMap by viewModel.dateAnimeMap.observeAsState()

    /** флаг состояния нижнего меню - открыто/закрыто */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(false)

    /** состояние обновления по свайпу */
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            viewModel.reset()
        }
    )

    BackHandler() {
        if (isDrawerOpen) {
            viewModel.isDrawerOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    BoxWithVerticalDrawer(
        mainModifier = Modifier
            .pullRefresh(state = pullRefreshState),
        isDrawerOpen = isDrawerOpen,
        drawerContent = {
            CalendarMalScreenDrawer(viewModel = viewModel, navigator = navigator)
        }
    ) {
        BoxWithSlideTopBar(
            toolbar = {
                CalendarMalSearchBar(viewModel = viewModel)
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
                        backgroundColor = ShikidroidTheme.colors.surface,
                    )
                }
            }
        ) { space ->
            /** состояние для вертикально расположенного списка */
            val lazyListState = rememberLazyListState()

            when {
                isLoading -> {
                    Loader()
                }
                dateAnimeMap.isNullOrEmpty() && !isLoading -> {
                    ListIsEmpty(text = EMPTY_SEARCH_TITLE)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(threeDP)
                    ) {

                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = space)
                            )
                        }

                        if (releasedToday?.isNotEmpty() == true) {
                            releasedToday?.let {
                                item {
                                    CalendarMalItemRow(
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
                                    CalendarMalItemRow(
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
    }
}

@Composable
internal fun CalendarMalSearchBar(
    viewModel: CalendarMalViewModel
) {
    /** строка для поиска аниме*/
    val searchValue by viewModel.searchValue.observeAsState(initial = "")

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
                    text = INPUT_TITLE_TEXT,
                    color = ShikidroidTheme.colors.onBackground
                )
            },
            singleLine = true,
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

/**
 * Элемент списка графика выхода серий вида "дата - горизонтальный список с аниме"
 *
 * @param rowTitle заголовок строки с датой
 * @param list список аниме, выходящих в этот день
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации по экранам
 */
@Composable
internal fun CalendarMalItemRow(
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
        RowTitleText(
            modifier = Modifier
                .clickable {
                    viewModel.currentDateString.value = rowTitle
                    viewModel.currentCalendar.value = list
                    viewModel.isDrawerOpen.value = viewModel.isDrawerOpen.value != true
                },
            text = rowTitle
        )
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
                AnimeMalCalendarCard(
                    modifier = Modifier
                        .padding(
                            start = if (anime == list.first()) fourteenDP else fiveDP,
                            end = if (anime == list.last()) fourteenDP else fiveDP
                        ),
                    animeModel = anime,
                    navigator = navigator
                )
            }
        }
    }
}

/**
 * Карточка элемента списка экрана "Календарь"
 *
 * @param modifier модификатор
 * @param animeModel модель данных элемента списка
 * @param navigator контроллер навигации
 */
@Composable
internal fun AnimeMalCalendarCard(
    modifier: Modifier = Modifier,
    animeModel: AnimeMalModel,
    navigator: NavHostController
) {
    /** флаг раскрытия карточки */
    var expanded by remember {
        mutableStateOf(false)
    }

    DefaultExpandCard(
        useCard = false,
        expanded = expanded,
        mainContent = {
            ImageTextCard(
                modifier = modifier,
                imageModifier = Modifier
                    .clickable {
                        navigateDetailsAnimeScreen(
                            id = animeModel.id,
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = animeModel.image?.large,
                overPicture = {
                    OverPictureOne(
                        textLeftTopCorner = animeModel.broadcast?.startTime
                    )
                },
                firstText = getEmptyIfBothNull(
                    one = animeModel.title,
                    two = animeModel.alternativeTitles?.en
                ),
                secondTextTwo = "${animeModel.episodes} эп.",
            )
        },
        status = animeModel.status,
        score = animeModel.score,
        episodes = animeModel.episodes,
        dateAired = animeModel.dateAired,
        dateReleased = animeModel.dateReleased,
        isMal = true
    )
}

/**
 * Нижнее выдвижное меню
 *
 * @param viewModel вью модель экрана
 * @param navigator навигатор для навигации по приложению
 */
@Composable
internal fun CalendarMalScreenDrawer(
    viewModel: CalendarMalViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** выбранная дата в виде строки */
    val currentDateString by viewModel.currentDateString.observeAsState()

    /** список выбранного расписания выхода эпизодов */
    val currentCalendar by viewModel.currentCalendar.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            currentCalendar?.let { calendar ->
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = currentDateString.orEmpty())
                }
                gridItems(
                    data = calendar,
                    columnCount = if (!isScreenHorizontal) 2 else 5
                ) { anime ->
                    AnimeMalCalendarCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        animeModel = anime,
                        navigator = navigator
                    )
                }
                item {
                    SpacerForList()
                }
            }
        }
        RoundedIconButton(
            modifier = Modifier
                .setStatusBarPadding(),
            icon = R.drawable.ic_chevron_up,
            tint = ShikidroidTheme.colors.onPrimary,
            onClick = {
                viewModel.isDrawerOpen.value =
                    viewModel.isDrawerOpen.value != true
            }
        )
    }
}