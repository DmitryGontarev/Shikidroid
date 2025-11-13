package com.shikidroid.domain.repository

import com.shikidroid.domain.models.auth.TokenModel
import io.reactivex.rxjava3.core.Completable

/**
 * Интерфейс токен репозитория
 */
internal interface TokenLocalRepository {

    /**
     * сохранить токен
     */
    fun saveToken(token: TokenModel?): Completable

    /**
     * получить токен
     */
    fun getToken(): TokenModel?

    /**
     * удалить токен
     */
    fun removeToken()

    /**
     * проверить, сохранён ли токен в памяти приложения
     */
    fun isTokenExists(): Boolean

}