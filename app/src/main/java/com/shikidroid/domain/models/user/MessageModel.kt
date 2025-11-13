package com.shikidroid.domain.models.user

import com.shikidroid.domain.models.common.LinkedContentModel

/**
 * Модель с информацией сообщения
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
data class MessageModel(
    val id: Long,
    val type: MessageType,
    val read: Boolean,
    val body: String,
    val htmlBody: String,
    val dateCreated: String,
    val linkedId: Long,
    val linkedType: String,
    val linked: LinkedContentModel?,
    val userFrom: UserBriefModel,
    val userTo: UserBriefModel
)
