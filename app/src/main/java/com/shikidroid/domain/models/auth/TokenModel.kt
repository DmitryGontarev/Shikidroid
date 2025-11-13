package com.shikidroid.domain.models.auth

/**
 * Модель токена domain слоя
 *
 * @property accessToken ключ доступа для взаимодействия с сервером
 * @property refreshToken ключ для обновления доступа
 */
data class TokenModel(
    val accessToken: String,
    val refreshToken: String
)
