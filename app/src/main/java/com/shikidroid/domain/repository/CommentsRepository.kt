package com.shikidroid.domain.repository

import com.shikidroid.domain.models.comment.CommentModel
import com.shikidroid.domain.models.comment.CommentableType
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения комментариев
 */
internal interface CommentsRepository {

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