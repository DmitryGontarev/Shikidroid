package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.CommentsApi
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.data.network.converters.toStringRequest
import com.shikidroid.domain.models.comment.CommentModel
import com.shikidroid.domain.models.comment.CommentableType
import com.shikidroid.domain.repository.CommentsRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Реализация репозитория [CommentsRepository]
 *
 * @property api для получения комментариев
 */
internal class CommentsRepositoryImpl @Inject constructor(
    private val api: CommentsApi
): CommentsRepository {

    override fun getComments(
        id: Long,
        type: CommentableType,
        page: Int?,
        limit: Int?,
        desc: Int?
    ): Single<List<CommentModel>> {
        return api.getComments(
            id = id,
            type = type.toStringRequest(),
            page = page,
            limit = limit,
            desc = desc
        ).map { comments ->
            comments.map { comment ->
                comment.toDomainModel()
            }
        }
    }

    override fun getComment(id: Long): Single<CommentModel> {
        return api.getComment(id = id)
            .map { comment ->
                comment.toDomainModel()
            }
    }
}