package com.shikidroid.utils

import java.util.*

/**
 * Метод сравнивает даты, и если совпадает возвращает true иначе false
 * @param comparedDate - дата с которой сравнивается текущая
 */
internal fun Date.isTheSameDay(comparedDate: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.withTime(this)
    val comparedCalendarDate = Calendar.getInstance()
    comparedCalendarDate.withTime(comparedDate)
    return calendar.get(Calendar.DAY_OF_YEAR) == comparedCalendarDate.get(Calendar.DAY_OF_YEAR) && calendar.get(
        Calendar.YEAR
    ) == comparedCalendarDate.get(Calendar.YEAR)
}

/**
 * Устанавливает дату в календарь и Обнуляет время
 * @param date - дата которая устанавливается в календарь
 */
fun Calendar.withTime(date: Date): Calendar {
    clear()
    time = date
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

/**
 * Устанавливает дату в календарь и выставляет максимальное время
 * @param date - дата которая устанавливается в календарь
 */
fun Calendar.endDayTime(date: Date): Calendar {
    clear()
    time = date
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
    return this
}

/**
 * Устанавливает дату в календарь и вычитает из нее переданное количество лет
 * @param yearCount - количество лет
 * @param date - дата которая устанавливается в календарь
 */
fun Calendar.minusYears(date: Date, yearCount: Int): Calendar {
    clear()
    time = date
    set(Calendar.YEAR, get(Calendar.YEAR) - yearCount)
    return this
}

/**
 * Устанавливает календарь на начало месяца
 */
fun Calendar.startMonth() = apply {
    set(Calendar.DATE, 1)
}

/**
 * Конвертирует месяц календаря в строку
 */
fun Calendar.toMonthString(infinitive: Boolean = true): String {
    return when (this.get(Calendar.MONTH)) {
        0 -> if (infinitive) "январь" else "января"
        1 -> if (infinitive) "февраль" else "февраля"
        2 -> if (infinitive) "март" else "марта"
        3 -> if (infinitive) "апрель" else "апреля"
        4 -> if (infinitive) "май" else "мая"
        5 -> if (infinitive) "июнь" else "июня"
        6 -> if (infinitive) "июль" else "июля"
        7 -> if (infinitive) "август" else "августа"
        8 -> if (infinitive) "сентябрь" else "сентября"
        9 -> if (infinitive) "октябрь" else "октября"
        10 -> if (infinitive) "ноябрь" else "ноября"
        11 -> if (infinitive) "декабрь" else "декабря"
        else -> ""
    }
}

/**
 * Конвертирует день недели календаря в строку
 */
fun Calendar.toDayOfWeekString(): String {
    return when (this.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Понедельник"
        Calendar.TUESDAY -> "Вторник"
        Calendar.WEDNESDAY -> "Среда"
        Calendar.THURSDAY -> "Четверг"
        Calendar.FRIDAY -> "Пятница"
        Calendar.SATURDAY -> "Суббота"
        Calendar.SUNDAY -> "Воскресенье"
        else -> ""
    }
}

/**
 * Конвертирует день недели календаря в порядковый номер дня недели
 */
fun Calendar.toDayOfWeekInt(): Int {
    return when (this.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 0
    }
}

/**
 * Конвертирует календарь в строку часы:минуты
 */
fun Calendar.toHourMinutes(): String {
    var string = ""
    val hours = this.get(Calendar.HOUR_OF_DAY)
    val minute = this.get(Calendar.MINUTE)
    string = String.format(
        "%02d:%02d", hours, minute
    )
    return string
}


/**
 * Метод устанавливает время переданной даты ровно на 12 часов дня
 *
 * @return дата и её время 12:00:00:000
 */
fun Date.toMidday(): Date {
    val c = Calendar.getInstance().withTime(this)
    c.set(Calendar.HOUR_OF_DAY, 12)
    return c.time
}

/**
 * Метод устанавливает время даты на 00:00:00:000
 *
 * @return дата и её время 00:00:00:000
 */
fun Date.toStartDay(): Date {
    return Calendar.getInstance().withTime(this).time
}