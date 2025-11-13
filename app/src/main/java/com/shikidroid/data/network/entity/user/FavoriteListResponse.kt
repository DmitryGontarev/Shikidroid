package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией списка с избранным пользователя
 *
 * @property animes список избранного аниме
 * @property mangas список избранной манги
 * @property characters список избранных персонажей
 * @property people список избранных людей
 * @property mangakas список избранных мангак
 * @property seyu список избранных сэйю
 * @property producers список избранных продюсеров
 */
data class FavoriteListResponse(
    @SerializedName("animes")
    val animes: List<FavoriteResponse>,
    @SerializedName("mangas")
    val mangas: List<FavoriteResponse>,
    @SerializedName("characters")
    val characters: List<FavoriteResponse>,
    @SerializedName("people")
    val people: List<FavoriteResponse>,
    @SerializedName("mangakas")
    val mangakas: List<FavoriteResponse>,
    @SerializedName("seyu")
    val seyu: List<FavoriteResponse>,
    @SerializedName("producers")
    val producers: List<FavoriteResponse>
)

/**
 * Сущность с информацией пункта спсика избранного пользователя
 *
 * @property id id произведения
 * @property name название произведения из списка
 * @property nameRu навзание на русском
 * @property image ссылка на картинку
 * @property url ссылка
 */
data class FavoriteResponse(
    @SerializedName("id")
    val id : Long,
    @SerializedName("name")
    val name : String,
    @SerializedName("russian")
    val nameRu : String?,
    @SerializedName("image")
    val image : String,
    @SerializedName("url")
    val url : String?
)
