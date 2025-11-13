package com.shikidroid.domain.models.roles

import com.shikidroid.domain.models.common.ImageModel

/**
 * Модель с информацией о персонаже
 *
 * @property id номер персонажа
 * @property name имя персонажа
 * @property nameRu имя персонажа на русском
 * @property image ссылки на картинки с персонажем
 * @property url ссылка на персонажа
 */
data class CharacterModel(
    val id: Long?,
    val name: String?,
    val nameRu: String?,
    val image: ImageModel?,
    val url: String?
)
