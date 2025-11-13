package com.shikidroid.data.network.entity.roles

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.manga.MangaResponse

/**
 * Сущность со списком персонажей, которых озвучивал актёр
 *
 * @property characters список персонажей
 * @property animes список аниме
 * @property mangas список манги
 */
data class SeyuRoleResponse(
    @SerializedName("characters")
    val characters: List<CharacterResponse>?,
    @SerializedName("animes")
    val animes: List<AnimeResponse>?,
    @SerializedName("mangas")
    val mangas: List<MangaResponse>?
)
