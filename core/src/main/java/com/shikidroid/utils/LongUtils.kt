package com.shikidroid.utils

import java.util.concurrent.TimeUnit

/**
 * Класс с функциями для типа Long
 */
object LongUtils {

    const val TIMER_PATTERN = "%02d:%02d"
    const val DAYS_PATTERN = "%2d"
    const val HOURS_MINUTES_PATTERN = "%2d ч %2d мин"
    const val HOURS_MINUTES_SECONDS_PATTERN = "%02d:%02d:%02d"
    const val MINUTES_PATTERN = "%2d мин"

    /**
     * Форматирует Long в строку с минутами - секундами
     *
     * @param pattern паттерн для форматирования строки
     */
    fun Long.formatMinSeconds(pattern: String = TIMER_PATTERN): String {
        return String.format(
            pattern,
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    }

    /**
     * Форматирует Long в строку с днями
     *
     * @param pattern паттерн для форматирования строки
     */
    fun Long.formatDays(pattern: String = DAYS_PATTERN): String {
        return if (this == 0L) {
            ""
        } else {
            String.format(
                pattern,
                TimeUnit.MILLISECONDS.toDays(this)
            ) + " " + DateUtils.getDayEndingString(days = TimeUnit.MILLISECONDS.toDays(this).toInt())
        }
    }

    /**
     * Форматирует Long в строку с часы - минуты
     *
     * @param pattern паттерн для форматирования строки
     */
    fun Long.formatHourMinutes(pattern: String = HOURS_MINUTES_PATTERN): String {
        return if (this == 0L) {
            ""
        } else {
            String.format(
                pattern,
                TimeUnit.MILLISECONDS.toHours(this),
                TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(this)
                )
            )
        }
    }

    /**
     * Форматирует Long в строку часы - минуты - секунды
     *
     * @param pattern паттерн для форматирования строки
     */
    fun Long.formatHourMinutesSeconds(pattern: String = HOURS_MINUTES_SECONDS_PATTERN): String {
        return String.format(
            pattern,
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        )
    }

    /**
     * Форматирует Long в строку с минутами
     *
     * @param pattern паттерн для форматирования строки
     */
    fun Long.formatMinutes(pattern: String = MINUTES_PATTERN): String {
        return if (this == 0L) {
            ""
        } else {
            String.format(
                pattern,
                TimeUnit.MILLISECONDS.toMinutes(this)
            )
        }
    }

    /**
     * Форматирует Long в строку с остатком времени дни/ часы - минуты / минуты
     */
    fun Long.getDiffTime(): String {
        return if (this == 0L) {
            ""
        } else {
            val days = TimeUnit.MILLISECONDS.toDays(this)
            val hours = TimeUnit.MILLISECONDS.toHours(this)
            when {
                days >= 1L -> {
                    this.formatDays()
                }
                hours >= 1L -> {
                    this.formatHourMinutes()
                }
                else -> {
                    this.formatMinutes()
                }
            }
        }
    }

    /** Форматирует Long в строку часы:минуты:секунды или минуты:секунды */
    fun Long.toVideoTime(): String {
        val hours = TimeUnit.MILLISECONDS.toHours(this)
        return when {
            hours >= 1L -> {
                this.formatHourMinutesSeconds()
            }
            else -> {
                this.formatMinSeconds()
            }
        }
    }

    /**
     * Конвертирует месяц календаря в строку
     */
    fun Long?.toMonthString(infinitive: Boolean = true): String {
        return when (this) {
            0L -> if (infinitive) "январь" else "января"
            1L -> if (infinitive) "февраль" else "февраля"
            2L -> if (infinitive) "март" else "марта"
            3L -> if (infinitive) "апрель" else "апреля"
            4L -> if (infinitive) "май" else "мая"
            5L -> if (infinitive) "июнь" else "июня"
            6L -> if (infinitive) "июль" else "июля"
            7L -> if (infinitive) "август" else "августа"
            8L -> if (infinitive) "сентябрь" else "сентября"
            9L -> if (infinitive) "октябрь" else "октября"
            10L -> if (infinitive) "ноябрь" else "ноября"
            11L -> if (infinitive) "декабрь" else "декабря"
            else -> ""
        }
    }
}