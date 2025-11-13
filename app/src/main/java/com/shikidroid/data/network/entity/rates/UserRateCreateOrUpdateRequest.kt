package com.shikidroid.data.network.entity.rates

import com.google.gson.annotations.SerializedName

/**
 * Сущность для создания или обновления элемента списка пользовательского рейтинга
 *
 * @property userRate сущность элемента списка пользовательского рейтинга
 */
data class UserRateCreateOrUpdateRequest(
    @SerializedName("user_rate")
    val userRate: UserRateResponse
)
