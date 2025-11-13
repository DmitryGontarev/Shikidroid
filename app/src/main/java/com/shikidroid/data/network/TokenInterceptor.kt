package com.shikidroid.data.network

import com.shikidroid.domain.repository.TokenLocalRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Перехватчик (Interceptor) для создания хэдера запроса токена
 */
internal class TokenInterceptor @Inject constructor(
    private val repository: TokenLocalRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()

        if (repository.isTokenExists()) {
            requestBuilder.addHeader(ACCESS_TOKEN_HEADER, String.format("Bearer %s", repository.getToken()?.accessToken))
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {

        private const val ACCESS_TOKEN_HEADER = "Authorization"
    }
}