package com.shikidroid.domain.repository

import com.shikidroid.domain.models.roles.PersonDetailsModel
import com.shikidroid.domain.models.roles.PersonModel
import com.shikidroid.domain.models.roles.RoleType
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения данных о роли человека
 */
internal interface PeopleRepository {

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
        role: RoleType?
    ): Single<List<PersonModel>>

}