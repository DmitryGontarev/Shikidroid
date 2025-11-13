package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.roles.PersonDetailsModel
import com.shikidroid.domain.models.roles.PersonModel
import com.shikidroid.domain.models.roles.RoleType
import com.shikidroid.domain.repository.PeopleRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения данных о роли человека
 */
internal interface PeopleInteractor {

    /**
     * Получение информации о человеке по его ID
     *
     * @param id идентификацонный номер человека
     */
    fun getPersonDetails(
        peopleId: Long
    ): Single<PersonDetailsModel>

    /**
     * Получение списка создателей
     *
     * @param peopleName имя создателя для поиска
     * @param role роль человека (должно быть одно из 3-ёх: seyu, mangaka, producer)
     */
    fun getPersonList(
        peopleName: String?,
        role: RoleType? = null
    ): Single<List<PersonModel>>
}

/**
 * Реализация интерактора [PeopleInteractor]
 *
 * @property peopleRepository репозиторий для получения данных о роли человека
 */
internal class PeopleInteractorImpl @Inject constructor(
    private val peopleRepository: PeopleRepository
) : PeopleInteractor {

    override fun getPersonDetails(peopleId: Long): Single<PersonDetailsModel> {
        return peopleRepository.getPersonDetails(peopleId = peopleId)
    }

    override fun getPersonList(peopleName: String?, role: RoleType?): Single<List<PersonModel>> {
        return peopleRepository.getPersonList(
            peopleName = peopleName,
            role = role
        )
    }
}