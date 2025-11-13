package com.shikidroid.presentation.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import com.shikidroid.R
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.presentation.navigation.navigateVideoPlayerScreen
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.Text13Sp
import com.shikidroid.presentation.screens.items.Text16SemiBold
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.ui.*
import com.shikidroid.ui.EMPTY_EPISODE_TITLE
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.ORIGINAL_TITLE
import com.shikidroid.ui.SUBTITLES_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*

@Composable
internal fun EpisodesTvScreen(
    animeNameRu: String?,
    animeImageUrl: String?,
    navigator: NavHostController,
    viewModel: EpisodeScreenViewModel
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** флаг состояния выпадающего меню - открыто/закрыто */
    val showEpisodesMenu by viewModel.showEpisodesMenu.observeAsState(false)

    viewModel.animeNameRu.value = animeNameRu

    BackHandler() {
        if (showEpisodesMenu == true) {
            viewModel.showEpisodesMenu.value = false
            return@BackHandler
        } else {
            navigator.popBackStack()
        }
    }

    if (isLoading) {
        Loader()
    } else {
        TvBoxWithLeftHorizontalDrawer(
            isDrawerOpen = showEpisodesMenu,
            drawerContent = {
                EpisodeTvDrawer(viewModel = viewModel)
            }
        ) {
            BoxWithBackground(
                backgroundColor = ShikidroidTheme.colors.background,
                backgroundAlpha = 0.90f,
                backgroundContent = {
                    AsyncImageLoader(
                        url = animeImageUrl
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    EpisodeTvToolbar(
                        nameRu = animeNameRu,
                        viewModel = viewModel,
                        navigator = navigator
                    )

                    EpisodeTvHeader(viewModel = viewModel)

                    EpisodeTvBody(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
internal fun EpisodeTvToolbar(
    nameRu: String?,
    viewModel: EpisodeScreenViewModel,
    navigator: NavHostController
) {
    /** флаг состояния выпадающего меню - открыто/закрыто */
    val showEpisodesMenu by viewModel.showEpisodesMenu.observeAsState(false)

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
                    if (showEpisodesMenu == true) {
                        viewModel.showEpisodesMenu.value = false
                    } else {
                        navigator.popBackStack()
                    }
                },
                tint = ShikidroidTheme.colors.onPrimary
            )
        }
        Text(
            modifier = Modifier
                .weight(weight = 0.95f)
                .padding(start = sevenDP),
            text = nameRu.orEmpty(),
            style = ShikidroidTheme.typography.body16sp,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun EpisodeTvHeader(
    viewModel: EpisodeScreenViewModel
) {
    /** количество эпизодов */
    val episodes by viewModel.episodes.observeAsState(0)

    /** номер эпизода */
    val episodeNumber = viewModel.episodeNumber.observeAsState(1)

    /** тип трансляции (оригинал, субтитры, озвучка) */
    val translationType by viewModel.translationType.observeAsState()

    /////////////////////////// HEADER ////////////////////////////
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = ninetyDP)
            .padding(fourteenDP)
    ) {

        val (btnEpisode, btns) = createRefs()

        if (episodes > 0) {
            TvSelectable { interactionSource, isFocused, scale ->
                RoundedTextButton(
                    modifier = Modifier
                        .constrainAs(btnEpisode) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value),
                    backgroundColor =
                    if (isFocused) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        ShikidroidTheme.colors.tvSelectable
                    },
                    text = episodeNumber.value.toString(),
                    textColor = ShikidroidTheme.colors.secondary,
                    onClick = {
                        viewModel.showEpisodesMenu.value =
                            viewModel.showEpisodesMenu.value != true
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .constrainAs(btns) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .wrapContentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = translationType == TranslationType.RAW) {
                    Text(
                        text = ORIGINAL_TITLE,
                        color = ShikidroidTheme.colors.onPrimary,
                        style = ShikidroidTheme.typography.body12sp
                    )
                }
                TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                    RoundedIconButton(
                        iconBoxModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        textModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        backgroundColor =
                        when {
                            translationType == TranslationType.RAW && !isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType == TranslationType.RAW && isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType != TranslationType.RAW && isFocused -> {
                                ShikidroidTheme.colors.tvSelectable
                            }

                            else -> Color.Transparent
                        },
                        onClick = {
                            viewModel.translationType.value = TranslationType.RAW
                        },
                        icon = R.drawable.ic_original,
                        tint =
                        if (translationType == TranslationType.RAW) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        },
                        isIcon = true
                    )
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = translationType == TranslationType.SUB_RU) {
                    Text(
                        text = SUBTITLES_TITLE,
                        color = ShikidroidTheme.colors.onPrimary,
                        style = ShikidroidTheme.typography.body12sp
                    )
                }
                TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                    RoundedIconButton(
                        iconBoxModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        textModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        backgroundColor =
                        when {
                            translationType == TranslationType.SUB_RU && !isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType == TranslationType.SUB_RU && isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType != TranslationType.SUB_RU && isFocused -> {
                                ShikidroidTheme.colors.tvSelectable
                            }

                            else -> Color.Transparent
                        },
                        onClick = {
                            viewModel.translationType.value = TranslationType.SUB_RU
                        },
                        icon = R.drawable.ic_subs,
                        tint =
                        if (translationType == TranslationType.SUB_RU) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        },
                        isIcon = true
                    )
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = translationType == TranslationType.VOICE_RU) {
                    Text(
                        text = DUB_TITLE,
                        color = ShikidroidTheme.colors.onPrimary,
                        style = ShikidroidTheme.typography.body12sp
                    )
                }
                TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                    RoundedIconButton(
                        iconBoxModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        textModifier = Modifier
                            .focusable(interactionSource = interactionSource)
                            .scale(scale = scale.value),
                        backgroundColor =
                        when {
                            translationType == TranslationType.VOICE_RU && !isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType == TranslationType.VOICE_RU && isFocused -> {
                                ShikidroidTheme.colors.secondaryVariant
                            }

                            translationType != TranslationType.VOICE_RU && isFocused -> {
                                ShikidroidTheme.colors.tvSelectable
                            }

                            else -> Color.Transparent
                        },
                        onClick = {
                            viewModel.translationType.value = TranslationType.VOICE_RU
                        },
                        icon = R.drawable.ic_voice,
                        tint =
                        if (translationType == TranslationType.VOICE_RU) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        },
                        isIcon = true
                    )
                }
            }
        }
    }
}

@Composable
internal fun EpisodeTvBody(
    viewModel: EpisodeScreenViewModel
) {
    /** флаг загрузки данных без полной блокировки экрана */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** список данных с трансляциями */
    val translations by viewModel.translations.observeAsState()

    /** контекст */
    val context = LocalContext.current

    if (isLoadingWithoutBlocking) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = ShikidroidTheme.colors.secondary
            )
        }
    } else {
        if (translations.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(vertical = threeDP),
                        text = EMPTY_SEARCH_TITLE
                    )
                    Text13Sp(
                        modifier = Modifier
                            .padding(vertical = threeDP),
                        text = EMPTY_EPISODE_TITLE,
                        color = ShikidroidTheme.colors.onBackground
                    )
                }
            }
        } else {
            TvLazyVerticalGrid(
                columns = TvGridCells.Fixed(2),
            ) {
                translations?.let {
                    items(
                        items = it
                    ) { translation ->

                        TvSelectable { interactionSource, isFocused, scale ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(oneHundredDP)
                                    .padding(horizontal = fourteenDP, vertical = threeDP)
                                    .focusable(interactionSource = interactionSource)
                                    .scale(scale = scale.value)
                                    .clickable {
                                        navigateVideoPlayerScreen(
                                            animeId = viewModel.animeId,
                                            animeNameEng = viewModel.animeNameEng,
                                            animeNameRu = viewModel.animeNameRu.value,
                                            translation = translation,
                                            context = context
                                        )
                                    },
                                backgroundColor = ShikidroidTheme.colors.background,
                                elevation = ShikidroidTheme.elevation,
                                border = BorderStroke(
                                    width = oneDP,
                                    color = if (isFocused) {
                                        ShikidroidTheme.colors.secondary
                                    } else {
                                        ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                                    }
                                ),
                                shape = ShikidroidTheme.shapes.roundedCorner7dp
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .wrapContentWidth()
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .padding(fourteenDP),
                                            text = (translation.author
                                                ?: translation.hosting).orEmpty(),
                                            color = ShikidroidTheme.colors.onPrimary,
                                            style = ShikidroidTheme.typography.bodySemiBold16sp,
                                        )
                                        Card(
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .padding(sevenDP),
                                            backgroundColor = ShikidroidTheme.colors.background,
                                            elevation = ShikidroidTheme.elevation,
                                            border = BorderStroke(
                                                width = oneDP,
                                                color = if (isFocused) {
                                                    ShikidroidTheme.colors.secondary
                                                } else {
                                                    ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                                                }
                                            ),
                                            shape = ShikidroidTheme.shapes.absoluteRounded50dp,
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(sevenDP),
                                                text = translation.hosting.orEmpty(),
                                                color = ShikidroidTheme.colors.secondary,
                                                style = ShikidroidTheme.typography.body13sp,
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .wrapContentWidth(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Spacer(modifier = Modifier.height(sevenDP))
                                        Text(
                                            modifier = Modifier
                                                .padding(sevenDP),
                                            text = translation.quality.orEmpty(),
                                            color = ShikidroidTheme.colors.onPrimary,
                                            style = ShikidroidTheme.typography.body13sp,
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
internal fun EpisodeTvDrawer(
    viewModel: EpisodeScreenViewModel
) {
    /** количество эпизодов */
    val episodes by viewModel.episodes.observeAsState(0)

    /** номер текущего эпизода */
    val episodeNumber by viewModel.episodeNumber.observeAsState(1)

    /** данные об аниме из пользовательского списка */
    val userRateModel by viewModel.userRateModel.observeAsState()

    val episodeList = remember {
        mutableListOf<Int>()
    }

    LaunchedEffect(key1 = episodes) {
        if (episodes > 0 && episodeList.isEmpty()) {
            for (i in 1..episodes) {
                episodeList.add(i)
            }
        }
    }

    TvLazyVerticalGrid(
        modifier = Modifier
            .fillMaxHeight()
            .width(width = 180.dp)
            .background(color = ShikidroidTheme.colors.background),
        columns = TvGridCells.Fixed(count = 2)
    ) {
        item {
            SpacerForList()
        }
        item {
            SpacerForList()
        }

        items(
            items = episodeList,
            key = { it }
        ) { i ->
            TvSelectable(scaleAnimation = 1.1f) { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .focusable(interactionSource = interactionSource)
                        .scale(scale = scale.value)
                        .background(
                            color =
                            when {
                                i == episodeNumber && !isFocused -> {
                                    ShikidroidTheme.colors.secondaryVariant
                                }

                                i == episodeNumber && isFocused -> {
                                    ShikidroidTheme.colors.secondaryVariant
                                }

                                i != episodeNumber && isFocused -> {
                                    ShikidroidTheme.colors.tvSelectable
                                }

                                else -> {
                                    Color.Transparent
                                }
                            }
                        )
                        .clickable {
                            viewModel.episodeNumber.value = i
                            viewModel.showEpisodesMenu.value =
                                viewModel.showEpisodesMenu.value != true
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = fourteenDP, vertical = threeDP),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text13Sp(
                            modifier = Modifier
                                .padding(sevenDP),
                            text = i.toString(),
                            color =
                            if (episodeNumber == i) {
                                ShikidroidTheme.colors.secondary
                            } else {
                                ShikidroidTheme.colors.onPrimary
                            }
                        )
                        userRateModel?.episodes?.let { userEpisodes ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = sevenDP)
                                    .size(fourteenDP)
                                    .clip(shape = AbsoluteRounded50dp)
                                    .background(
                                        color =
                                        if (i in 1..userEpisodes) {
                                            ShikidroidTheme.colors.secondaryVariant
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id =
                                        if (i in 1..userEpisodes) {
                                            R.drawable.ic_check
                                        } else {
                                            R.drawable.ic_plus
                                        }
                                    ),
                                    contentDescription = null,
                                    tint =
                                    if (i in 1..userEpisodes) {
                                        ShikidroidTheme.colors.secondary
                                    } else {
                                        ShikidroidTheme.colors.onBackground
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SpacerForList()
        }
        item {
            SpacerForList()
        }
    }
}