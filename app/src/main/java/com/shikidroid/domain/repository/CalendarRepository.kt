package com.shikidroid.domain.repository

import com.shikidroid.domain.models.calendar.CalendarModel
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения графика выхода эпизодов
 */
internal interface CalendarRepository {

    /**
     * Получение списка с графиком выхода эпизодов
     */
    fun getCalendar(): Single<List<CalendarModel>>
}