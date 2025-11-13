package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.calendar.CalendarModel
import com.shikidroid.domain.repository.CalendarRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения графика выхода эпизодов
 */
internal interface CalendarInteractor {

    /**
     * Получение списка с графиком выхода эпизодов
     */
    fun getCalendar(): Single<List<CalendarModel>>
}

/**
 * Реализация интерактора [CalendarInteractor]
 *
 * @property calendarRepository репозитория для получения графика выхода эпизодов
 */
internal class CalendarInteractorImpl @Inject constructor(
    private val calendarRepository: CalendarRepository
) : CalendarInteractor {

    override fun getCalendar(): Single<List<CalendarModel>> {
        return calendarRepository.getCalendar()
    }
}