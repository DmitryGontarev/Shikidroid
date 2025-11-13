package com.shikidroid.data.network.entity.club

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.ImageResponse

/**
 * Сущность клуба
 *
 * [id] id клуба
 * [name] название клуба
 * [image] логотип клуба
 * [isCensored] есть ли ограничение по возрасту
 * [policyJoin] политика вступления в клуб
 * [policyComment] политика возможности оставлять комментарии
 */
data class ClubResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val image: ImageResponse,
    @SerializedName("is_censored")
    val isCensored: Boolean,
    @SerializedName("join_policy")
    val policyJoin: String?,
    @SerializedName("comment_policy")
    val policyComment: String?
)
