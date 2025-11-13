package com.shikidroid.domain.models.studio

/**
 * Модель с информацией о студии аниме
 *
 * @property id номер студии
 * @property name название
 * @property nameFiltered
 * @property isReal
 * @property imageUrl ссылка на логотип студии
 */
data class StudioModel(
    val id: Long?,
    val name: String?,
    val nameFiltered: String?,
    val isReal: Boolean?,
    val imageUrl: String?
)
