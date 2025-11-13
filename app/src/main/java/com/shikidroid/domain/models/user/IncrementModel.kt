package com.shikidroid.domain.models.user

import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.rates.RateStatus

/**
 * Сущность с обновлёнными данными
 * при запросе на увеличение количества просмотренных/прочитанных эпизодов/глав
 *
 * @property id id элемента списка
 * @property userId id пользователя
 * @property targetId id
 * @property targetType тип Аниме/Манга
 * @property score оценка пользователя
 * @property status статус чтения или просмотра
 * @property rewatches количество повторных просмотров
 * @property episodes количество просмотренных эпизодов аниме
 * @property volumes количество прочитанных томов
 * @property chapters количество прочитанных глав манги
 * @property text комментарий
 * @property textHtml комментарий
 * @property createdDateTime дата добавления в пользовательский список
 * @property updatedDateTime дата обновления в пользовательском списке
 */
data class IncrementModel(
    val id: Long?,
    val userId: Long?,
    val targetId: Long?,
    val targetType: SectionType?,
    val score: Int?,
    val status: RateStatus?,
    val rewatches: Int?,
    val episodes: Int?,
    val volumes: Int?,
    val chapters: Int?,
    val text: String?,
    val textHtml: String?,
    val createdDateTime : String?,
    val updatedDateTime : String?,
)
