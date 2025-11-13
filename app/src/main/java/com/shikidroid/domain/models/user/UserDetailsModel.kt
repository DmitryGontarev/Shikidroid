package com.shikidroid.domain.models.user

import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.rates.RateStatus

/**
 * Модель детальной информации о пользователе
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
data class UserDetailsModel(
    val id: Long?,
    val nickname: String?,
    val image: UserImageModel?,
    val lastOnlineDate: String?,
    val name: String?,
    val sex: String?,
    val fullYears: Int?,
    val lastOnline: String?,
    val website: String?,
    val location: String?,
    val isBanned: Boolean?,
    val about: String?,
    val aboutHtml: String?,
    val commonInfo : List<String>?,
    val isShowComments : Boolean?,
    val isFriend : Boolean?,
    val isIgnored : Boolean?,
    val stats : UserStatsModel?,
    val styleId: String?
)

/**
 * Модель статистики пользователя
 *
 * @property statuses общие статусы по статистике (аниме, манга)
 * @property fullStatuses полные статусы по статистике (аниме, манга)
 * @property scores оценки (аниме, манга)
 * @property types типы (аниме, манга)
 * @property ratings рейтинг (аниме, манга)
 * @property hasAnime есть ли аниме в статистике пользователя
 * @property hasManga есть ли манга в статистике пользователя
 */
data class UserStatsModel(
    val statuses: StatusesModel?,
    val fullStatuses: StatusesModel?,
    val scores: StatusesModel?,
    val types: StatusesModel?,
    val ratings: StatusesModel?,
    val hasAnime: Boolean?,
    val hasManga: Boolean?,
)

/**
 * Модель каждого пунтка статистики пользователя
 *
 * @property anime статистика по аниме
 * @property manga статистика по манге
 */
data class StatusesModel(
    val anime: List<StatusModel>?,
    val manga: List<StatusModel>?
)

/**
 * Модель подпункта статистики пункта пользователя
 *
 * @property id id аниме или манги
 * @property groupId признак, по которму группируется аниме или манга (planned, watching, on_hold, rewatching, completed, dropped)
 * @property name название признака, по которму группируется аниме или манга
 * @property size количество серий или глав
 * @property type тип произведения (Anime, Manga)
 */
data class StatusModel(
    val id: Long?,
    val groupId: RateStatus?,
    val name: RateStatus?,
    val size: Int?,
    val type: SectionType?
)