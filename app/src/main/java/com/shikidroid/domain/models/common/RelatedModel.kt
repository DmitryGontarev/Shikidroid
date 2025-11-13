package com.shikidroid.domain.models.common

import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel

/**
 * Модель с информацией о аниме/манге связанных с текущим
 *
 * @property relation названия типа связи (например Адаптация)
 * @property relationRu названия связи на русском
 * @property anime связанное аниме, если есть
 * @property manga связанная манга, если есть
 */
data class RelatedModel(
    val relation : String?,
    val relationRu : String?,
    val anime : AnimeModel?,
    val manga : MangaModel?
)
