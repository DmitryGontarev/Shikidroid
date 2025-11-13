package com.shikidroid.uikit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Функция расширение для вставки сетки с фиксированным количеством элементов в [LazyColumn]
 * так как в [LazyColumn] нельзя использовать другие вертикально прокручиваемые списки,
 * то для сетки типа [LazyVerticalGrid] использовать эту функцию
 *
 * @param data список элементов
 * @param columnCount количество столбцов в сетке
 * @param modifier модификатор
 * @param horizontalArrangement горизонтальное выравнивание
 * @param itemContent содержимое элемента сетки
 */
fun <T> LazyListScope.gridItems(
    data: List<T>,
    columnCount: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(item: T) -> Unit
) {
    /** количество элементов в списке */
    val size = data.count()

    /** расчёт количества нужных строк */
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount

    items(rows) { rowIndex ->
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement
        ) {
            for (columnIndex in 0 until columnCount) {
                // расчёт индекса элемента
                val itemIndex = rowIndex * columnCount + columnIndex

                if (itemIndex < size) {
                    Box(
                        modifier = Modifier
                            .weight(weight = 1F, fill = true),
                        propagateMinConstraints = true
                    ) {
                        itemContent(data[itemIndex])
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(weight = 1F, fill = true)
                    )
                }
            }
        }
    }
}

/**
 * Функция расширение для вставки сетки с фиксированным количеством элементов в [ColumnScope]
 * так как в [ColumnScope] нельзя использовать другие вертикально прокручиваемые списки,
 * то для сетки типа [LazyVerticalGrid] использовать эту функцию
 *
 * @param data список элементов
 * @param columnCount количество столбцов в сетке
 * @param modifier модификатор
 * @param horizontalArrangement горизонтальное выравнивание
 * @param itemContent содержимое элемента сетки
 */
@Composable
fun <T> RowForColumnScope(
    columnScope: ColumnScope,
    data: List<T>,
    columnCount: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(item: T) -> Unit
) {
    /** количество элементов в списке */
    val size = data.count()

    /** расчёт количества нужных строк */
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount

    columnScope.apply {
        for (rowIndex in 0..rows) {
            Row(
                modifier = modifier,
                horizontalArrangement = horizontalArrangement
            ) {
                for (columnIndex in 0 until columnCount) {
                    // расчёт индекса элемента
                    val itemIndex = rowIndex * columnCount + columnIndex

                    if (itemIndex < size) {
                        Box(
                            modifier = Modifier
                                .weight(weight = 1F, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent(data[itemIndex])
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                        )
                    }
                }
            }
        }
    }
}