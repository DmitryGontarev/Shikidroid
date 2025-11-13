package com.shikidroid.presentation.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.converters.toAnimeRateModel
import com.shikidroid.domain.converters.toMangaRateModel
import com.shikidroid.domain.models.anime.AnimeDetailsModel
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.common.RelatedModel
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.models.user.StatisticModel
import com.shikidroid.openLink
import com.shikidroid.presentation.BottomSheetType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.presentation.converters.toBackgroundColor
import com.shikidroid.presentation.converters.toColor
import com.shikidroid.presentation.converters.toDrawable
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.*
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.GenreCard
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.LabelText
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.DetailsScreenViewModel
import com.shikidroid.presentation.viewmodels.RateHolder
import com.shikidroid.shareLink
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.ui.*
import com.shikidroid.ui.COMMENTS_TITLE
import com.shikidroid.ui.LINKS_TITLE
import com.shikidroid.ui.SHARE_TITLE
import com.shikidroid.ui.STATISTIC_TITLE
import com.shikidroid.ui.WEB_VERSION_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.IntUtils.toEpisodeTime
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
import com.shikidroid.utils.StringUtils.getEmptyIfBothNull
import com.shikidroid.utils.StringUtils.toUppercaseAndDeleteUnderscore

/**
 * Экран детальной информации о аниме/манге/ранобэ
 *
 * @param screenType типа экрана аниме/манге/ранобэ
 * @param prefs внутреннее хранилище приложения SharedPreferences
 * @param viewModel вью модель экрана
 * @param rateHolder интерфейс доступа к общей вью модели контейнера с боттом баром
 * @param navigator контроллер навигации экранов
 */
@Composable
internal fun DetailsScreen(
    screenType: DetailsScreenType?,
    prefs: SharedPreferencesProvider,
    viewModel: DetailsScreenViewModel,
    rateHolder: RateHolder?,
    navigator: NavHostController,
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** флаг показа экрана с ошибкой */
    val showErrorScreen by viewModel.showErrorScreen.observeAsState(initial = false)

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги/ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

    /** состояние выдвижного меню открыто/закрыто */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(false)

    /** состояние нижней шторки */
    val isBottomSheetVisible = rateHolder?.isBottomSheetVisible?.observeAsState(false)

    LaunchedEffect(key1 = isLoading) {
        if (isLoading == false) {
            if (screenType == DetailsScreenType.ANIME) {
                rateHolder?.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
            } else {
                rateHolder?.findAnimeMangaAndSetCurrentItem(rateModel = mangaModel?.toMangaRateModel())
            }
        }
    }

    BackHandler() {
        if (isDrawerOpen || isBottomSheetVisible?.value == true) {
            viewModel.isDrawerOpen.value = false
            rateHolder?.hideBottomSheet()
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
            ErrorScreen(
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
            BoxWithBackground(
                backgroundColor = ShikidroidTheme.colors.background,
                backgroundAlpha = 0.87f,
                backgroundContent = {
                    AsyncImageLoader(
                        url = animeModel?.image?.original ?: mangaModel?.image?.original
                    )
                }
            ) {
                BoxWithVerticalDrawer(
                    isDrawerOpen = isDrawerOpen,
                    drawerContent = {
                        DetailScreenDrawer(
                            animeModel = animeModel,
                            viewModel = viewModel,
                            navigator = navigator
                        )
                    }
                ) {
                    BoxWithSlideFloatingButton(
//                        prefs = prefs,
//                        xAxisKey = AppKeys.DETAIL_SCREEN_FLOATING_BUTTON_X,
//                        yAxisKey = AppKeys.DETAIL_SCREEN_FLOATING_BUTTON_Y,
                        showButton = screenType == DetailsScreenType.ANIME,
                        floatingButton = {
                            FloatingActionButton(
                                modifier = Modifier
                                    .padding(fourteenDP),
                                backgroundColor = ShikidroidTheme.colors.secondary,
                                onClick = {
                                    navigateEpisodeScreen(
                                        id = animeModel?.id,
                                        userRateId = animeModel?.userRate?.id,
                                        animeName = animeModel?.name.orEmpty(),
                                        animeNameRu = animeModel?.nameRu.orEmpty(),
                                        animeImageUrl = animeModel?.image?.original,
                                        navigator = navigator
                                    )
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_play_arrow),
                                    contentDescription = null
                                )
                            }
                        }
                    ) {
                        AnimatedHeaderToolbar(
                            toolbarColor = ShikidroidTheme.colors.surface,
                            headerWrapColors = ShikidroidTheme.colors.background,
                            hideToolbarByScroll = true,
                            toolbarContent = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = sevenDP, vertical = sevenDP),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RoundedIconButton(
                                        icon = R.drawable.ic_arrow_back,
                                        backgroundColor = Color.Transparent,
                                        onClick = {
                                            navigator.popBackStack()
                                        },
                                        tint = ShikidroidTheme.colors.onPrimary
                                    )
                                    AnimatedVisibility(
                                        visible = it,
                                        enter = fadeIn(animationSpec = tween(700)),
                                        exit = fadeOut(animationSpec = tween(700))
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .width(twoHundredFiftyDP),
                                            text = (animeModel?.nameRu
                                                ?: mangaModel?.nameRu).orEmpty(),
                                            style = ShikidroidTheme.typography.body16sp,
                                            color = ShikidroidTheme.colors.onPrimary,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .wrapContentSize()
                                    ) {
                                        RoundedIconButton(
                                            icon = R.drawable.ic_three_dots,
                                            backgroundColor = Color.Transparent,
                                            tint = ShikidroidTheme.colors.onPrimary,
                                            onClick = {
                                                viewModel.showDropdownMenu.value =
                                                    viewModel.showDropdownMenu.value != true
                                            }
                                        )
                                        DropdownDetailsMenu(
                                            viewModel = viewModel,
                                            navigator = navigator
                                        )
                                    }
                                }
                            },
                            headerContent = {
                                Header(
                                    screenType = screenType,
                                    viewModel = viewModel,
                                    rateHolder = rateHolder
                                )
                            }
                        ) {
                            Genres(
                                screenType = screenType,
                                viewModel = viewModel,
                                navigator = navigator
                            )
                            Divider(color = ShikidroidTheme.colors.primaryBorderVariant)
                            StudioRoles(
                                animeModel = animeModel,
                                navigator = navigator,
                                viewModel = viewModel
                            )
                            DescriptionDetails(viewModel = viewModel, navigator = navigator)
                            Characters(navigator = navigator, viewModel = viewModel)
                            Screenshots(viewModel = viewModel)
                            Videos(viewModel = viewModel, navigator = navigator)
                            Related(navigator = navigator, viewModel = viewModel)
                            SimilarAnime(viewModel = viewModel, navigator = navigator)
                            SimilarManga(viewModel = viewModel, navigator = navigator)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clickable {
                                        viewModel.showDrawer(type = DetailScreenDrawerType.COMMENTS)
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RowTitleText(text = COMMENTS_TITLE, textAlign = TextAlign.Center)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_down),
                                    tint = ShikidroidTheme.colors.onPrimary,
                                    contentDescription = null
                                )
                            }
                            SpacerForList()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Нижнее выдвижное меню
 *
 * @param animeModel модель данных аниме
 * @param viewModel вью модель экрана
 * @param navigator навигатор для навигации по приложению
 */
@Composable
internal fun DetailScreenDrawer(
    animeModel: AnimeDetailsModel?,
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {

    /** тип контента меню */
    val drawerType by viewModel.drawerType.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        when (drawerType) {
            DetailScreenDrawerType.CHARACTER -> {
                CharactersDrawer(navigator = navigator, viewModel = viewModel)
            }

            DetailScreenDrawerType.SCREENSHOTS -> {
                ScreenshotsDrawer(viewModel = viewModel)
            }

            DetailScreenDrawerType.VIDEOS -> {
                VideosDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.SIMILAR_ANIME -> {
                SimilarAnimeDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.SIMILAR_MANGA -> {
                SimilarMangaDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.ROLES -> {
                StudioRolesDrawer(
                    animeModel = animeModel,
                    viewModel = viewModel,
                    navigator = navigator
                )
            }

            DetailScreenDrawerType.RELATED -> {
                RelatedDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.EXTERNAL_LINKS -> {
                ExternalLinksDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.STATISTICS -> {
                Statistics(viewModel = viewModel)
            }

            DetailScreenDrawerType.COMMENTS -> {
                Comments(viewModel = viewModel)
            }

            else -> Unit
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

@Composable
internal fun DropdownDetailsMenu(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** контектс */
    val context = LocalContext.current

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги/ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownMenu by viewModel.showDropdownMenu.observeAsState(initial = false)

    SelectableCard(isFocused = false, scale = 1f) {
        DropdownMenu(
            modifier = Modifier
                .background(
                    color = ShikidroidTheme.colors.surface
                ),
            expanded = showDropdownMenu,
            onDismissRequest = {
                viewModel.showDropdownMenu.value = false
            }
        ) {

            DropdownMenuItem(
                modifier = Modifier
                    .clickable {
                        navigateWebViewScreen(
                            url = animeModel?.url ?: mangaModel?.url,
                            navigator = navigator
                        )
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_public,
                text = WEB_VERSION_TITLE
            )

            DropdownMenuItem(
                modifier = Modifier
                    .clickable {
                        viewModel.showDrawer(type = DetailScreenDrawerType.STATISTICS)
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_stats,
                text = STATISTIC_TITLE
            )

            DropdownMenuItem(
                modifier = Modifier
                    .clickable {
                        viewModel.showDrawer(type = DetailScreenDrawerType.EXTERNAL_LINKS)
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_links,
                text = LINKS_TITLE
            )

            DropdownMenuItem(
                modifier = Modifier
                    .clickable {
                        context.shareLink(
                            link = animeModel?.url ?: mangaModel?.url,
                            title = (animeModel?.url ?: mangaModel?.url).orEmpty()
                        )
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_sharing,
                text = SHARE_TITLE
            )
        }
    }
}

@Composable
internal fun DropdownMenuItem(
    modifier: Modifier = Modifier,
    icon: Int? = null,
    text: String
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .padding(start = sevenDP, end = sevenDP, bottom = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                modifier = Modifier
                    .size(thirtyDP)
                    .padding(horizontal = sevenDP),
                painter = painterResource(id = icon),
                tint = ShikidroidTheme.colors.onPrimary,
                contentDescription = null
            )
        }
        Text(
            text = text,
            style = ShikidroidTheme.typography.body12sp,
            color = ShikidroidTheme.colors.onPrimary
        )
    }
}

@Composable
internal fun Header(
    screenType: DetailsScreenType?,
    viewModel: DetailsScreenViewModel,
    rateHolder: RateHolder?
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

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
                if (screenType == DetailsScreenType.ANIME) {
                    LabelText(
                        text = animeModel?.type?.toScreenString().orEmpty(),
                        labelText = TYPE_TEXT
                    )
                    if (animeModel?.type != AnimeType.MOVIE) {
                        LabelText(text = animeModel?.episodes.toString(), labelText = EPISODES_TEXT)
                    }
                    animeModel?.duration?.let { duration ->
                        if (duration > 0) {
                            LabelText(
                                text = "${animeModel?.duration?.toEpisodeTime()}",
                                labelText = EPISODE_TIME_TEXT
                            )
                        }
                    }
                    animeModel?.ageRating?.let { age ->
                        if (age.toScreenString().isNotEmpty()) {
                            LabelText(text = age.toScreenString(), labelText = AGE_TEXT)
                        }
                    }
                    when {
                        animeModel?.anons == true -> {
                            animeModel?.dateAired?.let { date ->
                                LabelText(
                                    text = DateUtils.getDatePeriodFromString(
                                        dateStart = date
                                    ),
                                    labelText = RELEASE_DATE_TEXT
                                )
                            }
                        }

                        animeModel?.ongoing == true -> {
                            animeModel?.nextEpisodeDate?.let { date ->
                                LabelText(
                                    text = DateUtils.getDateBeforeCurrentFromString(
                                        dateString = date
                                    ),
                                    labelText = "до ${animeModel?.episodesAired?.plus(1)} эп."
                                )
                            }

                            if (animeModel?.nextEpisodeDate == null) {
                                animeModel?.dateAired?.let { date ->
                                    LabelText(
                                        text = DateUtils.getDatePeriodFromString(
                                            dateStart = date
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
                                        LabelText(
                                            text = DateUtils.getDatePeriodFromString(
                                                dateStart = date
                                            ),
                                            labelText = RELEASE_DATE_TEXT
                                        )
                                    }
                                }

                                else -> {
                                    when {
                                        animeModel?.dateAired.isNullOrEmpty()
                                            .not() && animeModel?.dateReleased.isNullOrEmpty() -> {
                                            LabelText(
                                                text = DateUtils.getDatePeriodFromString(
                                                    dateStart = animeModel?.dateAired
                                                ),
                                                labelText = RELEASE_DATE_TEXT
                                            )
                                        }

                                        animeModel?.dateAired.isNullOrEmpty() && animeModel?.dateReleased.isNullOrEmpty()
                                            .not() -> {
                                            LabelText(
                                                text = DateUtils.getDatePeriodFromString(
                                                    dateStart = animeModel?.dateReleased
                                                ), labelText = RELEASE_DATE_TEXT
                                            )
                                        }

                                        else -> {
                                            LabelText(
                                                text = DateUtils.getDatePeriodFromString(
                                                    dateStart = animeModel?.dateAired,
                                                    dateEnd = animeModel?.dateReleased,
                                                    isNextLine = true
                                                ), labelText = RELEASE_DATE_TEXT
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LabelText(
                        text = mangaModel?.type?.toScreenString().orEmpty(),
                        labelText = TYPE_TEXT
                    )
                    mangaModel?.chapters?.let { chapters ->
                        if (chapters > 0) {
                            LabelText(
                                text = mangaModel?.chapters.toString(),
                                labelText = CHAPTERS_TEXT
                            )
                        }
                    }
                    mangaModel?.volumes?.let { volumes ->
                        if (volumes > 0) {
                            LabelText(text = "${mangaModel?.volumes}", labelText = VOLUMES_TEXT)
                        }
                    }
                    when {
                        mangaModel?.anons == true || mangaModel?.ongoing == true -> {
                            mangaModel?.dateAired?.let { date ->
                                LabelText(
                                    text = DateUtils.getDatePeriodFromString(
                                        dateStart = date
                                    ),
                                    labelText = RELEASE_DATE_TEXT
                                )
                            }
                        }

                        else -> {
                            when {
                                mangaModel?.dateAired.isNullOrEmpty()
                                    .not() && mangaModel?.dateReleased.isNullOrEmpty() -> {
                                    LabelText(
                                        text = DateUtils.getDatePeriodFromString(
                                            dateStart = mangaModel?.dateAired
                                        ), labelText = RELEASE_DATE_TEXT
                                    )
                                }

                                mangaModel?.dateAired.isNullOrEmpty() && mangaModel?.dateReleased.isNullOrEmpty()
                                    .not() -> {
                                    LabelText(
                                        text = DateUtils.getDatePeriodFromString(
                                            dateStart = mangaModel?.dateReleased
                                        ), labelText = RELEASE_DATE_TEXT
                                    )
                                }

                                else -> {
                                    LabelText(
                                        text = DateUtils.getDatePeriodFromString(
                                            dateStart = mangaModel?.dateAired,
                                            dateEnd = mangaModel?.dateReleased,
                                            isNextLine = true
                                        ), labelText = RELEASE_DATE_TEXT
                                    )
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
                ImageWithOverPicture(
                    url = animeModel?.image?.original ?: mangaModel?.image?.original,
                )
                Row(
                    modifier = Modifier
                        .padding(all = sevenDP),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShikiRatingBar(
                        value = (animeModel?.score?.toFloat() ?: mangaModel?.score?.toFloat())
                            ?: 0.0f,
                        numStars = 10,
                        starSize = sevenDP,
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
                        text = "${animeModel?.score?.toFloat() ?: mangaModel?.score?.toFloat()}",
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

                (animeModel?.status ?: mangaModel?.status)?.let { airedStatus ->
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
                rateHolder?.let { rateHolder ->
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
                        RoundedIconButton(
                            icon = R.drawable.ic_edit_small,
                            tint = ShikidroidTheme.colors.onPrimary,
                            onClick = {
                                if (screenType == DetailsScreenType.ANIME) {
                                    rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
                                } else {
                                    rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = mangaModel?.toMangaRateModel())
                                }
                                rateHolder.showBottomSheet(
                                    bottomType = BottomSheetType.RATE_MODEL_EDIT
                                )
                            }
                        )
                        RoundedIconButton(
                            icon = currentItem?.value?.status.toDrawable(),
                            tint = currentItem?.value?.status?.toColor()
                                ?: ShikidroidTheme.colors.onPrimary,
                            backgroundColor = currentItem?.value?.status.toBackgroundColor(),
                            onClick = {
                                if (screenType == DetailsScreenType.ANIME) {
                                    rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = animeModel?.toAnimeRateModel())
                                } else {
                                    rateHolder.findAnimeMangaAndSetCurrentItem(rateModel = mangaModel?.toMangaRateModel())
                                }
                                rateHolder.showBottomSheet(
                                    bottomType = BottomSheetType.RATE_LIST_EDIT
                                )
                            }
                        )
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
                text = (animeModel?.nameRu ?: mangaModel?.nameRu).orEmpty(),
                style = ShikidroidTheme.typography.header24sp,
                color = ShikidroidTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = (animeModel?.name ?: mangaModel?.name).orEmpty(),
                style = ShikidroidTheme.typography.body12sp,
                color = ShikidroidTheme.colors.onBackground
            )
        }
    }
}

@Composable
internal fun Genres(
    screenType: DetailsScreenType?,
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

    val genres: List<GenreModel>? = animeModel?.genres ?: mangaModel?.genres

    LazyRow(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(vertical = sevenDP)
    ) {
        if (genres.isNullOrEmpty().not()) {
            genres?.let { genres ->
                items(
                    items = genres,
                    key = { it.id ?: 0 }
                ) { genre ->
                    GenreCard(
                        modifier = Modifier
                            .paddingByList(
                                items = genres,
                                item = genre
                            )
                            .clickable {
                                when (screenType) {
                                    DetailsScreenType.ANIME -> {
                                        navigateSearchScreenByAnimeGenreId(
                                            id = genre.id,
                                            navigator = navigator
                                        )
                                    }

                                    DetailsScreenType.MANGA -> {
                                        navigateSearchScreenByMangaGenreId(
                                            id = genre.id,
                                            navigator = navigator
                                        )
                                    }

                                    DetailsScreenType.RANOBE -> {
                                        navigateSearchScreenByRanobeGenreId(
                                            id = genre.id,
                                            navigator = navigator
                                        )
                                    }

                                    else -> Unit
                                }
                            },
                        text = genre.nameRu.orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
internal fun StudioRoles(
    animeModel: AnimeDetailsModel?,
    navigator: NavHostController,
    viewModel: DetailsScreenViewModel
) {
    /** список с создателями */
    val rolesList by viewModel.rolesList.observeAsState()

    if (rolesList.isNullOrEmpty().not()) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            RowTitleText(
                modifier = Modifier
                    .clickable {
                        viewModel.showDrawer(type = DetailScreenDrawerType.ROLES)
                    },
                paddingValues = PaddingValues(
                    top = sevenDP,
                    bottom = zeroDP,
                    start = fourteenDP
                ),
                text = CREATORS_TITLE
            )

            LazyRow(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(vertical = sevenDP),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                animeModel?.studios?.let { studios ->
                    studios.map { studio ->
                        item {
                            Card(
                                modifier = Modifier
                                    .height(seventyDP)
                                    .wrapContentWidth()
                                    .paddingByList(
                                        items = studios,
                                        item = studio
                                    )
                                    .clickable {
                                        navigateSearchScreenByAnimeStudioId(
                                            id = studio.id,
                                            navigator = navigator
                                        )
                                    },
                                backgroundColor = Color.Transparent,
                                shape = ShikidroidTheme.shapes.roundedCorner7dp,
                                border = BorderStroke(
                                    width = oneDP,
                                    color = ShikidroidTheme.colors.primaryBorderVariant
                                ),
                                elevation = zeroDP
                            ) {
                                LabelText(
                                    modifier = Modifier
                                        .padding(vertical = sevenDP, horizontal = fourteenDP),
                                    text = studio.name.orEmpty(),
                                    labelText = STUDIO_TEXT
                                )
                            }
                        }
                    }
                }

                if (animeModel?.studios?.isNotEmpty() == true && rolesList?.isNotEmpty() == true) {
                    item {
                        VerticalDivider(color = ShikidroidTheme.colors.onBackground)
                    }
                }

                rolesList?.let { roles ->
                    val rolesName = roles.filter { it.person?.name.isNullOrEmpty().not() }
                    rolesName.map { role ->
                        if (role.person?.name.isNullOrEmpty().not()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .height(seventyDP)
                                        .wrapContentWidth()
                                        .paddingByList(
                                            items = rolesName,
                                            item = role
                                        )
                                        .clickable {
                                            navigatePeopleScreen(
                                                id = role.person?.id,
                                                navigator = navigator
                                            )
                                        },
                                    backgroundColor = Color.Transparent,
                                    shape = ShikidroidTheme.shapes.roundedCorner7dp,
                                    border = BorderStroke(
                                        width = oneDP,
                                        color = ShikidroidTheme.colors.primaryBorderVariant
                                    ),
                                    elevation = zeroDP
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .height(seventyDP)
                                                .wrapContentWidth()
                                                .clip(ShikidroidTheme.shapes.roundedCorner7dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImageLoader(url = role.person?.image?.original)
                                        }
                                        LabelText(
                                            text = role.person?.name.orEmpty(),
                                            textStyle = ShikidroidTheme.typography.body12sp,
                                            labelText = role.rolesRu?.firstOrNull().orEmpty(),
                                            textAlign = TextAlign.Start,
                                            labelTextAlign = TextAlign.Start
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
}

@Composable
internal fun StudioRolesDrawer(
    animeModel: AnimeDetailsModel?,
    navigator: NavHostController,
    viewModel: DetailsScreenViewModel
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список с создателями */
    val rolesList by viewModel.rolesList.observeAsState()

    if (rolesList.isNullOrEmpty().not()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                SpacerStatusBar()
            }
            animeModel?.studios?.let { studios ->
                if (studios.isEmpty().not()) {
                    item {
                        RowTitleText(text = STUDIOS_TITLE)
                    }
                    studios.map { studio ->
                        item {
                            ImageTextCard(
                                modifier = Modifier
                                    .padding(horizontal = fourteenDP)
                                    .clickable {
                                        navigateSearchScreenByAnimeStudioId(
                                            id = studio.id,
                                            navigator = navigator
                                        )
                                    },
                                url = studio.imageUrl,
                                height = twoHundredDP,
                                width = twoHundredFiftyDP,
                                firstText = studio.name,
                                secondTextTwo = STUDIO_TEXT
                            )
                        }
                    }
                }
            }
            rolesList?.let { roles ->
                val rolesName = roles.filter { it.person?.name.isNullOrEmpty().not() }
                item {
                    RowTitleText(text = CREATORS_TITLE)
                }
                gridItems(
                    data = rolesName,
                    columnCount =
                    if (!isScreenHorizontal) 2 else 5
                ) { role ->
                    ImageTextCard(
                        modifier = Modifier
                            .padding(bottom = fiveDP)
                            .clickable {
                                navigatePeopleScreen(
                                    id = role.person?.id,
                                    navigator = navigator
                                )
                            },
                        url = role.person?.image?.original,
                        firstText = role.person?.name,
                        secondTextTwo = role.rolesRu?.firstOrNull().orEmpty()
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
internal fun DescriptionDetails(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги/ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

    val description: String? = animeModel?.description ?: mangaModel?.description

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
                        .padding(bottom = fourteenDP),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    RowTitleText(text = DESCRIPTION_TITLE)
                    ExpandableBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = fourteenDP),
                        showTextModifier = Modifier
                            .padding(top = sevenDP),
                        showTextColor = ShikidroidTheme.colors.secondary,
                        showTextStyle = ShikidroidTheme.typography.body13sp
                    ) {
                        ClickableText(
                            text = annotatedString,
                            style = ShikidroidTheme.typography.body13sp.copy(lineHeight = 20.sp),
                            onClick = { offset ->
                                descriptionTypeLinkName.map { typeLinkName ->
                                    annotatedString.getStringAnnotations(
                                        tag = typeLinkName.third,
                                        start = offset,
                                        end = offset
                                    ).firstOrNull()?.let {
                                        navigateByLinkType(
                                            linkType = typeLinkName.first,
                                            link = it.item,
                                            navigator = navigator
                                        )
                                        return@ClickableText
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Characters(
    navigator: NavHostController,
    viewModel: DetailsScreenViewModel
) {
    /** список с создателями аниме */
    val charactersList by viewModel.charactersList.observeAsState(initial = listOf())

    /** флаг, есть ли персонажи */
    val isCharactersExist by viewModel.isCharactersExist.observeAsState(initial = false)

    /** включен ли поиск персонажа */
    val isSearch = remember { mutableStateOf(value = false) }

    /** имя персонажа для поиска */
    val searchCharacter by viewModel.searchCharacter.observeAsState(initial = "")

    /** функционал фокуса */
    val focusRequester = remember { FocusRequester() }

    /** функционал управления фокусом */
    val focusManager = LocalFocusManager.current

    if (isCharactersExist == true) {
        charactersList?.let { characters ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {

                FilteredTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = sevenDP)
                        .focusRequester(focusRequester = focusRequester),
                    value = searchCharacter,
                    onValueChange = {
                        viewModel.searchCharacter.value = it.deleteEmptySpaces()
                    },
                    enabled = isSearch.value,
                    placeholder = {
                        if (isSearch.value) {
                            Text(
                                text = INPUT_CHARACTER_NAME_TEXT,
                                color = ShikidroidTheme.colors.onBackground,
                                style = ShikidroidTheme.typography.body16sp
                            )
                        } else {
                            RowTitleText(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.showDrawer(type = DetailScreenDrawerType.CHARACTER)
                                    },
                                paddingValues = PaddingValues(zeroDP),
                                text = CHARACTERS_TITLE
                            )
                        }
                    },
                    textStyle = ShikidroidTheme.typography.body16sp,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ShikidroidTheme.colors.onPrimary,
                        backgroundColor = Color.Transparent,
                        cursorColor = ShikidroidTheme.colors.secondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        RoundedIconButton(
                            icon = if (isSearch.value) R.drawable.ic_close else R.drawable.ic_search,
                            tint = ShikidroidTheme.colors.onPrimary,
                            onClick = {
                                isSearch.value = !isSearch.value
                                if (isSearch.value) {
                                    focusRequester.requestFocus()
                                } else {
                                    focusManager.clearFocus()
                                }
                                viewModel.searchCharacter.value = ""
                            }
                        )
                    },
                    handleColor = ShikidroidTheme.colors.secondary
                )

                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    if (characters.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(
                                        start = fourteenDP,
                                        end = fourteenDP
                                    )
                                    .animateItemPlacement(
                                        tween(durationMillis = animationSevenHundred)
                                    ),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Card(
                                    modifier = Modifier
                                        .wrapContentSize(),
                                    backgroundColor = Color.Transparent,
                                    border = BorderStroke(
                                        width = oneDP,
                                        color = ShikidroidTheme.colors.primaryBorderVariant
                                    ),
                                    shape = ShikidroidTheme.shapes.roundedCorner7dp
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(oneHundredDP)
                                            .width(oneHundredDP)
                                            .clip(shape = ShikidroidTheme.shapes.roundedCorner7dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_dono),
                                            tint = ShikidroidTheme.colors.onPrimary,
                                            contentDescription = null
                                        )
                                    }
                                }
                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(top = fiveDP),
                                    text = CHARACTER_NOT_FOUND_TEXT,
                                    style = ShikidroidTheme.typography.body12sp,
                                    color = ShikidroidTheme.colors.onPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(
                            items = characters,
                            key = { it?.id ?: 0 }
                        ) { character ->
                            CharacterItem(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .paddingByList(
                                        items = charactersList,
                                        item = character
                                    )
                                    .animateItemPlacement(
                                        tween(durationMillis = animationSevenHundred)
                                    ),
                                character = character,
                                height = oneHundredDP,
                                width = oneHundredDP,
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
internal fun CharactersDrawer(
    navigator: NavHostController,
    viewModel: DetailsScreenViewModel
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список главных персонажей */
    val mainCharactersList by viewModel.mainCharactersList.observeAsState(initial = listOf())

    /** список второстепенных персонажей */
    val supportingCharactersList by viewModel.supportingCharactersList.observeAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        if (mainCharactersList.isNullOrEmpty().not()) {
            item {
                RowTitleText(text = MAIN_CHARACTERS_TITLE)
            }

            gridItems(
                data = mainCharactersList,
                columnCount =
                if (!isScreenHorizontal) 2 else 5,
                modifier = Modifier
            ) { character ->
                CharacterItem(
                    modifier = Modifier
                        .padding(fiveDP),
                    character = character,
                    height = 202.dp,
                    width = 144.dp,
                    navigator = navigator
                )
            }
        }

        if (supportingCharactersList.isNullOrEmpty().not()) {
            item {
                RowTitleText(text = SUPPORTED_CHARACTERS_TITLE)
            }
            gridItems(
                data = supportingCharactersList,
                columnCount =
                if (!isScreenHorizontal) 2 else 5,
                modifier = Modifier
            ) { character ->
                CharacterItem(
                    modifier = Modifier
                        .padding(fiveDP),
                    character = character,
                    height = 202.dp,
                    width = 144.dp,
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
internal fun CharacterItem(
    modifier: Modifier,
    character: CharacterModel?,
    height: Dp,
    width: Dp,
    navigator: NavHostController
) {
    ImageTextCard(
        modifier = modifier
            .clickable {
                navigateCharacterScreen(
                    id = character?.id,
                    navigator = navigator
                )
            },
        url = character?.image?.original,
        height = height,
        width = width,
        firstText = getEmptyIfBothNull(one = character?.nameRu, two = character?.name)
    )
}

@Composable
internal fun Screenshots(
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
                RowTitleText(
                    modifier = Modifier
                        .clickable {
                            viewModel.showDrawer(type = DetailScreenDrawerType.SCREENSHOTS)
                        },
                    text = SCREENSHOTS_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = sevenDP)
                ) {
                    items(
                        items = screenshots,
                        key = { it.hashCode() }
                    ) { screenshot ->
                        ImageWithOverPicture(
                            modifier = Modifier
                                .paddingByList(
                                    items = screenshots,
                                    item = screenshot
                                ),
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

@Composable
internal fun ScreenshotsDrawer(
    viewModel: DetailsScreenViewModel
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список скриншотов */
    val screenshots by viewModel.animeScreenshots.observeAsState(initial = listOf())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        item {
            RowTitleText(
                text = SCREENSHOTS_TITLE
            )
        }
        gridItems(
            data = screenshots,
            columnCount = if (!isScreenHorizontal) 2 else 3,
            modifier = Modifier
        ) { screenshot ->
            ImageWithOverPicture(
                modifier = Modifier
                    .padding(fiveDP),
                url = screenshot.preview,
                height =
                if (isScreenHorizontal) {
                    screenshotDrawerHorizontalHeight
                } else {
                    screenshotHeight
                },
                width = screenshotDrawerWidth
            )
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun Videos(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {

    /** список скриншотов */
    val animeVideos by viewModel.animeVideos.observeAsState(initial = listOf())

    /** контекст */
    val context = LocalContext.current

    if (animeVideos.isNullOrEmpty().not()) {
        animeVideos?.let { videos ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                RowTitleText(
                    modifier = Modifier
                        .clickable {
                            viewModel.showDrawer(type = DetailScreenDrawerType.VIDEOS)
                        },
                    text = VIDEO_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = sevenDP)
                ) {
                    items(
                        items = videos,
                        key = { it.id }
                    ) { video ->
                        ImageTextCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = videos,
                                    item = video
                                )
                                .clickable {
//                                    context.openLink(link = video.url ?: video.playerUrl)

                                    navigateWebViewScreen(
                                        url = video.url ?: video.playerUrl,
                                        navigator = navigator
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

@Composable
internal fun VideosDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список скриншотов */
    val animeVideos by viewModel.animeVideos.observeAsState(initial = listOf())

    /** контекст */
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        item {
            RowTitleText(text = VIDEO_TITLE)
        }
        gridItems(
            data = animeVideos,
            columnCount = if (!isScreenHorizontal) 2 else 4,
            modifier = Modifier
        ) { video ->
            ImageTextCard(
                modifier = Modifier
                    .clickable {
//                        val url = video.url ?: video.playerUrl
//                        if (url?.contains("youtube") == true) {
//                            context.openLink(link = video.url ?: video.playerUrl)
//                        } else {
//                            navigateWebViewScreen(
//                                url = video.url ?: video.playerUrl,
//                                navigator = navigator
//                            )
//                        }

                        navigateWebViewScreen(
                            url = video.url ?: video.playerUrl,
                            navigator = navigator
                        )
                    },
                url = video.imageUrl,
                height = oneHundredDP,
                width = 165.dp,
                firstText = video.name.orEmpty(),
                secondTextTwo = video.hosting?.uppercase().orEmpty()
            )
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun Related(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    if (relatedList.isNullOrEmpty().not()) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            RowTitleText(
                modifier = Modifier
                    .clickable {
                        viewModel.showDrawer(type = DetailScreenDrawerType.RELATED)
                    },
                text = RELATED_TITLE
            )

            LazyRow(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(vertical = sevenDP)
            ) {
                relatedList?.map { relatedModel ->
                    item {
                        RelatedCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = relatedList.orEmpty(),
                                    item = relatedModel
                                ),
                            relatedModel = relatedModel,
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun RelatedDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        if (relatedList.isNullOrEmpty().not()) {
            relatedList?.let { related ->
                item {
                    RowTitleText(text = RELATED_TITLE)
                }
                gridItems(
                    data = related,
                    columnCount =
                    if (!isScreenHorizontal) 2 else 5,
                ) { relatedModel ->
                    RelatedCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        relatedModel = relatedModel,
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
internal fun RelatedCard(
    modifier: Modifier,
    relatedModel: RelatedModel,
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
                        navigateDetailsScreen(
                            id = relatedModel.anime?.id ?: relatedModel.manga?.id,
                            detailsType =
                            when {
                                relatedModel.anime?.id != null -> DetailsScreenType.ANIME
                                relatedModel.manga?.id != null &&
                                        (relatedModel.manga.type == MangaType.LIGHT_NOVEL ||
                                                relatedModel.manga.type == MangaType.NOVEL) -> DetailsScreenType.RANOBE

                                else -> DetailsScreenType.MANGA
                            },
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = (relatedModel.anime?.image?.original
                    ?: relatedModel.manga?.image?.original),
                firstText = relatedModel.relationRu.orEmpty(),
                secondTextOne =
                when {
                    relatedModel.anime != null -> {
                        "${relatedModel.anime.type?.toScreenString()}"
                    }

                    else -> {
                        "${relatedModel.manga?.type?.toScreenString()}"
                    }
                },
                secondTextTwo =
                when {
                    relatedModel.anime != null -> {
                        DateUtils.getYearString(
                            date = DateUtils.fromString(
                                dateString = relatedModel.anime.dateReleased
                                    ?: relatedModel.anime.dateAired
                            )
                        )
                    }

                    else -> {
                        DateUtils.getYearString(
                            date = DateUtils.fromString(
                                dateString = relatedModel.manga?.dateReleased
                                    ?: relatedModel.manga?.dateAired
                            )
                        )
                    }
                }
            )
        },
        status = relatedModel.anime?.status ?: relatedModel.manga?.status,
        score = relatedModel.anime?.score ?: relatedModel.manga?.score,
        episodes = relatedModel.anime?.episodes,
        episodesAired = relatedModel.anime?.episodesAired,
        chapters = relatedModel.manga?.chapters,
        volumes = relatedModel.manga?.volumes,
        dateAired = relatedModel.anime?.dateAired ?: relatedModel.manga?.dateAired,
        dateReleased = relatedModel.anime?.dateReleased ?: relatedModel.manga?.dateReleased
    )
}

@Composable
internal fun SimilarAnime(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RowTitleText(
                        modifier = Modifier
                            .clickable {
                                viewModel.showDrawer(type = DetailScreenDrawerType.SIMILAR_ANIME)
                            },
                        text = SIMILAR_TITLE
                    )
                    RoundedIconButton(
                        icon = R.drawable.ic_similar,
                        tint = ShikidroidTheme.colors.onPrimary
                    )
                }
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    items(
                        items = similarAnime,
                        key = { it.id ?: 1L }
                    ) { anime ->
                        AnimeCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = similarAnime,
                                    item = anime
                                ),
                            anime = anime,
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SimilarAnimeDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** похожее аниме */
    val animeSimilar by viewModel.animeSimilar.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        if (animeSimilar.isNullOrEmpty().not()) {
            animeSimilar?.let { similarAnime ->
                item {
                    RowTitleText(text = SIMILAR_TITLE)
                }
                gridItems(
                    data = similarAnime,
                    columnCount =
                    if (!isScreenHorizontal) 2 else 5,
                    modifier = Modifier
                ) { anime ->
                    AnimeCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        anime = anime,
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
internal fun SimilarManga(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** похожая манга/ранобэ */
    val mangaSimilar by viewModel.mangaSimilar.observeAsState()

    if (mangaSimilar.isNullOrEmpty().not()) {
        mangaSimilar?.let { similarManga ->
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RowTitleText(
                        modifier = Modifier
                            .clickable {
                                viewModel.showDrawer(type = DetailScreenDrawerType.SIMILAR_MANGA)
                            },
                        text = SIMILAR_TITLE
                    )
                    RoundedIconButton(
                        icon = R.drawable.ic_similar,
                        tint = ShikidroidTheme.colors.onPrimary
                    )
                }
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    mangaSimilar?.let {
                        items(
                            items = similarManga,
                            key = { it.id ?: 1L }
                        ) { manga ->
                            MangaCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = similarManga,
                                        item = manga
                                    ),
                                manga = manga,
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
internal fun SimilarMangaDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** похожее аниме */
    val mangaSimilar by viewModel.mangaSimilar.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        if (mangaSimilar.isNullOrEmpty().not()) {
            mangaSimilar?.let { similarManga ->
                item {
                    RowTitleText(text = SIMILAR_TITLE)
                }
                gridItems(
                    data = similarManga,
                    columnCount =
                    if (!isScreenHorizontal) 2 else 5,
                    modifier = Modifier
                ) { manga ->
                    MangaCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        manga = manga,
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
internal fun ExternalLinksDrawer(
    viewModel: DetailsScreenViewModel,
    navigator: NavHostController
) {
    /** контекст */
    val context = LocalContext.current

    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** список ссылок сайтов с информацией */
    val externalLinks by viewModel.externalLinks.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        if (externalLinks.isNullOrEmpty().not()) {
            externalLinks?.let { links ->
                item {
                    RowTitleText(text = LINKS_TITLE)
                }

                gridItems(
                    data = links,
                    columnCount =
                    if (!isScreenHorizontal) 1 else 2,
                ) { link ->
                    Card(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(
                                vertical = threeDP,
                                horizontal = sevenDP
                            ),
                        backgroundColor = ShikidroidTheme.colors.background,
                        elevation = ShikidroidTheme.elevation,
                        border = BorderStroke(
                            width = oneDP,
                            color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigateWebViewScreen(
                                        url = link.url,
                                        navigator = navigator
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        horizontal = fourteenDP,
                                        vertical = threeDP
                                    ),
                                text = link.name.orEmpty().toUppercaseAndDeleteUnderscore(),
                                color = ShikidroidTheme.colors.onPrimary,
                                style = ShikidroidTheme.typography.bodySemiBold16sp
                            )
                            RoundedIconButton(
                                icon = R.drawable.ic_sharing,
                                tint = ShikidroidTheme.colors.onPrimary,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        link.name.orEmpty().toUppercaseAndDeleteUnderscore()
                                    )
                                    intent.putExtra(Intent.EXTRA_TEXT, link.url)
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            link.name.orEmpty().toUppercaseAndDeleteUnderscore()
                                        )
                                    )
                                }
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

@Composable
internal fun Statistics(
    viewModel: DetailsScreenViewModel
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** модель с данными манги/ранобэ */
    val mangaModel by viewModel.mangaDetails.observeAsState()

    /** статистика оценок */
    val rateScoresStats = animeModel?.rateScoresStats ?: mangaModel?.rateScoresStats

    /** статистика статусов в пользовательских списках */
    val rateStatusesStats = animeModel?.rateStatusesStats ?: mangaModel?.rateStatusesStats

    /** общее количество оценок */
    var overallScores = 0

    /** общее количество статусов в списках */
    var overallRateStatuses = 0

    rateScoresStats?.map { scores ->
        overallScores += scores.value ?: 0
    }

    rateStatusesStats?.map { statuses ->
        overallRateStatuses += statuses.value ?: 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }
        rateScoresStats?.let { scores ->
            item {
                RowTitleText(text = STATISTIC_TITLE)
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = fourteenDP,
                            bottom = sevenDP,
                            start = fourteenDP,
                            end = fourteenDP
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text16SemiBold(
                        text = GRADE_TITLE
                    )
                    Text16SemiBold(
                        text = "$overallScores"
                    )
                }
            }
            items(
                items = scores
            ) { score ->
                StatisticSlider(score = score, overallCount = overallScores)
            }
        }

        rateStatusesStats?.let { statuses ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = fourteenDP,
                            bottom = sevenDP,
                            start = fourteenDP,
                            end = fourteenDP
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text16SemiBold(
                        text = LIST_TITLE
                    )
                    Text16SemiBold(
                        text = "$overallRateStatuses"
                    )
                }
            }
            items(
                items = statuses
            ) { status ->
                StatisticSlider(score = status, overallCount = overallRateStatuses)
            }
            item {
                SpacerForList()
            }
        }
    }
}

@Composable
internal fun StatisticSlider(
    score: StatisticModel,
    overallCount: Int = 0
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = fourteenDP,
                vertical = threeDP
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = threeDP),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = score.name.orEmpty(),
                color = ShikidroidTheme.colors.onPrimary,
                style = ShikidroidTheme.typography.bodySemiBold13sp
            )
            Text(
                text = "${score.value}",
                color = ShikidroidTheme.colors.onPrimary,
                style = ShikidroidTheme.typography.bodySemiBold13sp
            )
        }
        LineSlider(
            modifier = Modifier
                .padding(vertical = threeDP),
            enabled = false,
            initialValue = score.value ?: 0,
            minValue = 0,
            maxValue = overallCount,
            primaryColor = ShikidroidTheme.colors.secondary,
            secondaryColor = ShikidroidTheme.colors.tvSelectable,
            strokeWidth = 15f,
            onPositionChange = {

            }
        )
    }
}

/**
 * Комментарии к произведению
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun Comments(
    viewModel: DetailsScreenViewModel
) {

    /** флаг загрузки данных без блокировки экрана */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список комментариев */
    val commentsList by viewModel.commentsList.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SpacerStatusBar()
        }

        item {
            RowTitleText(text = COMMENTS_TITLE)
        }

        item {
            RowTitleText(
                modifier = Modifier
                    .clickable {
                        viewModel.commentsPage.value = viewModel.commentsPage.value?.plus(1)
                    },
                text = DOWNLOAD_YET_ONE
            )
        }

        commentsList?.let { comments ->
            if (isLoadingWithoutBlocking) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ShikidroidTheme.colors.secondary
                        )
                    }
                }
            }

            items(
                items = comments,
                key = { it.id ?: 0 }
            ) { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            vertical = threeDP,
                            horizontal = sevenDP
                        ),
                    backgroundColor = ShikidroidTheme.colors.background,
                    elevation = ShikidroidTheme.elevation,
                    border = BorderStroke(
                        width = oneDP,
                        color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(sevenDP)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = sevenDP
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(

                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(fiftyDP)
                                        .clip(
                                            shape = RoundedCorner7dp
                                        )
                                ) {
                                    AsyncImageLoader(url = comment.user?.image?.x160)
                                }
                                LabelText(
                                    horizontalAlignment = Alignment.Start,
                                    text = comment.user?.nickname.orEmpty(),
                                    textAlign = TextAlign.Start,
                                    labelText = DateUtils.getDateStringFromString(
                                        dateString = comment.dateCreated
                                    ),
                                    labelTextAlign = TextAlign.Start
                                )
                            }
                        }
                        Text(
                            text = comment.body.orEmpty(),
                            color = ShikidroidTheme.colors.onPrimary,
                            style = ShikidroidTheme.typography.body12sp
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