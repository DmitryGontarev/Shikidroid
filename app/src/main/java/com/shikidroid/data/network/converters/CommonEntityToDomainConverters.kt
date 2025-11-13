package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.common.*
import com.shikidroid.data.network.entity.common.RolesResponse
import com.shikidroid.domain.models.common.*
import com.shikidroid.utils.appendHost

/**
 * конвертация [ImageResponse] в модель domain слоя
 *
 * @return [ImageModel]
 */
internal fun ImageResponse.toDomainModel() =
    ImageModel(
        original = original?.appendHost(),
        preview = preview?.appendHost(),
        x96 = x96?.appendHost(),
        x48 = x48?.appendHost()
    )

internal fun ImageJsonModel.toDomainModel() =
    ImageModel(
        original = original?.appendHost(),
        preview = preview?.appendHost(),
        x96 = x96?.appendHost(),
        x48 = x48?.appendHost()
    )

/**
 * конвертация [GenreResponse] в модель domain слоя
 *
 * @return [GenreModel]
 */
fun GenreResponse.toDomainModel() =
    GenreModel(
        id = id,
        name = name,
        nameRu = nameRu,
        type = type
    )

/**
 * конвертация [LinkedContentResponse] в модель domain слоя
 *
 * @return [LinkedContentModel]
 */
fun LinkedContentResponse.toDomainModel() =
    LinkedContentModel(
        id = id,
        topicUrl = topicUrl,
        threadId = threadId,
        topicId = topicId,
        productType = productType?.toDomainEnumSectionType(),
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url,
        type = type?.toDomainEnumLinkedContentProductType(),
        score = score,
        status = status,
        episodes = episodes,
        episodesAired = episodesAired,
        volumes = volumes,
        chapters = chapters,
        dateAired = dateAired,
        dateReleased = dateReleased
    )

/**
 * конвертация [RolesResponse] в модель domain слоя
 *
 * @return [RolesModel]
 */
fun RolesResponse.toDomainModel() =
    RolesModel(
        roles = roles,
        rolesRu = rolesRu,
        character = character?.toDomainModel(),
        person = person?.toDomainModel()
    )

/**
 * конвертация [RelatedResponse] в модель domain слоя
 *
 * @return [RelatedModel]
 */
fun RelatedResponse.toDomainModel() =
    RelatedModel(
        relation = relation,
        relationRu = relationRu,
        anime = anime?.toDomainModel(),
        manga = manga?.toDomainModel()
    )

/**
 * конвертация [FranchiseResponse] в модель domain слоя
 *
 * @return [FranchiseModel]
 */
fun FranchiseResponse.toDomainModel() =
    FranchiseModel(
        relations = relations.map { it.toDomainModel() },
        nodes = nodes.map { it.toDomainModel() },
        currentId = currentId
    )

/**
 * конвертация [FranchiseRelationResponse] в модель domain слоя
 *
 * @return [FranchiseRelationModel]
 */
fun FranchiseRelationResponse.toDomainModel() =
    FranchiseRelationModel(
        id = id,
        sourceId = sourceId,
        targetId = targetId,
        sourceNodeIndex = sourceNodeIndex,
        targetNodeIndex = targetNodeIndex,
        weight = weight,
        relation = relation?.toDomainEnumRelationType()
    )

/**
 * конвертация [FranchiseNodeResponse] в модель domain слоя
 *
 * @return [FranchiseNodeModel]
 */
fun FranchiseNodeResponse.toDomainModel() =
    FranchiseNodeModel(
        id = id,
        date = date,
        name = name,
        imageUrl = imageUrl?.appendHost(),
        url = url.appendHost(),
        year = year,
        type = type,
        weight = weight
    )

/**
 * конвертация [LinkResponse] в модель domain слоя
 *
 * @return [LinkModel]
 */
fun LinkResponse.toDomainModel() =
    LinkModel(
        id = id,
        name = name,
        url = url.appendHost()
    )