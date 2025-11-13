package com.shikidroid.data.network.repository

import com.shikidroid.data.network.api.UserApi
import com.shikidroid.data.network.converters.toDataModel
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.data.network.converters.toStringRequest
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
 * Реализация репозитория [UserRepository]
 *
 * @property api для получения данных пользователя из сети
 */
internal class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    ///////////////////////////////////////////////////////////////////////////
    // USERS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    override fun getUsersList(page: Int?, limit: Int?): Single<List<UserBriefModel>> {
        return api.getUsersList(page = page, limit = limit)
            .map { listUserBriefResponse ->
                listUserBriefResponse.map { userBriefResponse ->
                    userBriefResponse.toDomainModel()
                }
            }
    }

    override fun getUserProfileById(id: Long, isNickName: String?): Single<UserDetailsModel> {
        return api.getUserProfileById(id = id, isNickName = isNickName)
            .map { userDetailsResponse ->
                userDetailsResponse.toDomainModel()
            }
    }

    override fun getUserBriefInfoById(id: Long): Single<UserBriefModel> {
        return api.getUserBriefInfoById(id = id)
            .map { userBriefResponse ->
                userBriefResponse.toDomainModel()
            }
    }

    override fun getCurrentUserBriefInfo(): Single<UserBriefModel> {
        return api.getCurrentUserBriefInfo()
            .map { userBriefResponse ->
                userBriefResponse.toDomainModel()
            }
    }

    override fun signOutUser(): Completable {
        return api.signOutUser()
    }

    override fun getUserFriends(id: Long): Single<List<UserBriefModel>> {
        return api.getUserFriends(id = id)
            .map { listUserBriefResponse ->
                listUserBriefResponse.map { userBriefResponse ->
                    userBriefResponse.toDomainModel()
                }
            }
    }

    override fun getUserClubs(id: Long): Single<List<ClubModel>> {
        return api.getUserClubs(id = id)
            .map { listClubResponse ->
                listClubResponse.map { clubResponse ->
                    clubResponse.toDomainModel()
                }
            }
    }

    override fun getUserAnimeRates(
        id: Long,
        page: Int?,
        limit: Int?,
        status: RateStatus?,
        censored: Boolean?
    ): Single<List<RateModel>> {
        return api.getUserAnimeRates(
            id = id,
            page = page,
            limit = limit,
            status = status?.toStringRequest(),
            censored = censored
        )
            .map { listRateResponse ->
                listRateResponse.map { rateResponse ->
                    rateResponse.toDomainModel()
                }
            }
    }

    override fun getUserMangaRates(
        id: Long,
        page: Int?,
        limit: Int?,
        status: RateStatus?,
        censored: Boolean?
    ): Single<List<RateModel>> {
        return api.getUserMangaRates(
            id = id,
            page = page,
            limit = limit,
            status = status?.toStringRequest(),
            censored = censored
        )
            .map { listRateResponse ->
                listRateResponse.map { rateResponse ->
                    rateResponse.toDomainModel()
                }
            }
    }

    override fun getUserFavourites(id: Long): Single<FavoriteListModel> {
        return api.getUserFavourites(id = id)
            .map { favoriteListResponse ->
                favoriteListResponse.toDomainModel()
            }
    }

    override fun getUserMessages(
        id: Long,
        page: Int?,
        type: MessageType,
        limit: Int?
    ): Single<List<MessageModel>> {
        return api.getUserMessages(
            id = id,
            page = page,
            type = type.toStringRequest(),
            limit = limit
        )
            .map { listMessageResponse ->
                listMessageResponse.map { messageResponse ->
                    messageResponse.toDomainModel()
                }
            }
    }

    override fun getUnreadMessages(id: Long): Single<UserUnreadMessagesCountModel> {
        return api.getUnreadMessages(
            id = id
        )
            .map {
                it.toDomainModel()
            }
    }

    override fun getUserHistory(
        id: Long,
        page: Int?,
        limit: Int?,
        targetId: Int?,
        targetType: SectionType?
    ): Single<List<UserHistoryModel>> {
        return api.getUserHistory(
            id = id,
            page = page,
            limit = limit,
            targetId = targetId,
            targetType = targetType?.toStringRequest()
        )
            .map { listUserHistoryResponse ->
                listUserHistoryResponse.map { userHistoryResponse ->
                    userHistoryResponse.toDomainModel()
                }
            }
    }

    ///////////////////////////////////////////////////////////////////////////
    // USER IGNORE (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    override fun ignoreUser(id: Long): Completable {
        return api.ignoreUser(id = id)
    }

    override fun unignoreUser(id: Long): Completable {
        return api.unignoreUser(id = id)
    }

    ///////////////////////////////////////////////////////////////////////////
    // USER RATES (Shikimori API v2)
    ///////////////////////////////////////////////////////////////////////////

    override fun getRate(id: Long): Single<UserRateModel> {
        return api.getRate(id = id)
            .map { userRateResponse ->
                userRateResponse.toDomainModel()
            }
    }

    override fun getUserRates(
        userId: Long,
        targetId: Long?,
        targetType: SectionType?,
        status: RateStatus?,
        page: Int?,
        limit: Int?
    ): Single<List<UserRateModel>> {
        return api.getUserRates(
            userId = userId,
            targetId = targetId,
            targetType = targetType?.toStringRequest(),
            status = status?.toStringRequest(),
            page = page,
            limit = limit
        )
            .map { listUserRateResponse ->
                listUserRateResponse.map { userRateResponse ->
                    userRateResponse.toDomainModel()
                }
            }
    }

    override fun createRate(request: UserRateCreateOrUpdateModel): Single<UserRateModel> {
        return api.createRate(request = request.toDataModel())
            .map { userRateResponse ->
                userRateResponse.toDomainModel()
            }
    }

    override fun updateRate(
        id: Long,
        request: UserRateCreateOrUpdateModel
    ): Single<UserRateModel> {
        return api.updateRate(
            id = id,
            request = request.toDataModel()
        )
            .map { userRateResponse ->
                userRateResponse.toDomainModel()
            }
    }

    override fun increment(id: Long): Single<IncrementModel> {
        return api.increment(id = id)
            .map { incrementResponse ->
                incrementResponse.toDomainModel()
            }
    }

    override fun deleteRate(id: Long): Completable {
        return api.deleteRate(id = id)
    }

    ///////////////////////////////////////////////////////////////////////////
    // FRIENDS (Shikimori API v1)
    ///////////////////////////////////////////////////////////////////////////

    override fun addToFriends(id: Long): Completable {
        return api.addToFriends(id = id)
    }

    override fun deleteFriend(id: Long): Completable {
        return api.deleteFriend(id = id)
    }
}