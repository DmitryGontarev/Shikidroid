package com.shikidroid.data.network.entity.user

import com.google.gson.annotations.SerializedName

/**
 * Сущность с обновлёнными данными
 * при запросе на увеличение количества просмотренных/прочитанных эпизодов/глав
 *
 * @property id id элемента списка
 * @property userId id пользователя
 * @property targetId id
 * @property targetType тип Аниме/Манга
 * @property score оценка пользователя
 * @property status статус чтения или просмотра
 * @property rewatches количество повторных просмотров
 * @property episodes количество просмотренных эпизодов аниме
 * @property volumes количество прочитанных томов
 * @property chapters количество прочитанных глав манги
 * @property text комментарий
 * @property textHtml комментарий
 * @property createdDateTime дата добавления в пользовательский список
 * @property updatedDateTime дата обновления в пользовательском списке
 */
data class IncrementResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("user_id")
    val userId: Long?,
    @SerializedName("target_id")
    val targetId: Long?,
    @SerializedName("target_type")
    val targetType: String?,
    @SerializedName("score")
    val score: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("rewatches")
    val rewatches: Int?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("text_html")
    val textHtml: String?,
    @SerializedName("created_at")
    val createdDateTime : String?,
    @SerializedName("updated_at")
    val updatedDateTime : String?,
)
