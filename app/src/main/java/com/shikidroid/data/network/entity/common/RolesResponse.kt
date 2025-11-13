package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.roles.CharacterResponse
import com.shikidroid.data.network.entity.roles.PersonResponse

/**
 * Сущность с информацией о роли принимавшего участие в создании аниме
 *
 * @property roles список с названием ролей
 * @property rolesRu список с названием ролей на руссколм
 * @property character информация о персонаже
 * @property person информация о персоне
 */
data class RolesResponse(
    @SerializedName("roles")
    val roles: List<String>?,
    @SerializedName("roles_russian")
    val rolesRu: List<String>?,
    @SerializedName("character")
    val character: CharacterResponse?,
    @SerializedName("person")
    val person: PersonResponse?
)
