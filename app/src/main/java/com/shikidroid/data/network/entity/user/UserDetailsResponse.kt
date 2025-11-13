package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность детальной информации о пользователе
 *
 * @property id id пользователя
 * @property nickname ник пользлвателя
 * @property image ссылки на разные размеры аватара
 * @property lastOnlineDate дата, когда пользователь был последний раз онлайн
 * @property name имя пользователя
 * @property sex пол пользователя
 * @property fullYears количество полных лет
 * @property lastOnline статус нахождения пользователя в сети ("сейчас на сайте")
 * @property website ссылка на указанный сайт пользователя
 * @property location местонахождение пользователя (город проживания)
 * @property isBanned есть ли бан у пользователя
 * @property about информация, которую указал о себе пользователь
 * @property aboutHtml информация о сайте, указанным пользователем
 * @property commonInfo общая информация о пользователе ("Нет личных данных","на сайте с 19 мая 2020 г.")
 * @property isShowComments показывать ли комментарии
 * @property isFriend в друзьях ли пользователь
 * @property isIgnored находиться ли пользователь в списке игнора
 * @property stats статистика пользователя (аниме, манга, жанры, студии и т.д.)
 * @property styleId id стиля страницы пользователя
 */
data class UserDetailsResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("image")
    val image: UserImageResponse?,
    @SerializedName("last_online_at")
    val lastOnlineDate: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("sex")
    val sex: String?,
    @SerializedName("full_years")
    val fullYears: Int?,
    @SerializedName("last_online")
    val lastOnline: String?,
    @SerializedName("website")
    val website: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("banned")
    val isBanned: Boolean?,
    @SerializedName("about")
    val about: String?,
    @SerializedName("about_html")
    val aboutHtml: String?,
    @SerializedName("common_info")
    val commonInfo : List<String>?,
    @SerializedName("show_comments")
    val isShowComments : Boolean?,
    @SerializedName("in_friends")
    val isFriend : Boolean?,
    @SerializedName("is_ignored")
    val isIgnored : Boolean?,
    @SerializedName("stats")
    val stats : UserStatsResponse?,
    @SerializedName("style_id")
    val styleId: String?
)

/**
 * Сущность статистики пользователя
 *
 * @property statuses общие статусы по статистике (аниме, манга)
 * @property fullStatuses полные статусы по статистике (аниме, манга)
 * @property scores оценки (аниме, манга)
 * @property types типы (аниме, манга)
 * @property ratings рейтинг (аниме, манга)
 * @property hasAnime есть ли аниме в статистике пользователя
 * @property hasManga есть ли манга в статистике пользователя
 */
data class UserStatsResponse(
    @SerializedName("statuses")
    val statuses: StatusesResponse?,
    @SerializedName("full_statuses")
    val fullStatuses: StatusesResponse?,
    @SerializedName("scores")
    val scores: StatusesResponse?,
    @SerializedName("types")
    val types: StatusesResponse?,
    @SerializedName("ratings")
    val ratings: StatusesResponse?,
    @SerializedName("has_anime?")
    val hasAnime: Boolean?,
    @SerializedName("has_manga?")
    val hasManga: Boolean?,
)

/**
 * Сущность каждого пунтка статистики пользователя
 *
 * @property anime статистика по аниме
 * @property manga статистика по манге
 */
data class StatusesResponse(
    @SerializedName("anime")
    val anime: List<StatusResponse>?,
    @SerializedName("manga")
    val manga: List<StatusResponse>?
)

/**
 * Сущность подпункта статистики пункта пользователя
 *
 * @property id id аниме или манги
 * @property groupId признак, по которму группируется аниме или манга (planned, watching, on_hold, rewatching, completed, dropped)
 * @property name название признака, по которму группируется аниме или манга
 * @property size количество серий или глав
 * @property type тип произведения (Anime, Manga)
 */
data class StatusResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("grouped_id")
    val groupId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("size")
    val size: Int?,
    @SerializedName("type")
    val type: String?
)
