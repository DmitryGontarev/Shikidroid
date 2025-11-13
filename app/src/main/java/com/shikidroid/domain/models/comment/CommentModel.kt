package com.shikidroid.domain.models.comment

import com.shikidroid.domain.models.user.UserBriefModel

/**
 * Модель данных комментария
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
data class CommentModel(
    val id: Long?,
    val userId: Long?,
    val commentableId: Long?,
    val commentableType: CommentableType?,
    val body: String?,
    val bodyHtml: String?,
    val dateCreated: String?,
    val dateUpdated: String?,
    val isOfftopic: Boolean?,
    val isSummary: Boolean?,
    val isEditable: Boolean?,
    val user: UserBriefModel?
)
