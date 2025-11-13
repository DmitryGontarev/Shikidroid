package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.comment.CommentModel
import com.shikidroid.domain.models.comment.CommentableType
import com.shikidroid.domain.repository.CommentsRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения комментариев
 */
internal interface CommentsInteractor {

    /**
     * Получить список комментариевэ
     *
     * @param id идентификационный номер коментируемого произведения (обязательно)
     * @param type тип комментария (Topic, User) (обязательно)
     * @param page номер страницы от 1 до 100 000 (необязательно)
     * @param limit лимит комментариев для загрузки, максимум 30 за раз (необязательно)
     * @param desc должен быть 1 или 0 (необязательно)
     */
    fun getComments(
        id: Long,
        type: CommentableType,
        page: Int?,
        limit: Int?,
        desc: Int?
    ): Single<List<CommentModel>>

    /**
     * Показать комментарий по его id
     *
     * @param id идентификационный номер комментария
     */
    fun getComment(id: Long): Single<CommentModel>
}

internal class CommentsInteractorImpl @Inject constructor(
    private val repository: CommentsRepository
) : CommentsInteractor {

    override fun getComments(
        id: Long,
        type: CommentableType,
        page: Int?,
        limit: Int?,
        desc: Int?
    ): Single<List<CommentModel>> {
        return repository.getComments(
            id = id,
            type = type,
            page = page,
            limit = limit,
            desc = desc
        )
    }

    override fun getComment(id: Long): Single<CommentModel> {
        return repository.getComment(id = id)
    }
}