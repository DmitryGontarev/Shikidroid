package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.manga.MangaDetailsResponse
import com.shikidroid.data.network.entity.manga.MangaResponse
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.utils.appendHost

/**
 * конвертация [MangaResponse] в модель domain слоя
 *
 * @return [MangaModel]
 */
fun MangaResponse.toDomainModel() =
    MangaModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        type = type?.toDomainEnumMangaType(),
        score = score,
        status = status?.toDomainEnumAiredStatus(),
        volumes = volumes,
        chapters = chapters,
        dateAired = dateAired,
        dateReleased = dateReleased
    )

/**
 * конвертация [MangaDetailsResponse] в модель domain слоя
 *
 * @return [MangaDetailsModel]
 */
fun MangaDetailsResponse.toDomainModel() =
    MangaDetailsModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        type = type?.toDomainEnumMangaType(),
        score = score,
        status = status?.toDomainEnumAiredStatus(),
        volumes = volumes,
        chapters = chapters,
        dateAired = dateAired,
        dateReleased = dateReleased,
        namesEnglish = namesEnglish,
        namesJapanese = namesJapanese,
        synonyms = synonyms,
        description = description,
        descriptionHtml = descriptionHtml,
        descriptionSource = descriptionSource,
        franchise = franchise,
        favoured = favoured,
        anons = anons,
        ongoing = ongoing,
        threadId = threadId,
        topicId = topicId,
        myAnimeListId = myAnimeListId,
        rateScoresStats = rateScoresStats?.map { it.toDomainModel() },
        rateStatusesStats = rateStatusesStats?.map { it.toDomainModel() },
        genres = genres?.map { it.toDomainModel() },
        userRate = userRate?.toDomainModel()
    )