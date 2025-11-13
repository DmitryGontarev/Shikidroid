package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.domain.models.user.UserBriefModel
import com.shikidroid.domain.repository.UserLocalRepository
import javax.inject.Inject

/**
 * Интерфейс интерактора для локального сохранения данных пользователя
 */
internal interface UserLocalInteractor {

    /**
     * Метод для сохранения id пользователя в хранилище
     */
    fun setUserId(id: Long)

    /**
     * Метод для получения id пользователя из хранилища
     */
    fun getUserId(): Long

    /**
     * Метод для сохранения модели профиля пользователя в хранилище
     */
    fun setUserBrief(user: UserBriefModel)

    /**
     * Метод для получения модели профиля пользователя
     */
    fun getUserBrief(): UserBriefModel?

    /**
     * Метод для установки статуса авторизации пользователя
     */
    fun setUserAuthStatus(userAuthStatus: UserAuthStatus)

    /**
     * Метод для получения статуса авторизации пользователя
     */
    fun getUserAuthStatus(): UserAuthStatus?

    /**
     * Метод для удаления данных пользователя из локального хранилища
     */
    fun clearUser()
}

/**
 * Реализация [UserLocalInteractor]
 *
 * @property repository локальное хранилище данных пользователя
 */
internal class UserLocalInteractorImpl @Inject constructor(
    private val repository: UserLocalRepository
) : UserLocalInteractor {

    override fun setUserId(id: Long) {
        return repository.setUserId(id = id)
    }

    override fun getUserId(): Long {
        return repository.getUserId()
    }

    override fun setUserBrief(user: UserBriefModel) {
        return repository.setUserBrief(user = user)
    }

    override fun getUserBrief(): UserBriefModel? {
        return repository.getUserBrief()
    }

    override fun setUserAuthStatus(userAuthStatus: UserAuthStatus) {
        return repository.setUserAuthStatus(userAuthStatus = userAuthStatus)
    }

    override fun getUserAuthStatus(): UserAuthStatus? {
        return repository.getUserAuthStatus()
    }

    override fun clearUser() {
        return repository.clearUser()
    }
}