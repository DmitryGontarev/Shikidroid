package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.roles.PersonDetailsResponse
import com.shikidroid.data.network.entity.roles.PersonResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс API для получения данных о человеке,
 * принимавшем участие в создании аниме/манги
 */
internal interface PeopleApi {

    /**
     * Получение информации о человеке по его ID
     *
     * @param id идентификацонный номер человека
     */
    @GET("/api/people/{id}")
    fun getPersonDetails(
        @Path("id")
        peopleId: Long
    ): Single<PersonDetailsResponse>

    /**
     * Получение списка создателей
     *
     * @param peopleName имя создателя для поиска
     * @param role роль человека (должно быть одно из 3-ёх: seyu, mangaka, producer)
     */
    @GET("/api/people/search")
    fun getPersonList(
        @Query("search")
        peopleName: String?,
        @Query("kind")
        role: String?
    ): Single<List<PersonResponse>>
}