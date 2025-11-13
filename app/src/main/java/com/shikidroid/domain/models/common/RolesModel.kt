package com.shikidroid.domain.models.common

import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.models.roles.PersonModel

/**
 * Сущность с информацией о роли принимавшего участие в создании аниме
 *
 * @property roles список с названием ролей
 * @property rolesRu список с названием ролей на русском
 * @property character информация о персонаже
 * @property person информация о человеке
 */
data class RolesModel(
    val roles: List<String>?,
    val rolesRu: List<String>?,
    val character: CharacterModel?,
    val person: PersonModel?
)
