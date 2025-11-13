package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.calendar.CalendarResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 * Интерфейс API для получения данных о дате выпуска аниме
 */
internal interface CalendarApi {

    /**
     * Получение списка с графиком выхода эпизодов
     */
    @GET("/api/calendar")
    fun getCalendar(): Single<List<CalendarResponse>>
}