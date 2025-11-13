package com.shikidroid.domain.models.roles

/**
 * Модель с информацией о дате, связанной с человеком
 *
 * @property day день
 * @property year год
 * @property month месяц
 */
data class RoleDateModel(
    val day: Long?,
    val year: Long?,
    val month: Long?
)
