package com.shikidroid.data.network.entity.comment

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.user.UserBriefResponse

/**
 * Сущность с данными о комментарии
 *
 * @property id идентификационный номер коментируемого произведения
 * @property userId идентификационный номер пользователя, оставившего комментарий
 * @property commentableId идентификационный номер комментария
 * @property commentableType тип комментария (Topic, User)
 * @property body строка комментария
 * @property bodyHtml строка комментария для HTML
 * @property dateCreated дата создания комментария
 * @property dateUpdated дата обновления комментария
 * @property isOfftopic флаг, что это оффтоп
 * @property isSummary флаг, что это обзор
 * @property isEditable можно ли редактировать комментарий
 * @property user данные пользователя, оставившего комментарий
 */
data class CommentResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("user_id")
    val userId: Long?,
    @SerializedName("commentable_id")
    val commentableId: Long?,
    @SerializedName("commentable_type")
    val commentableType: String?,
    @SerializedName("body")
    val body: String?,
    @SerializedName("html_body")
    val bodyHtml: String?,
    @SerializedName("created_at")
    val dateCreated: String?,
    @SerializedName("updated_at")
    val dateUpdated: String?,
    @SerializedName("is_offtopic")
    val isOfftopic: Boolean?,
    @SerializedName("is_summary")
    val isSummary: Boolean?,
    @SerializedName("can_be_edited")
    val isEditable: Boolean?,
    @SerializedName("user")
    val user: UserBriefResponse?
)
