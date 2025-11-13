package com.shikidroid.uikit

import androidx.navigation.NavHostController

/**
 * Сохранить данные для бэкстека
 *
 * @param key ключ в словаре
 * @param value данные для сохранения
 */
fun <T> NavHostController.setData(key: String, value: T) {
    this.currentBackStackEntry?.savedStateHandle?.set(
        key = key,
        value = value
    )
}

/**
 * Получить данные из бэкстека
 *
 * @param key ключ в словаре
 */
fun NavHostController.getData(key: String): Any? {
    return this.previousBackStackEntry?.savedStateHandle?.get(
        key = key
    )
}

/**
 * Удалить данные из бэкстека
 *
 * @param key ключ в словаре
 */
fun <T> NavHostController.removeData(key: String) {
    this.currentBackStackEntry?.savedStateHandle?.remove<T>(
        key = key
    )
    this.previousBackStackEntry?.savedStateHandle?.remove<T>(
        key = key
    )
}