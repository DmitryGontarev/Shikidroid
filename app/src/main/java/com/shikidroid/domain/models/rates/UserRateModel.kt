package com.shikidroid.domain.models.rates

import com.shikidroid.domain.models.common.SectionType

/**
 * Модель элемента списка пользовательского рейтинга
 *
 * @property id id номер элемента
 * @property userId id номер пользователя
 * @property targetId id номер элемента списка
 * @property targetType тип произведения (Anime, Manga, Ranobe)
 * @property score оценка
 * @property status в каком статусе (watched, planned)
 * @property rewatches количество повторных просмотров
 * @property episodes количество эпизодов
 * @property volumes количестов томов
 * @property chapters количество глав
 * @property text описание
 * @property textHtml описание в виде HTML
 * @property dateCreated дата добавления в пользовательский список
 * @property dateUpdated дата обновления
 */
data class UserRateModel(
    val id: Long? = null,
    val userId: Long? = null,
    val targetId: Long? = null,
    val targetType: SectionType? = null,
    val score: Double? = null,
    val status: RateStatus? = null,
    val rewatches: Int? = null,
    val episodes: Int? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    val text: String? = null,
    val textHtml: String? = null,
    val dateCreated: String? = null,
    val dateUpdated: String? = null
)
