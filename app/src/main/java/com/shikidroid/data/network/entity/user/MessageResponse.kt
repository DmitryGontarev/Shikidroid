package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName
import com.shikidroid.data.network.entity.common.LinkedContentResponse

/**
 * Сущность с информацией сообщения
 *
 * @property id id сообщения
 * @property type тип статуса релиза в сообщении (inbox, private, sent, news, anons, ongoing)
 * @property read прочитано или нет
 * @property body текст сообщения
 * @property htmlBody текст сообщения в формате HTML
 * @property dateCreated дата сообщения
 * @property linkedId id прикреплённого контента
 * @property linkedType тип прикреплённого контента (аниме, манга, ранобэ)
 * @property linked прикреплённый контент (аниме, манга, ранобэ)
 * @property userFrom от какого пользователя сообщение
 * @property userTo кому сообщение
 */
data class MessageResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("kind")
    val type: String,
    @SerializedName("read")
    val read: Boolean,
    @SerializedName("body")
    val body: String,
    @SerializedName("html_body")
    val htmlBody: String,
    @SerializedName("created_at")
    val dateCreated: String,
    @SerializedName("linked_id")
    val linkedId: Long,
    @SerializedName("linked_type")
    val linkedType: String,
    @SerializedName("linked")
    val linked: LinkedContentResponse?,
    @SerializedName("from")
    val userFrom: UserBriefResponse,
    @SerializedName("to")
    val userTo: UserBriefResponse
)
