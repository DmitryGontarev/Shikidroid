package com.shikidroid.data.network.converters

import com.shikidroid.appconstants.BaseUrl
import com.shikidroid.data.network.entity.myanimelist.AlternativeTitlesEntity
import com.shikidroid.data.network.entity.myanimelist.AnimeMalEntity
import com.shikidroid.data.network.entity.myanimelist.BroadcastEntity
import com.shikidroid.data.network.entity.myanimelist.MainPictureEntity
import com.shikidroid.data.network.entity.myanimelist.RelatedAnimeEntity
import com.shikidroid.domain.models.myanimelist.AlternativeTitlesModel
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.BroadcastModel
import com.shikidroid.domain.models.myanimelist.MainPictureModel
import com.shikidroid.domain.models.myanimelist.RelatedAnimeModel
import com.shikidroid.utils.appendHost

/**
 * конвертация [AnimeMalEntity] в модель domain слоя
 */
fun AnimeMalEntity.toDomainModel(): AnimeMalModel {
    return AnimeMalModel(
        id = id,
        title = title,
        synopsys = synopsys,
        image = image?.toDomainModel(),
        alternativeTitles = alternativeTitles?.toDomainModel(),
        dateAired = dateAired,
        dateReleased = dateReleased,
        genres = genres?.map { it.toDomainModel() },
        type = type?.toDomainEnumAnimeType(),
        status = status?.toDomainEnumAiredStatus(),
        episodes = episodes,
        broadcast = broadcast?.toDomainModel(),
        episodeDuration = episodeDuration,
        ageRating = ageRating?.toDomainEnumAgeRating(),
        pictures = pictures?.map { it.toDomainModel() },
        backgroundInfo = backgroundInfo,
        relatedAnime = relatedAnime?.map { it.toDomainModel() },
        recommendations = recommendations?.map { it.anime.toDomainModel() },
        studios = studios?.map { it.toDomainMoodle() },
        score = score ?: 0.0
    )
}


/**
 * конвертация [MainPictureEntity] в модель domain слоя
 */
fun MainPictureEntity.toDomainModel() =
    MainPictureModel(
        medium = medium?.appendHost(baseUrl = BaseUrl.MY_ANIME_LIST_BASE_URL),
        large = large?.appendHost(baseUrl = BaseUrl.MY_ANIME_LIST_BASE_URL)
    )

/**
 * конвертация [AlternativeTitlesEntity] в модель domain слоя
 */
fun AlternativeTitlesEntity.toDomainModel() =
    AlternativeTitlesModel(
        synonyms = synonyms,
        en = en,
        ja = ja
    )

/**
 * конвертация [BroadcastEntity] в модель domain слоя
 */
fun BroadcastEntity.toDomainModel() =
    BroadcastModel(
        dayOfTheWeek = dayOfTheWeek,
        startTime = startTime
    )

/**
 * конвертация [RelatedAnimeEntity] в модель domain слоя
 */
fun RelatedAnimeEntity.toDomainModel() =
    RelatedAnimeModel(
        anime = anime?.toDomainModel(),
        relationType = relationType,
        relationTypeFormatted = relationTypeFormatted
    )