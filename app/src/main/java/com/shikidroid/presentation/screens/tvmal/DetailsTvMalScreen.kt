package com.shikidroid.presentation.screens.tvmal

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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.GenreModel
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.presentation.converters.*
import com.shikidroid.presentation.navigation.navigateDetailsTvAnimeScreen
import com.shikidroid.presentation.navigation.navigateEpisodeTvScreen
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.screens.items.SelectableGenreCard
import com.shikidroid.presentation.screens.tv.ErrorTvScreen
import com.shikidroid.presentation.viewmodels.mal.DetailsScreenMalViewModel
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

/**
 * Экран детальной информации об аниме для AndroidTV
 *
 * @param viewModel вью модель экрана
 * @param navigator контроллер навигации экранов
 */
@Composable
internal fun DetailsTvMalScreen(
    viewModel: DetailsScreenMalViewModel,
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

    /** состояние прокрутки */
    val scrollState = rememberScrollState(0)

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
                    DetailsScreenTvMalDrawer(
                        viewModel = viewModel,
                        navigator = navigator
                    )
                }
            ) {
                BoxWithBackground(
                    backgroundColor = ShikidroidTheme.colors.background,
                    backgroundAlpha = 0.90f,
                    backgroundContent = {
                        AsyncImageLoader(url = animeModel?.image?.large)
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
                        DetailsTvMalToolbar(
                            animeModel = animeModel,
                            navigator = navigator
                        )
                        AnimeTvMalHeader(
                            viewModel = viewModel,
                            navigator = navigator
                        )
                        AnimeTvMalGenres(viewModel = viewModel)
                        AnimeTvMalDescription(viewModel = viewModel, navigator = navigator)
                        AdditionalInfoDetailsTvMal(viewModel = viewModel)
                        ScreenshotsTvMal(viewModel = viewModel)
                        RelatedAnimeTvMal(navigator = navigator, viewModel = viewModel)
                        SimilarAnimeTvMal(viewModel = viewModel, navigator = navigator)
                        SpacerForList()
                    }
                }
            }
        }
    }
}

@Composable
internal fun DetailsTvMalToolbar(
    animeModel: AnimeMalModel?,
    navigator: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = sevenDP, vertical = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
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
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(start = sevenDP),
            text = animeModel?.title.orEmpty(),
            style = ShikidroidTheme.typography.body16sp,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun AnimeTvMalHeader(
    viewModel: DetailsScreenMalViewModel,
    navigator: NavHostController
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

                LabelTvText(
                    text = animeModel?.type?.toScreenString().orEmpty(),
                    labelText = TYPE_TEXT
                )
                if (animeModel?.type != AnimeType.MOVIE) {
                    LabelTvText(text = animeModel?.episodes.toString(), labelText = EPISODES_TEXT)
                }
                animeModel?.episodeDuration?.let { duration ->
                    if (duration > 0) {
                        LabelTvText(
                            text = (duration / 60).toEpisodeTime(),
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
                    animeModel?.status == AiredStatus.ANONS || animeModel?.status == AiredStatus.ONGOING -> {
                        animeModel?.dateAired?.let { date ->
                            LabelTvText(
                                text = DateUtils.getDatePeriod(
                                    DateUtils.fromString(dateString = date)
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
                    AsyncImageLoader(url = animeModel?.image?.large)
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
                                        userRateId = null,
                                        animeName = animeModel?.title.orEmpty(),
                                        animeNameRu = animeModel?.title,
                                        animeImageUrl = animeModel?.image?.large,
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
internal fun AnimeTvMalGenres(
    viewModel: DetailsScreenMalViewModel
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
                    SelectableGenreCard(
                        modifier = Modifier
                            .paddingByList(
                                items = studios,
                                item = studio,
                                top = sevenDP,
                                bottom = sevenDP
                            ),
                        text = studio.name.orEmpty(),
                        textColor = ShikidroidTheme.colors.onPrimary,
                        selectBackgroundColor = ShikidroidTheme.colors.tvSelectable,
                        selectBorderColor = ShikidroidTheme.colors.onPrimary,
                        borderColor = ShikidroidTheme.colors.onBackground
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
                    SelectableGenreCard(
                        modifier = Modifier
                            .paddingByList(
                                items = genres,
                                item = genre,
                                top = sevenDP,
                                bottom = sevenDP
                            ),
                        text = genre.name.orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnimeTvMalDescription(
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
internal fun AdditionalInfoDetailsTvMal(
    viewModel: DetailsScreenMalViewModel
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
                        .padding(start = fourteenDP, end = fourteenDP, bottom = fourteenDP),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = fourteenDP)
                            .fillMaxWidth(),
                        text = ADDITIONAL_INFO_TITLE,
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
internal fun ScreenshotsTvMal(
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
    }
}

@Composable
internal fun ScreenshotsTvMalDrawer(
    viewModel: DetailsScreenMalViewModel
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
                        url = screenshot.large,
                        height = screenshotDrawerTvHeight,
                        width = screenshotDrawerTvWidth,
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
internal fun RelatedAnimeTvMal(
    navigator: NavHostController,
    viewModel: DetailsScreenMalViewModel
) {
    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    val animeRelatedList = mutableListOf<AnimeMalModel>()

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
                            DetailsAnimeTvMalCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = animeRelatedList,
                                        item = anime
                                    ),
                                animeId = anime.id,
                                imageUrl = anime.image?.large,
                                firstText = relatedModel.anime.title,
                                secondTextOne = null,
                                secondTextTwo = relatedModel.relationTypeFormatted,
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
internal fun RelatedAnimeTvMalDrawer(
    viewModel: DetailsScreenMalViewModel,
    navigator: NavHostController
) {
    /** список контента, связанного с текущим */
    val relatedList by viewModel.relatedList.observeAsState()

    val animeModelList = mutableListOf<AnimeMalModel>()

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
                    DetailsAnimeTvMalCard(
                        modifier = Modifier
                            .padding(fiveDP),
                        animeId = relatedModel.anime?.id,
                        imageUrl = relatedModel.anime?.image?.large,
                        firstText = relatedModel.anime?.title,
                        secondTextOne = null,
                        secondTextTwo = relatedModel.relationTypeFormatted,
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
internal fun SimilarAnimeTvMal(
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
                        DetailsAnimeTvMalCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = similarAnime,
                                    item = anime
                                ),
                            animeId = anime.id,
                            imageUrl = anime.image?.large,
                            firstText = StringUtils.getEmptyIfBothNull(
                                one = anime.title,
                                two = anime.alternativeTitles?.en
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
internal fun SimilarAnimeTvMalDrawer(
    viewModel: DetailsScreenMalViewModel,
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
                DetailsAnimeTvMalCard(
                    modifier = Modifier
                        .padding(fiveDP),
                    animeId = anime.id,
                    imageUrl = anime.image?.large,
                    firstText = StringUtils.getEmptyIfBothNull(
                        one = anime.title,
                        two = anime.alternativeTitles?.en
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
internal fun DetailsScreenTvMalDrawer(
    viewModel: DetailsScreenMalViewModel,
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
                ScreenshotsTvMalDrawer(viewModel = viewModel)
            }
            DetailScreenDrawerType.RELATED -> {
                RelatedAnimeTvMalDrawer(viewModel = viewModel, navigator = navigator)
            }
            DetailScreenDrawerType.SIMILAR_ANIME -> {
                SimilarAnimeTvMalDrawer(viewModel = viewModel, navigator = navigator)
            }
            else -> Unit
        }
    }
}

@Composable
internal fun DetailsAnimeTvMalCard(
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