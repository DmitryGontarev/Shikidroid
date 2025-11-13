package com.shikidroid.domain.models.common

/**
 * Модель с жанрами
 *
 * @property id жанра
 * @property name название жанра
 * @property nameRu название жанра на рускком
 * @property type тип жанра
 */
data class GenreModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val type: String?
)
