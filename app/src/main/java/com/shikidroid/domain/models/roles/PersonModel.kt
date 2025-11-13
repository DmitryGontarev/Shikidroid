package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.common.ImageModel

/**
 * Модель с информацией о человеке, принимавшем участиве в создании аниме
 *
 * @property id номер
 * @property name имя
 * @property nameRu имя на русском
 * @property image ссылки на фото
 * @property url ссылка
 */
data class PersonModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?
)
