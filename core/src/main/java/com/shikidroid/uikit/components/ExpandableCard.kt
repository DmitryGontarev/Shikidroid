package com.shikidroid.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shikidroid.uikit.RoundedCorner7dp

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    expand: Boolean = false,
    useCard: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    shape: Shape = RoundedCorner7dp,
    elevation: Dp = 0.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    mainContent: @Composable () -> Unit,
    underContent: (@Composable () -> Unit)? = null
) {

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    var height by remember {
        mutableStateOf(0.dp)
    }

    val offset : Dp by animateDpAsState(
        targetValue = if (expand) (height.value / 1.7).dp else 0.dp,
        animationSpec = tween(
            durationMillis = 700
        )
    )

    if (useCard) {
        Card(
            modifier = modifier
                .wrapContentSize(),
            shape = shape,
            backgroundColor = backgroundColor,
            border = BorderStroke(
                width = borderWidth,
                color = borderColor
            ),
            elevation = elevation
        ) {
//        Box(
//            modifier = Modifier
//                .onGloballyPositioned {
//                    height = with(localDensity) {
//                        it.size.width.toDp()
//                    }
//                }
//        ) {
//            mainContent()
//
//            Box(
//                modifier = Modifier
//                    .offset(x = offset)
//            ) {
//                underContent()
//            }
//        }
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                mainContent()

                underContent?.let {
                    AnimatedVisibility(
                        visible = expand
                    ) {
                        it()
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            mainContent()

            underContent?.let {
                AnimatedVisibility(
                    visible = expand
                ) {
                    it()
                }
            }
        }
    }
}