package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.common.ImageModel
import com.shikidroid.domain.models.manga.MangaModel

/**
 * Модель с детельной информацией о персонаже
 *
 * @property id номер персонажа
 * @property name имя персонажа
 * @property nameRu имя персонажа на русском
 * @property image ссылки на картинки с персонажем
 * @property url ссылка на персонажа
 * @property nameAlt альтернативное имя
 * @property nameJp имя на японском
 * @property description описание
 * @property descriptionSource источник описания
 * @property favoured флаг есть ли в списке избранного
 * @property threadId идентификацонный номер треда
 * @property topicId идентификацонный номер топика
 * @property dateUpdated дата обновления
 * @property seyu акётр озвучивания
 * @property animes список аниме с персонажем
 * @property mangas список манги с персонажем
 */
data class CharacterDetailsModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?,
    val nameAlt: String?,
    val nameJp: String?,
    val description: String?,
    val descriptionHtml: String?,
    val descriptionSource: String?,
    val favoured: Boolean?,
    val threadId: Long?,
    val topicId: Long?,
    val dateUpdated: String?,
    val seyu: List<PersonModel>?,
    val animes: List<AnimeModel>?,
    val mangas: List<MangaModel>?
)
