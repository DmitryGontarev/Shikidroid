package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.presentation.converters.toColor
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.DotTextDivider
import com.shikidroid.uikit.components.RoundedIconButton
import com.shikidroid.uikit.fiftyDP
import com.shikidroid.uikit.fourteenDP
import com.shikidroid.uikit.oneDP
import com.shikidroid.uikit.oneHundredDP
import com.shikidroid.uikit.oneHundredFiftyDP
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.threeDP
import com.shikidroid.utils.DateUtils

@Composable
internal fun DotText12spDivider() {
    DotTextDivider(
        style = ShikidroidTheme.typography.body12sp,
        color = ShikidroidTheme.colors.onBackground
    )
}

/**
 * Карточка аниме для списка пользовательского рейтинга
 *
 * @param userScore оценка пользователя
 * @param userEpisodes количество просмотренных пользователем эпизодов
 * @param anime модель с информацией об аниме
 */
@Composable
internal fun AnimeRateCard(
    modifier: Modifier = Modifier,
    userScore: Int?,
    userEpisodes: Int?,
    anime: AnimeModel,
    borderColor: Color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f),
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onPlayClick: (() -> Unit)? = null
) {
    /** название аниме */
    val animeTitle = remember {
        mutableStateOf(anime.nameRu)
    }

    Card(
        modifier = modifier
            .padding(horizontal = fourteenDP, vertical = threeDP)
            .height(oneHundredFiftyDP)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        when (animeTitle.value) {
                            anime.nameRu -> animeTitle.value = anime.name
                            else -> animeTitle.value = anime.nameRu
                        }
                    },
                    onTap = {
                        onTap?.let {
                            it()
                        }
                    },
                    onDoubleTap = {
                        onDoubleTap?.let {
                            it()
                        }
                    }
                )
            },
        backgroundColor = ShikidroidTheme.colors.background,
        elevation = ShikidroidTheme.elevation,
        border = BorderStroke(
            width = oneDP,
            color = borderColor
        ),
        shape = ShikidroidTheme.shapes.roundedCorner7dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {
            ImageWithOverPicture(
                url = anime.image?.original.orEmpty(),
                width = oneHundredDP,
                height = oneHundredFiftyDP,
                overPicture = {
                    OverPictureOne(
                        textLeftTopCorner = "${anime.score}",
                        textRightBottomCorner =
                        if ((userScore ?: 0) > 0) {
                            "$userScore"
                        } else {
                            null
                        }
                    )
                }
            )
            Column(
                modifier = Modifier
                    .padding(
                        vertical = sevenDP,
                        horizontal = fourteenDP
                    )
            ) {
                ////////////////////////////////////////////////////////////////////////////////////
                // Название
                ////////////////////////////////////////////////////////////////////////////////////
                Text12Sp(
                    text = animeTitle.value.orEmpty(),
                    textAlign = TextAlign.Start
                )

                ////////////////////////////////////////////////////////////////////////////////////
                // Строка с информацией о статусе релиза, типе трансляции, кол-во эпизодов и т.д.
                ////////////////////////////////////////////////////////////////////////////////////
                Row(
                    modifier = Modifier
                        .padding(
                            top = threeDP
                        )
                ) {
                    ////////////////////////////////////////////////////////////////////////////////////
                    // Статус выхода
                    ////////////////////////////////////////////////////////////////////////////////////
                    Text12Sp(
                        text = anime.status?.toScreenString().orEmpty(),
                        color = anime.status?.toColor() ?: Color.Transparent
                    )

                    DotText12spDivider()

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Год выхода
                    ////////////////////////////////////////////////////////////////////////////////////
                    when {
                        anime.type == AnimeType.MOVIE && anime.status != AiredStatus.ANONS -> {
                            (anime.dateReleased ?: anime.dateAired)?.let {
                                Text12Sp(
                                    text = DateUtils.getYearString(
                                        date = DateUtils.fromString(
                                            dateString = it
                                        )
                                    ),
                                    color = ShikidroidTheme.colors.onBackground
                                )
                                DotText12spDivider()
                            }
                        }

                        anime.status == AiredStatus.ANONS -> {
                            anime.dateAired?.let {
                                Text12Sp(
                                    text = DateUtils.getDateString(
                                        date = DateUtils.fromString(
                                            dateString = it
                                        )
                                    ),
                                    color = ShikidroidTheme.colors.onBackground
                                )
                                DotText12spDivider()
                            }
                        }

                        else -> {
                            (anime.dateReleased ?: anime.dateAired)?.let {
                                Text12Sp(
                                    text = DateUtils.getYearString(
                                        date = DateUtils.fromString(
                                            dateString = it
                                        )
                                    ),
                                    color = ShikidroidTheme.colors.onBackground
                                )
                                DotText12spDivider()
                            }
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Тип аниме (TV, Фильм, OVA и т.д.)
                    ////////////////////////////////////////////////////////////////////////////////////
                    Text12Sp(
                        text = anime.type?.toScreenString().orEmpty(),
                        color = ShikidroidTheme.colors.onBackground
                    )

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Количество эпизодов
                    ////////////////////////////////////////////////////////////////////////////////////
                    if (anime.type != AnimeType.MOVIE) {
                        when {
                            anime.status == AiredStatus.ANONS && anime.episodes != 0 -> {
                                DotText12spDivider()
                                Text12Sp(
                                    text = "${anime.episodesAired} / ${anime.episodes} эп.",
                                    color = ShikidroidTheme.colors.onBackground
                                )
                            }

                            anime.status == AiredStatus.ONGOING -> {
                                DotText12spDivider()
                                Text12Sp(
                                    text = "${anime.episodesAired} / ${anime.episodes} эп.",
                                    color = ShikidroidTheme.colors.onBackground
                                )
                            }

                            anime.status == AiredStatus.RELEASED -> {
                                DotText12spDivider()
                                Text12Sp(
                                    text = "${anime.episodes} эп.",
                                    color = ShikidroidTheme.colors.onBackground
                                )
                            }
                        }
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////////
                // Строка с количеством просмотренных эпизодов и кнопками
                ////////////////////////////////////////////////////////////////////////////////////
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ////////////////////////////////////////////////////////////////////////////////////
                    // Количество просмотренных эпизодов
                    ////////////////////////////////////////////////////////////////////////////////////
                    Box(
                        modifier = Modifier
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if ((userEpisodes ?: 0) > 0) {
                            Text16Sp(
                                text = "$userEpisodes"
                            )
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Кнопки редактирования и перехода на экран просмотра с плеером
                    ////////////////////////////////////////////////////////////////////////////////////

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        onEditClick?.let {
                            RoundedIconButton(
                                backgroundColor = ShikidroidTheme.colors.secondaryVariant,
                                onClick = {
                                    it()
                                },
                                icon = R.drawable.ic_edit_small,
                                tint = ShikidroidTheme.colors.secondary,
                                isIcon = true
                            )
                        }

                        onPlayClick?.let {
                            RoundedIconButton(
                                backgroundColor = ShikidroidTheme.colors.secondaryVariant,
                                onClick = {
                                    it()
                                },
                                icon = R.drawable.ic_play_arrow,
                                tint = ShikidroidTheme.colors.secondary,
                                isIcon = true
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Карточка аниме для списка пользовательского рейтинга
 *
 * @param userScore оценка пользователя
 * @param userChapters количество просмотренных пользователем эпизодов
 * @param manga модель с информацией о манге/ранобэ
 */
@Composable
internal fun MangaRateCard(
    userScore: Int?,
    userChapters: Int?,
    manga: MangaModel,
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null
) {
    /** название манги */
    val mangaTitle = remember {
        mutableStateOf(manga.nameRu)
    }

    Card(
        modifier = Modifier
            .padding(horizontal = fourteenDP, vertical = threeDP)
            .height(oneHundredFiftyDP)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        when (mangaTitle.value) {
                            manga.nameRu -> mangaTitle.value = manga.name
                            else -> mangaTitle.value = manga.nameRu
                        }
                    },
                    onTap = {
                        onTap?.let {
                            it()
                        }
                    },
                    onDoubleTap = {
                        onDoubleTap?.let {
                            it()
                        }
                    }
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
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {
            ImageWithOverPicture(
                url = manga.image?.original.orEmpty(),
                width = oneHundredDP,
                height = oneHundredFiftyDP,
                overPicture = {
                    OverPictureOne(
                        textLeftTopCorner = "${manga.score}",
                        textRightBottomCorner =
                        if ((userScore ?: 0) > 0) {
                            "$userScore"
                        } else {
                            null
                        }
                    )
                }
            )
            Column(
                modifier = Modifier
                    .padding(
                        vertical = sevenDP,
                        horizontal = fourteenDP
                    )
            ) {
                ////////////////////////////////////////////////////////////////////////////////////
                // Название
                ////////////////////////////////////////////////////////////////////////////////////
                Text12Sp(
                    text = mangaTitle.value.orEmpty(),
                    textAlign = TextAlign.Start
                )

                ////////////////////////////////////////////////////////////////////////////////////
                // Строка с информацией о статусе релиза, кол-во глав и т.д.
                ////////////////////////////////////////////////////////////////////////////////////
                Row(
                    modifier = Modifier
                        .padding(
                            top = threeDP
                        )
                ) {
                    ////////////////////////////////////////////////////////////////////////////////////
                    // Статус выхода
                    ////////////////////////////////////////////////////////////////////////////////////
                    Text12Sp(
                        text = manga.status?.toScreenString().orEmpty(),
                        color = manga.status?.toColor() ?: Color.Transparent
                    )

                    DotText12spDivider()

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Год выхода
                    ////////////////////////////////////////////////////////////////////////////////////
                    when (manga.status) {
                        AiredStatus.ANONS -> {
                            manga.dateAired?.let {
                                Text12Sp(
                                    text = DateUtils.getDateString(
                                        date = DateUtils.fromString(
                                            dateString = it
                                        )
                                    ),
                                    color = ShikidroidTheme.colors.onBackground
                                )
                                DotText12spDivider()
                            }
                        }
                        else -> {
                            (manga.dateReleased ?: manga.dateAired)?.let {
                                Text12Sp(
                                    text = DateUtils.getYearString(
                                        date = DateUtils.fromString(
                                            dateString = it
                                        )
                                    ),
                                    color = ShikidroidTheme.colors.onBackground
                                )
                                DotText12spDivider()
                            }
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Тип манги
                    ////////////////////////////////////////////////////////////////////////////////////
                    Text12Sp(
                        text = manga.type?.toScreenString().orEmpty(),
                        color = ShikidroidTheme.colors.onBackground
                    )

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Количество глав
                    ////////////////////////////////////////////////////////////////////////////////////
                    if (manga.status == AiredStatus.RELEASED) {
                        DotText12spDivider()
                        Text12Sp(
                            text = "${manga.chapters} гл.",
                            color = ShikidroidTheme.colors.onBackground
                        )
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////////
                // Строка с количеством просмотренных эпизодов и кнопкок Редактирования
                ////////////////////////////////////////////////////////////////////////////////////
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ////////////////////////////////////////////////////////////////////////////////////
                    // Количество просмотренных эпизодов
                    ////////////////////////////////////////////////////////////////////////////////////
                    Box(
                        modifier = Modifier
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if ((userChapters ?: 0) > 0) {
                            Text16Sp(
                                text = "$userChapters"
                            )
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////
                    // Кнопки редактирования и перехода на экран просмотра с плеером
                    ////////////////////////////////////////////////////////////////////////////////////

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        onEditClick?.let {
                            RoundedIconButton(
                                backgroundColor = ShikidroidTheme.colors.secondaryVariant,
                                onClick = {
                                    it()
                                },
                                icon = R.drawable.ic_edit_small,
                                tint = ShikidroidTheme.colors.secondary,
                                isIcon = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RateCardLoader() {
    Card(
        modifier = Modifier
            .padding(horizontal = fourteenDP, vertical = threeDP)
            .height(oneHundredFiftyDP)
            .fillMaxWidth(),
        backgroundColor = ShikidroidTheme.colors.background,
        elevation = ShikidroidTheme.elevation,
        border = BorderStroke(
            width = oneDP,
            color = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f)
        ),
        shape = ShikidroidTheme.shapes.roundedCorner7dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(fiftyDP),
                color = ShikidroidTheme.colors.secondary
            )
        }
    }
}