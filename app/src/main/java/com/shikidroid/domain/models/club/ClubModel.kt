package com.shikidroid.domain.models.club

import com.shikidroid.domain.models.common.ImageModel

/**
 * Модель клуба
 *
 * [id] id клуба
 * [name] название клуба
 * [image] логотип клуба
 * [isCensored] есть ли ограничение по возрасту
 * [policyJoin] политика вступления в клуб
 * [policyComment] политика возможности оставлять комментарии
 */
data class ClubModel(
    val id: Long,
    val name: String,
    val image: ImageModel,
    val isCensored: Boolean,
    val policyJoin: ClubPolicy?,
    val policyComment: ClubCommentPolicy?
)


