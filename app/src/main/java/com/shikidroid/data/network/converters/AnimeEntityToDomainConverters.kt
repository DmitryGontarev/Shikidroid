package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.anime.AnimeDetailsResponse
import com.shikidroid.data.network.entity.anime.AnimeResponse
import com.shikidroid.data.network.entity.anime.AnimeVideoResponse
import com.shikidroid.data.network.entity.anime.ScreenshotResponse
import com.shikidroid.data.network.entity.studio.StudioResponse
import com.shikidroid.domain.models.anime.AnimeDetailsModel
import com.shikidroid.domain.models.anime.AnimeJsonModel
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeVideoModel
import com.shikidroid.domain.models.anime.ScreenshotModel
import com.shikidroid.domain.models.studio.StudioModel
import com.shikidroid.utils.appendHost

/**
 * конвертация [AnimeResponse] в модель domain слоя
 *
 * @return [AnimeModel]
 */
fun AnimeResponse.toDomainModel() =
    AnimeModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        type = type?.toDomainEnumAnimeType(),
        score = score,
        status = status?.toDomainEnumAiredStatus(),
        episodes = episodes,
        episodesAired = episodesAired,
        dateAired = dateAired,
        dateReleased = dateReleased
    )

fun AnimeJsonModel.toDomainModel() =
    AnimeModel(
        id = id,
        name = name,
        nameRu = russian,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        type = kind?.toDomainEnumAnimeType(),
        score = score,
        status = status?.toDomainEnumAiredStatus(),
        episodes = episodes,
        episodesAired = episodes_aired,
        dateAired = aired_on,
        dateReleased = released_on
    )

/**
 * конвертация [AnimeResponse] в модель domain слоя
 *
 * @return [AnimeModel]
 */
fun AnimeDetailsResponse.toDomainModel() =
    AnimeDetailsModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        type = type?.toDomainEnumAnimeType(),
        score = score,
        status = status?.toDomainEnumAiredStatus(),
        episodes = episodes,
        episodesAired = episodesAired,
        dateAired = dateAired,
        dateReleased = dateReleased,
        ageRating = ageRating?.toDomainEnumAgeRating(),
        namesEnglish = namesEnglish,
        namesJapanese = namesJapanese,
        synonyms = synonyms,
        duration = duration,
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
        updatedAt = updatedAt,
        nextEpisodeDate = nextEpisodeDate,
        genres = genres?.map { it.toDomainModel() },
        studios = studios?.map { it.toDomainMoodle() },
        videos = videos?.map { it.toDomainModel() },
        screenshots = screenshots?.map { it.toDomainModel() },
        userRate = userRate?.toDomainModel()
    )

/**
 * конвертация [StudioResponse] в модель domain слоя
 *
 * @return [StudioModel]
 */
fun StudioResponse.toDomainMoodle() =
    StudioModel(
        id = id,
        name = name,
        nameFiltered = nameFiltered,
        isReal = isReal,
        imageUrl = imageUrl?.appendHost()
    )

/**
 * конвертация [AnimeVideoResponse] в модель domain слоя
 *
 * @return [AnimeVideoModel]
 */
fun AnimeVideoResponse.toDomainModel() =
    AnimeVideoModel(
        id = id,
        url = url,
        imageUrl = imageUrl?.appendHost(),
        playerUrl = playerUrl,
        name = name,
        type = type?.toDomainEnumAnimeVideoType(),
        hosting = hosting
    )

/**
 * конвертация [ScreenshotResponse] в модель domain слоя
 *
 * @return [ScreenshotModel]
 */
fun ScreenshotResponse.toDomainModel() =
    ScreenshotModel(
        original = original?.appendHost(),
        preview = preview?.appendHost()
    )