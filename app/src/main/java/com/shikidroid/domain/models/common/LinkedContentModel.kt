package com.shikidroid.domain.models.common

/**
 * Модель с прикреплённом контенте об аниме, манге, ранобэ для уведомления в сообщениях
 *
 * @property id id номер аниме
 * @property topicUrl ссылка на топик
 * @property threadId id треда
 * @property topicId id топика
 * @property productType тип произведения (аниме, манга, ранобэ)
 * @property name название аниме
 * @property nameRu название аниме на русском
 * @property image ссылка на постер аниме
 * @property url ссылка на страницу сайта с аниме
 * @property type тип релиза аниме, манги, ранобэ (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48, manga, manhwa, manhua, novel, oneshot, doujin)
 * @property score оцена аниме по 10-тибалльной шкале
 * @property status статус релиза (anons, ongoing, released)
 * @property episodes общее количество эпизодов
 * @property episodesAired количество вышедших эпизодов
 * @property volumes количество томов
 * @property chapters количество глав
 * @property dateAired дата начала трансляции
 * @property dateReleased дата выхода
 */
data class LinkedContentModel(
    val id: Long?,
    val topicUrl: String?,
    val threadId: Long?,
    val topicId: Long?,
    val productType: SectionType?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?,
    val type: LinkedContentProductType?,
    val score : Double?,
    val status: String?,
    val episodes: Int?,
    val episodesAired: Int?,
    val volumes: Int?,
    val chapters: Int?,
    val dateAired: String?,
    val dateReleased: String?
)
