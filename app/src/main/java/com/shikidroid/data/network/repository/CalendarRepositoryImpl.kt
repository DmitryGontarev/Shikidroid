package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.CalendarApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.domain.models.calendar.CalendarModel
import com.shikidroid.domain.repository.CalendarRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [CalendarRepository]
 *
 * @property api для получения графика выхода эпизодов
 */
internal class CalendarRepositoryImpl @Inject constructor(
    private val api: CalendarApi
) : CalendarRepository {

    override fun getCalendar(): Single<List<CalendarModel>> {
        return api.getCalendar()
            .map { calendarList ->
                calendarList.map { calendar ->
                    calendar.toDomainModel()
                }
            }
    }
}