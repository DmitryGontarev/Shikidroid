package com.shikidroid.data.local

import com.shikidroid.appconstants.SettingsKeys
import com.shikidroid.domain.repository.SettingsRepository
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import javax.inject.Inject

internal class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferencesProvider
) : SettingsRepository {

    override var downloadFolder: String
        get() = prefs.getString(key = SettingsKeys.DOWNLOAD_FOLDER, default = "Movies") ?: "Movies"
        set(value) {
            prefs.putString(key = SettingsKeys.DOWNLOAD_FOLDER, value)
        }
}