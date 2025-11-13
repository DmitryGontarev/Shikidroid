package com.shikidroid.data.local

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.domain.models.auth.TokenModel
import com.shikidroid.domain.repository.TokenLocalRepository
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

/**
 * Реализация [TokenLocalRepository]
 *
 * @param prefs хранилище [SharedPreferencesProvider]
 * @param gson сериализатор Gson
 */
internal class TokenLocalRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferencesProvider,
    private val gson: Gson
): TokenLocalRepository {

    private var token: TokenModel? = null

    override fun saveToken(token: TokenModel?): Completable {
        return Completable.fromAction {
            val json = gson.toJson(token)
            val value =
                if (json == "null") {
                    null
                } else {
                    json
                }
            prefs.putString(AppKeys.ARGUMENT_TOKEN, value)
        }
    }

    override fun getToken(): TokenModel? {
        return try {
            val json = prefs.getString(AppKeys.ARGUMENT_TOKEN, "")
            when (json.isNullOrEmpty().not()) {
                true -> gson.fromJson<TokenModel>(json, TokenModel::class.java)
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            return null
        }
    }

    override fun removeToken() {
        prefs.remove(AppKeys.ARGUMENT_TOKEN)
    }

    override fun isTokenExists(): Boolean {
        return prefs.contains(AppKeys.ARGUMENT_TOKEN)
    }
}