package com.shikidroid.data.network.entity.rates

import com.google.gson.annotations.SerializedName

/**
 * Сущность элемента списка пользовательского рейтинга
 *
 * @property id id номер элемента
 * @property userId id номер пользователя
 * @property targetId id номер элемента списка
 * @property targetType тип произведения (Anime, Manga, Ranobe)
 * @property score оценка
 * @property status в каком статусе (watched, planned)
 * @property rewatches количество повторных просмотров
 * @property episodes количество эпизодов
 * @property volumes количестов томов
 * @property chapters количество глав
 * @property text описание
 * @property textHtml описание в виде HTML
 * @property dateCreated дата добавления в пользовательский список
 * @property dateUpdated дата обновления
 */
data class UserRateResponse(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("user_id")
    val userId: Long? = null,
    @SerializedName("target_id")
    val targetId: Long? = null,
    @SerializedName("target_type")
    val targetType: String? = null,
    @SerializedName("score")
    val score: Double? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("rewatches")
    val rewatches: Int? = null,
    @SerializedName("episodes")
    val episodes: Int? = null,
    @SerializedName("volumes")
    val volumes: Int? = null,
    @SerializedName("chapters")
    val chapters: Int? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("text_html")
    val textHtml: String? = null,
    @SerializedName("created_at")
    val dateCreated: String? = null,
    @SerializedName("updated_at")
    val dateUpdated: String? = null
)
