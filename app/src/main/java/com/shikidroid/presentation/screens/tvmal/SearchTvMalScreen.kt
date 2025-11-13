package com.shikidroid.presentation.screens.tvmal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.tv.foundation.lazy.grid.*
import com.shikidroid.R
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.navigateDetailsTvAnimeScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.OverPictureTwo
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.SearchScreenViewModel
import com.shikidroid.presentation.viewmodels.mal.SearchScreenMalViewModel
import com.shikidroid.ui.*
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.INPUT_TITLE_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces

/**
 * Экран "Поиск" для AndroidTV
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации
 */
@Composable
internal fun SearchTvMalScreen(
    viewModel: SearchScreenMalViewModel,
    navigator: NavHostController
) {
    BackHandler() {
        navigator.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = ShikidroidTheme.colors.background
            )
    ) {
        SearchTvMalBar(viewModel = viewModel)
        SearchTvMalAnimeGrid(
            viewModel = viewModel,
            navigator = navigator
        )
    }
}

@Composable
internal fun SearchTvMalBar(
    viewModel: SearchScreenMalViewModel
) {
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
                        viewModel.reset()
                    }
                )
            }
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
                    viewModel.reset()
                }
            )
        }
    }
}

@Composable
internal fun TvMalLazyGridForPage(
    tvLazyGridState: TvLazyGridState,
    viewModel: SearchScreenMalViewModel,
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
internal fun SearchTvMalAnimeGrid(
    viewModel: SearchScreenMalViewModel,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** список аниме из поиска */
    val animeSearch by viewModel.animeSearch.observeAsState(listOf())

    /** состояния для списка сетки */
    val tvLazyGridState = rememberTvLazyGridState()

    when {
        isLoading -> {
            Loader()
        }
        animeSearch.isEmpty() && !isLoading -> {
            ListIsEmpty(text = EMPTY_SEARCH_TITLE)
        }
        else -> {
            TvMalLazyGridForPage(
                tvLazyGridState = tvLazyGridState,
                viewModel = viewModel
            ) {
                it.apply {
                    items(
                        items = animeSearch,
                        key = { it.id ?: 0 }
                    ) { anime ->
                        TvSelectable() { interactionSource, isFocused, scale ->
                            SelectableCard(
                                modifier = Modifier
                                    .padding(sevenDP),
                                interactionSource = interactionSource,
                                isFocused = isFocused,
                                scale = scale.value,
                                unselectColor = Color.Transparent
                            ) {
                                SearchTvMalAnimeCard(
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
internal fun SearchTvMalAnimeCard(
    animeModel: AnimeMalModel,
    navigator: NavHostController
) {
    ImageWithOverPicture(
        url = animeModel.image?.large,
        height = 242.dp,
        width = 174.dp,
        overPicture = {
            OverPictureTwo(
                expanded = false,
                textLeftTopCorner = animeModel.type?.toScreenString(),
                textBottomCenter = StringUtils.getEmptyIfBothNull(
                    one = animeModel.title,
                    two = animeModel.alternativeTitles?.en
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