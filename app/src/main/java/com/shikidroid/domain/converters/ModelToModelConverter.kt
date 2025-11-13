package com.shikidroid.domain.converters

import com.shikidroid.domain.models.anime.AnimeDetailsModel
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.UserRateModel

/**
 * Конвертация [RateModel] в [UserRateModel]
 *
 * @param userId идентификацонный номер пользователя
 */
fun RateModel.toUserRateModel(
    userId: Long? = this.user?.id
): UserRateModel {
    return UserRateModel(
        id = id,
        userId = userId ?: user?.id,
        targetId = anime?.id ?: manga?.id,
        targetType = if (anime?.id != null) SectionType.ANIME else SectionType.MANGA,
        score = score?.toDouble(),
        status = status,
        rewatches = rewatches,
        episodes = episodes,
        volumes = volumes,
        chapters = chapters,
        text = text,
        dateCreated = createdDateTime,
        dateUpdated = updatedDateTime
    )
}

/**
 * Коневертация [AnimeDetailsModel] в [RateModel]
 */
fun AnimeDetailsModel.toAnimeRateModel(): RateModel {
    return RateModel(
        id = userRate?.id,
        score = userRate?.score?.toInt(),
        status = userRate?.status,
        text = userRate?.text,
        episodes = userRate?.episodes,
        chapters = userRate?.chapters,
        volumes = userRate?.volumes,
        textHtml = userRate?.textHtml,
        rewatches = userRate?.rewatches,
        createdDateTime = userRate?.dateCreated,
        updatedDateTime = userRate?.dateUpdated,
        anime = AnimeModel(
            id = id,
            name = name,
            nameRu = nameRu,
            image = image,
            url = url,
            type = type,
            score = score,
            status = status,
            episodes = episodes,
            episodesAired = episodesAired,
            dateAired = dateAired,
            dateReleased = dateReleased
        ),
        manga = null
    )
}

/**
 * Коневертация [MangaDetailsModel] в [RateModel]
 */
fun MangaDetailsModel.toMangaRateModel(): RateModel {
    return RateModel(
        id = userRate?.id,
        score = userRate?.score?.toInt(),
        status = userRate?.status,
        text = userRate?.text,
        episodes = userRate?.episodes,
        chapters = userRate?.chapters,
        volumes = userRate?.volumes,
        textHtml = userRate?.textHtml,
        rewatches = userRate?.rewatches,
        createdDateTime = userRate?.dateCreated,
        updatedDateTime = userRate?.dateUpdated,
        anime = null,
        manga = MangaModel(
            id = id,
            name = name,
            nameRu = nameRu,
            image = image,
            url = url,
            type = type,
            score = score,
            status = status,
            volumes = volumes,
            chapters = chapters,
            dateAired = dateAired,
            dateReleased = dateReleased
        )
    )
}