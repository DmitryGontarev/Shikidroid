package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность с данными пользователя
 *
 * @property id id пользователя
 * @property nickname ник пользователя
 * @property avatar ссылка на аватар пользователя
 * @property image ссылки на разные размеры аватара
 * @property lastOnlineDate дата, когда пользователь был последний раз онлайн
 * @property name имя пользователя
 * @property sex пол пользователя
 * @property website ссылка на сайт, который указал пользователь
 * @property birtDate день рождения пользователя
 * @property locale регион пользователя (ru)
 */
data class UserBriefResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("image")
    val image: UserImageResponse,
    @SerializedName("last_online_at")
    val lastOnlineDate: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("sex")
    val sex: String?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("birth_on")
    val birtDate: String?,
    @SerializedName("locale")
    val locale: String?
)

/**
 * Сущность картинки страницы пользователя
 *
 * @property x160 картинка 160 пикселей
 * @property x148 картинка 160 пикселей
 * @property x80 картинка 80 пикселей
 * @property x64 картинка 64 пикселей
 * @property x48 картинка 48 пикселей
 * @property x32 картинка 32 пикселей
 * @property x16 картинка 16 пикселей
 */
data class UserImageResponse(
    @SerializedName("x160")
    val x160: String?,
    @SerializedName("x148")
    val x148: String?,
    @SerializedName("x80")
    val x80: String?,
    @SerializedName("x64")
    val x64: String?,
    @SerializedName("x48")
    val x48: String?,
    @SerializedName("x32")
    val x32: String?,
    @SerializedName("x16")
    val x16: String?
)
