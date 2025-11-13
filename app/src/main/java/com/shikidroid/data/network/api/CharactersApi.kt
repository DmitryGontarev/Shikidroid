package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.roles.CharacterDetailsResponse
import com.shikidroid.data.network.entity.roles.CharacterResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс API для получения данных о персонаже
 */
internal interface CharactersApi {

    /**
     * Получение информации о персонаже по его ID
     *
     * @param id идентификацонный номер персонажа
     */
    @GET("/api/characters/{id}")
    fun getCharacterDetails(
        @Path("id")
        id: Long
    ): Single<CharacterDetailsResponse>

    /**
     * Получение списка персонажей по имени
     *
     * @param characterName имя персонажа для поиска
     */
    @GET("/api/characters/search")
    fun getCharacterList(
        @Query("search")
        characterName: String
    ): Single<List<CharacterResponse>>
}