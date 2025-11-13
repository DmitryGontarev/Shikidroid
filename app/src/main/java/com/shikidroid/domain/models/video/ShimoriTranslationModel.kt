package com.shikidroid.domain.models.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель с информацией типа трансляции эипозда аниме (оригинал, субтитры, озвучка)
 *
 * @param id идентификационный номер эпизода
 * @param kind тип трансляции
 * @param targetId идентификационный номер
 * @param episode порядковый номер эпизода
 * @param url ссылка на эпизод
 * @param hosting навзание хостинга
 * @param language язык трансляции
 * @param author автор загрузки
 * @param quality качество видео
 * @param episodesTotal количество эпизодов
 */
@Parcelize
data class ShimoriTranslationModel(
    val id: Long?,
    val kind: TranslationType?,
    val targetId: Long?,
    val episode: Int?,
    val url: String?,
    val hosting: String?,
    val language: String?,
    val author: String?,
    val quality: String?,
    val episodesTotal: Int?
) : Parcelable
