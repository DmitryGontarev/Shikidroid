package com.shikidroid.domain.models.user


/**
 * Модель с информацией списка с избранным пользователя
 *
 * @property animes список избранного аниме
 * @property mangas список избранной манги
 * @property characters список избранных персонажей
 * @property people список избранных людей
 * @property mangakas список избранных мангак
 * @property seyu список избранных сэйю
 * @property producers список избранных продюсеров
 */
data class FavoriteListModel(
    val animes: List<FavoriteModel>,
    val mangas: List<FavoriteModel>,
    val characters: List<FavoriteModel>,
    val people: List<FavoriteModel>,
    val mangakas: List<FavoriteModel>,
    val seyu: List<FavoriteModel>,
    val producers: List<FavoriteModel>
)

/**
 * Модель с информацией пункта спсика избранного пользователя
 *
 * @property id id произведения
 * @property name название произведения из списка
 * @property nameRu навзание на русском
 * @property image ссылка на картинку
 * @property url ссылка
 */
data class FavoriteModel(
    val id : Long,
    val name : String,
    val nameRu : String?,
    val image : String,
    val url : String?
)