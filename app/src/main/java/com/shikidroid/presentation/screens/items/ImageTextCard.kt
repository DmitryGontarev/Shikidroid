package com.shikidroid.presentation.screens.items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.shikidroid.R
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.presentation.converters.toColor
import com.shikidroid.presentation.converters.toDrawable
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.AbsoluteRounded50dp
import com.shikidroid.uikit.components.AsyncImageLoader
import com.shikidroid.uikit.components.DotTextDivider
import com.shikidroid.uikit.oneDP
import com.shikidroid.uikit.sevenDP
import com.shikidroid.uikit.standardImageHeight
import com.shikidroid.uikit.standardImageWidth
import com.shikidroid.uikit.tenDP
import com.shikidroid.uikit.thirtyFiveDP
import com.shikidroid.uikit.threeDP

/**
 * Картинка с двумя или одной строкой текста под ней
 *
 *
 */
@Composable
internal fun ImageTextCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    columnTextModifier: Modifier = Modifier,
    url: String?,
    height: Dp = standardImageHeight,
    width: Dp = standardImageWidth,
    overPicture: (@Composable () -> Unit)? = null,
    firstText: String? = null,
    secondTextOne: String? = null,
    secondTextTwo: String? = null
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .width(width),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageWithOverPicture(
            modifier = imageModifier,
            url = url,
            height = height,
            width = width,
            overPicture = overPicture
        )
        Column(
            modifier = columnTextModifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (firstText.isNullOrEmpty().not()) {
                Text(
                    modifier = Modifier
                        .padding(top = threeDP),
                    text = firstText.orEmpty(),
                    color = ShikidroidTheme.colors.onPrimary,
                    style = ShikidroidTheme.typography.body12sp,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            if (secondTextOne.isNullOrEmpty().not() || secondTextTwo.isNullOrEmpty().not()) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = oneDP),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (secondTextOne.isNullOrEmpty().not()) {
                        Text(
                            text = secondTextOne.orEmpty(),
                            color = ShikidroidTheme.colors.onPrimary,
                            style = ShikidroidTheme.typography.body12sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                    if (secondTextOne.isNullOrEmpty().not() && secondTextTwo.isNullOrEmpty()
                            .not()
                    ) {
                        DotTextDivider(
                            style = ShikidroidTheme.typography.body12sp,
                            color = ShikidroidTheme.colors.onBackground
                        )
                    }
                    if (secondTextTwo.isNullOrEmpty().not()) {
                        Text(
                            text = secondTextTwo.orEmpty(),
                            color = ShikidroidTheme.colors.onBackground,
                            style = ShikidroidTheme.typography.body12sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ImageWithOverPicture(
    modifier: Modifier = Modifier,
    url: String?,
    height: Dp = standardImageHeight,
    width: Dp = standardImageWidth,
    overPicture: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .width(width = width)
            .height(height = height)
            .clip(shape = ShikidroidTheme.shapes.roundedCorner7dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImageLoader(url = url)
        overPicture?.let {
            it()
        }
    }
}

@Composable
internal fun OverPictureOne(
    textLeftTopCorner: String? = null,
    textRightBottomCorner: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        textLeftTopCorner?.let {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .align(alignment = Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomEnd7dp
                    )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(threeDP),
                    style = ShikidroidTheme.typography.body12sp,
                    color = Color.White
                )
            }
        }

        textRightBottomCorner?.let {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .align(alignment = Alignment.BottomEnd)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomEnd7dp
                    )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(threeDP),
                    style = ShikidroidTheme.typography.body12sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
internal fun OverPictureTwo(
    expanded: Boolean = false,
    textLeftTopCorner: String? = null,
    textBottomCenter: String? = null,
    showIcon: Boolean = true,
    onClick: () -> Unit
) {

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        textLeftTopCorner?.let {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .align(alignment = Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomEnd7dp
                    )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(threeDP),
                    style = ShikidroidTheme.typography.body12sp,
                    color = Color.White
                )
            }
        }

        textBottomCenter?.let {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = ShikidroidTheme.shapes.roundedCorner7dp
                    )
                    .clickable {
                        onClick()
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(threeDP),
                    style = ShikidroidTheme.typography.body12sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if (showIcon) {
                    Icon(
                        modifier = Modifier
                            .size(tenDP)
                            .rotate(rotationState),
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
internal fun CalendarOverPicture(
    textLeftTopCorner: String? = null,
    rateStatusTopCorner: RateStatus? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        textLeftTopCorner?.let {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .align(alignment = Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = ShikidroidTheme.shapes.roundedCornerTopStartBottomEnd7dp
                    )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(threeDP),
                    style = ShikidroidTheme.typography.body12sp,
                    color = Color.White
                )
            }
        }

        rateStatusTopCorner?.let {
            Box(
                modifier = Modifier
                    .padding(all = sevenDP)
                    .size(size = thirtyFiveDP)
                    .clip(shape = AbsoluteRounded50dp)
                    .background(color = Color.Black.copy(alpha = 0.6f))
                    .align(alignment = Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = rateStatusTopCorner.toDrawable()),
                    tint = rateStatusTopCorner.toColor(),
                    contentDescription = null
                )
            }
        }
    }
}