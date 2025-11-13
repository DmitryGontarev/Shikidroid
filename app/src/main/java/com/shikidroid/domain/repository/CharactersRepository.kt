package com.shikidroid.domain.repository

import com.shikidroid.domain.models.roles.CharacterDetailsModel
import com.shikidroid.domain.models.roles.CharacterModel
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения данных о персонаже
 */
internal interface CharactersRepository {

    /**
     * Получение информации о персонаже по его ID
     *
     * @param id идентификацонный номер персонажа
     */
    fun getCharacterDetails(id: Long): Single<CharacterDetailsModel>

    /**
     * Получение списка персонажей по имени
     *
     * @param characterName имя персонажа для поиска
     */
    fun getCharacterList(characterName: String): Single<List<CharacterModel>>
}