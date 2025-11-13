package com.shikidroid.data.network.entity.auth

import com.google.gson.annotations.SerializedName

/**
 * Сущность токена
 *
 * @property accessToken ключ доступа для взаимодействия с сервером
 * @property refreshToken ключ для обновления доступа
 */
data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
