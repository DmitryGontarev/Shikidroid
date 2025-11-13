package com.shikidroid.data.local

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.shikidroid.appconstants.SettingsExtras
import com.shikidroid.appconstants.NetworkConstants
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.domain.models.user.UserBriefModel
import com.shikidroid.domain.repository.UserLocalRepository
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import javax.inject.Inject

/**
 * Реализация интерфейса [UserLocalRepository]
 */
internal class UserLocalRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferencesProvider,
    private val gson: Gson
): UserLocalRepository {

    override fun setUserId(id: Long) {
        prefs.putLong(key = SettingsExtras.USER_ID, long = id)
    }

    override fun getUserId(): Long {
        return prefs.getLong(key = SettingsExtras.USER_ID, default = NetworkConstants.NO_ID)
    }

    override fun setUserBrief(user: UserBriefModel) {
        prefs.putString(key = SettingsExtras.USER_BRIEF, s = gson.toJson(user))
    }

    override fun getUserBrief(): UserBriefModel? {
        return try {
            val json = prefs.getString(SettingsExtras.USER_BRIEF, "")
            gson.fromJson<UserBriefModel>(json, UserBriefModel::class.java)
        } catch (e: JsonSyntaxException) {
            throw IllegalStateException("User doesn't exist")
        }
    }

    override fun setUserAuthStatus(userAuthStatus: UserAuthStatus) {
        prefs.putString(SettingsExtras.USER_STATUS, userAuthStatus.name)
    }

    override fun getUserAuthStatus(): UserAuthStatus? {
        val value = prefs.getString(key = SettingsExtras.USER_STATUS, default = UserAuthStatus.GUEST.name)
        return value?.let {
            UserAuthStatus.valueOf(it)
        }
    }

    override fun clearUser() {
        prefs.remove(SettingsExtras.USER_ID)
        prefs.remove(SettingsExtras.USER_BRIEF)
        setUserAuthStatus(UserAuthStatus.GUEST)
    }
}