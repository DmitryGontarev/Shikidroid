package com.shikidroid.presentation.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import com.shikidroid.R
import com.shikidroid.domain.converters.toAnimeRateModel
import com.shikidroid.domain.models.anime.AnimeDetailsModel
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.openLink
import com.shikidroid.presentation.BottomSheetType
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.navigateDetailsTvAnimeScreen
import com.shikidroid.presentation.navigation.navigateEpisodeTvScreen
import com.shikidroid.presentation.navigation.navigateSearchTvScreenByAnimeGenreId
import com.shikidroid.presentation.navigation.navigateSearchTvScreenByAnimeStudioId
import com.shikidroid.presentation.navigation.navigateWebViewScreen
import com.shikidroid.presentation.navigation.navigateWebViewTvScreen
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.screens.items.SelectableGenreCard
import com.shikidroid.presentation.viewmodels.DetailsScreenViewModel
import com.shikidroid.presentation.viewmodels.RateHolder
import com.shikidroid.ui.*
import com.shikidroid.ui.AGE_TEXT
import com.shikidroid.ui.EPISODES_TEXT
import com.shikidroid.ui.EPISODE_TIME_TEXT
import com.shikidroid.ui.RELEASE_DATE_TEXT
import com.shikidroid.ui.TYPE_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.IntUtils.toEpisodeTime
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.containsOnlyZero
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.StringUtils.firstNotZeroIndex

/**
 * Экран детальной информации об аниме для AndroidTV
 *
 * @param viewModel вью модель экрана
 * @param rateHolder интерфейс доступа к общей вью модели контейнера
 * @param navigator контроллер навигации экранов
 */
@Composable
internal fun DetailsTvScreen(
    viewModel: DetailsScreenViewModel,
    rateHolder: RateHolder?,
    navigator: NavHostController
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** флаг показа экрана с ошибкой */
    val showErrorScreen by viewModel.showErrorScreen.observeAsState(initial = false)

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** состояние выдвижного меню открыто/закрыто */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(false)

    /** тип нижней шторки */
    val rightDrawerType = rateHolder?.bottomSheetType?.observeAsState()

    /** состояние бокового выдвижного меню открыто/закрыто */
    val isRightDrawerOpen = rateHolder?.isBottomSheetVisible?.observeAsState(false)

    /** состояние прокрутки */
    val scrollState = rememberScrollState(0)

    LaunchedEffect(key1 = isLoading) {
        if (isLoading == false) {
            rateHolder?.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
        }
    }

    BackHandler() {
        if (isDrawerOpen || isRightDrawerOpen?.value == true) {
            rateHolder?.hideBottomSheet()
            viewModel.isDrawerOpen.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    when {
        isLoading -> {
            Loader()
        }

        showErrorScreen -> {
            ErrorTvScreen(
                mainClick = {
                    viewModel.id?.let {
                        viewModel.hideErrorScreen()
                        viewModel.loadDetails(id = it)
                    }
                },
                altClick = {
                    navigator.popBackStack()
                }
            )
        }

        else -> {
            TvBoxWithVerticalDrawer(
                mainModifier = Modifier,
                isDrawerOpen = isDrawerOpen,
                drawerContent = {
                    DetailsScreenTvDrawer(
                        viewModel = viewModel,
                        navigator = navigator
                    )
                }
            ) {
                TvBoxWithRightHorizontalDrawer(
                    isDrawerOpen = isRightDrawerOpen?.value ?: false,
                    drawerContent = {
                        when (rightDrawerType?.value) {
                            BottomSheetType.RATE_MODEL_EDIT -> {
                                RightDrawerAnimeEdit(
                                    animeRateHolder = rateHolder
                                )
                            }

                            BottomSheetType.RATE_LIST_EDIT -> {
                                RightDrawerAnimeList(
                                    animeRateHolder = rateHolder
                                )
                            }

                            else -> Unit
                        }
                    }
                ) {
                    BoxWithBackground(
                        backgroundColor = ShikidroidTheme.colors.background,
                        backgroundAlpha = 0.90f,
                        backgroundContent = {
                            AsyncImageLoader(url = animeModel?.image?.original)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .verticalScroll(
                                    state = scrollState
                                )
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 700,
                                        easing = LinearOutSlowInEasing
                                    )
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DetailsTvToolbar(
                                animeModel = animeModel,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                            AnimeHeader(
                                viewModel = viewModel,
                                rateHolder = rateHolder,
                                navigator = navigator
                            )
                            AnimeGenres(viewModel = viewModel, navigator = navigator)
                            AnimeDescription(viewModel = viewModel, navigator = navigator)
                            ScreenshotsTv(viewModel = viewModel)
                            VideosTv(viewModel = viewModel)
                            RelatedAnimeTv(navigator = navigator, viewModel = viewModel)
                            SimilarAnimeTv(viewModel = viewModel, navigator = navigator)
                            SpacerForList()
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DetailsTvToolbar(
    animeModel: AnimeDetailsModel?,
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = sevenDP, vertical = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TvSelectable { interactionSource, isFocused, scale ->
            RoundedIconButton(
                modifier = Modifier
                    .focusable(interactionSource = interactionSource)
                    .scale(scale = scale.value),
                icon = R.drawable.ic_arrow_back,
                backgroundColor =
                if (isFocused) {
                    ShikidroidTheme.colors.secondaryVariant
                } else {
                    Color.Transparent
                },
                onClick = {
                    navigator.popBackStack()
                },
                tint = ShikidroidTheme.colors.onPrimary
            )
        }
//        Text(
//            modifier = Modifier
//                .wrapContentSize()
//                .weight(weight = 0.95f)
//                .padding(start = sevenDP),
//            text = animeModel?.nameRu.orEmpty(),
//            style = ShikidroidTheme.typography.body16sp,
//            color = ShikidroidTheme.colors.onPrimary,
//            overflow = TextOverflow.Ellipsis,
//            textAlign = TextAlign.Start
//        )
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            TvSelectable { interactionSource, isFocused, scale ->
                RoundedIconButton(
                    modifier = Modifier
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    icon = R.drawable.ic_three_dots,
                    backgroundColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        Color.Transparent
                    },
                    tint = ShikidroidTheme.colors.onPrimary,
                    onClick = {
                        viewModel.showDropdownMenu.value =
                            viewModel.showDropdownMenu.value != true
                    }
                )
            }
            DropdownDetailsTvMenu(viewModel = viewModel)
        }
    }
}

@Composable
internal fun DropdownDetailsTvMenu(
    viewModel: DetailsScreenViewModel
) {
    /** контекст */
    val context = LocalContext.current

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownMenu by viewModel.showDropdownMenu.observeAsState(initial = false)

    DropdownMenu(
        modifier = Modifier
            .background(
                color = ShikidroidTheme.colors.surface
            ),
        expanded = showDropdownMenu,
        onDismissRequest = { viewModel.showDropdownMenu.value = false }
    ) {

        TvSelectable { interactionSource, isFocused, scale ->

            com.shikidroid.presentation.screens.DropdownMenuItem(
                modifier = Modifier
                    .focusable(enabled = true, interactionSource = interactionSource)
                    .scale(scale = scale.value)
                    .background(
                        color =
                        when {
                            isFocused -> ShikidroidTheme.colors.secondaryVariant
                            else -> Color.Transparent
                        }
                    )
                    .clickable {
                        navigateWebViewTvScreen(
                            url = animeModel?.url,
                            context = context
                        )
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_public,
                text = WEB_VERSION_TITLE
            )

        }
    }
}

@Composable
internal fun AnimeHeader(
    viewModel: DetailsScreenViewModel,
    rateHolder: RateHolder?,
    navigator: NavHostController
) {
    /** состояние бокового выдвижного меню открыто/закрыто */
    val isRightDrawerOpen = rateHolder?.isBottomSheetVisible?.observeAsState(false)

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** выбранный элемента списка */
    val currentItem = rateHolder?.currentItem?.observeAsState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = fourteenDP, end = fourteenDP, bottom = threeDP),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {

            val (info, card, buttons) = createRefs()

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(info) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(card.start)
                    },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LabelTvText(
                    text = animeModel?.type?.toScreenString().orEmpty(),
                    labelText = TYPE_TEXT
                )
                if (animeModel?.type != AnimeType.MOVIE) {
                    LabelTvText(text = animeModel?.episodes.toString(), labelText = EPISODES_TEXT)
                }
                animeModel?.duration?.let { duration ->
                    if (duration > 0) {
                        LabelTvText(
                            text = "${animeModel?.duration?.toEpisodeTime()}",
                            labelText = EPISODE_TIME_TEXT
                        )
                    }
                }
                animeModel?.ageRating?.let { age ->
                    if (age.toScreenString().isNotEmpty()) {
                        LabelTvText(text = age.toScreenString(), labelText = AGE_TEXT)
                    }
                }
                when {
                    animeModel?.anons == true -> {
                        animeModel?.dateAired?.let { date ->
                            LabelTvText(
                                text = DateUtils.getDatePeriod(
                                    DateUtils.fromString(dateString = date)
                                ),
                                labelText = RELEASE_DATE_TEXT
                            )
                        }
                    }

                    animeModel?.ongoing == true -> {
                        animeModel?.nextEpisodeDate?.let { date ->
                            LabelTvText(
                                text = DateUtils.getDateBeforeCurrent(
                                    DateUtils.fromString(dateString = date)
                                ),
                                labelText = "до ${animeModel?.episodesAired?.plus(1)} эп."
                            )
                        }

                        if (animeModel?.nextEpisodeDate == null) {
                            animeModel?.dateAired?.let { date ->
                                LabelTvText(
                                    text = DateUtils.getDatePeriod(
                                        DateUtils.fromString(dateString = date)
                                    ),
                                    labelText = "по ${DateUtils.getDayFromString(dateString = date)}"
                                )
                            }
                        }
                    }

                    else -> {
                        when (animeModel?.type) {
                            AnimeType.MOVIE, AnimeType.SPECIAL -> {
                                (animeModel?.dateAired
                                    ?: animeModel?.dateReleased)?.let { date ->
                                    LabelTvText(
                                        text = DateUtils.getDatePeriod(
                                            DateUtils.fromString(dateString = date)
                                        ),
                                        labelText = RELEASE_DATE_TEXT
                                    )
                                }
                            }

                            else -> {
                                when {
                                    animeModel?.dateAired.isNullOrEmpty()
                                        .not() && animeModel?.dateReleased.isNullOrEmpty() -> {
                                        LabelTvText(
                                            text = DateUtils.getDatePeriod(
                                                DateUtils.fromString(
                                                    dateString = animeModel?.dateAired
                                                )
                                            ), labelText = RELEASE_DATE_TEXT
                                        )
                                    }

                                    animeModel?.dateAired.isNullOrEmpty() && animeModel?.dateReleased.isNullOrEmpty()
                                        .not() -> {
                                        LabelTvText(
                                            text = DateUtils.getDatePeriod(
                                                DateUtils.fromString(
                                                    dateString = animeModel?.dateReleased
                                                )
                                            ), labelText = RELEASE_DATE_TEXT
                                        )
                                    }

                                    else -> {
                                        LabelTvText(
                                            text = DateUtils.getDatePeriod(
                                                DateUtils.fromString(dateString = animeModel?.dateAired),
                                                DateUtils.fromString(dateString = animeModel?.dateReleased),
                                                isNextLine = true
                                            ), labelText = RELEASE_DATE_TEXT
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(card) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(232.dp)
                        .width(184.dp)
                        .clip(shape = ShikidroidTheme.shapes.roundedCorner7dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImageLoader(url = animeModel?.image?.original)
                }
                Row(
                    modifier = Modifier
                        .padding(all = sevenDP),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShikiRatingBar(
                        value = animeModel?.score?.toFloat() ?: 0.0f,
                        numStars = 10,
                        starSize = tenDP,
                        enabled = false,
                        isStepSize = true,
                        activeColor = ShikidroidTheme.colors.secondary,
                        inactiveColor = ShikidroidTheme.colors.onBackground,
                        onValueChange = { },
                        onRatingChanged = { }
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = sevenDP),
                        text = "${animeModel?.score?.toFloat()}",
                        style = ShikidroidTheme.typography.body13sp,
                        color = ShikidroidTheme.colors.onPrimary
                    )
                }
            }

            ConstraintLayout(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(buttons) {
                        top.linkTo(parent.top)
                        start.linkTo(card.end, margin = sevenDP)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                val (status, btns) = createRefs()

                animeModel?.status?.let { airedStatus ->
                    Text(
                        modifier = Modifier
                            .constrainAs(status) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(btns.top, margin = sevenDP)
                                end.linkTo(parent.end)
                            },
                        text = airedStatus.toScreenString(),
                        color = airedStatus.toColor(),
                        style = ShikidroidTheme.typography.body13sp,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .constrainAs(btns) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    rateHolder?.let {
                        TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                            Row(
                                modifier = Modifier
                                    .wrapContentSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                AnimatedVisibility(visible = isFocused) {
                                    Text(
                                        text = EDIT_TEXT,
                                        color = ShikidroidTheme.colors.onPrimary,
                                        style = ShikidroidTheme.typography.body12sp
                                    )
                                }
                                RoundedIconButton(
                                    iconBoxModifier = Modifier
                                        .focusable(interactionSource = interactionSource)
                                        .scale(scale = scale.value),
                                    icon = R.drawable.ic_edit_small,
                                    tint = ShikidroidTheme.colors.onPrimary,
                                    backgroundColor =
                                    if (isFocused) {
                                        ShikidroidTheme.colors.secondaryVariant
                                    } else {
                                        BackgroundLightGray
                                    },
                                    onClick = {
                                        if (isRightDrawerOpen?.value == true) {
                                            rateHolder.hideBottomSheet()
                                        } else {
                                            rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
                                            rateHolder.showBottomSheet(
                                                bottomType = BottomSheetType.RATE_MODEL_EDIT
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                            Row(
                                modifier = Modifier
                                    .wrapContentSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                AnimatedVisibility(visible = isFocused) {
                                    Text(
                                        text = IN_LIST_TEXT,
                                        color = ShikidroidTheme.colors.onPrimary,
                                        style = ShikidroidTheme.typography.body12sp
                                    )
                                }
                                RoundedIconButton(
                                    iconBoxModifier = Modifier
                                        .focusable(interactionSource = interactionSource)
                                        .scale(scale = scale.value),
                                    icon = currentItem?.value?.status.toDrawable(),
                                    tint = currentItem?.value?.status?.toColor()
                                        ?: ShikidroidTheme.colors.onPrimary,
                                    backgroundColor =
                                    if (isFocused) {
                                        ShikidroidTheme.colors.secondaryVariant
                                    } else {
                                        currentItem?.value?.status.toBackgroundColor()
                                    },
                                    onClick = {
                                        if (isRightDrawerOpen?.value == true) {
                                            rateHolder.hideBottomSheet()
                                        } else {
                                            rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
                                            rateHolder.showBottomSheet(
                                                bottomType = BottomSheetType.RATE_LIST_EDIT
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                        Row(
                            modifier = Modifier
                                .wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            AnimatedVisibility(visible = isFocused) {
                                Text(
                                    text = WATCH_TEXT,
                                    color = ShikidroidTheme.colors.onPrimary,
                                    style = ShikidroidTheme.typography.body12sp
                                )
                            }
                            RoundedIconButton(
                                iconBoxModifier = Modifier
                                    .focusable(interactionSource = interactionSource)
                                    .scale(scale = scale.value),
                                icon = R.drawable.ic_play_arrow,
                                tint =
                                if (isFocused) {
                                    ShikidroidTheme.colors.secondary
                                } else {
                                    ShikidroidTheme.colors.onPrimary
                                },
                                backgroundColor =
                                if (isFocused) {
                                    ShikidroidTheme.colors.secondaryVariant
                                } else {
                                    BackgroundLightGray
                                },
                                onClick = {
                                    navigateEpisodeTvScreen(
                                        id = animeModel?.id,
                                        userRateId = animeModel?.userRate?.id,
                                        animeName = animeModel?.name.orEmpty(),
                                        animeNameRu = animeModel?.nameRu.orEmpty(),
                                        animeImageUrl = animeModel?.image?.original,
                                        navigator = navigator
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = animeModel?.nameRu.orEmpty(),
                style = ShikidroidTheme.typography.header24sp,
                color = ShikidroidTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = animeModel?.name.orEmpty(),
                style = ShikidroidTheme.typography.body12sp,
                color = ShikidroidTheme.colors.onBackground
            )
        }
    }
}

@Composable
internal fun AnimeGenres(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    val genres: List<GenreModel>? = animeModel?.genres

    LazyRow(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(vertical = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        animeModel?.studios?.let { studios ->
            if (studios.isNotEmpty()) {
                items(
                    items = studios
                ) { studio ->
                    TvSelectable { interactionSource, isFocused, scale ->
                        SelectableGenreCard(
                            modifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value)
                                .paddingByList(
                                    items = studios,
                                    item = studio,
                                    top = sevenDP,
                                    bottom = sevenDP
                                )
                                .clickable {
                                    navigateSearchTvScreenByAnimeStudioId(
                                        id = studio.id,
                                        navigator = navigator
                                    )
                                },
                            text = studio.name.orEmpty(),
                            textColor = ShikidroidTheme.colors.onPrimary,
                            selectBackgroundColor = ShikidroidTheme.colors.tvSelectable,
                            selectBorderColor = ShikidroidTheme.colors.onPrimary,
                            borderColor = ShikidroidTheme.colors.onBackground,
                            isSelect = isFocused
                        )
                    }
                }
            }
        }

        if (animeModel?.studios?.isNotEmpty() == true && genres.isNullOrEmpty().not()) {
            item {
                VerticalDivider(
                    color = ShikidroidTheme.colors.onBackground,
                    height = twentyFiveDP
                )
            }
        }

        if (genres.isNullOrEmpty().not()) {
            genres?.let { genres ->
                items(
                    items = genres,
                    key = { it.id ?: 0 }
                ) { genre ->
                    TvSelectable() { interactionSource, isFocused, scale ->
                        SelectableGenreCard(
                            modifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value)
                                .paddingByList(
                                    items = genres,
                                    item = genre,
                                    top = sevenDP,
                                    bottom = sevenDP
                                )
                                .clickable {
                                    navigateSearchTvScreenByAnimeGenreId(
                                        id = genre.id,
                                        navigator = navigator
                                    )
                                },
                            text = genre.nameRu.orEmpty(),
                            isSelect = isFocused
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AnimeDescription(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    val description: String? = animeModel?.description

    if (description?.trim().isNullOrEmpty().not()) {
        description?.let { descriptionString ->

            val annotation = StringUtils.getAnnotationString(
                text = descriptionString,
                textSize = 13.sp,
                primaryColor = ShikidroidTheme.colors.onPrimary,
                annotationColor = ShikidroidTheme.colors.secondary
            )

            val descriptionTypeLinkName = annotation.first

            val descriptionAnnotations = annotation.second

            descriptionAnnotations?.let { annotatedString ->

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(start = fourteenDP, end = fourteenDP, bottom = fourteenDP),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = fourteenDP)
                            .fillMaxWidth(),
                        text = "Описание",
                        color = ShikidroidTheme.colors.onSurface,
                        style = ShikidroidTheme.typography.bodySemiBold16sp,
                        textAlign = TextAlign.Start
                    )
                    ClickableText(
                        text = descriptionAnnotations,
                        style = ShikidroidTheme.typography.body13sp.copy(lineHeight = 20.sp),
                        onClick = { offset ->
                            descriptionTypeLinkName.forEach { typeLinkName ->
                                descriptionAnnotations.getStringAnnotations(
                                    tag = typeLinkName.third,
                                    start = offset,
                                    end = offset
                                ).firstOrNull()?.let {
//                                    navigateByLinkType(
//                                        link = it.item,
//                                        linkType = typeLinkName.first,
//                                        navigator = navigator
//                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ScreenshotsTv(
    viewModel: DetailsScreenViewModel
) {
    /** список скриншотов */
    val screenshotsList by viewModel.animeScreenshots.observeAsState(initial = listOf())

    if (screenshotsList.isNullOrEmpty().not()) {
        screenshotsList?.let { screenshots ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                TvSelectable(scaleAnimation = 1f) { interactionSource, isFocused, scale ->
                    SelectableCard(
                        interactionSource = interactionSource,
                        isFocused = isFocused,
                        scale = scale.value,
                        backgroundColor = Color.Transparent,
                        elevation = zeroDP,
                        unselectColor = Color.Transparent
                    ) {
                        RowTitleText(
                            modifier = Modifier
                                .scale(scale = scale.value)
                                .clickable {
                                    viewModel.showDrawer(type = DetailScreenDrawerType.SCREENSHOTS)
                                },
                            text = SCREENSHOTS_TITLE
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = sevenDP)
                ) {
                    items(
                        items = screenshots
                    ) { screenshot ->
                        TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                            SelectableCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = screenshots,
                                        item = screenshot
                                    ),
                                interactionSource = interactionSource,
                                isFocused = isFocused,
                                scale = scale.value,
                                unselectColor = Color.Transparent,
                            ) {
                                ImageWithOverPicture(
                                    modifier = Modifier,
                                    url = screenshot.preview,
                                    height = screenshotHeight,
                                    width = screenshotWidth
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
internal fun ScreenshotsTvDrawer(
    viewModel: DetailsScreenViewModel
) {
    /** список скриншотов */
    val screenshots by viewModel.animeScreenshots.observeAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            RowTitleText(text = SCREENSHOTS_TITLE)
        }
        gridItems(
            data = screenshots,
            columnCount = 3,
            modifier = Modifier
        ) { screenshot ->
            TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
                SelectableCard(
                    modifier = Modifier
                        .padding(sevenDP),
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value,
                    unselectColor = Color.Transparent
                ) {
                    ImageWithOverPicture(
                        modifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        url = screenshot.original,
                        height = screenshotTvDrawerHeight,
                        width = screenshotTvDrawerWidth,
                    )
                }
            }
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun VideosTv(
    viewModel: DetailsScreenViewModel,
) {
    /** контекст */
    val context = LocalContext.current

    /** список скриншотов */
    val animeVideos by viewModel.animeVideos.observeAsState(initial = listOf())

    if (animeVideos.isNullOrEmpty().not()) {
        animeVideos?.let { videos ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TvSelectable(scaleAnimation = 1f) { interactionSource, isFocused, scale ->
                    SelectableCard(
                        interactionSource = interactionSource,
                        isFocused = isFocused,
                        scale = scale.value,
                        backgroundColor = Color.Transparent,
                        elevation = zeroDP,
                        unselectColor = Color.Transparent
                    ) {
                        RowTitleText(
                            modifier = Modifier
                                .scale(scale = scale.value)
                                .clickable {
                                    viewModel.showDrawer(type = DetailScreenDrawerType.VIDEOS)
                                },
                            text = VIDEO_TITLE
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = sevenDP)
                ) {
                    items(
                        items = videos
                    ) { video ->
                        TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                            SelectableCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = videos,
                                        item = video
                                    ),
                                interactionSource = interactionSource,
                                isFocused = isFocused,
                                scale = scale.value,
                                unselectColor = Color.Transparent,
                                backgroundColor = Color.Transparent
                            ) {
                                ImageTextCard(
                                    modifier = Modifier
                                        .clickable {
                                            navigateWebViewTvScreen(
                                                url = video.url ?: video.playerUrl,
                                                context = context
                                            )
                                        },
                                    url = video.imageUrl,
                                    height = oneHundredDP,
                                    width = 165.dp,
                                    firstText = video.name.orEmpty(),
                                    secondTextTwo = video.hosting?.uppercase().orEmpty()
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
internal fun VideosTvDrawer(
    viewModel: DetailsScreenViewModel,
) {
    /** контекст */
    val context = LocalContext.current

    /** список скриншотов */
    val animeVideos by viewModel.animeVideos.observeAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            RowTitleText(text = VIDEO_TITLE)
        }
        gridItems(
            data = animeVideos,
            columnCount = 3,
            modifier = Modifier
        ) { video ->
            TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
                SelectableCard(
                    modifier = Modifier
                        .padding(sevenDP),
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value,
                    unselectColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                ) {
                    ImageTextCard(
                        modifier = Modifier
                            .clickable {
                                navigateWebViewTvScreen(
                                    url = video.url ?: video.playerUrl,
                                    context = context
                                )
                            },
                        url = video.imageUrl,
                        height = 167.dp,
                        width = screenshotTvDrawerWidth,
                        firstText = video.name.orEmpty(),
                        secondTextTwo = video.hosting?.uppercase().orEmpty()
                    )
                }
            }
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun RelatedAnimeTv(
    navigator: NavHostController,
    viewModel: DetailsScreenViewModel
) {
    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    val animeRelatedList = mutableListOf<AnimeModel>()

    relatedList?.forEach { related ->
        related.anime?.let {
            animeRelatedList.add(it)
        }
    }

    if (animeRelatedList.isEmpty().not()) {

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            TvSelectable(scaleAnimation = 1f) { interactionSource, isFocused, scale ->
                SelectableCard(
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    scale = scale.value,
                    backgroundColor = Color.Transparent,
                    elevation = zeroDP,
                    unselectColor = Color.Transparent
                ) {
                    RowTitleText(
                        modifier = Modifier
                            .scale(scale = scale.value)
                            .clickable {
                                viewModel.showDrawer(type = DetailScreenDrawerType.RELATED)
                            },
                        text = RELATED_TITLE
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(vertical = sevenDP)
            ) {
                relatedList?.map { relatedModel ->
                    relatedModel.anime?.let { anime ->
                        item {
                            DetailsAnimeTvCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = animeRelatedList,
                                        item = anime
                                    ),
                                animeId = anime.id,
                                imageUrl = anime.image?.original,
                                firstText = relatedModel.relationRu.orEmpty(),
                                secondTextOne = anime.type?.toScreenString(),
                                secondTextTwo = DateUtils.getYearString(
                                    date = DateUtils.fromString(
                                        dateString = anime.dateReleased
                                            ?: anime.dateAired
                                    )
                                ),
                                navigator = navigator
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RelatedAnimeTvDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    val animeModelList = mutableListOf<AnimeModel>()

    relatedList?.forEach { related ->
        related.anime?.let {
            animeModelList.add(it)
        }
    }

    val animeRelated = relatedList?.filter {
        it.anime != null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (animeRelated.isNullOrEmpty().not()) {
            animeRelated?.let { related ->
                item {
                    RowTitleText(text = RELATED_TITLE)
                }
                gridItems(
                    data = related,
                    columnCount = 5,
                ) { relatedModel ->
                    DetailsAnimeTvCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        animeId = relatedModel.anime?.id,
                        imageUrl = relatedModel.anime?.image?.original,
                        firstText = relatedModel.relationRu,
                        secondTextOne = relatedModel.anime?.type?.toScreenString(),
                        secondTextTwo = DateUtils.getYearString(
                            date = DateUtils.fromString(
                                dateString = relatedModel.anime?.dateReleased
                                    ?: relatedModel.anime?.dateAired
                            )
                        ),
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

@Composable
internal fun SimilarAnimeTv(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** похожее аниме */
    val animeSimilar by viewModel.animeSimilar.observeAsState()

    if (animeSimilar.isNullOrEmpty().not()) {
        animeSimilar?.let { similarAnime ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                TvSelectable(scaleAnimation = 1f) { interactionSource, isFocused, scale ->
                    SelectableCard(
                        interactionSource = interactionSource,
                        isFocused = isFocused,
                        scale = scale.value,
                        backgroundColor = Color.Transparent,
                        elevation = zeroDP,
                        unselectColor = Color.Transparent
                    ) {
                        RowTitleText(
                            modifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value)
                                .clickable {
                                    viewModel.showDrawer(type = DetailScreenDrawerType.SIMILAR_ANIME)
                                },
                            text = SIMILAR_TITLE
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
                        items = similarAnime
                    ) { anime ->
                        DetailsAnimeTvCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = similarAnime,
                                    item = anime
                                ),
                            animeId = anime.id,
                            imageUrl = anime.image?.original,
                            firstText = StringUtils.getEmptyIfBothNull(
                                one = anime.nameRu,
                                two = anime.name
                            ),
                            secondTextOne = anime.type?.toScreenString(),
                            secondTextTwo = DateUtils.getYearString(
                                date = DateUtils.fromString(
                                    dateString = anime.dateReleased
                                        ?: anime.dateAired
                                )
                            ),
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SimilarAnimeTvDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** похожее аниме */
    val animeSimilar by viewModel.animeSimilar.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        animeSimilar?.let { animeSimilar ->
            item {
                RowTitleText(text = SIMILAR_TITLE)
            }
            gridItems(
                data = animeSimilar,
                columnCount = 5
            ) { anime ->
                DetailsAnimeTvCard(
                    modifier = Modifier
                        .padding(fiveDP),
                    animeId = anime.id,
                    imageUrl = anime.image?.original,
                    firstText = StringUtils.getEmptyIfBothNull(
                        one = anime.nameRu,
                        two = anime.name
                    ),
                    secondTextOne = anime.type?.toScreenString(),
                    secondTextTwo = DateUtils.getYearString(
                        date = DateUtils.fromString(
                            dateString = anime.dateReleased
                                ?: anime.dateAired
                        )
                    ),
                    navigator = navigator
                )
            }
            item {
                SpacerForList()
            }
        }
    }
}

@Composable
internal fun DetailsScreenTvDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {

    /** тип контента меню */
    val drawerType by viewModel.drawerType.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = ShikidroidTheme.colors.background
            ),
        contentAlignment = Alignment.TopCenter
    ) {

        when (drawerType) {
            DetailScreenDrawerType.SCREENSHOTS -> {
                ScreenshotsTvDrawer(viewModel = viewModel)
            }

            DetailScreenDrawerType.VIDEOS -> {
                VideosTvDrawer(viewModel = viewModel)
            }

            DetailScreenDrawerType.RELATED -> {
                RelatedAnimeTvDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.SIMILAR_ANIME -> {
                SimilarAnimeTvDrawer(viewModel = viewModel, navigator = navigator)
            }

            else -> Unit
        }
    }
}

@Composable
internal fun DetailsAnimeTvCard(
    modifier: Modifier,
    animeId: Long?,
    imageUrl: String?,
    firstText: String?,
    secondTextOne: String?,
    secondTextTwo: String?,
    navigator: NavHostController
) {
    TvSelectable(scaleAnimation = 1.03f) { interactionSource, isFocused, scale ->
        SelectableCard(
            modifier = modifier,
            interactionSource = interactionSource,
            isFocused = isFocused,
            scale = scale.value,
            backgroundColor = Color.Transparent,
            unselectColor = Color.Transparent
        ) {
            ImageTextCard(
                modifier = Modifier
                    .clickable {
                        navigateDetailsTvAnimeScreen(
                            id = animeId,
                            navigator = navigator
                        )
                    },
                url = imageUrl,
                firstText = firstText,
                secondTextOne = secondTextOne,
                secondTextTwo = secondTextTwo
            )
        }
    }
}

@Composable
internal fun RightDrawerAnimeEdit(
    animeRateHolder: RateHolder?
) {
    /** статусы списка для показа в шторке */
    val bottomItems = remember {
        RateStatus.values().filter {
            it != RateStatus.UNKNOWN
        }
    }

    animeRateHolder?.let { rateHolder ->

        /** статус выбранного элемента в пользовательском списке */
        val currentItemStatus by rateHolder.currentItemStatus.observeAsState(initial = RateStatus.PLANNED)

        /** текущий выбранный элемент для передачи в шторку */
        val currentItem by rateHolder.currentItem.observeAsState(
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
        val ratingBarScore by rateHolder.ratingBarScore.observeAsState(initial = 0f)

        /** количество просмотренных или прочитанных глав/эпизодов */
        val episodeChapterCount by rateHolder.episodeChapterCount.observeAsState(initial = "")

        /** количество повторных просмотров/прочитываний глав/эпизодов */
        val reWatchReReadCount by rateHolder.reWatchReReadCount.observeAsState(initial = "")

        /** флаг показа ошибки ввода просмотренных/прочитанных эпизодов/глав */
        val isEpisodeChapterError by rateHolder.isEpisodeChapterError.observeAsState(initial = false)

        /** флаг показа ошибки ввода количества пересмотров/перечитываний */
        val isReWatchesError by rateHolder.isReWatchesError.observeAsState(initial = false)

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(width = 500.dp)
                .background(
                    color = ShikidroidTheme.colors.background
                )
        ) {

            ////////////////////////////////////////////////////////////////////////////////////////
            // кнопки Подтвердить / Удалить
            ////////////////////////////////////////////////////////////////////////////////////////

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(fourteenDP),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TvSelectable { interactionSource, isFocused, scale ->
                    RoundedIconButton(
                        iconBoxModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        icon = R.drawable.ic_check,
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
                            if (currentItem?.status != null) {
                                currentItem?.let { rateHolder.changeRateModel(it) }
                            } else {
                                currentItem?.let { rateHolder.createRateStatus(it) }
                            }
                            rateHolder.hideBottomSheet()
                        }
                    )
                }
                TvSelectable { interactionSource, isFocused, scale ->
                    RoundedIconButton(
                        iconBoxModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        icon = R.drawable.ic_delete,
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
                            currentItem?.let { rateHolder.deleteRateModel(it) }
                            rateHolder.hideBottomSheet()
                        }
                    )
                }
            }

            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )

            ////////////////////////////////////////////////////////////////////////////////////////
            // кнопки выбора пользовательского списка
            ////////////////////////////////////////////////////////////////////////////////////////

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(fourteenDP),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                RowForColumnScope(
                    columnScope = this,
                    data = bottomItems,
                    columnCount = 3
                ) { item ->
                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            modifier = Modifier
                                .padding(sevenDP),
                            iconBoxModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            isIcon = true,
                            icon = item.toDrawable(),
                            tint =
                            if (currentItemStatus == item) {
                                item.toColor()
                            } else {
                                ShikidroidTheme.colors.onBackground
                            },
                            backgroundColor =
                            when {
                                currentItemStatus == item && isFocused -> {
                                    item.toBackgroundColor()
                                }

                                currentItemStatus == item && !isFocused -> {
                                    item.toBackgroundColor()
                                }

                                currentItemStatus != item && isFocused -> {
                                    ShikidroidTheme.colors.secondaryVariant
                                }

                                else -> {
                                    ShikidroidTheme.colors.tvSelectable
                                }
                            },
                            text = item.toAnimePresentationString(),
                            onClick = {
                                rateHolder.currentItemStatus.value = item
                            }
                        )
                    }
                }
            }

            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )

            ////////////////////////////////////////////////////////////////////////////////////////
            // Индикатор пользовательской оценки в виде звёзд с кнопками +/-
            ////////////////////////////////////////////////////////////////////////////////////////

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(0.95f)
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
                        enabled = false,
                        numStars = 10,
                        activeColor = ShikidroidTheme.colors.secondary,
                        inactiveColor = ShikidroidTheme.colors.onBackground,
                        onValueChange = {},
                        onRatingChanged = {}
                    )
                }

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = sevenDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            iconBoxModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            icon = R.drawable.ic_minus,
                            isIcon = true,
                            tint =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondary
                            } else {
                                ShikidroidTheme.colors.onBackground
                            },
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                rateHolder.ratingBarScore.value =
                                    rateHolder.ratingBarScore.value?.minus(1f)?.coerceAtLeast(
                                        minimumValue = 0f
                                    )
                            },
                        )
                    }

                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            iconBoxModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            icon = R.drawable.ic_plus,
                            isIcon = true,
                            tint =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondary
                            } else {
                                ShikidroidTheme.colors.onBackground
                            },
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                rateHolder.ratingBarScore.value =
                                    rateHolder.ratingBarScore.value?.plus(1f)?.coerceAtMost(
                                        maximumValue = 10f
                                    )
                            },
                        )
                    }
                }
            }

            Divider(
                color = ShikidroidTheme.colors.primaryBorderVariant
            )

            ////////////////////////////////////////////////////////////////////////////////////////
            // Поле ввода количества просмотренных эпизодов с кнопками +/-
            ////////////////////////////////////////////////////////////////////////////////////////

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                InputEpisodeReWatchCount(
                    modifier = Modifier
                        .weight(0.95f)
                        .padding(fourteenDP),
                    isError = isEpisodeChapterError,
                    value = episodeChapterCount,
                    valueChanged = {
                        rateHolder.isEpisodeChapterError.value = false
                        if (it.contains(StringUtils.DIGITS_REGEX)) {
                            when {
                                it.containsOnlyZero() || it.isEmpty() -> {
                                    rateHolder.episodeChapterCount.value = "0"
                                }

                                it.length > 1 && it.first() == '0' -> {
                                    rateHolder.episodeChapterCount.value =
                                        it.substring(it.firstNotZeroIndex())
                                            .deleteEmptySpaces()
                                }

                                it.deleteEmptySpaces().toInt() > (
                                        when (currentItem?.anime?.status) {
                                            AiredStatus.ANONS -> 0
                                            AiredStatus.ONGOING -> currentItem?.anime?.episodesAired
                                                ?: 0

                                            AiredStatus.RELEASED -> currentItem?.anime?.episodes
                                                ?: 0

                                            else -> currentItem?.anime?.episodes ?: 0
                                        }
                                        ) -> {
                                    rateHolder.isEpisodeChapterError.value = true
                                    rateHolder.episodeChapterCount.value =
                                        when (currentItem?.anime?.status) {
                                            AiredStatus.ANONS, AiredStatus.ONGOING -> {
                                                currentItem?.anime?.episodesAired.toString()
                                                    .deleteEmptySpaces()
                                            }

                                            else -> currentItem?.anime?.episodes.toString()
                                                .deleteEmptySpaces()
                                        }
                                }

                                else -> {
                                    rateHolder.episodeChapterCount.value =
                                        it.deleteEmptySpaces()
                                }
                            }
                        } else {
                            rateHolder.episodeChapterCount.value = it.deleteEmptySpaces()
                        }
                    },
                    label = {
                        Text(
                            text =
                            if (isEpisodeChapterError == true) {
                                when (currentItem?.anime?.status) {
                                    AiredStatus.ANONS -> NOT_YET_RELEASED_TEXT
                                    AiredStatus.ONGOING -> "Вышло ${currentItem?.anime?.episodesAired} эп."
                                    AiredStatus.RELEASED -> "Всего ${currentItem?.anime?.episodes} эп."
                                    else -> ""
                                }
                            } else {
                                ALL_WATCHED
                            },
                            style = ShikidroidTheme.typography.body12sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = sevenDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            iconBoxModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            icon = R.drawable.ic_minus,
                            isIcon = true,
                            tint =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondary
                            } else {
                                ShikidroidTheme.colors.onBackground
                            },
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                if (episodeChapterCount.contains(StringUtils.DIGITS_REGEX)) {
                                    rateHolder.isEpisodeChapterError.value = false
                                    if (episodeChapterCount.toInt() < 1) {
                                        rateHolder.episodeChapterCount.value = "0"
                                    } else {
                                        rateHolder.episodeChapterCount.value =
                                            rateHolder.episodeChapterCount.value?.toInt()
                                                ?.minus(1).toString()
                                    }
                                }
                            }
                        )
                    }

                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            iconBoxModifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .scale(scale = scale.value),
                            icon = R.drawable.ic_plus,
                            isIcon = true,
                            tint =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondary
                            } else {
                                ShikidroidTheme.colors.onBackground
                            },
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                if (episodeChapterCount == "") {
                                    rateHolder.episodeChapterCount.value = "0"
                                }
                                if (episodeChapterCount.contains(StringUtils.DIGITS_REGEX)) {
                                    when {
                                        episodeChapterCount.toInt() >= (
                                                when (currentItem?.anime?.status) {
                                                    AiredStatus.ANONS, AiredStatus.ONGOING -> {
                                                        rateHolder.isEpisodeChapterError.value =
                                                            true
                                                        currentItem?.anime?.episodesAired
                                                            ?: 0
                                                    }

                                                    else -> {
                                                        rateHolder.isEpisodeChapterError.value =
                                                            true
                                                        currentItem?.anime?.episodes ?: 0
                                                    }
                                                }
                                                ) -> {
                                            return@RoundedIconButton
                                        }

                                        else -> {
                                            rateHolder.isEpisodeChapterError.value = false
                                            rateHolder.episodeChapterCount.value =
                                                rateHolder.episodeChapterCount.value?.toInt()
                                                    ?.plus(1).toString()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun InputEpisodeReWatchCount(
    modifier: Modifier = Modifier,
    isError: Boolean,
    value: String,
    valueChanged: (String) -> Unit,
    label: @Composable () -> Unit,
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
                value = value,
                onValueChange = {
                    valueChanged(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    label()
                },
                textStyle = ShikidroidTheme.typography.body16sp,
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
                isError = isError
            )
        }
    }
}

@Composable
internal fun RightDrawerAnimeList(
    animeRateHolder: RateHolder?
) {
    /** статусы списка для показа в шторке */
    val bottomItems = remember {
        RateStatus.values().filter {
            it != RateStatus.UNKNOWN
        }
    }

    animeRateHolder?.let { rateHolder ->
        /** статус выбранного элемента в пользовательском списке */
        val currentItemStatus by rateHolder.currentItemStatus.observeAsState(initial = RateStatus.PLANNED)

        /** текущий выбранный элемент для передачи в шторку */
        val currentItem by rateHolder.currentItem.observeAsState(
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

        TvLazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .width(width = 200.dp)
                .background(
                    color = ShikidroidTheme.colors.background
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = bottomItems,
                key = { it.ordinal }
            ) { item ->
                TvSelectable() { interactionSource, isFocused, scale ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(fiftyDP)
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value)
                            .padding(start = fourteenDP, top = threeDP, bottom = threeDP)
                            .background(
                                color =
                                when {
                                    currentItemStatus == item && isFocused -> {
                                        item.toBackgroundColor()
                                    }

                                    currentItemStatus == item && !isFocused -> {
                                        item.toBackgroundColor()
                                    }

                                    currentItemStatus != item && isFocused -> {
                                        ShikidroidTheme.colors.secondaryVariant
                                    }

                                    else -> {
                                        Color.Transparent
                                    }
                                },
                                shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomStart30dp,
                            )
                            .clickable {
                                rateHolder.currentItemStatus.value = item
                                if (currentItem?.status != null) {
                                    currentItem?.let {
                                        rateHolder.changeRateStatus(
                                            rateModel = it,
                                            newStatus = item
                                        )
                                    }
                                } else {
                                    currentItem?.let {
                                        rateHolder.createRateStatus(it)
                                    }
                                }
                                rateHolder.hideBottomSheet()
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
                            when {
                                currentItemStatus == item && isFocused -> {
                                    item.toColor()
                                }

                                currentItemStatus == item && !isFocused -> {
                                    item.toColor()
                                }

                                else -> {
                                    ShikidroidTheme.colors.onPrimary
                                }
                            }
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = sevenDP),
                            text = item.toAnimePresentationString(),
                            textAlign = TextAlign.Center,
                            color =
                            when {
                                currentItemStatus == item && isFocused -> {
                                    item.toColor()
                                }

                                currentItemStatus == item && !isFocused -> {
                                    item.toColor()
                                }

                                else -> {
                                    ShikidroidTheme.colors.onPrimary
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun LabelTvText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = ShikidroidTheme.colors.onPrimary,
    textStyle: TextStyle = ShikidroidTheme.typography.body13sp,
    labelText: String,
    labelTextStyle: TextStyle = ShikidroidTheme.typography.body12sp,
    textAlign: TextAlign = TextAlign.Center,
    textLines: Int = Int.MAX_VALUE,
    labelTextAlign: TextAlign = TextAlign.Center,
    labelTextLines: Int = Int.MAX_VALUE
) {
    TwoLinesText(
        modifier = modifier.padding(sevenDP),
        text = text,
        textColor = textColor,
        textStyle = textStyle,
        secondText = labelText,
        secondTextColor = ShikidroidTheme.colors.onBackground,
        secondTextStyle = labelTextStyle,
        textAlign = textAlign,
        textMaxLines = textLines,
        secondTextAlign = labelTextAlign,
        secondTextMaxLines = labelTextLines
    )
}