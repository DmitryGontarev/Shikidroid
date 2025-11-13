package com.shikidroid.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread

/**
* Реализация [SharedPreferencesProvider]
 *
* @param context контекст [Context] приложения
* @param storageName название шаредпреф-контейнера. По умолчанию - имя глобальных шаредпрефов приложения
**/
open class SharedPreferencesProviderImpl @JvmOverloads constructor(
    context: Context,
    storageName: String = PREFERENCES_NAME
): SharedPreferencesProvider {

    protected var prefs: SharedPreferences = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)

    @WorkerThread
    override fun clear() {
        prefs
            .edit()
            .clear()
            .commit()
    }

    override fun remove(key: String) {
        prefs
            .edit()
            .remove(key)
            .apply()
    }

    override fun putString(key: String, s: String?) {
        prefs
            .edit()
            .putString(key, s)
            .apply()
    }

    override fun putInt(key: String, int: Int) {
        prefs
            .edit()
            .putInt(key, int)
            .apply()
    }

    override fun putLong(key: String, long: Long) {
        prefs
            .edit()
            .putLong(key, long)
            .apply()
    }

    override fun putBoolean(key: String, boolean: Boolean) {
        prefs
            .edit()
            .putBoolean(key, boolean)
            .apply()
    }

    override fun putFloat(key: String, float: Float) {
        prefs
            .edit()
            .putFloat(key, float)
            .apply()
    }

    override fun putStringSet(key: String, set: Set<String>) {
        prefs
            .edit()
            .putStringSet(key, set)
            .apply()
    }

    override fun getString(key: String, default: String?): String? =
        prefs.getString(key, default)

    override fun getInt(key: String, default: Int): Int =
        prefs.getInt(key, default)

    override fun getLong(key: String, default: Long): Long =
        prefs.getLong(key, default)

    override fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    override fun getFloat(key: String, default: Float): Float =
        prefs.getFloat(key, default)

    override fun getStringSet(key: String, default: Set<String>?): MutableSet<String>? =
        prefs.getStringSet(key, default)

    override fun contains(key: String): Boolean = prefs.contains(key)

    companion object {

        private const val PREFERENCES_NAME = "Shikidroid"
    }
}