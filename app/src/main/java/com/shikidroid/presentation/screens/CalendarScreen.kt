package com.shikidroid.presentation.screens

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
import com.shikidroid.domain.models.calendar.CalendarModel
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.DefaultExpandCard
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.viewmodels.CalendarViewModel
import com.shikidroid.presentation.viewmodels.RateHolder
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.RELEASED_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.StringUtils.getEmptyIfBothNull
import com.shikidroid.utils.toHourMinutes
import java.util.*

/**
 * Экран графика выхода серий аниме
 *
 * @param viewModel вью модель экрана
 * @param rateHolder интерфейс доступа к общей вью модели контейнера с боттом баром
 * @param navigator контроллер навигации экранов
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CalendarScreen(
    viewModel: CalendarViewModel,
    rateHolder: RateHolder? = null,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** ссылка для перехода в WebView и получения ответа, если ошибка 403 */
    val urlWebViewIf403 by viewModel.urlWebViewIf403.observeAsState(initial = "")

    /** список аниме календаря */
    val calendarAnimeList by viewModel.calendarAnimeList.observeAsState(initial = listOf())

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

    /** пользовательский список аниме */
    val ratesList = mutableListOf<RateModel>()

    /**
     * функция получения горизонтального списка Календаря с учётом списка пользователя
     *
     * @param itemList элементы горизонтального списка
     */
    fun getCalendarRow(itemList: List<CalendarModel>): List<CalendarModel> {
        val calendarRow: MutableList<CalendarModel> = mutableListOf()

        if (ratesList.isNotEmpty()) {
            // аниме, которые есть в списках пользователя
            val rates = mutableListOf<CalendarModel>()

            // список остальных аниме
            val others = mutableListOf<CalendarModel>()

            // индекс для оптимизации прохода по списку
            var index = 0

            val ratesListSize = ratesList.size

            for (calendarModel in itemList) {

                // цикл для добавления аниме в список пользователя
                for (n in index until ratesListSize) {
                    val rate = ratesList[n]

                    if (calendarModel.anime?.id == rate.anime?.id) {
                        if (!rates.contains(calendarModel)) {
                            calendarModel.status = rate.status
                            rates.add(calendarModel)
                        }

                        // устанавливаем значение индекса для следующего прохождения основного цикла
                        index =
                            if (n < ratesListSize - 1) {
                                n + 1
                            } else {
                                n
                            }

                        // прерываем текущий цикл, так как элемент добавлен
                        break
                    }
                }

                // после конца внутреннего цикла добавляем данные во второй список,
                // если они не были добавлены в первый список
                if (!rates.contains(calendarModel)) {
                    if (!others.contains(calendarModel)) {
                        others.add(calendarModel)
                    }
                }
            }

            calendarRow.addAll(rates)
            calendarRow.addAll(others)
        }

        if (calendarRow.isEmpty()) {
            return itemList
        } else {
            return calendarRow
        }
    }

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
            CalendarScreenDrawer(viewModel = viewModel, navigator = navigator)
        }
    ) {
        BoxWithSlideTopBar(
            toolbar = {
                CalendarSearchBar(viewModel = viewModel)
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

                    rateHolder?.let { holder ->
                        calendarAnimeList.map { calendarAnime ->
                            if (calendarAnime?.id != null) {
                                holder.animeRatesForCalendar.find { rate ->
                                    calendarAnime.id == rate.anime?.id
                                }?.let {
                                    if (!ratesList.contains(it)) {
                                        ratesList.add(it)
                                    }
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(threeDP),
                        contentPadding = PaddingValues(top = space, bottom = space)
                    ) {

                        // Вышедшее
                        if (releasedToday?.isNotEmpty() == true) {
                            releasedToday?.let { released ->

                                item {
                                    CalendarItemRow(
                                        rowTitle = RELEASED_TITLE,
                                        list = getCalendarRow(itemList = released),
                                        viewModel = viewModel,
                                        navigator = navigator
                                    )
                                }
                            }
                        }

                        // Остальные горизонтальные списки с эпизодами
                        dateAnimeMap?.keys?.forEach { date ->
                            dateAnimeMap?.get(key = date)?.let { dateAnimeList ->

                                val rowTitle = (DateUtils.getDateString(
                                    date = date,
                                    pattern = DateUtils.EEEE_D_MMMM
                                )).replaceFirstChar {
                                    it.uppercase()
                                }

                                item {
                                    CalendarItemRow(
                                        rowTitle = rowTitle,
                                        list = getCalendarRow(itemList = dateAnimeList),
                                        viewModel = viewModel,
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

    if (!urlWebViewIf403.isNullOrEmpty()) {
        WebViewScreen(
            url = urlWebViewIf403,
            getHtmlBodyString = { bodyString ->
                viewModel.setCalendarListFromJson(jsonString = bodyString)
            },
            navigator = navigator
        )
    }
}

@Composable
internal fun CalendarSearchBar(
    viewModel: CalendarViewModel
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
internal fun CalendarItemRow(
    rowTitle: String,
    list: List<CalendarModel>,
    viewModel: CalendarViewModel,
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
                key = { it.anime?.id ?: 1 }
            ) { anime ->
                AnimeCalendarCard(
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
 * Карточка элемента списка экрана "Календарь"
 *
 * @param modifier модификатор
 * @param calendarModel модель данных элемента списка
 * @param navigator контроллер навигации
 */
@Composable
internal fun AnimeCalendarCard(
    modifier: Modifier = Modifier,
    calendarModel: CalendarModel,
    navigator: NavHostController
) {
    /** текуще время */
    val currentTime = Calendar.getInstance().time

    /** время следующего эпизода */
    val nextEpisodeDate = DateUtils.fromString(calendarModel.nextEpisodeDate)

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
                            id = calendarModel.anime?.id,
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = calendarModel.anime?.image?.original,
                overPicture = {
                    nextEpisodeDate?.let { episodeDate ->
                        if (episodeDate > currentTime) {
                            val calendarDate = DateUtils.getCalendarByDate(date = episodeDate)
                            CalendarOverPicture(
                                textLeftTopCorner = "${calendarDate?.toHourMinutes()}",
                                rateStatusTopCorner = calendarModel.status
                            )
                        } else {
                            CalendarOverPicture(
                                rateStatusTopCorner = calendarModel.status
                            )
                        }
                    }
                },
                firstText = getEmptyIfBothNull(
                    one = calendarModel.anime?.nameRu,
                    two = calendarModel.anime?.name
                ),
                secondTextOne = "${calendarModel.nextEpisode} эп.",
                secondTextTwo = nextEpisodeDate?.let { episodeDate ->
                    if (episodeDate > currentTime) {
                        DateUtils.getDateBeforeCurrent(episodeDate)
                    } else {
                        null
                    }
                }
            )
        },
        status = calendarModel.anime?.status,
        score = calendarModel.anime?.score,
        episodes = calendarModel.anime?.episodes,
        episodesAired = calendarModel.anime?.episodesAired,
        dateAired = calendarModel.anime?.dateAired,
        dateReleased = calendarModel.anime?.dateReleased
    )
}

/**
 * Нижнее выдвижное меню
 *
 * @param viewModel вью модель экрана
 * @param navigator навигатор для навигации по приложению
 */
@Composable
internal fun CalendarScreenDrawer(
    viewModel: CalendarViewModel,
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
                    AnimeCalendarCard(
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