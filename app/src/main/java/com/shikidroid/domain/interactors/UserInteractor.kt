package com.shikidroid.domain.interactors

import com.shikidroid.appconstants.NetworkConstants
import com.shikidroid.domain.models.club.ClubModel
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.rates.UserRateCreateOrUpdateModel
import com.shikidroid.domain.models.rates.UserRateModel
import com.shikidroid.domain.models.user.*
import com.shikidroid.domain.repository.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора для получения/обновления данных пользовательского профиля в сети
 */
internal interface UserInteractor {

    ///////////////////////////////////////////////////////////////////////////
    // USERS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Получение списка пользователей
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 100 максимум (необязательно)
     */
    fun getUsersList(page: Int? = null, limit: Int? = null): Single<List<UserBriefModel>>

    /**
     * Найти пользователя по его id
     *
     * @param id пользователя
     * @param isNickName ник пользователя, если нужно найти пользователя по нику (необязательно)
     */
    fun getUserProfileById(id: Long, isNickName: String? = null): Single<UserDetailsModel>

    /**
     * Получить информацию о пользователе по его id
     *
     * @param id пользователя
     */
    fun getUserBriefInfoById(id: Long): Single<UserBriefModel>

    /**
     * Получить информацию по своему профилю пользователя
     */
    fun getCurrentUserBriefInfo(): Single<UserBriefModel>

    /**
     * Разлогинить пользователя
     */
    fun signOutUser(): Completable

    /**
     * Получение списка друзей
     *
     * @param id пользователя, по которому нужно получить список друзей
     */
    fun getUserFriends(id: Long): Single<List<UserBriefModel>>

    /**
     * Получить список клубов пользователя
     *
     * @param id пользователя, по которому нужно получить список клубов
     */
    fun getUserClubs(id: Long): Single<List<ClubModel>>

    /**
     * Получить пользовательский список с рейтингом аниме
     *
     * @param id пользователя, по которому нужно получить список с рейтингом аниме
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 5000 максимум (необязательно)
     * @param status статус аниме (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param censored включть цензуру, true убирает hentai (необязательно)
     */
    fun getUserAnimeRates(
        id: Long,
        page: Int? = null,
        limit: Int? = null,
        status: RateStatus? = null,
        censored: Boolean? = null
    ): Single<List<RateModel>>

    /**
     * Получить пользовательский список с рейтингом манги
     *
     * @param id пользователя, по которому нужно получить список с рейтингом аниме
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit должно быть числом, 5000 максимум (необязательно)
     * @param status статус аниме (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param censored включть цензуру, true убирает hentai, yaoi, yuri (необязательно)
     */
    fun getUserMangaRates(
        id: Long,
        page: Int? = null,
        limit: Int? = null,
        status: RateStatus? = null,
        censored: Boolean? = null
    ): Single<List<RateModel>>

    /**
     * Получить список избранного пользователя
     *
     * @param id пользователя, по которому нужно получить список избранного
     */
    fun getUserFavourites(id: Long): Single<FavoriteListModel>

    /**
     * Получить список сообщений пользователя
     *
     * @param id пользователя, по которому нужно получить список сообщений
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param type тип сообщений (inbox, private, sent, news, notifications) (ОБЯЗАТЕЛЬНО)
     * @param limit лимит сообщений, число максимум 100 (необязательно)
     */
    fun getUserMessages(
        id: Long,
        page: Int? = null,
        type: MessageType,
        limit: Int? = NetworkConstants.DEFAULT_LIMIT
    ): Single<List<MessageModel>>

    /**
     * Получить количество непрочитанных сообщений пользователя
     *
     * @param id пользователя, по которому нужно получить количество непрочитанных сообщений
     */
    fun getUnreadMessages(id: Long): Single<UserUnreadMessagesCountModel>

    /**
     * Получить историю действия пользователя
     *
     * @param id пользователя, по которому нужно получить историю пользователя
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит действий из истории, число максимум 100 (необязательно)
     * @param targetId номер действия (ноебязательно)
     * @param targetType тип контекта (Anime, Manga) (необязательно)
     */
    fun getUserHistory(
        id: Long,
        page: Int? = null,
        limit: Int? = null,
        targetId: Int? = null,
        targetType: SectionType? = SectionType.ANIME
    ): Single<List<UserHistoryModel>>

    ///////////////////////////////////////////////////////////////////////////
    // USER IGNORE (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Включить игнор пользователя
     *
     * @param id пользователя, которого нужно отправить в игнор
     */
    fun ignoreUser(id: Long): Completable

    /**
     * Вытащить пользователя из игнора
     *
     * @param id пользователя, которого нужно удалить из игнора
     */
    fun unIgnoreUser(id: Long): Completable

    ///////////////////////////////////////////////////////////////////////////
    // USER RATES (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Получить элемент списка пользовательского рейтинга
     *
     * @param id номер элемента списка, информацию по которому нужно получить
     */
    fun getRate(id: Long): Single<UserRateModel>

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
    fun getUserRates(
        userId : Long,
        targetId : Long? = null,
        targetType : SectionType? = SectionType.ANIME,
        status : RateStatus? = null,
        page : Int? = null,
        limit : Int? = null
    ) : Single<List<UserRateModel>>

    /**
     * Добавить новый элемент в список пользовательского рейтинга
     *
     * @param request информация для добавления элемента списка
     */
    fun createRate(request: UserRateCreateOrUpdateModel): Single<UserRateModel>

    /**
     * Обновить элемент списка пользовательского рейтинга
     *
     * @param id элемента для обновления
     * @param request информация для обновления элемента списка
     */
    fun updateRate(id: Long, request: UserRateCreateOrUpdateModel): Single<UserRateModel>

    /**
     * Увеличить количество просмотренных эпизодов/глав
     *
     * @param id элемента, который нужно увеличить
     */
    fun increment(id: Long): Single<IncrementModel>

    /**
     * Удалить элемент списка пользовательского рейтинга
     *
     * @param id элемента списка, который нужно удалить
     */
    fun deleteRate(id: Long): Completable

    ///////////////////////////////////////////////////////////////////////////
    // FRIENDS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Добавить в друзья
     *
     * @param id пользователя для добавления в друзья
     */
    fun addToFriends(id: Long): Completable

    /**
     * Удалить из друзей
     *
     * @param id пользователя для удаления из друзей
     */
    fun deleteFriend(id: Long): Completable

}

/**
 * Реализация интерактора [UserInteractor]
 *
 * @property userRepository репозиторий для получения данных пользователя
 */
internal class UserInteractorImpl @Inject constructor(
    private val userRepository: UserRepository
) : UserInteractor {

    ///////////////////////////////////////////////////////////////////////////
    // USERS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    override fun getUsersList(page: Int?, limit: Int?): Single<List<UserBriefModel>> {
        return userRepository.getUsersList(page = page, limit = limit)
    }

    override fun getUserProfileById(id: Long, isNickName: String?): Single<UserDetailsModel> {
        return userRepository.getUserProfileById(id = id, isNickName = isNickName)
    }

    override fun getUserBriefInfoById(id: Long): Single<UserBriefModel> {
        return userRepository.getUserBriefInfoById(id = id)
    }

    override fun getCurrentUserBriefInfo(): Single<UserBriefModel> {
        return userRepository.getCurrentUserBriefInfo()
    }

    override fun signOutUser(): Completable {
        return userRepository.signOutUser()
    }

    override fun getUserFriends(id: Long): Single<List<UserBriefModel>> {
        return userRepository.getUserFriends(id = id)
    }

    override fun getUserClubs(id: Long): Single<List<ClubModel>> {
        return userRepository.getUserClubs(id = id)
    }

    override fun getUserAnimeRates(
        id: Long,
        page: Int?,
        limit: Int?,
        status: RateStatus?,
        censored: Boolean?
    ): Single<List<RateModel>> {
        return userRepository.getUserAnimeRates(
            id = id,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    override fun getUserMangaRates(
        id: Long,
        page: Int?,
        limit: Int?,
        status: RateStatus?,
        censored: Boolean?
    ): Single<List<RateModel>> {
        return userRepository.getUserMangaRates(
            id = id,
            page = page,
            limit = limit,
            status = status,
            censored = censored
        )
    }

    override fun getUserFavourites(id: Long): Single<FavoriteListModel> {
        return userRepository.getUserFavourites(id = id)
    }

    override fun getUserMessages(
        id: Long,
        page: Int?,
        type: MessageType,
        limit: Int?
    ): Single<List<MessageModel>> {
        return userRepository.getUserMessages(
            id = id,
            page = page,
            type = type,
            limit = limit
        )
    }

    override fun getUnreadMessages(id: Long): Single<UserUnreadMessagesCountModel> {
        return userRepository.getUnreadMessages(id = id)
    }

    override fun getUserHistory(
        id: Long,
        page: Int?,
        limit: Int?,
        targetId: Int?,
        targetType: SectionType?
    ): Single<List<UserHistoryModel>> {
        return userRepository.getUserHistory(
            id = id,
            page = page,
            limit = limit,
            targetId = targetId,
            targetType = targetType
        )
    }

    override fun ignoreUser(id: Long): Completable {
        return userRepository.ignoreUser(id = id)
    }

    override fun unIgnoreUser(id: Long): Completable {
        return userRepository.unignoreUser(id = id)
    }

    override fun getRate(id: Long): Single<UserRateModel> {
        return userRepository.getRate(id = id)
    }

    override fun getUserRates(
        userId: Long,
        targetId: Long?,
        targetType: SectionType?,
        status: RateStatus?,
        page: Int?,
        limit: Int?
    ): Single<List<UserRateModel>> {
        return userRepository.getUserRates(
            userId = userId,
            targetId = targetId,
            targetType = targetType,
            status = status,
            page = page,
            limit = limit
        )
    }

    override fun createRate(request: UserRateCreateOrUpdateModel): Single<UserRateModel> {
        return userRepository.createRate(request = request)
    }

    override fun updateRate(
        id: Long,
        request: UserRateCreateOrUpdateModel
    ): Single<UserRateModel> {
        return userRepository.updateRate(
            id = id,
            request = request
        )
    }

    override fun increment(id: Long): Single<IncrementModel> {
        return userRepository.increment(id = id)
    }

    override fun deleteRate(id: Long): Completable {
        return userRepository.deleteRate(id = id)
    }

    override fun addToFriends(id: Long): Completable {
        return userRepository.addToFriends(id = id)
    }

    override fun deleteFriend(id: Long): Completable {
        return userRepository.deleteFriend(id = id)
    }

}