package com.shikidroid.sharedprefs

import android.content.SharedPreferences
import androidx.annotation.WorkerThread

/**
 * Обёртка над стандартным [SharedPreferences]
 * поддерживает все основные возможности по добавлени/удалению/поиску значений в локальном хранилище
 **/
interface SharedPreferencesProvider {

    /**
     * Очистить все сохраненные в [SharedPreferences] данные приложения
     * для лучшей производительности рекомендуется вызывать не из главного потока
     */
    @WorkerThread
    fun clear()

    /**
     * Удаляем запрашиваемый ключ из хранилища
     * @param key ключ который хотим удалить
     */
    fun remove(key: String)

    /**
     * Помещает строку в хранилище
     * @param key ключ для сохранения
     * @param s строка которую сохраняем
     */
    fun putString(key: String, s: String?)

    /**
     * Помещает [Int] в хранилище
     * @param key ключ для сохранения
     * @param int значение для сохранения
     */
    fun putInt(key: String, int: Int)

    /**
     * Помещает [Long] в хранилище
     * @param key ключ для сохранения
     * @param long значение для сохранения
     */
    fun putLong(key: String, long: Long)

    /**
     * Помещает [Boolean] в хранилище
     * @param key ключ для сохранения
     * @param boolean значение для сохранения
     */
    fun putBoolean(key: String, boolean: Boolean)

    /**
     * Помещает [Float] в хранилище
     * @param key ключ для сохранения
     * @param float значение для сохранения
     */
    fun putFloat(key: String, float: Float)

    /**
     * Помещает [Set] из [String] в хранилище
     * @param key ключ для сохранения
     * @param set значение для сохранения
     */
    fun putStringSet(key: String, set: Set<String>)

    /**
     * Получить значение [String] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено(по умолчанию "")
     */
    fun getString(key: String, default: String? = ""): String?

    /**
     * Получить значение [Int] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено(по умолчанию 0)
     */
    fun getInt(key: String, default: Int = 0): Int

    /**
     * Получить значение [Long] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено(по умолчанию 0)
     */
    fun getLong(key: String, default: Long = 0L): Long

    /**
     * Получить значение [Boolean] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено(по умолчанию false)
     */
    fun getBoolean(key: String, default: Boolean = false): Boolean

    /**
     * Получить значение [Float] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено(по умолчанию 0)
     */
    fun getFloat(key: String, default: Float = 0F): Float

    /**
     * Получить значение [Set] [String] из хранилища
     * @param key ключ по которому ищем значение
     * @param default дефолтное значение, возвращаемое в случае если по ключу ничего не найдено
     */
    fun getStringSet(key: String, default: Set<String>?): Set<String>?

    /**
     * Проверяет наличие настройки в хранилище SharedPref
     *
     * @param key название настройки в хранилище SharedPref
     *
     * @return флаг наличия настройки в хранилище SharedPref
     */
    fun contains(key: String): Boolean
}