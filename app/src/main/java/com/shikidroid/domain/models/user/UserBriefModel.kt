package com.shikidroid.domain.models.user

/**
 * Модель пользователя
 *
 * @property id id пользователя
 * @property nickname ник пользователя
 * @property avatar ссылка на аватар пользователя
 * @property image ссылки на разные размеры аватара
 * @property lastOnlineDate дата, когда пользователь был последний раз онлайн
 * @property name имя пользователя
 * @property sex пол пользователя
 * @property website ссылка на сайт, который указал пользователь
 * @property birthDate день рождения пользователя
 * @property locale регион пользователя (ru)
 */
data class UserBriefModel(
    val id: Long,
    val nickname: String,
    val avatar: String?,
    val image: UserImageModel,
    val lastOnlineDate: String?,
    val name: String?,
    val sex: String?,
    val website: String?,
    val birthDate: String?,
    val locale: String?
)

/**
 * Модель ошибки авторизации
 *
 * @property error причина ошибки
 * @property error_description описание ошибки
 * @property state состояние авторизации
 */
data class UserAuthorizationErrorModel(
    val error: String?,
    val error_description: String?,
    val state: String?
)

/**
 * Модель картинки страницы пользователя
 *
 * @property x160 картинка 160 пикселей
 * @property x148 картинка 160 пикселей
 * @property x80 картинка 80 пикселей
 * @property x64 картинка 64 пикселей
 * @property x48 картинка 48 пикселей
 * @property x32 картинка 32 пикселей
 * @property x16 картинка 16 пикселей
 */
data class UserImageModel(
    val x160: String?,
    val x148: String?,
    val x80: String?,
    val x64: String?,
    val x48: String?,
    val x32: String?,
    val x16: String?
)