package com.shikidroid.domain.models.anime

/**
 * Модель данных с информацией о видеоматериалах к аниме
 *
 * @property id номер в списке
 * @property url ссылка на видео
 * @property imageUrl ссылка на картинку превью
 * @property playerUrl ссылка на плеер
 * @property name название видео
 * @property type тип видео
 * @property hosting название видеохостинга
 */
data class AnimeVideoModel(
    val id: Long,
    val url: String?,
    val imageUrl: String?,
    val playerUrl: String?,
    val name: String?,
    val type: AnimeVideoType?,
    val hosting: String?
)
