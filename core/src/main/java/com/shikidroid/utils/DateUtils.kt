package com.shikidroid.utils

import android.util.Log
import com.shikidroid.utils.LongUtils.getDiffTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DateUtils"

const val SDF_STANDARD_FULL = "dd.MM.yyyy HH:mm:ss"
const val SDF_STANDARD_SHORT_WITH_DASHES = "dd-MM-yyyy"
const val SDF_DAY_MONTH_ONLY = "dd MMMM"
const val SDF_DATE_WITH_FULL_MONTH = "d MMMM yyyy"

/**
 * Формат даты "dd.MM", например "05.04"
 */
const val SDF_NUM_SHORT_DAY_MONTH = "dd.MM"

/**
 * Формат даты "dd.MM.yyyy", например "05.04.2021"
 */
const val SDF_NUM_FULL_DAY_MONTH_YEAR = "dd.MM.yyyy"

/**
 * Формат даты только с названием месяца, например "апрель"
 */
const val SDF_MONTH_ONLY = "LLLL"

/**
 * Класс utils для форматирования Date в нужный формат.
 *
 * @author Ivan Lauhin
 */
object DateUtils {

    const val SDF_COMMA_SEPARATED_FULL_DATE_FORMAT = "d MMMM yyyy, HH:mm"
    const val SDF_COMMA_SEPARATED_FULL_DATE_FORMAT_NUM = "d.MM.yyyy, HH:mm"
    const val SDF_TIME_FORMAT = "HH:mm"

    const val UTC_TIMEZONE = "UTC"
    const val YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss"
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val D_MM_YYYY = "d.MM.yyyy"
    const val D_MMM_YYYY = "d MMM yyyy"
    const val DD_MM_YYYY = "dd.MM.yyyy"
    const val D_MM_YYYY_HH_MM_SS = "d MMM yyyy HH:mm"
    const val MMM_DD_YYYY_HH_MM_SS_AAA = "MMM dd, yyyy HH:mm:ss aaa"
    const val MMM_DD_YYYY_HH_MM_SS = "MMM dd, yyyy HH:mm:ss"
    const val EEE_DD_MMM_YYYY_HH_MM_SS_Z = "EEE, dd MMM yyyy HH:mm:ss z"
    const val EEE_DD_MMM_YYYY_HH_MM_SS_Z1 = "EEE, dd MMM yyyy HH:mm:ss Z"
    const val EEEE_D_MMMM = "EEEE, d MMMM"

    /**
     * Формат даты "d MMMM", например "2 января"
     */
    const val SDF_SHORT_DAY_MONTH_ONLY = "d MMMM"

    /**
     * Формат даты и времени, например 2020-12-28T00:00:00
     */
    const val SDF_DATETIME_WITH_DASHES_INVERTED = "yyyy-MM-dd'T'HH:mm:ss"

    /**
     * Формат даты и времени, например 2020-12-28T00:00:00Z
     */
    const val SDF_DATETIME_WITH_DASHES_INVERTED_WITH_ZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    /**
     * Форматирование Date по паттерну
     *
     * @param date Дата
     * @param pattern паттерн даты
     *
     * @return отформатированную дату
     */
    fun formatDateByPattern(date: Date?, pattern: String): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return date?.let { format.format(it) }.orEmpty()
    }

    /**
     * Форматирование времени в миллисекундах по паттерну
     *
     * @param millis время в иллисекундах
     * @param @param pattern паттерн даты
     *
     * @return отформатированную дату
     */
    fun formatMillisByPattern(millis: Long, pattern: String): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return format.format(calendar.time)
    }

    /**
     * Форматирует дату приходящую в виде строки.
     * - для выполнения `parse` использует [SimpleDateFormat] в patterns
     * - для выполнения `format` использует [SimpleDateFormat] в outputDateFormat
     *
     * @param value значение даты в виде строки
     * @param patterns список возможных паттернов
     * @param outputDateFormat паттерн для выходной даты
     * @return строку с датой отформатированной по правилу [DATE_FORMAT_FOR_MODEL],
     * в случае невозможности выполнения `parse` вернет это значение `как есть`
     */
    fun tryToFormatStringValue(
        value: String,
        patterns: List<SimpleDateFormat>,
        outputDateFormat: SimpleDateFormat
    ): String {
        patterns.forEach { pattern ->
            try {
                return outputDateFormat.format(pattern.parse(value))
            } catch (e: ParseException) {
                Log.d(TAG, "$e")
            }
        }

        return value
    }

    /**
     * Парсит строку в формате переданного [pattern]
     *
     * @param value значение даты в виде строки
     * @param pattern [SimpleDateFormat] возможный паттерн парсинга даты
     *
     * @return дата или null, если не поддерживает [pattern]
     */
    fun parse(value: String, pattern: SimpleDateFormat): Date? =
        runCatching { pattern.parse(value) }
            .onFailure { Log.d(TAG, "$it") }
            .getOrNull()

    /**
     * Метод для преобразования строки в дату по первому подходящему шаблону и локали
     * Если формат даты не поддерживается, вернется null
     *
     * @param dateString строка в отформатированом виде
     * @return Объект класса Date Timezone для даты UTC
     */
    fun fromString(dateString: String?, timeZone: TimeZone = TimeZone.getDefault()): Date? {
        if (dateString.isNullOrBlank()) return null
        val dateFormats = arrayOf(
            YYYY_MM_DD_T_HH_MM_SS,
            YYYY_MM_DD_HH_MM_SS,
            YYYY_MM_DD,
            DD_MM_YYYY,
            MMM_DD_YYYY_HH_MM_SS_AAA,
            MMM_DD_YYYY_HH_MM_SS,
            EEE_DD_MMM_YYYY_HH_MM_SS_Z,
            EEE_DD_MMM_YYYY_HH_MM_SS_Z1
        )
        val locales = arrayOf(
            Locale.ENGLISH,
            Locale.getDefault()
        )
        var date: Date? = null
        for (locale in locales) {
            for (dateFormat in dateFormats) {
                date = try {
                    val format = SimpleDateFormat(dateFormat, locale)
                        .also { it.timeZone = timeZone }
                    format.parse(dateString)
                } catch (e: ParseException) {
                    Log.d(TAG, "can't convert date $dateString")
                    null
                }
                if (date != null) return date
            }
        }
        return date
    }

    /**
     * Метод возвращает строку вида "17 сен 2022" / "17 сен - 20 окт 2022" / "17 сен 2022 - 20 окт 2023",
     * если переданы две даты
     *
     * @param dateStart дата начала периода
     * @param dateEnd дата конца периода
     * @param isNextLine переносить ли на следующую строку
     */
    fun getDatePeriod(
        dateStart: Date?,
        dateEnd: Date? = null,
        isNextLine: Boolean = false
    ): String {
        var periodString: String = ""
        val periodList = mutableListOf<String>()
        if (dateStart != null) {
            val calendarDateStart = Calendar.getInstance().withTime(dateStart)
            val date = calendarDateStart.get(Calendar.DAY_OF_MONTH).toString()
            val month = calendarDateStart.toMonthString(calendarDateStart.get(Calendar.MONTH) != 4)
                .substring(0..2)
            val year = calendarDateStart.get(Calendar.YEAR).toString()
            periodList.add(date)
            periodList.add(" $month")
            periodList.add(" $year")
        }
        if (dateEnd != null) {
            val calendarDateEnd = Calendar.getInstance().withTime(dateEnd)
            val date = calendarDateEnd.get(Calendar.DAY_OF_MONTH).toString()
            val month = calendarDateEnd.toMonthString().substring(0..2)
            val year = calendarDateEnd.get(Calendar.YEAR).toString()
            periodList.forEach {
                if (it == " $year") {
                    periodList.remove(it)
                }
            }
            periodList.add(if (isNextLine) " -\n" else " -")
            periodList.add(if (isNextLine) date else " $date")
            periodList.add(" $month")
            periodList.add(" $year")
        }
        periodString = periodList.joinToString("")
        return periodString
    }

    /**
     * Обёртка над [getDatePeriod]
     * возвращает строку с датами сразу из строк
     *
     * @param dateStart дата начала периода
     * @param dateEnd дата конца периода
     * @param isNextLine переносить ли на следующую строку
     */
    fun getDatePeriodFromString(
        dateStart: String?,
        dateEnd: String? = null,
        isNextLine: Boolean = false
    ): String {
        val start = fromString(dateString = dateStart)
        val end = fromString(dateString = dateEnd)
        return getDatePeriod(
            dateStart = start,
            dateEnd = end,
            isNextLine = isNextLine
        )
    }

    /**
     * Метод возвращает строку для подстановки к количеству дней
     *
     * @param days количество дней
     */
    fun getDayEndingString(days: Int): String {
        var ending: String = ""
        when {
            days % 10 == 1 && days % 100 != 11 -> {
                ending = "день"
            }
            days % 10 in 2..4 && days !in 11..14 -> {
                ending = "дня"
            }
            else -> {
                ending = "дней"
            }
        }
        return ending
    }

    /**
     * Метод возвращает строку вида "22 мин" / "7 ч 15 мин" / "7 дней"
     * количество времени от текущей до будущей даты
     *
     * @param date Дата для формирования строки
     */
    fun getDateBeforeCurrent(date: Date?): String {
        var dateString: String = ""
        if (date != null) {
            val currentCalendarDate = Calendar.getInstance(Locale.getDefault())
            val calendarDate = Calendar.getInstance(Locale.getDefault()).apply {
                clear()
                time = date.apply {
                    Locale.getDefault()
                }
            }
            dateString =
                (calendarDate.timeInMillis - currentCalendarDate.timeInMillis).getDiffTime()
        }
        return dateString
    }

    /**
     * Обёртка над [getDateBeforeCurrent], чтобы получить готовую строку сразу из строки
     *
     * @param dateString строка с датой
     */
    fun getDateBeforeCurrentFromString(dateString: String?): String {
        return getDateBeforeCurrent(
            fromString(dateString = dateString)
        )
    }

    /**
     * Переводит дату в календарь
     *
     * @param date дата
     */
    fun getCalendarByDate(date: Date?): Calendar? {
        var calendarDate: Calendar? = null
        date?.let {
            calendarDate = Calendar.getInstance(Locale.getDefault()).apply {
                clear()
                time = date.apply {
                    Locale.getDefault()
                }
            }
        }
        return calendarDate
    }

    /**
     * Переводит дату в год
     *
     * @param date дата
     */
    fun getYearString(date: Date?): String {
        var year: String = ""
        if (date != null) {
            val calendar = getCalendarByDate(date = date)
            year = calendar?.get(Calendar.YEAR).toString()
        }
        return year
    }

    /**
     * Обёртка над [getYearString], чтобы получить готовую строку сразу из строки
     *
     * @param dateString строка с датой
     */
    fun getYearStringFromString(dateString: String?): String {
        return getYearString(
            date = fromString(
                dateString = dateString
            )
        )
    }

    /**
     * Переводит дату в строку
     *
     * @param date дата
     */
    fun getDateString(date: Date?, pattern: String = DD_MM_YYYY): String {
        var dateString: String = ""
        if (date != null) {
            dateString = formatDateByPattern(
                date = date,
                pattern = pattern
            )
        }
        return dateString
    }

    /**
     * Переводит строку в дату и форматирует в нужную строку по паттерну
     *
     * @param dateString строка даты
     * @param pattern паттерн для форматирования
     */
    fun getDateStringFromString(
        dateString: String?,
        pattern: String = D_MM_YYYY_HH_MM_SS
    ): String {
        var string = ""
        if (dateString.isNullOrEmpty().not()) {
            val date = fromString(dateString = dateString)
            string = getDateString(date = date, pattern = pattern)
        }
        return string
    }

    /**
     * Переводит строку в дату и возвращает сокращённое название дня недели
     *
     * @param dateString строка даты
     */
    fun getDayFromString(
        dateString: String?,
    ): String {
        val date = fromString(dateString = dateString)
        val day = getCalendarByDate(date)?.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            2 -> "пн."
            3 -> "вт."
            4 -> "ср."
            5 -> "чт."
            6 -> "пт."
            7 -> "сб."
            1 -> "вс."
            else -> ""
        }
    }
}