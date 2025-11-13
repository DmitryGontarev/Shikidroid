package com.shikidroid.domain.models.rates

import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.user.UserBriefModel

/**
 * Модель элемента пользовательского списка рейтинга аниме
 *
 * @property id id номер элемента
 * @property score оценка пользователя
 * @property status статус чтения или просмотра
 * @property text комментарий к аниме/манге
 * @property episodes количество просмотренных эпизодов аниме
 * @property chapters количество прочитанных глав манги
 * @property volumes количество прочитанных томов
 * @property textHtml
 * @property rewatches количество повторных просмотров
 * @property createdDateTime дата добавления в пользовательский список
 * @property updatedDateTime дата обновления в пользовательском списке
 * @property user информация о пользователе
 * @property anime информация о аниме
 * @property manga информация о манге
 */
data class RateModel(
    val id: Long? = null,
    var score: Int? = null,
    var status: RateStatus? = null,
    var text: String? = null,
    var episodes: Int? = null,
    var chapters: Int? = null,
    val volumes: Int? = null,
    val textHtml: String? = null,
    var rewatches: Int? = null,
    var createdDateTime: String? = null,
    var updatedDateTime: String? = null,
    val user: UserBriefModel? = null,
    val anime: AnimeModel? = null,
    val manga: MangaModel? = null
)
