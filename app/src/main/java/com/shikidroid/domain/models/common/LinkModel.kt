package com.shikidroid.domain.models.common

/**
 * Модель со ссылками на ресурс произведения
 *
 * @property id id ссылки
 * @property name название ресурса (например myanimelist, wikipedia)
 * @property url ссылка на ресурс
 */
data class LinkModel(
    val id: Long,
    val name: String?,
    val url: String
)
