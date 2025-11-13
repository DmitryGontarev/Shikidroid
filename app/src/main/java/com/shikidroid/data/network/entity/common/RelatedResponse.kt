package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.manga.MangaResponse

/**
 * Сущность с информацией о аниме/манге связанных с текущим
 *
 * @property relation названия типа связи (например Адаптация)
 * @property relationRu названия связи на русском
 * @property anime связанное аниме, если есть
 * @property manga связанная манга, если есть
 */
data class RelatedResponse(
    @SerializedName("relation")
    val relation : String?,
    @SerializedName("relation_russian")
    val relationRu : String?,
    @SerializedName("anime")
    val anime : AnimeResponse?,
    @SerializedName("manga")
    val manga : MangaResponse?
)
