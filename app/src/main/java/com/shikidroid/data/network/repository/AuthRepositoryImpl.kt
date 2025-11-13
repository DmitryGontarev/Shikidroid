package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.AuthApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.domain.models.auth.TokenModel
import com.shikidroid.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [AuthRepository]
 *
 */
internal class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override fun signIn(authCode: String): Single<TokenModel> {
        return authApi.getAccessToken(grantType = AUTH_CODE, code = authCode)
            .map { tokenEntity ->
                tokenEntity.toDomainModel()
            }
    }

    override fun refreshToken(refreshToken: String): Single<TokenModel> {
        return authApi.getAccessToken(
            grantType = REFRESH_TOKEN, refreshToken = refreshToken
        ).map { tokenEntity ->
            tokenEntity.toDomainModel()
        }
    }

    companion object {

        private const val AUTH_CODE = "authorization_code"
        private const val REFRESH_TOKEN = "refresh_token"
    }
}