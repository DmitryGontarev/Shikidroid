package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.auth.TokenModel
import com.shikidroid.domain.repository.TokenLocalRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

/**
 * Интерфейс для интерактора сохранения или получения токена доступа
 */
internal interface TokenLocalInteractor {

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

/**
 * Реализация интерактора [TokenLocalInteractor]
 *
 * @property tokenLocalRepository локальное хранилище токена
 */
internal class TokenLocalInteractorImpl @Inject constructor(
    private val tokenLocalRepository: TokenLocalRepository
) : TokenLocalInteractor {

    override fun saveToken(token: TokenModel?): Completable {
        return tokenLocalRepository.saveToken(token = token)
    }

    override fun getToken(): TokenModel? {
        return tokenLocalRepository.getToken()
    }

    override fun removeToken() {
        tokenLocalRepository.removeToken()
    }

    override fun isTokenExists(): Boolean {
        return tokenLocalRepository.isTokenExists()
    }
}