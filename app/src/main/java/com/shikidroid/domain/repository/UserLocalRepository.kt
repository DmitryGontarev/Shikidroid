package com.shikidroid.domain.repository

import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.domain.models.user.UserBriefModel

/**
 * Интерфейс репозитория для локального сохранения данных пользователя
 */
internal interface UserLocalRepository {

    /**
     * Метод для сохранения id пользователя в хранилище
     *
     * @param id идентификационный номер пользователя
     */
    fun setUserId(id: Long)

    /**
     * Метод для получения id пользователя из хранилища
     */
    fun getUserId(): Long

    /**
     * Метод для сохранения модели профиля пользователя в хранилище
     *
     * @param user модель данных пользователя
     */
    fun setUserBrief(user: UserBriefModel)

    /**
     * Метод для получения модели профиля пользователя
     */
    fun getUserBrief(): UserBriefModel?

    /**
     * Метод для установки статуса авторизации пользователя
     *
     * @param userAuthStatus статус авторизации пользователя
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