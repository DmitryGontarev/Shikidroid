package com.shikidroid.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.openLink
import com.shikidroid.presentation.converters.toSourceResolution
import com.shikidroid.presentation.navigation.navigateVideoPlayerScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.GenreCard
import com.shikidroid.presentation.screens.items.SelectableCard
import com.shikidroid.presentation.screens.items.Text13Sp
import com.shikidroid.presentation.screens.items.Text16SemiBold
import com.shikidroid.presentation.viewmodels.EpisodeScreenViewModel
import com.shikidroid.shareLink
import com.shikidroid.ui.DUB_TITLE
import com.shikidroid.ui.EMPTY_EPISODE_TITLE
import com.shikidroid.ui.EMPTY_SEARCH_TITLE
import com.shikidroid.ui.ORIGINAL_TITLE
import com.shikidroid.ui.REQUEST_ERROR_TITLE
import com.shikidroid.ui.SUBTITLES_TITLE
import com.shikidroid.ui.TRY_ANOTHER_ONE_ERROR_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.getEpisodeShareMessage
import com.shikidroid.utils.toDownloadLink

@Composable
internal fun EpisodeScreen(
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
        BoxWithHorizontalDrawer(
            isDrawerOpen = showEpisodesMenu,
            drawerContent = {
                EpisodeDrawer(viewModel = viewModel)
            }
        ) {
            BoxWithBackground(
                backgroundColor = ShikidroidTheme.colors.background,
                backgroundAlpha = 0.87f,
                backgroundContent = {
                    AsyncImageLoader(
                        url = animeImageUrl
                    )
                }
            ) {
                AnimatedHeaderToolbar(
                    hideToolbarByScroll = true,
                    headerHeight = oneHundredDP,
                    toolbarColor = ShikidroidTheme.colors.surface,
                    headerWrapColors = ShikidroidTheme.colors.background,
                    toolbarContent = {
                        EpisodeToolbar(
                            nameRu = animeNameRu,
                            viewModel = viewModel,
                            navigator = navigator
                        )
                    },
                    headerContent = {
                        EpisodeHeader(
                            viewModel = viewModel
                        )
                    }
                ) { columnScope ->
                    EpisodeBody(
                        columnScope = columnScope,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
internal fun EpisodeToolbar(
    nameRu: String?,
    viewModel: EpisodeScreenViewModel,
    navigator: NavHostController
) {
    /** флаг состояния выпадающего меню - открыто/закрыто */
    val showEpisodesMenu by viewModel.showEpisodesMenu.observeAsState(false)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = sevenDP, vertical = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RoundedIconButton(
            icon = R.drawable.ic_arrow_back,
            backgroundColor = Color.Transparent,
            onClick = {
                if (showEpisodesMenu == true) {
                    viewModel.showEpisodesMenu.value = false
                } else {
                    navigator.popBackStack()
                }
            },
            tint = ShikidroidTheme.colors.onPrimary
        )
        Text(
            modifier = Modifier
                .weight(weight = 0.95f),
            text = nameRu.orEmpty(),
            style = ShikidroidTheme.typography.body16sp,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun EpisodeHeader(
    viewModel: EpisodeScreenViewModel
) {
    /** количество эпизодов */
    val episodes by viewModel.episodes.observeAsState(0)

    /** номер эпизода */
    val episodeNumber = viewModel.episodeNumber.observeAsState(1)

    /** тип трансляции (оригинал, субтитры, озвучка) */
    val translationType by viewModel.translationType.observeAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = ninetyDP)
            .padding(fourteenDP)
    ) {

        val (btnEpisode, btns) = createRefs()

        if (episodes > 0) {
            RoundedTextButton(
                modifier = Modifier
                    .constrainAs(btnEpisode) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                backgroundColor = ShikidroidTheme.colors.secondaryVariant,
                onClick = {
                    viewModel.showEpisodesMenu.value =
                        viewModel.showEpisodesMenu.value != true
                },
                text = episodeNumber.value.toString(),
                textColor = ShikidroidTheme.colors.secondary,
                textStyle = ShikidroidTheme.typography.bodySemiBold16sp
            )
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
                RoundedIconButton(
                    backgroundColor =
                    if (translationType == TranslationType.RAW) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        Color.Transparent
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
                RoundedIconButton(
                    backgroundColor =
                    if (translationType == TranslationType.SUB_RU) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        Color.Transparent
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
                RoundedIconButton(
                    backgroundColor =
                    if (translationType == TranslationType.VOICE_RU) {
                        ShikidroidTheme.colors.secondaryVariant
                    } else {
                        Color.Transparent
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

@Composable
internal fun EpisodeDrawer(
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

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxHeight()
            .width(width = 180.dp),
        columns = GridCells.Fixed(count = 2),
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
            Box(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                    .background(
                        color =
                        if (i == episodeNumber) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        viewModel.episodeNumber.value = i
                        viewModel.showEpisodesMenu.value = false
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
                        if (i == episodeNumber) {
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
        item {
            SpacerForList()
        }
        item {
            SpacerForList()
        }
    }
}

@Composable
internal fun EpisodeBody(
    columnScope: ColumnScope,
    viewModel: EpisodeScreenViewModel
) {
    /** список данных с трансляциями */
    val translations by viewModel.translations.observeAsState()

    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** флаг загрузки данных без полной блокировки экрана */
    val isLoadingWithoutBlocking by viewModel.isLoadingWithoutBlocking.observeAsState(false)

    /** выбранный элемент для получения ссылки на скачивание */
    val currentAuthor by viewModel.currentAuthor.observeAsState(initial = "")

    /** выбранный хостинг для получения ссылки на скачивание */
    val currentHosting by viewModel.currentHosting.observeAsState(initial = "")

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
            translations?.let { translationList ->
                RowForColumnScope(
                    columnScope = columnScope,
                    data = translationList,
                    columnCount = if (!isScreenHorizontal) 1 else 2
                ) { translation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(oneHundredDP)
                            .padding(horizontal = fourteenDP, vertical = threeDP)
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
                            color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
                        ),
                        shape = ShikidroidTheme.shapes.roundedCorner7dp
                    ) {
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            val (title, hosting, button, quality) = createRefs()

                            /** Название автора */
                            Text16SemiBold(
                                modifier = Modifier
                                    .padding(fourteenDP)
                                    .constrainAs(title) {
                                        top.linkTo(parent.top)
                                        start.linkTo(parent.start)
                                    },
                                text = (translation.author ?: translation.hosting).orEmpty()
                            )

                            /** Название хостинга */
                            GenreCard(
                                modifier = Modifier
                                    .padding(sevenDP)
                                    .constrainAs(hosting) {
                                        start.linkTo(parent.start)
                                        bottom.linkTo(parent.bottom)
                                    },
                                text = translation.hosting.orEmpty(),
                                textStyle = ShikidroidTheme.typography.body13sp
                            )

                            /** Кнопка загрузки ссылки для скачивания видео */
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .constrainAs(button) {
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end)
                                    }
                            ) {
                                RoundedIconButton(
                                    icon = R.drawable.ic_download,
                                    tint = ShikidroidTheme.colors.onPrimary,
                                    onClick = {
                                        viewModel.currentAuthor.value = translation.author
                                        viewModel.currentHosting.value = translation.hosting
                                        viewModel.loadVideoForDownload(
                                            malId = translation.targetId ?: 1,
                                            episode = translation.episode ?: 1,
                                            translationType = translation.kind,
                                            author = translation.author,
                                            hosting = translation.hosting.orEmpty(),
                                            hostingId = 1,
                                            videoId = translation.id,
                                            url = translation.url
                                        )
                                        viewModel.showDropdownDownloadMenu.value = true
                                    }
                                )
                                if (currentAuthor == translation.author && currentHosting == translation.hosting) {
                                    DropdownDownload(
                                        author = translation.author ?: translation.hosting,
                                        viewModel = viewModel
                                    )
                                }
                            }

                            /** Строка с качеством видео */
                            Text13Sp(
                                modifier = Modifier
                                    .padding(sevenDP)
                                    .constrainAs(quality) {
                                        end.linkTo(parent.end)
                                        bottom.linkTo(parent.bottom)
                                    },
                                text = translation.quality
                            )
                        }
                    }
                }
                SpacerForList()
            }
        }
    }
}

@Composable
internal fun DropdownDownload(
    author: String?,
    viewModel: EpisodeScreenViewModel
) {
    /** контекст */
    val context = LocalContext.current
    
    /** состояние загрузки элемента */
    val isLoadingWithItemProgress by viewModel.isLoadingWithItemProgress.observeAsState(initial = false)

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownDownloadMenu by viewModel.showDropdownDownloadMenu.observeAsState(initial = false)

    /** данные для загрузки видео в память */
    val videoForDownload by viewModel.videoForDownload.observeAsState()

    /** флаг показа ошибки, если не удалось получить ссылки для загрузки */
    val videoForDownloadError by viewModel.videoForDownloadError.observeAsState(initial = false)

    SelectableCard(isFocused = false, scale = 1f) {
        DropdownMenu(
            modifier = Modifier
                .background(
                    color = ShikidroidTheme.colors.surface
                ),
            expanded = showDropdownDownloadMenu,
            onDismissRequest = {
                viewModel.showDropdownDownloadMenu.value = false
                viewModel.videoForDownloadError.value = false
            }
        ) {
            if (isLoadingWithItemProgress) {
                Box(
                    modifier = Modifier
                        .size(oneHundredDP),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ShikidroidTheme.colors.secondary
                    )
                }
            } else {
                Text13SemiBold(
                    modifier = Modifier
                        .padding(start = sevenDP),
                    text = author
                )
                if (videoForDownloadError) {
                    Text14Sp(
                        modifier = Modifier
                            .padding(all = sevenDP),
                        text = REQUEST_ERROR_TITLE
                    );
                } else {
                    videoForDownload?.let { video ->
                        video.tracks?.map { link ->
                            Row(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(vertical = sevenDP),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RoundedTextButton(
                                    text = link?.quality.toSourceResolution(),
                                    textColor = ShikidroidTheme.colors.onPrimary,
                                    textStyle = ShikidroidTheme.typography.bodySemiBold16sp
                                )
                                RoundedIconButton(
                                    iconModifier = Modifier
                                        .size(twentyDP),
                                    icon = R.drawable.ic_public,
                                    tint = ShikidroidTheme.colors.onPrimary,
                                    onClick = {
                                        context.openLink(link = link?.url?.toDownloadLink())
                                        viewModel.showDropdownDownloadMenu.value = false
                                    }
                                )
                                RoundedIconButton(
                                    icon = R.drawable.ic_sharing,
                                    tint = ShikidroidTheme.colors.onPrimary,
                                    onClick = {
                                        context.shareLink(
                                            link = link?.url?.toDownloadLink(),
                                            title = getEpisodeShareMessage(
                                                title = viewModel.animeNameRu.value.orEmpty(),
                                                episode = video.episodeId?.toInt() ?: 1,
                                                url = link?.url?.toDownloadLink() ?: ""
                                            )
                                        )
                                        viewModel.showDropdownDownloadMenu.value = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}