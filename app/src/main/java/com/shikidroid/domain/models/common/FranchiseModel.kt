package com.shikidroid.domain.models.common

/**
 * Сущность с информацией о франшие аниме
 *
 * @property relations списко связанных произведений
 * @property nodes список связей
 * @property currentId текущий id
 */
data class FranchiseModel(
    val relations : List<FranchiseRelationModel>,
    val nodes: List<FranchiseNodeModel>,
    val currentId: Long
)

/**
 * Сущность с информацией о связанном произведении
 *
 * @property id id произведения
 * @property sourceId id источника
 * @property targetId целевое id
 * @property sourceNodeIndex индекс источника связи
 * @property targetNodeIndex целевой индекс
 * @property weight
 * @property relation тип связи (например adaptation)
 */
data class FranchiseRelationModel(
    val id: Long,
    val sourceId: Long,
    val targetId: Long,
    val sourceNodeIndex: Int,
    val targetNodeIndex: Int,
    val weight: Int,
    val relation: RelationType?
)

/**
 * Сущность с подробной информацией о связанном произведении
 *
 * @property id id произведения
 * @property date дата выхода
 * @property name название
 * @property imageUrl ссылка на картинку
 * @property url ссылка на сайт с произведением
 * @property year год выхода
 * @property type тип произведения (например tv)
 * @property weight
 */
data class FranchiseNodeModel(
    val id: Long,
    val date: Long,
    val name: String,
    val imageUrl: String?,
    val url: String,
    val year: Int?,
    val type: String?,
    val weight: Int
)
