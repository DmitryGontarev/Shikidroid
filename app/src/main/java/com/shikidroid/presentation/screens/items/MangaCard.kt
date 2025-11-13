package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.navigation.navigateDetailsScreen
import com.shikidroid.utils.DateUtils

@Composable
internal fun MangaCard(
    modifier: Modifier,
    manga: MangaModel,
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
                            id = manga.id,
                            detailsType =
                            if (manga.type == MangaType.LIGHT_NOVEL || manga.type == MangaType.NOVEL) {
                                DetailsScreenType.RANOBE
                            } else {
                                DetailsScreenType.MANGA
                            },
                            navigator = navigator
                        )
                    },
                columnTextModifier = Modifier
                    .clickable {
                        expanded = !expanded
                    },
                url = manga.image?.original,
                firstText = manga.nameRu.orEmpty(),
                secondTextOne = manga.type?.toScreenString(),
                secondTextTwo = DateUtils.getYearStringFromString(
                    dateString = manga.dateReleased ?: manga.dateAired
                )
            )
        },
        status = manga.status,
        score = manga.score,
        chapters = manga.chapters,
        volumes = manga.volumes,
        dateAired = manga.dateAired,
        dateReleased = manga.dateReleased
    )
}