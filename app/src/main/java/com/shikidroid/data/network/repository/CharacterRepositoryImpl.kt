package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.CharactersApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.domain.models.roles.CharacterDetailsModel
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.repository.CharactersRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [CharacterRepository]
 *
 * @property api для получения данных о персонаже из сети
 */
internal class CharacterRepositoryImpl @Inject constructor(
    private val api: CharactersApi
): CharactersRepository {

    override fun getCharacterDetails(id: Long): Single<CharacterDetailsModel> {
        return api.getCharacterDetails(id = id)
            .map { characterDetails ->
                characterDetails.toDomainModel()
            }
    }

    override fun getCharacterList(characterName: String): Single<List<CharacterModel>> {
        return api.getCharacterList(characterName = characterName)
            .map { characterList ->
                characterList.map { character ->
                    character.toDomainModel()
                }
            }
    }
}