package com.shikidroid.presentation.screens.mal

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RelatedAnimeModel
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.presentation.converters.toColor
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.*
import com.shikidroid.presentation.screens.ErrorScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.GenreCard
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.LabelText
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.viewmodels.mal.DetailsScreenMalViewModel
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.ui.*
import com.shikidroid.ui.STATISTIC_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.IntUtils.toEpisodeTime
import com.shikidroid.utils.StringUtils

/**
 * Экран детальной информации о аниме/манге/ранобэ
 *
 * @param screenType типа экрана аниме/манге/ранобэ
 * @param prefs внутреннее хранилище приложения SharedPreferences
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации экранов
 */
@Composable
internal fun DetailsMalScreen(
    screenType: DetailsScreenType?,
    prefs: SharedPreferencesProvider,
    viewModel: DetailsScreenMalViewModel,
    navigator: NavHostController,
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** флаг показа экрана с ошибкой */
    val showErrorScreen by viewModel.showErrorScreen.observeAsState(initial = false)

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    /** состояние выдвижного меню открыто/закрыто */
    val isDrawerOpen by viewModel.isDrawerOpen.observeAsState(false)

    BackHandler() {
        if (isDrawerOpen) {
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
                        url = animeModel?.image?.large
                    )
                }
            ) {
                BoxWithVerticalDrawer(
                    isDrawerOpen = isDrawerOpen,
                    drawerContent = {
                        DetailScreenMalDrawer(
                            animeModel = animeModel,
                            viewModel = viewModel,
                            navigator = navigator
                        )
                    }
                ) {
                    BoxWithFloatingButton(
                        prefs = prefs,
                        xAxisKey = AppKeys.DETAIL_SCREEN_FLOATING_BUTTON_X,
                        yAxisKey = AppKeys.DETAIL_SCREEN_FLOATING_BUTTON_Y,
                        showButton = screenType == DetailsScreenType.ANIME,
                        floatingButton = {
                            FloatingActionButton(
                                modifier = Modifier
                                    .padding(fourteenDP),
                                backgroundColor = ShikidroidTheme.colors.secondary,
                                onClick = {
                                    navigateEpisodeScreen(
                                        id = animeModel?.id,
                                        userRateId = null,
                                        animeName = animeModel?.title,
                                        animeNameRu = animeModel?.title,
                                        animeImageUrl = animeModel?.image?.large,
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
                                            text = animeModel?.title.orEmpty(),
                                            style = ShikidroidTheme.typography.body16sp,
                                            color = ShikidroidTheme.colors.onPrimary,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .wrapContentSize()
                                    ) {
//                                        RoundedIconButton(
//                                            icon = R.drawable.ic_three_dots,
//                                            backgroundColor = Color.Transparent,
//                                            tint = ShikidroidTheme.colors.onPrimary,
//                                            onClick = {
//                                                viewModel.showDropdownMenu.value =
//                                                    viewModel.showDropdownMenu.value != true
//                                            }
//                                        )
//                                        DropdownDetailsMalMenu(
//                                            viewModel = viewModel,
//                                        )
                                    }
                                }
                            },
                            headerContent = {
                                HeaderMal(
                                    viewModel = viewModel,
                                )
                            }
                        ) {
                            GenresMal(
                                viewModel = viewModel,
                            )
                            Divider(color = ShikidroidTheme.colors.primaryBorderVariant)
                            DescriptionDetailsMal(viewModel = viewModel, navigator = navigator)
                            AdditionalInfoDetailsMal(viewModel = viewModel, navigator = navigator)
                            ScreenshotsMal(viewModel = viewModel)
                            RelatedMal(navigator = navigator, viewModel = viewModel)
                            SimilarAnimeMal(viewModel = viewModel, navigator = navigator)
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
internal fun DetailScreenMalDrawer(
    animeModel: AnimeMalModel?,
    viewModel: DetailsScreenMalViewModel,
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

            DetailScreenDrawerType.SCREENSHOTS -> {
                ScreenshotsMalDrawer(viewModel = viewModel)
            }

            DetailScreenDrawerType.SIMILAR_ANIME -> {
                SimilarAnimeMalDrawer(viewModel = viewModel, navigator = navigator)
            }

            DetailScreenDrawerType.RELATED -> {
                RelatedMalDrawer(viewModel = viewModel, navigator = navigator)
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
internal fun DropdownDetailsMalMenu(
    viewModel: DetailsScreenMalViewModel,
) {
    /** контектс */
    val context = LocalContext.current

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

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

            com.shikidroid.presentation.screens.DropdownMenuItem(
                modifier = Modifier
                    .clickable {
                        viewModel.showDrawer(type = DetailScreenDrawerType.STATISTICS)
                        viewModel.showDropdownMenu.value = false
                    },
                icon = R.drawable.ic_stats,
                text = STATISTIC_TITLE
            )
        }
    }
}

@Composable
internal fun DropdownMenuMalItem(
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
internal fun HeaderMal(
    viewModel: DetailsScreenMalViewModel,
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

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
                LabelText(
                    text = animeModel?.type?.toScreenString().orEmpty(),
                    labelText = TYPE_TEXT
                )
                if (animeModel?.type != AnimeType.MOVIE) {
                    LabelText(text = animeModel?.episodes.toString(), labelText = EPISODES_TEXT)
                }
                animeModel?.episodeDuration?.let { duration ->
                    if (duration > 0) {
                        LabelText(
                            text = (duration / 60).toEpisodeTime(),
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
                    animeModel?.status == AiredStatus.ANONS || animeModel?.status == AiredStatus.ONGOING -> {
                        animeModel?.dateAired?.let { date ->
                            LabelText(
                                text = DateUtils.getDatePeriodFromString(
                                    dateStart = date
                                ),
                                labelText = RELEASE_DATE_TEXT
                            )
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
                    url = animeModel?.image?.large,
                )
                Row(
                    modifier = Modifier
                        .padding(all = sevenDP),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShikiRatingBar(
                        value = animeModel?.score?.toFloat() ?: 0.0f,
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
                        text = "${animeModel?.score}",
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
                text = animeModel?.title.orEmpty(),
                style = ShikidroidTheme.typography.header24sp,
                color = ShikidroidTheme.colors.onPrimary
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = animeModel?.alternativeTitles?.ja.orEmpty(),
                style = ShikidroidTheme.typography.body12sp,
                color = ShikidroidTheme.colors.onBackground
            )
        }
    }
}

@Composable
internal fun GenresMal(
    viewModel: DetailsScreenMalViewModel,
) {

    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    val genres: List<GenreModel>? = animeModel?.genres

    LazyRow(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(vertical = sevenDP)
    ) {
        if (animeModel?.studios?.isNotEmpty() == true) {
            animeModel?.studios?.let { studios ->
                items(
                    items = studios
                ) { studio ->
                    GenreCard(
                        modifier = Modifier
                            .paddingByList(
                                items = studios,
                                item = studio
                            ),
                        text = studio.name.orEmpty(),
                        textColor = ShikidroidTheme.colors.onPrimary,
                        borderColor = ShikidroidTheme.colors.onPrimary
                    )
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
                    GenreCard(
                        modifier = Modifier
                            .paddingByList(
                                items = genres,
                                item = genre
                            ),
                        text = genre.name.orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
internal fun DescriptionDetailsMal(
    viewModel: DetailsScreenMalViewModel,
    navigator: NavHostController
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    val description: String? = animeModel?.synopsys

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

@Composable
internal fun AdditionalInfoDetailsMal(
    viewModel: DetailsScreenMalViewModel,
    navigator: NavHostController
) {
    /** модель с данными аниме */
    val animeModel by viewModel.animeDetails.observeAsState()

    val description: String? = animeModel?.backgroundInfo

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
                    RowTitleText(text = ADDITIONAL_INFO_TITLE)
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

@Composable
internal fun ScreenshotsMal(
    viewModel: DetailsScreenMalViewModel
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
                            url = screenshot.large,
                            height = standardImageHeight,
                            width = standardImageWidth
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ScreenshotsMalDrawer(
    viewModel: DetailsScreenMalViewModel
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
            RowTitleText(text = SCREENSHOTS_TITLE)
        }
        gridItems(
            data = screenshots,
            columnCount = if (!isScreenHorizontal) 2 else 3,
            modifier = Modifier
        ) { screenshot ->
            ImageWithOverPicture(
                modifier = Modifier
                    .padding(fiveDP),
                url = screenshot.large,
                height =
                if (isScreenHorizontal) {
                    malHorizontalPictureHeight
                } else {
                    malPictureHeight
                },
                width = if (isScreenHorizontal) {
                    malHorizontalPictureWidth
                } else {
                    malPictureWidth
                }
            )
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun RelatedMal(
    viewModel: DetailsScreenMalViewModel,
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
                        RelatedMalCard(
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
internal fun RelatedMalDrawer(
    viewModel: DetailsScreenMalViewModel,
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
                    RelatedMalCard(
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
internal fun RelatedMalCard(
    modifier: Modifier,
    relatedModel: RelatedAnimeModel,
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
                            id = relatedModel.anime?.id,
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = relatedModel.anime?.image?.large,
                firstText = relatedModel.anime?.title,
                secondTextTwo = relatedModel.relationTypeFormatted
            )
        },
        status = relatedModel.anime?.status,
        score = relatedModel.anime?.score,
        episodes = relatedModel.anime?.episodes,
        dateAired = relatedModel.anime?.dateAired,
        dateReleased = relatedModel.anime?.dateReleased,
        isMal = true
    )
}

@Composable
internal fun SimilarAnimeMal(
    viewModel: DetailsScreenMalViewModel,
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
                        AnimeMalCard(
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
internal fun SimilarAnimeMalDrawer(
    viewModel: DetailsScreenMalViewModel,
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
                    AnimeMalCard(
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