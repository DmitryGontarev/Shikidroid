package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel

/**
 * Модель со списком персонажей, которых озвучивал актёр
 *
 * @property characters список персонажей
 * @property animes список аниме
 * @property mangas список манги
 */
data class SeyuRoleModel(
    val characters: List<CharacterModel>?,
    val animes: List<AnimeModel>?,
    val mangas: List<MangaModel>?
)
