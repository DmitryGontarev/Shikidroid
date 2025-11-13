package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.navigateDetailsAnimeScreen
import com.shikidroid.utils.DateUtils

@Composable
internal fun AnimeCard(
    modifier: Modifier,
    anime: AnimeModel,
    firstText: String? = null,
    secondTextOne: String? = null,
    secondTextTwo: String? = null,
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
                            id = anime.id,
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = anime.image?.original,
                firstText = firstText ?: anime.nameRu.orEmpty(),
                secondTextOne = secondTextOne ?: "${anime.type?.toScreenString()}",
                secondTextTwo = secondTextTwo ?: DateUtils.getYearStringFromString(
                    dateString = anime.dateReleased ?: anime.dateAired
                )
            )
        },
        status = anime.status,
        score = anime.score,
        episodes = anime.episodes,
        episodesAired = anime.episodesAired,
        dateAired = anime.dateAired,
        dateReleased = anime.dateReleased
    )
}

@Composable
internal fun AnimeMalCard(
    modifier: Modifier,
    anime: AnimeMalModel,
    firstText: String? = null,
    secondTextOne: String? = null,
    secondTextTwo: String? = null,
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
                            id = anime.id,
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = anime.image?.large,
                firstText = firstText ?: anime.title.orEmpty(),
                secondTextOne = secondTextOne,
                secondTextTwo = secondTextTwo ?: DateUtils.getYearStringFromString(
                    dateString = anime.dateReleased ?: anime.dateAired
                )
            )
        },
        status = anime.status,
        score = anime.score,
        episodes = anime.episodes,
        dateAired = anime.dateAired,
        dateReleased = anime.dateReleased,
        isMal = true
    )
}