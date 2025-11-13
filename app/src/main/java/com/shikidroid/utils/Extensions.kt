package com.shikidroid.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shikidroid.appconstants.BaseUrl
import retrofit2.HttpException
import java.lang.reflect.Type

/**
 * Добавляет адрес хоста к ссылке, если его нет
 *
 * @param baseUrl базовый адрес сервера
 */
fun String.appendHost(baseUrl: String = BaseUrl.SHIKIMORI_BASE_URL): String {
    return if (this.contains("http")) this else baseUrl + this
}

/**
 * Убирает m3u8 из ссылки на видео часть для потоковой загрузки
 */
fun String.toDownloadLink(): String {
    return if (this.contains("m3u8")) {
        this.replaceAfterLast("mp4", "")
    } else {
        this
    }
}

/**
 * Формирует строку для показа в меню системы для передачи ссылки
 */
fun getEpisodeShareMessage(title: String, episode: Int, url: String): String {
    return "$episode эпизод\n$title \n\n$url"
}

/**
 * Сортировка списка по возрастанию/убыванию
 *
 * @param isAscend флаг в какую сторону сортировать список
 * @param selector коллбэк с возвратом текущего элемента списка
 */
fun <T, R : Comparable<R>> Iterable<T>.sortedAscendDescend(isAscend: Boolean = true, selector: (T) -> R?): List<T> {
    if (isAscend) {
        return this.sortedBy {
            selector(it)
        }
    } else {
        return this.sortedByDescending {
            selector(it)
        }
    }
}

/** Возвращает URL запроса при ошибке */
fun Throwable?.getHttpUrl(): String? {
    return if (this is HttpException) {
        this.response()?.raw()?.request?.url?.toUrl().toString()
    } else {
        null
    }
}

/** Возвращает статус код сервера при ошибке */
fun Throwable?.getHttpStatusCode(): Int? {
    return if (this is HttpException) {
        this.code()
    } else {
        null
    }
}

/**
 * Возвращает модель из строки JSON
 *
 * @param T тип дата класса, который нужно вернуть
 */
inline fun <reified T> Throwable?.getHttpErrorModel(): T? {
    return if (this is HttpException) {
        val jsonBodyError = this.response()?.errorBody()?.string()
        if (!jsonBodyError.isNullOrEmpty()) {
            return Gson().fromJson(jsonBodyError, T::class.java)
        } else {
            return null
        }
    } else {
        null
    }
}

/**
 * Возвращает модель из строки JSON
 *
 * @param T тип дата класса, который нужно вернуть
 */
inline fun <reified T> String?.getDataFromJson(): T? {
    if (this.isNullOrEmpty()) {
        return null
    } else {
        try {
            return Gson().fromJson(this, T::class.java)
        } catch (e: Exception) {
            return null
        }
    }
}

/**
 * Возвращает список моделей из строки JSON
 *
 * @param T тип дата класса в списке, который нужно вернуть
 */
inline fun <reified T> String?.getListFromJson(): List<T>? {
    if (this.isNullOrEmpty()) {
        return null
    } else {
        try {
            val type: Type? = TypeToken.getParameterized(List::class.java, T::class.java).type
            return Gson().fromJson(this, type)
        } catch (e: Exception) {
            return null
        }
    }
}