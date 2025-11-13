package com.shikidroid.data.network.entity.common

import com.google.gson.annotations.SerializedName

/**
 * Сущность с информацией о франшие аниме
 *
 * @property relations списко связанных произведений
 * @property nodes список связей
 * @property currentId текущий id
 */
data class FranchiseResponse(
    @SerializedName("links")
    val relations : List<FranchiseRelationResponse>,
    @SerializedName("nodes")
    val nodes: List<FranchiseNodeResponse>,
    @SerializedName("current_id")
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
data class FranchiseRelationResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("source_id")
    val sourceId: Long,
    @SerializedName("target_id")
    val targetId: Long,
    @SerializedName("source")
    val sourceNodeIndex: Int,
    @SerializedName("target")
    val targetNodeIndex: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("relation")
    val relation: String?
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
data class FranchiseNodeResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("date")
    val date: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("url")
    val url: String,
    @SerializedName("year")
    val year: Int?,
    @SerializedName("kind")
    val type: String?,
    @SerializedName("weight")
    val weight: Int
)