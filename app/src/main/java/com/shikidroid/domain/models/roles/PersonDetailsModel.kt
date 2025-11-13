package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.common.ImageModel

/**
 * Модель с детельной информацией о персонаже
 *
 * @property id номер человека
 * @property name имя человека
 * @property nameRu имя человека на русском
 * @property image ссылки на фото с человеком
 * @property url ссылка на человека
 * @property nameJp имя на японском
 * @property jobTitle название работы
 * @property birthOn дата рождения
 * @property deceasedOn дата смерти
 * @property website ссылка на сайт
 * @property rolesGrouped список групповых ролей
 * @property roles список ролей
 * @property works список работ
 * @property topicId идентификацонный номер топика
 * @property isFavoritePerson
 * @property isProducer
 * @property isFavoriteProducer
 * @property isMangaka
 * @property isFavoriteMangaka
 * @property isSeyu
 * @property isFavoriteSeyu
 * @property updatedAt
 * @property threadId идентификацонный номер треда
 * @property birthday
 */
data class PersonDetailsModel(
    val id : Long?,
    val name : String?,
    val nameRu : String?,
    val image : ImageModel?,
    val url : String?,
    val nameJp : String?,
    val jobTitle : String?,
    val birthOn : RoleDateModel?,
    val deceasedOn: RoleDateModel?,
    val website: String?,
    val rolesGrouped : List<List<String?>?>?,
    val roles : List<SeyuRoleModel>?,
    val works : List<WorkModel>?,
    val topicId : Long?,
    val isFavoritePerson : Boolean?,
    val isProducer : Boolean?,
    val isFavoriteProducer : Boolean?,
    val isMangaka : Boolean?,
    val isFavoriteMangaka : Boolean?,
    val isSeyu : Boolean?,
    val isFavoriteSeyu : Boolean?,
    val updatedAt: String?,
    val threadId: Long?,
    val birthday: RoleDateModel?
)
