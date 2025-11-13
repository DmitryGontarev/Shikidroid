package com.shikidroid.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.roles.*
import com.shikidroid.presentation.CharacterPeopleScreenDrawerType
import com.shikidroid.presentation.CharacterPeopleScreenType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.*
import com.shikidroid.presentation.navigation.navigateByLinkType
import com.shikidroid.presentation.navigation.navigateCharacterScreen
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.presentation.navigation.navigateDetailsScreen
import com.shikidroid.presentation.navigation.navigatePeopleScreen
import com.shikidroid.presentation.screens.items.*
import com.shikidroid.presentation.screens.items.DefaultExpandCard
import com.shikidroid.presentation.screens.items.ImageTextCard
import com.shikidroid.presentation.screens.items.ImageWithOverPicture
import com.shikidroid.presentation.screens.items.RowTitleText
import com.shikidroid.presentation.viewmodels.CharacterPeopleScreenViewModel
import com.shikidroid.ui.*
import com.shikidroid.ui.ANIME_TITLE
import com.shikidroid.ui.BEST_ROLES_TITLE
import com.shikidroid.ui.DESCRIPTION_TITLE
import com.shikidroid.ui.ROLES_ANIME_MANGA_TITLE
import com.shikidroid.ui.WEB_VERSION_TITLE
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.*
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.LongUtils.toMonthString
import com.shikidroid.utils.StringUtils
import com.shikidroid.utils.StringUtils.getNullIfEmpty

@Composable
internal fun CharacterPeopleScreen(
    screenType: CharacterPeopleScreenType?,
    navigator: NavHostController,
    viewModel: CharacterPeopleScreenViewModel
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(true)

    /** флаг показа экрана с ошибкой */
    val showErrorScreen by viewModel.showErrorScreen.observeAsState(initial = false)

    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    /** детальная информация о человеке */
    val peopleDetails by viewModel.peopleDetails.observeAsState()

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
                        viewModel.loadData(id = it)
                    }
                },
                altClick = {
                    navigator.popBackStack()
                }
            )
        }

        else -> {
            BoxWithVerticalDrawer(
                isDrawerOpen = isDrawerOpen,
                drawerContent = {
                    CharacterPeopleScreenDrawer(
                        viewModel = viewModel,
                        navigator = navigator
                    )
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
                                    text = (characterDetails?.nameRu
                                        ?: peopleDetails?.nameRu).orEmpty(),
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
                                DropdownCharacterPeopleMenu(
                                    viewModel = viewModel,
                                    navigator = navigator
                                )
                            }
                        }
                    },
                    headerContent = {
                        if (screenType == CharacterPeopleScreenType.CHARACTER) {
                            characterDetails?.let { HeaderCharacter(characterDetails = it) }
                        } else {
                            peopleDetails?.let {
                                HeaderPeople(
                                    peopleDetails = it,
                                    navigator = navigator
                                )
                            }
                        }
                    }
                ) {
                    if (screenType == CharacterPeopleScreenType.CHARACTER) {
                        DescriptionCharacter(viewModel = viewModel, navigator = navigator)
                        Seyu(viewModel = viewModel, navigator = navigator)
                        Anime(viewModel = viewModel, navigator = navigator)
                        Manga(viewModel = viewModel, navigator = navigator)
                    } else {
                        peopleDetails?.let {
                            RolesGrouped(peopleDetails = it)
                        }
                        BestRoles(viewModel = viewModel, navigator = navigator)
                        BestWorks(viewModel = viewModel, navigator = navigator)
                    }
                    SpacerForList()
                }
            }
        }
    }
}

@Composable
internal fun CharacterPeopleScreenDrawer(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {

    /** тип контента меню */
    val drawerType by viewModel.drawerType.observeAsState()

    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (drawerType) {
            CharacterPeopleScreenDrawerType.SEYU -> {
                SeyuDrawer(characterDetails = characterDetails, navigator = navigator)
            }

            CharacterPeopleScreenDrawerType.ANIME -> {
                AnimeDrawer(characterDetails = characterDetails, navigator = navigator)
            }

            CharacterPeopleScreenDrawerType.MANGA -> {
                MangaDrawer(characterDetails = characterDetails, navigator = navigator)
            }

            CharacterPeopleScreenDrawerType.BEST_ROLES -> {
                BestRolesDrawer(viewModel = viewModel, navigator = navigator)
            }

            CharacterPeopleScreenDrawerType.BEST_WORKS -> {
                BestWorksDrawer(viewModel = viewModel, navigator = navigator)
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
internal fun DropdownCharacterPeopleMenu(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {

    /** детальная информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    /** детальная информация о человеке */
    val peopleDetails by viewModel.peopleDetails.observeAsState()

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
                            url = characterDetails?.url ?: peopleDetails?.url,
                            navigator = navigator
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
internal fun HeaderCharacter(characterDetails: CharacterDetailsModel) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = fourteenDP, end = fourteenDP, bottom = threeDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ImageWithOverPicture(
            modifier = Modifier
                .padding(end = fourteenDP),
            url = characterDetails.image?.original
        )
        Column(
            modifier = Modifier
                .height(202.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (characterDetails.nameRu.isNullOrEmpty().not()) {
                CharacterNameText(
                    text = "имя на русском:",
                    labelText = characterDetails.nameRu.orEmpty()
                )
            }
            if (characterDetails.nameRu.isNullOrEmpty() &&
                characterDetails.name.isNullOrEmpty().not()
            ) {
                CharacterNameText(
                    text = "имя на английском:",
                    labelText = characterDetails.name.orEmpty()
                )
            }
            if (characterDetails.nameJp.isNullOrEmpty().not()) {
                CharacterNameText(
                    text = "имя на японском:",
                    labelText = characterDetails.nameJp.orEmpty()
                )
            }
            if (characterDetails.nameAlt.isNullOrEmpty().not()) {
                CharacterNameText(
                    text = "прочие:",
                    labelText = characterDetails.nameAlt.orEmpty()
                )
            }
        }
    }
}

@Composable
fun CharacterNameText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = ShikidroidTheme.colors.onBackground,
    textStyle: TextStyle = ShikidroidTheme.typography.body13sp,
    labelText: String,
    secondTextColor: Color = ShikidroidTheme.colors.onPrimary,
    labelTextStyle: TextStyle = ShikidroidTheme.typography.body14sp,
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
        secondTextColor = secondTextColor,
        secondTextStyle = labelTextStyle,
        textAlign = textAlign,
        textMaxLines = textLines,
        secondTextAlign = labelTextAlign,
        secondTextMaxLines = labelTextLines
    )
}

@Composable
internal fun DescriptionCharacter(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    val description: String? = characterDetails?.description

    if (description?.trim().isNullOrEmpty().not()) {
        description?.let { descriptionString ->

            val annotations = StringUtils.toDescriptionSpoilerAnnotation(
                string = descriptionString,
                textSize = 13.sp,
                primaryColor = ShikidroidTheme.colors.onPrimary,
                annotationColor = ShikidroidTheme.colors.secondary
            )

            val descTypeLinkName = annotations[0].first
            val descStringAnnotat = annotations[0].second

            val spoilerTypeLinkName = annotations[1].first
            val spoilerStringAnnotat = annotations[1].second

            descStringAnnotat?.let { annotatedString ->

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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            ClickableText(
                                text = annotatedString,
                                style = ShikidroidTheme.typography.body13sp.copy(lineHeight = 20.sp),
                                onClick = { offset ->
                                    descTypeLinkName.map { typeLinkName ->
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
                            spoilerStringAnnotat?.let { spoiler ->
                                ExpandableBox(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    initialHeight = 7.dp,
                                    showTextModifier = Modifier
                                        .padding(sevenDP),
                                    showMoreText = "Спойлер",
                                    showLessText = "Свернуть спойлер",
                                    showTextColor = LightBlue,
                                    showTextStyle = ShikidroidTheme.typography.body13sp,
                                    showTextAlign = TextAlign.Center
                                ) {
                                    ClickableText(
                                        text = spoiler,
                                        style = ShikidroidTheme.typography.body13sp.copy(lineHeight = 20.sp),
                                        onClick = { offset ->
                                            spoilerTypeLinkName.map { typeLinkName ->
                                                spoiler.getStringAnnotations(
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
        }
    }
}

@Composable
internal fun Seyu(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    if (characterDetails?.seyu.isNullOrEmpty().not()) {
        characterDetails?.seyu?.let { seyuList ->
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
                            viewModel.showDrawer(type = CharacterPeopleScreenDrawerType.SEYU)
                        },
                    text = SEYU_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    items(
                        items = seyuList,
                        key = { it.id ?: 0 }
                    ) { seyu ->
                        SeyuItem(
                            modifier = Modifier
                                .paddingByList(
                                    items = seyuList,
                                    item = seyu
                                ),
                            seyu = seyu,
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SeyuDrawer(
    characterDetails: CharacterDetailsModel?,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    if (characterDetails?.seyu.isNullOrEmpty().not()) {
        characterDetails?.seyu?.let { seyuList ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = SEYU_TITLE)
                }
                gridItems(
                    data = seyuList,
                    columnCount = if (!isScreenHorizontal) 2 else 5
                ) { seyu ->
                    SeyuItem(
                        modifier = Modifier
                            .padding(fiveDP),
                        seyu = seyu,
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
internal fun SeyuItem(
    modifier: Modifier,
    seyu: PersonModel,
    navigator: NavHostController
) {
    ImageTextCard(
        modifier = modifier
            .clickable {
                navigatePeopleScreen(
                    id = seyu.id,
                    navigator = navigator
                )
            },
        url = seyu.image?.original,
        firstText = StringUtils.getEmptyIfBothNull(
            one = seyu.nameRu,
            two = seyu.name
        )
    )
}

@Composable
internal fun Anime(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    if (characterDetails?.animes.isNullOrEmpty().not()) {
        characterDetails?.animes?.let { animes ->
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
                            viewModel.showDrawer(type = CharacterPeopleScreenDrawerType.ANIME)
                        },
                    text = ANIME_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    items(
                        items = animes,
                        key = { it.id ?: 0 }
                    ) { anime ->
                        AnimeCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = animes,
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
internal fun AnimeDrawer(
    characterDetails: CharacterDetailsModel?,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    if (characterDetails?.animes.isNullOrEmpty().not()) {
        characterDetails?.animes?.let { animes ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = ANIME_TITLE)
                }
                gridItems(
                    data = animes,
                    columnCount = if (!isScreenHorizontal) 2 else 5
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
internal fun Manga(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** информация о персонаже */
    val characterDetails by viewModel.characterDetails.observeAsState()

    if (characterDetails?.mangas.isNullOrEmpty().not()) {
        characterDetails?.mangas?.let { mangas ->
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
                            viewModel.showDrawer(type = CharacterPeopleScreenDrawerType.MANGA)
                        },
                    text = MANGA_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    items(
                        items = mangas,
                        key = { it.id ?: 0 }
                    ) { manga ->
                        MangaCard(
                            modifier = Modifier
                                .paddingByList(
                                    items = mangas,
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

@Composable
internal fun MangaDrawer(
    characterDetails: CharacterDetailsModel?,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    if (characterDetails?.mangas.isNullOrEmpty().not()) {
        characterDetails?.mangas?.let { mangas ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = MANGA_TITLE)
                }
                gridItems(
                    data = mangas,
                    columnCount = if (!isScreenHorizontal) 2 else 5
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
internal fun HeaderPeople(
    peopleDetails: PersonDetailsModel,
    navigator: NavHostController
) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = fourteenDP, end = fourteenDP, bottom = threeDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ImageWithOverPicture(url = peopleDetails.image?.original)
        Column(
            modifier = Modifier
                .height(202.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (peopleDetails.nameRu.isNullOrEmpty().not()) {
                CharacterNameText(
                    text = "имя на русском:",
                    labelText = peopleDetails.nameRu.orEmpty()
                )
            }
            if (peopleDetails.nameRu.isNullOrEmpty() &&
                peopleDetails.name.isNullOrEmpty().not()
            ) {
                CharacterNameText(
                    text = "имя на английском:",
                    labelText = peopleDetails.name.orEmpty()
                )
            }
            if (peopleDetails.nameJp.isNullOrEmpty().not()) {
                CharacterNameText(
                    text = "имя на японском:",
                    labelText = peopleDetails.nameJp.orEmpty()
                )
            }
            peopleDetails.birthOn?.let {
                if (it.day != null) {
                    CharacterNameText(
                        text = "дата рождения:",
                        labelText = if (it.year == null) {
                            "${it.day} ${it.month.toMonthString(infinitive = false)}"
                        } else {
                            "${it.day} ${it.month.toMonthString(infinitive = false)} ${it.year}"
                        }
                    )
                }
            }
            peopleDetails.deceasedOn?.let {
                if (it.day != null) {
                    CharacterNameText(
                        text = "дата смерти:",
                        labelText = if (it.year == null) {
                            "${it.day} ${it.month.toMonthString(infinitive = false)}"
                        } else {
                            "${it.day} ${it.month.toMonthString(infinitive = false)} ${it.year}"
                        }
                    )
                }
            }
            peopleDetails.website?.let {
                if (it.trim().isEmpty().not()) {
                    CharacterNameText(
                        modifier = Modifier
                            .clickable {
                                navigateByLinkType(
                                    linkType = "url",
                                    link = it,
                                    navigator = navigator
                                )
                            },
                        text = "сайт:",
                        labelText = it,
                        secondTextColor = ShikidroidTheme.colors.secondary
                    )
                }
            }
        }
    }
}

@Composable
internal fun RolesGrouped(
    peopleDetails: PersonDetailsModel
) {
    if (peopleDetails.rolesGrouped?.isEmpty()?.not() == true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            RowTitleText(text = ROLES_ANIME_MANGA_TITLE)
            peopleDetails.rolesGrouped.map {
                Text(
                    modifier = Modifier
                        .padding(bottom = sevenDP, start = fourteenDP, end = fourteenDP),
                    text = "${it?.firstOrNull()}: ${it?.lastOrNull()}",
                    color = ShikidroidTheme.colors.onSurface,
                    style = ShikidroidTheme.typography.body13sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
internal fun BestRoles(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** список персонажей, озвученных актёром */
    val bestRoles by viewModel.seyuBestRoles.observeAsState()

    if (bestRoles.isNullOrEmpty().not()) {
        bestRoles?.let { roles ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                RowTitleText(
                    modifier = Modifier
                        .clickable {
                            viewModel.showDrawer(type = CharacterPeopleScreenDrawerType.BEST_ROLES)
                        },
                    text = BEST_ROLES_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    items(
                        items = roles,
                        key = { it.id ?: 0 }
                    ) { character ->
                        BestRolesItem(
                            modifier = Modifier
                                .paddingByList(
                                    items = roles,
                                    item = character
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

@Composable
internal fun BestRolesDrawer(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** детальная информация о человеке */
    val peopleDetails by viewModel.peopleDetails.observeAsState()

    if (peopleDetails?.roles.isNullOrEmpty().not()) {
        peopleDetails?.roles?.let { rolesList ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = BEST_ROLES_TITLE)
                }
                items(
                    items = rolesList
                ) { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = threeDP),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .weight(0.5f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            RowForColumnScope(
                                columnScope = this,
                                data = role.characters.orEmpty(),
                                columnCount = if (!isScreenHorizontal) 1 else 2
                            ) { character ->
                                BestRolesItem(
                                    modifier = Modifier
                                        .padding(fiveDP),
                                    character = character,
                                    height = oneHundredDP,
                                    width = oneHundredDP,
                                    navigator = navigator
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .weight(0.5f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            RowForColumnScope(
                                columnScope = this,
                                data = role.animes.orEmpty(),
                                columnCount = if (!isScreenHorizontal) 1 else 2
                            ) { anime ->
                                AnimeCard(
                                    modifier = Modifier
                                        .padding(fiveDP),
                                    anime = anime,
                                    navigator = navigator
                                )
                            }
                        }
                    }
                    Divider(color = ShikidroidTheme.colors.onBackground)
                }
                item {
                    SpacerForList()
                }
            }
        }
    }
}

@Composable
internal fun BestRolesItem(
    modifier: Modifier,
    character: CharacterModel,
    height: Dp,
    width: Dp,
    navigator: NavHostController
) {
    ImageTextCard(
        modifier = modifier
            .clickable {
                navigateCharacterScreen(
                    id = character.id,
                    navigator = navigator
                )
            },
        url = character.image?.original,
        height = height,
        width = width,
        firstText = StringUtils.getEmptyIfBothNull(
            one = character.nameRu,
            two = character.name
        )
    )
}

@Composable
internal fun BestWorks(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** информация о персонаже */
    val peopleDetails by viewModel.peopleDetails.observeAsState()

    if (peopleDetails?.works.isNullOrEmpty().not()) {
        peopleDetails?.works?.let { peopleWorks ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                RowTitleText(
                    modifier = Modifier
                        .clickable {
                            viewModel.showDrawer(type = CharacterPeopleScreenDrawerType.BEST_WORKS)
                        },
                    text = BEST_WORKS_TITLE
                )
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(vertical = sevenDP)
                ) {
                    peopleWorks.map { work ->
                        item {
                            /** флаг раскрытия карточки */
                            var expanded by remember {
                                mutableStateOf(false)
                            }

                            DefaultExpandCard(
                                modifier = Modifier
                                    .paddingByList(
                                        items = peopleWorks,
                                        item = work
                                    ),
                                useCard = false,
                                expanded = expanded,
                                mainContent = {
                                    ImageTextCard(
                                        modifier = Modifier
                                            .clickable {
                                                navigateDetailsScreen(
                                                    id = work.anime?.id ?: work.manga?.id,
                                                    detailsType =
                                                    when {
                                                        work.anime?.id != null -> DetailsScreenType.ANIME
                                                        work.manga?.id != null &&
                                                                (work.manga.type == MangaType.LIGHT_NOVEL ||
                                                                        work.manga.type == MangaType.NOVEL) -> DetailsScreenType.RANOBE

                                                        else -> DetailsScreenType.MANGA
                                                    },
                                                    navigator = navigator
                                                )
                                            },
                                        columnTextModifier = Modifier
                                            .clickable {
                                                expanded = !expanded
                                            },
                                        url = work.anime?.image?.original
                                            ?: work.manga?.image?.original,
                                        firstText = StringUtils.getEmptyIfBothNull(
                                            one = work.anime?.nameRu,
                                            two = work.anime?.name
                                        ).getNullIfEmpty() ?: StringUtils.getEmptyIfBothNull(
                                            one = work.manga?.nameRu,
                                            two = work.manga?.name
                                        ),
                                        secondTextTwo = work.role.orEmpty()
                                    )
                                },
                                status = work.anime?.status ?: work.manga?.status,
                                score = work.anime?.score ?: work.manga?.score,
                                episodes = work.anime?.episodes,
                                episodesAired = work.anime?.episodesAired,
                                chapters = work.manga?.chapters,
                                volumes = work.manga?.volumes,
                                dateAired = work.anime?.dateAired ?: work.manga?.dateAired,
                                dateReleased = work.manga?.dateReleased ?: work.manga?.dateReleased
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun BestWorksDrawer(
    viewModel: CharacterPeopleScreenViewModel,
    navigator: NavHostController
) {
    /** флаг, горизонтальное ли положение экрана */
    val isScreenHorizontal = isComposableHorizontalOrientation()

    /** информация о персонаже */
    val peopleDetails by viewModel.peopleDetails.observeAsState()

    /** список лучших работ человек аниме/манга/ранобэ */
    val animeMangaRanobe by viewModel.bestWorksAnimeMangaRanobe.observeAsState()

    if (peopleDetails?.works.isNullOrEmpty().not()) {
        peopleDetails?.works?.let { works ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SpacerStatusBar()
                }
                item {
                    RowTitleText(text = BEST_WORKS_TITLE)
                }
                animeMangaRanobe?.first?.let { animes ->
                    if (animes.isNotEmpty()) {
                        item {
                            RowTitleText(text = ANIME_TITLE)
                        }
                        gridItems(
                            data = animes,
                            columnCount = if (!isScreenHorizontal) 2 else 5
                        ) { anime ->
                            AnimeCard(
                                modifier = Modifier
                                    .padding(fiveDP),
                                anime = anime,
                                navigator = navigator
                            )
                        }
                    }
                }
                animeMangaRanobe?.second?.let { mangas ->
                    if (mangas.isNotEmpty()) {
                        item {
                            RowTitleText(text = MANGA_TITLE)
                        }
                        gridItems(
                            data = mangas,
                            columnCount = if (!isScreenHorizontal) 2 else 5
                        ) { manga ->
                            MangaCard(
                                modifier = Modifier
                                    .padding(fiveDP),
                                manga = manga,
                                navigator = navigator
                            )
                        }
                    }
                }
                animeMangaRanobe?.third?.let { ranobes ->
                    if (ranobes.isNotEmpty()) {
                        item {
                            RowTitleText(text = RANOBE_TITLE)
                        }
                        gridItems(
                            data = ranobes,
                            columnCount = if (!isScreenHorizontal) 2 else 5
                        ) { ranobe ->
                            MangaCard(
                                modifier = Modifier
                                    .padding(fiveDP),
                                manga = ranobe,
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