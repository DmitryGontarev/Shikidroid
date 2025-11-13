package com.shikidroid.data.network.api

import com.shikidroid.appconstants.BaseUrl
import com.shikidroid.data.network.entity.auth.TokenResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Интерфейс API для авторизации
 *
 */
internal interface AuthApi {

    /**
     * Метод для получения токена доступа и токена обновления
     *
     * @param grantType тип запроса - код авторизации или обновление токена доступа
     * @param clientId идентификационный номер клиента
     * @param clientSecret секретный ключ приложения
     * @param code код
     * @param redirectUri ссылка для перенаправления
     * @param refreshToken токен для обновления токена доступа
     */
    @POST("/oauth/token")
    fun getAccessToken(
        @Query("grant_type")
        grantType: String,
        @Query("client_id")
        clientId: String = BaseUrl.CLIENT_ID,
        @Query("client_secret")
        clientSecret: String = BaseUrl.CLIENT_SECRET,
        @Query("code")
        code: String? = null,
        @Query("redirect_uri")
        redirectUri: String? = BaseUrl.REDIRECT_URI,
        @Query("refresh_token")
        refreshToken: String? = null
    ): Single<TokenResponse>
}