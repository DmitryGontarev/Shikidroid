package com.shikidroid.utils

import androidx.lifecycle.MutableLiveData

/**
 * Позволяет вернуть non null значение лайв даты, либо указанное значение
 *
 * @param defaultValue значение по умолчанию, если значение лайв даты null
 */
fun <T : Any> MutableLiveData<T>?.svalue(defaultValue: T) = this?.value ?: defaultValue

/**
 * Возвращает значение LiveData<Boolean>, либо false, если оно null
 */
fun MutableLiveData<Boolean>?.svalue(): Boolean = svalue(false)

/**
 * Возвращает значение LiveData<String>, либо "", если оно null
 */
fun MutableLiveData<String>?.svalue(): String = svalue("")

/**
 * Возращает значение LiveData<Int>, либо 0, если оно null
 */
fun MutableLiveData<Int>?.svalue(): Int = svalue(0)

/**
 * Возращает значение LiveData<Float>, либо 0, если оно null
 */
fun MutableLiveData<Float>?.svalue(): Float = svalue(0F)