package com.shikidroid.domain.models.user

/**
 * Статус авторизации пользователя
 */
enum class UserAuthStatus {

    /**
     * пользователь авторизован (уже заходил в систем)
     */
    AUTHORIZED,

    /**
     * пользователь неавторизован
     */
    UNAUTHORIZED,

    /**
     * пользователь зашёл как гость
     */
    GUEST
}