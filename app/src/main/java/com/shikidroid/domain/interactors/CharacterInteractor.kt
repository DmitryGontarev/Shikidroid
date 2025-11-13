package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.roles.CharacterDetailsModel
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.repository.CharactersRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения данных о персонаже
 */
internal interface CharacterInteractor {

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

/**
 * Реализация интерактора [CharacterInteractor]
 *
 * @property characterRepository репозиторий для получения данных об аниме
 */
internal class CharacterInteractorImpl @Inject constructor(
    private val characterRepository: CharactersRepository
): CharacterInteractor {

    override fun getCharacterDetails(id: Long): Single<CharacterDetailsModel> {
        return characterRepository.getCharacterDetails(id = id)
    }

    override fun getCharacterList(characterName: String): Single<List<CharacterModel>> {
        return characterRepository.getCharacterList(characterName = characterName)
    }
}