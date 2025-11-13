package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.roles.WorkResponse
import com.shikidroid.data.network.entity.roles.*
import com.shikidroid.domain.models.roles.*
import com.shikidroid.utils.appendHost

/**
 * конвертация [CharacterDetailsResponse] в модель domain слоя
 *
 * @return [CharacterDetailsModel]
 */
fun CharacterDetailsResponse.toDomainModel() =
    CharacterDetailsModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        nameAlt = nameAlt,
        nameJp = nameJp,
        description = description,
        descriptionHtml = descriptionHtml,
        descriptionSource = descriptionSource,
        favoured = favoured,
        threadId = threadId,
        topicId = topicId,
        dateUpdated = dateUpdated,
        seyu = seyu?.map { it.toDomainModel() },
        animes = animes?.map { it.toDomainModel() },
        mangas = mangas?.map { it.toDomainModel() }
    )

/**
 * конвертация [CharacterResponse] в модель domain слоя
 *
 * @return [CharacterModel]
 */
fun CharacterResponse.toDomainModel() =
    CharacterModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost()
    )

/**
 * конвертация [PersonDetailsResponse] в модель domain слоя
 *
 * @return [PersonDetailsModel]
 */
fun PersonDetailsResponse.toDomainModel() =
    PersonDetailsModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost(),
        nameJp = nameJp,
        jobTitle = jobTitle,
        birthOn = birthOn?.toDomainModel(),
        deceasedOn = deceasedOn?.toDomainModel(),
        website = website,
        rolesGrouped = rolesGrouped,
        roles = roles?.map {
            it.toDomainModel()
        },
        works = works?.map {
            it.toDomainModel()
        },
        topicId = topicId,
        isFavoritePerson = isFavoritePerson,
        isProducer = isProducer,
        isFavoriteProducer = isFavoriteProducer,
        isMangaka = isMangaka,
        isFavoriteMangaka = isFavoriteMangaka,
        isSeyu = isSeyu,
        isFavoriteSeyu = isFavoriteSeyu,
        updatedAt = updatedAt,
        threadId = threadId,
        birthday = birthday?.toDomainModel()
    )

/**
 * конвертация [PersonResponse] в модель domain слоя
 *
 * @return [PersonModel]
 */
fun PersonResponse.toDomainModel() =
    PersonModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image?.toDomainModel(),
        url = url?.appendHost()
    )

/**
 * конвертация [SeyuRoleResponse] в модель domain слоя
 *
 * @return [SeyuRoleModel]
 */
fun SeyuRoleResponse.toDomainModel() =
    SeyuRoleModel(
        characters = characters?.map {
            it.toDomainModel()
        },
        animes = animes?.map {
            it.toDomainModel()
        },
        mangas = mangas?.map {
            it.toDomainModel()
        }
    )

/**
 * конвертация [WorkResponse] в модель domain слоя
 *
 * @return [WorkModel]
 */
fun WorkResponse.toDomainModel() =
    WorkModel(
        anime = anime?.toDomainModel(),
        manga = manga?.toDomainModel(),
        role = role
    )

/**
 * конвертация [RoleDateResponse] в модель domain слоя
 *
 * @return [RoleDateModel]
 */
fun RoleDateResponse.toDomainModel() =
    RoleDateModel(
        day = day,
        year = year,
        month = month
    )