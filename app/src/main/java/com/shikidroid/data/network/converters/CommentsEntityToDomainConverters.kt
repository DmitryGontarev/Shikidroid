package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.comment.CommentResponse
import com.shikidroid.domain.models.comment.CommentModel


/**
 * конвертация [CommentResponse] в модель domain слоя
 *
 * @return [CommentModel]
 */
fun CommentResponse.toDomainModel() =
    CommentModel(
        id = id,
        userId = userId,
        commentableId = commentableId,
        commentableType = commentableType?.toDomainCommentableType(),
        body = body,
        bodyHtml = bodyHtml,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated,
        isOfftopic = isOfftopic,
        isSummary = isSummary,
        isEditable = isEditable,
        user = user?.toDomainModel()
    )