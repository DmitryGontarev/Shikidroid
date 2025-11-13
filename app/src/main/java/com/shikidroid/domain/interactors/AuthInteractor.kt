package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.auth.TokenModel
import com.shikidroid.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора авторизации
 */
internal interface AuthInteractor {

    /**
     * Метод для получения токена по коду авторизации
     *
     * @param authCode код авторизации, который пришёл с сайта
     */
    fun signIn(authCode: String): Single<TokenModel>

    /**
     * Метод для обновления токена доступа
     *
     * @param refreshToken токен для обновления токена
     */
    fun refreshToken(refreshToken: String): Single<TokenModel>
}

/**
 * Реализация интерактора авторизации [AuthInteractor]
 *
 * @property authRepository репозиторий авторизации
 */
internal class AuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository
) : AuthInteractor {

    override fun signIn(authCode: String): Single<TokenModel> {
        return authRepository.signIn(authCode = authCode)
    }

    override fun refreshToken(refreshToken: String): Single<TokenModel> {
        return authRepository.refreshToken(refreshToken = refreshToken)
    }

}