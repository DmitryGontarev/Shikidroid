package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность с детельной информацией о человеке
 *
 * @property id номер человека
 * @property name имя человека
 * @property nameRu имя человека на русском
 * @property image ссылки на фото с человеком
 * @property url ссылка на человека
 * @property nameJp имя на японском
 * @property jobTitle название работы
 * @property birthOn
 * @property deceasedOn
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
data class PersonDetailsResponse(
    @SerializedName("id") val id : Long?,
    @SerializedName("name") val name : String?,
    @SerializedName("russian") val nameRu : String?,
    @SerializedName("image") val image : ImageResponse?,
    @SerializedName("url") val url : String?,
    @SerializedName("japanese") val nameJp : String?,
    @SerializedName("job_title") val jobTitle : String?,
    @SerializedName("birth_on") val birthOn : RoleDateResponse?,
    @SerializedName("deceased_on") val deceasedOn: RoleDateResponse?,
    @SerializedName("website") val website: String?,
    @SerializedName("groupped_roles") val rolesGrouped : List<List<String?>?>?,
    @SerializedName("roles") val roles : List<SeyuRoleResponse>?,
    @SerializedName("works") val works : List<WorkResponse>?,
    @SerializedName("topic_id") val topicId : Long?,
    @SerializedName("person_favoured") val isFavoritePerson : Boolean?,
    @SerializedName("producer") val isProducer : Boolean?,
    @SerializedName("producer_favoured") val isFavoriteProducer : Boolean?,
    @SerializedName("mangaka") val isMangaka : Boolean?,
    @SerializedName("mangaka_favoured") val isFavoriteMangaka : Boolean?,
    @SerializedName("seyu") val isSeyu : Boolean?,
    @SerializedName("seyu_favoured") val isFavoriteSeyu : Boolean?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("thread_id") val threadId: Long?,
    @SerializedName("birthday") val birthday: RoleDateResponse?
)
