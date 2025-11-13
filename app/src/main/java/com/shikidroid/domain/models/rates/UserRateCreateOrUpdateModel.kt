package com.shikidroid.domain.models.rates

/**
 * Модель для создания или обновления элемента списка пользовательского рейтинга
 *
 * @property userRate модель элемента списка пользовательского рейтинга
 */
data class UserRateCreateOrUpdateModel(
    val userRate: UserRateModel
)