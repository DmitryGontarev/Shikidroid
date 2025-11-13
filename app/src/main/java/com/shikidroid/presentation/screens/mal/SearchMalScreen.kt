package com.shikidroid.presentation.screens.mal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.*
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.OverPictureTwo
import com.shikidroid.presentation.viewmodels.mal.SearchScreenMalViewModel
import com.shikidroid.ui.*
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces

/**
 * Экран "Поиск"
 *
 * @param prefs доступ к локальному хранилищу системы Android
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SearchMalScreen(
    viewModel: SearchScreenMalViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** состояние обновления по свайпу */
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.reset() }
    )

    BackHandler() {
        navigator.popBackStack()
    }

    BoxWithSlideTopBar(
        toolbar = {
            SearchMalScreenSearchBar(viewModel = viewModel)
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
        AnimeMalSearch(
            spacer = spacer,
            viewModel = viewModel,
            navigator = navigator
        )
    }
}

@Composable
internal fun SearchMalScreenSearchBar(
    viewModel: SearchScreenMalViewModel
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
            label = {
                if (searchValue.isNullOrEmpty()) {
                    Text(
                        text = INPUT_TITLE_TEXT,
                        color = ShikidroidTheme.colors.onBackground
                    )
                }
                if (searchValue.deleteEmptySpaces().isNotEmpty()) {
                    Text(
                        text = INPUT_MIN_THREE_SYMBOLS,
                        color = ShikidroidTheme.colors.onPrimary
                    )
                }
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
                        viewModel.reset()
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
internal fun LazyGridForPageMal(
    spacer: Dp,
    lazyGridState: LazyGridState,
    viewModel: SearchScreenMalViewModel,
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
        columns = GridCells.Fixed(if (!isScreenHorizontal) 2 else 4)
    ) {
        for (i in 0 until if (!isScreenHorizontal) 2 else 4) {
            item {
                Spacer(modifier = Modifier.height(height = spacer))
            }
        }

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

        for (i in 0 until if (!isScreenHorizontal) 2 else 5) {
            item {
                SpacerForList()
            }
        }
    }
}

@Composable
internal fun AnimeMalSearch(
    spacer: Dp,
    viewModel: SearchScreenMalViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** список аниме из поиска */
    val animeSearch by viewModel.animeSearch.observeAsState(listOf())

    /** состояния для списка сетки */
    val lazyGridState = rememberLazyGridState()

    when {
        isLoading -> {
            Loader()
        }

        animeSearch.isEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }

        else -> {
            LazyGridForPageMal(
                spacer = spacer,
                lazyGridState = lazyGridState,
                viewModel = viewModel
            ) {
                it.apply {
                    items(
                        items = animeSearch,
                        key = { it.id ?: 0 }
                    ) { anime ->
                        SearchMalCard(
                            onClick = {
                                navigateDetailsAnimeScreen(
                                    id = anime.id,
                                    navigator = navigator
                                )
                            },
                            url = anime.image?.large,
                            textLeftTop = anime.type?.toScreenString(),
                            textBottom =
                            StringUtils.getEmptyIfBothNull(
                                one = anime.title,
                                two = anime.alternativeTitles?.en
                            ),
                            status = anime.status,
                            score = anime.score,
                            episodes = anime.episodes,
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
internal fun SearchMalCard(
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
        dateReleased = dateReleased,
        isMal = true
    )
}