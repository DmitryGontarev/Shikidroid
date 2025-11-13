package com.shikidroid.paginator

/**
 *
 */
data class ScreenState<T>(
    val isLoading: Boolean = false,
    val items: List<T> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)