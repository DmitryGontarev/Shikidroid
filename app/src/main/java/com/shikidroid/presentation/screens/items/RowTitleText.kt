package com.shikidroid.presentation.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.fourteenDP

/**
 * Текст для строки заголовка
 */
@Composable
internal fun RowTitleText(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(
        fourteenDP
    ),
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    textIcon: (@Composable () -> Unit)? = null,
    endIcons: (@Composable () -> Unit)? = null
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues = paddingValues)
    ) {
        val (title, icons) = createRefs()

        Row(
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = text,
                color = ShikidroidTheme.colors.onSurface,
                style = ShikidroidTheme.typography.bodySemiBold16sp,
                textAlign = textAlign
            )
            textIcon?.let {
                it()
            }
        }

        endIcons?.let {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(icons) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                it()
            }
        }
    }
}