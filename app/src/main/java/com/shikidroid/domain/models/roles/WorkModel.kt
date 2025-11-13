package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel

/**
 * Модель с информацией об аниме/манги, в создании которого принимал участие человек
 *
 * @param anime аниме
 * @param manga манга
 * @param role роль в создании
 */
data class WorkModel(
    val anime : AnimeModel?,
    val manga : MangaModel?,
    val role : String?
)
