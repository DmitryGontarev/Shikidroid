package com.shikidroid.domain.models.club

/**
 * Политика вступления в клуб
 */
enum class ClubPolicy {

    /**
     * Свободный вход
     */
    FREE,

    /**
     * Приглашать может только админ
     */
    ADMIN_INVITE,

    /**
     * Приглашать может только создатель клуба
     */
    OWNER_INVITE,

    /**
     * Неизвестная политика клуба (если придёт что-то не из списка)
     */
    UNKNOWN
}