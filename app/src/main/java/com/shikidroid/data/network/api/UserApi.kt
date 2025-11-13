package com.shikidroid.data.network.api

import com.shikidroid.data.network.entity.club.ClubResponse
import com.shikidroid.data.network.entity.rates.RateResponse
import com.shikidroid.data.network.entity.rates.UserRateCreateOrUpdateRequest
import com.shikidroid.data.network.entity.rates.UserRateResponse
import com.shikidroid.data.network.entity.user.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * Api для получения списка пользователей
 */
internal interface UserApi {

    ///////////////////////////////////////////////////////////////////////////
    // USERS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Получение списка пользователей
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 100 максимум (необязательно)
     */
    @GET("/api/users")
    fun getUsersList(
        @Query("page")
        page: Int?,
        @Query("limit")
        limit: Int?
    ): Single<List<UserBriefResponse>>

    /**
     * Найти пользователя по его id
     *
     * @param id пользователя
     * @param isNickName ник пользователя, если нужно найти пользователя по нику (необязательно)
     */
    @GET("/api/users/{id}")
    fun getUserProfileById(
        @Path("id")
        id: Long,
        @Query("is_nickname")
        isNickName: String?
    ): Single<UserDetailsResponse>

    /**
     * Получить информацию о пользователе по его id
     *
     * @param id пользователя
     */
    @GET("/api/users/{id}/info")
    fun getUserBriefInfoById(@Path("id") id: Long): Single<UserBriefResponse>

    /**
     * Получить информацию по своему профилю пользователя
     */
    @GET("/api/users/whoami")
    fun getCurrentUserBriefInfo(): Single<UserBriefResponse>

    /**
     * Разлогинить пользователя
     */
    @GET("/api/users/sign_out")
    fun signOutUser(): Completable

    /**
     * Получение списка друзей
     *
     * @param id пользователя, по которому нужно получить список друзей
     */
    @GET("/api/users/{id}/friends")
    fun getUserFriends(@Path("id") id: Long): Single<List<UserBriefResponse>>

    /**
     * Получить список клубов пользователя
     *
     * @param id пользователя, по которому нужно получить список клубов
     */
    @GET("/api/users/{id}/clubs")
    fun getUserClubs(@Path("id") id: Long): Single<List<ClubResponse>>

    /**
     * Получить пользовательский список с рейтингом аниме
     *
     * @param id пользователя, по которому нужно получить список с рейтингом аниме
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 5000 максимум (необязательно)
     * @param status статус аниме (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param censored включть цензуру, true убирает hentai (необязательно)
     */
    @GET("/api/users/{id}/anime_rates")
    fun getUserAnimeRates(
        @Path("id") id: Long,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("status") status: String?,
        @Query("censored") censored: Boolean?
    ): Single<List<RateResponse>>

    /**
     * Получить пользовательский список с рейтингом манги
     *
     * @param id пользователя, по которому нужно получить список с рейтингом манга
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 5000 максимум (необязательно)
     * @param status статус аниме (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param censored включть цензуру, true убирает hentai, yaoi, yuri (необязательно)
     */
    @GET("/api/users/{id}/manga_rates")
    fun getUserMangaRates(
        @Path("id") id: Long,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("status") status: String?,
        @Query("censored") censored: Boolean?
    ): Single<List<RateResponse>>

    /**
     * Получить список избранного пользователя
     *
     * @param id пользователя, по которому нужно получить список избранного
     */
    @GET("/api/users/{id}/favourites")
    fun getUserFavourites(@Path("id") id: Long): Single<FavoriteListResponse>

    /**
     * Получить список сообщений пользователя
     *
     * @param id пользователя, по которому нужно получить список сообщений
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param type тип сообщений (inbox, private, sent, news, notifications) (ОБЯЗАТЕЛЬНО)
     * @param limit лимит сообщений, число максимум 100 (необязательно)
     */
    @GET("/api/users/{id}/messages")
    fun getUserMessages(
        @Path("id") id: Long,
        @Query("page") page: Int?,
        @Query("type") type: String,
        @Query("limit") limit: Int?
    ): Single<List<MessageResponse>>

    /**
     * Получить количество непрочитанных сообщений пользователя
     *
     * @param id пользователя, по которому нужно получить количество непрочитанных сообщений
     */
    @GET("/api/users/{id}/unread_messages")
    fun getUnreadMessages(@Path("id") id: Long): Single<UserUnreadMessagesCountResponse>

    /**
     * Получить историю действия пользователя
     *
     * @param id пользователя, по которому нужно получить историю пользователя
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит действий из истории, число максимум 100 (необязательно)
     * @param targetId номер действия (ноебязательно)
     * @param targetType тип контекта (Anime, Manga) (необязательно)
     */
    @GET("/api/users/{id}/history")
    fun getUserHistory(
        @Path("id") id: Long,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("target_id") targetId: Int?,
        @Query("target_type") targetType: String?
    ): Single<List<UserHistoryResponse>>

    ///////////////////////////////////////////////////////////////////////////
    // USER IGNORE (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Включить игнор пользователя
     *
     * @param id пользователя, которого нужно отправить в игнор
     */
    @POST(" /api/v2/users/{user_id}/ignore")
    fun ignoreUser(@Path("user_id") id: Long): Completable

    /**
     * Вытащить пользователя из игнора
     *
     * @param id пользователя, которого нужно удалить из игнора
     */
    @DELETE(" /api/v2/users/{user_id}/ignore")
    fun unignoreUser(@Path("user_id") id: Long): Completable

    ///////////////////////////////////////////////////////////////////////////
    // USER RATES (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Получить элемент списка пользовательского рейтинга
     *
     * @param id номер элемента списка, информацию по которому нужно получить
     */
    @GET("/api/v2/user_rates/{id}")
    fun getRate(@Path("id") id: Long): Single<UserRateResponse>

    /**
     * Получить список пользовательского рейтинга
     *
     * @param userId пользователя, по которому нужно получить список с рейтингом (необязательно)
     * @param targetId номер элемента (необязательно)
     * @param targetType тип элемента (необязательно) (Must be one of: Anime, Manga)
     * @param status статус аниме (необязательно) (Must be one of: planned, watching, rewatching, completed, on_hold, dropped)
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно) (This field is ignored when user_id is set)
     * @param limit должно быть числом, 5000 максимум (необязательно)
     */
    @GET("/api/v2/user_rates")
    fun getUserRates(
        @Query("user_id")
        userId : Long,
        @Query("target_id")
        targetId : Long?,
        @Query("target_type")
        targetType : String?,
        @Query("status")
        status : String?,
        @Query("page")
        page : Int?,
        @Query("limit")
        limit : Int?
    ) : Single<List<UserRateResponse>>

    /**
     * Добавить новый элемент в список пользовательского рейтинга
     *
     * @param request информация для добавления элемента списка
     */
    @POST("/api/v2/user_rates")
    fun createRate(@Body request: UserRateCreateOrUpdateRequest): Single<UserRateResponse>

    /**
     * Обновить элемент списка пользовательского рейтинга
     *
     * @param id элемента для обновления
     * @param request информация для обновления элемента списка
     */
    @PATCH("/api/v2/user_rates/{id}")
    fun updateRate(
        @Path("id") id: Long,
        @Body
        request: UserRateCreateOrUpdateRequest
    ): Single<UserRateResponse>

    /**
     * Увеличить количество просмотренных эпизодов/глав
     *
     * @param id элемента, который нужно увеличить
     */
    @POST("/api/v2/user_rates/{id}/increment")
    fun increment(@Path("id") id: Long): Single<IncrementResponse>

    /**
     * Удалить элемент списка пользовательского рейтинга
     *
     * @param id элемента списка, который нужно удалить
     */
    @DELETE("/api/v2/user_rates/{id}")
    fun deleteRate(@Path("id") id: Long): Completable

    ///////////////////////////////////////////////////////////////////////////
    // FRIENDS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Добавить в друзья
     *
     * @param id пользователя для добавления в друзья
     */
    @POST("/api/friends/{id}")
    fun addToFriends(@Path("id") id: Long): Completable

    /**
     * Удалить из друзей
     *
     * @param id пользователя для удаления из друзей
     */
    @DELETE("/api/friends/{id}")
    fun deleteFriend(@Path("id") id: Long): Completable
}