package com.shikidroid.utils

object IntUtils {

    fun Int.toEpisodeTime(): String {
        val hours = this / 60
        val minutes = this % 60
        return if (this <= 0) {
            ""
        } else {
            when {
                hours >= 1 -> {
                    "$hours ч $minutes мин"
                }
                else -> {
                    "$this мин"
                }
            }
        }
    }

    /**
     * Конвертирует день недели в строку
     */
    fun Int.toDayOfWeekString(): String {
        return when (this) {
            1 -> "Понедельник"
            2 -> "Вторник"
            3 -> "Среда"
            4 -> "Четверг"
            5 -> "Пятница"
            6 -> "Суббота"
            7 -> "Воскресенье"
            else -> ""
        }
    }
}