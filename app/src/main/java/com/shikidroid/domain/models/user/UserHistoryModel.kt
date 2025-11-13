package com.shikidroid.domain.models.user

import com.shikidroid.domain.models.common.LinkedContentModel

/**
 * Модель с информацией о пользовательской истории действий
 *
 * @property id номера списка действий
 * @property dateCreated дата совершённого действия
 * @property description описание совершённого действия
 * @property target изменённый контент
 */
data class UserHistoryModel(
    val id : Long?,
    val dateCreated : String?,
    val description : String?,
    val target : LinkedContentModel?
)
