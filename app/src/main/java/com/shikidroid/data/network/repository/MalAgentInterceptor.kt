package com.shikidroid.data.network.repository

import com.shikidroid.appconstants.BaseUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Перехватчик (Interceptor) для создания хэдера запроса токена
 */
internal class MalAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()
        requestBuilder.addHeader(MY_ANIME_LIST_HEADER, BaseUrl.MY_ANIME_LIST_CLIENT_ID)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {

        const val MY_ANIME_LIST_HEADER = "X-MAL-CLIENT-ID"
    }
}