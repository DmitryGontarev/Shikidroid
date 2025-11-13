package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.comment.CommentResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интейрфейс API для получения комментариев
 */
internal interface CommentsApi {

    /**
     * Получить список комментариевэ
     *
     * @param id идентификационный номер коментируемого произведения (обязательно)
     * @param type тип комментария (Topic, User) (обязательно)
     * @param page номер страницы от 1 до 100 000 (необязательно)
     * @param limit лимит комментариев для загрузки, максимум 30 за раз (необязательно)
     * @param desc должен быть 1 или 0 (необязательно)
     */
    @GET("/api/comments")
    fun getComments(
        @Query("commentable_id") id: Long,
        @Query("commentable_type") type: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("desc") desc: Int?
    ): Single<List<CommentResponse>>

    /**
     * Показать комментарий по его id
     *
     * @param id идентификационный номер комментария
     */
    @GET("/api/comments/:id")
    fun getComment(@Path("id") id: Long): Single<CommentResponse>
}