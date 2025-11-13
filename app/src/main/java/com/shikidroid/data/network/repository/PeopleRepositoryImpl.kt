package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.PeopleApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.data.network.converters.toStringRequest
import com.shikidroid.domain.models.roles.PersonDetailsModel
import com.shikidroid.domain.models.roles.PersonModel
import com.shikidroid.domain.models.roles.RoleType
import com.shikidroid.domain.repository.PeopleRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [PeopleRepository]
 *
 * @property api для получения данных о роли человека из сети
 */
internal class PeopleRepositoryImpl @Inject constructor(
    private val api: PeopleApi
) : PeopleRepository {

    override fun getPersonDetails(peopleId: Long): Single<PersonDetailsModel> {
        return api.getPersonDetails(peopleId = peopleId)
            .map { personDetails ->
                personDetails.toDomainModel()
            }
    }

    override fun getPersonList(peopleName: String?, role: RoleType?): Single<List<PersonModel>> {
        return api.getPersonList(
            peopleName = peopleName,
            role = role?.toStringRequest()
        ).map { personList ->
            personList.map { person ->
                person.toDomainModel()
            }
        }
    }

}