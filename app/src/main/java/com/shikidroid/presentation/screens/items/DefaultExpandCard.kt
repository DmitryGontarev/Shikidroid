package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.shikidroid.R
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.presentation.converters.toColor
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.ui.CHAPTERS_TEXT
import com.shikidroid.ui.EPISODES_TEXT
import com.shikidroid.ui.RELEASE_DATE_TEXT
import com.shikidroid.ui.VOLUMES_TEXT
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.components.ExpandableCard
import com.shikidroid.uikit.fourteenDP
import com.shikidroid.uikit.oneDP
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.threeDP
import com.shikidroid.utils.DateUtils

@Composable
internal fun DefaultExpandCard(
    modifier: Modifier = Modifier,
    useCard: Boolean = true,
    expanded: Boolean,
    mainContent: @Composable () -> Unit,
    status: AiredStatus?,
    score: Double?,
    episodes: Int? = null,
    episodesAired: Int? = null,
    chapters: Int? = null,
    volumes: Int? = null,
    dateAired: String?,
    dateReleased: String?,
    isMal: Boolean = false
) {
    ExpandableCard(
        modifier = modifier,
        useCard = useCard,
        expand = expanded,
        backgroundColor = ShikidroidTheme.colors.surface,
        elevation = ShikidroidTheme.elevation,
        borderWidth = oneDP,
        borderColor = ShikidroidTheme.colors.primaryVariant.copy(alpha = 0.15f),
        mainContent = {
            mainContent()
        }
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(sevenDP),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text12Sp(
                    text = status?.toScreenString().orEmpty(),
                    color = status?.toColor() ?: Color.White
                )
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = sevenDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        modifier = Modifier
                            .size(fourteenDP)
                            .padding(end = threeDP),
                        painter = painterResource(id = R.drawable.ic_mini_star),
                        tint = ShikidroidTheme.colors.secondary,
                        contentDescription = null
                    )
                    Text12Sp(text = score.toString())
                }
            }
            when (status) {
                AiredStatus.ANONS, AiredStatus.RELEASED, AiredStatus.PAUSED, AiredStatus.DISCONTINUED -> {
                    episodes?.let {
                        if (it > 0) {
                            LabelText(
                                text = "$it",
                                labelText = EPISODES_TEXT
                            )
                        }
                    }
                    chapters?.let {
                        if (it > 0) {
                            LabelText(
                                text = "$it",
                                labelText = CHAPTERS_TEXT
                            )
                        }
                    }
                    volumes?.let {
                        if (it > 0) {
                            LabelText(
                                text = "$it",
                                labelText = VOLUMES_TEXT
                            )
                        }
                    }
                }
                AiredStatus.ONGOING -> {
                    if (isMal) {
                        if (episodes != null) {
                            LabelText(
                                text = "$episodes",
                                labelText = EPISODES_TEXT
                            )
                        }
                    } else {
                        episodes?.let { ep ->
                            episodesAired?.let { epAir ->
                                LabelText(
                                    text = "$epAir из $ep",
                                    labelText = EPISODES_TEXT
                                )
                            }
                        }
                        chapters?.let {
                            if (it > 0) {
                                LabelText(
                                    text = "$it",
                                    labelText = CHAPTERS_TEXT
                                )
                            }
                        }
                        volumes?.let {
                            if (it > 0) {
                                LabelText(
                                    text = "$it",
                                    labelText = VOLUMES_TEXT
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
            when (status) {
                AiredStatus.ANONS, AiredStatus.ONGOING -> {
                    dateAired?.let {
                        LabelText(
                            text = DateUtils.getDatePeriodFromString(
                                dateStart = it
                            ),
                            labelText = RELEASE_DATE_TEXT
                        )
                    }
                }
                AiredStatus.RELEASED, AiredStatus.PAUSED, AiredStatus.DISCONTINUED -> {
                    LabelText(
                        text = DateUtils.getDatePeriodFromString(
                            dateStart = dateAired,
                            dateEnd = dateReleased
                        ),
                        labelText = RELEASE_DATE_TEXT
                    )
                }
                else -> {}
            }
        }
    }
}