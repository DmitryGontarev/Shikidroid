package com.shikidroid.domain.repository

import com.shikidroid.domain.models.auth.TokenModel
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория авторизации
 */
internal interface AuthRepository {

    /**
     * Метод для получения токена доступа (Access Token)
     *
     * @param authCode код авторизации, полученный с сайта
     *
     * @return [TokenModel] модель токена
     */
    fun signIn(authCode: String): Single<TokenModel>

    /**
     * Метод для получения нового токена доступа и токена обновления
     *
     * @param refreshToken токен для обновления доступа
     */
    fun refreshToken(refreshToken: String): Single<TokenModel>

}