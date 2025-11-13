package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.auth.TokenResponse
import com.shikidroid.data.network.entity.club.ClubResponse
import com.shikidroid.data.network.entity.manga.MangaResponse
import com.shikidroid.data.network.entity.rates.RateResponse
import com.shikidroid.data.network.entity.rates.UserRateResponse
import com.shikidroid.data.network.entity.user.*
import com.shikidroid.domain.models.auth.TokenModel
import com.shikidroid.domain.models.club.ClubModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.UserRateModel
import com.shikidroid.domain.models.user.*


/**
 * конвертация [TokenResponse] в модель domain слоя
 *
 * @return [TokenModel]
 */
fun TokenResponse.toDomainModel(): TokenModel =
    TokenModel(
        accessToken = accessToken,
        refreshToken = refreshToken
    )

/**
 * конвертация [UserBriefResponse] в модель domain слоя
 *
 * @return [UserBriefModel]
 */
fun UserBriefResponse.toDomainModel() =
    UserBriefModel(
        id = id,
        nickname = nickname,
        avatar = avatar,
        image = image.toDomainModel(),
        lastOnlineDate = lastOnlineDate,
        name = name,
        sex = sex,
        website = website,
        birthDate = birtDate,
        locale = locale
    )

/**
 * конвертация [UserImageResponse] в модель domain слоя
 *
 * @return [UserImageModel]
 */
fun UserImageResponse.toDomainModel() =
    UserImageModel(
        x160 = x160,
        x148 = x148,
        x80 = x80,
        x64 = x64,
        x48 = x48,
        x32 = x32,
        x16 = x16
    )

/**
 * конвертация [UserDetailsResponse] в модель domain слоя
 *
 * @return [UserDetailsModel]
 */
fun UserDetailsResponse.toDomainModel() =
    UserDetailsModel(
        id = id,
        nickname = nickname,
        image = image?.toDomainModel(),
        lastOnlineDate = lastOnlineDate,
        name = name,
        sex = sex,
        fullYears = fullYears,
        lastOnline = lastOnline,
        website = website,
        location = location,
        isBanned = isBanned,
        about = about,
        aboutHtml = aboutHtml,
        commonInfo = commonInfo,
        isShowComments = isShowComments,
        isFriend = isFriend,
        isIgnored = isIgnored,
        stats = stats?.toDomainModel(),
        styleId = styleId
    )

/**
 * конвертация [UserStatsResponse] в модель domain слоя
 *
 * @return [UserStatsModel]
 */
fun UserStatsResponse.toDomainModel() =
    UserStatsModel(
        statuses = statuses?.toDomainModel(),
        fullStatuses = fullStatuses?.toDomainModel(),
        scores = scores?.toDomainModel(),
        types = types?.toDomainModel(),
        ratings = ratings?.toDomainModel(),
        hasAnime = hasAnime,
        hasManga = hasManga
    )

/**
 * конвертация [StatusesResponse] в модель domain слоя
 *
 * @return [StatusesModel]
 */
fun StatusesResponse.toDomainModel() =
    StatusesModel(
        anime = anime?.map { it.toDomainModel() },
        manga = manga?.map { it.toDomainModel() }
    )

/**
 * конвертация [StatusResponse] в модель domain слоя
 *
 * @return [StatusesModel]
 */
fun StatusResponse.toDomainModel() =
    StatusModel(
        id = id,
        groupId = groupId?.toDomainEnumRateStatus(),
        name = name?.toDomainEnumRateStatus(),
        size = size,
        type = type?.toDomainEnumSectionType()
    )

/**
 * конвертация [ClubResponse] в модель domain слоя
 *
 * @return [ClubModel]
 */
fun ClubResponse.toDomainModel() =
    ClubModel(
        id = id,
        name = name,
        image = image.toDomainModel(),
        isCensored = isCensored,
        policyJoin = policyJoin?.toDomainEnumClubPolicy(),
        policyComment = policyComment?.toDomainEnumClubCommentPolicy()
    )

/**
 * конвертация [RateResponse] в модель domain слоя
 *
 * @return [RateModel]
 */
fun RateResponse.toDomainModel() =
    RateModel(
        id = id,
        score = score,
        status = status?.toDomainEnumRateStatus(),
        text = text,
        episodes = episodes,
        chapters = chapters,
        volumes = volumes,
        textHtml = textHtml,
        rewatches = rewatches,
        createdDateTime = createdDateTime,
        updatedDateTime = updatedDateTime,
        user = user?.toDomainModel(),
        anime = anime?.toDomainModel(),
        manga = manga?.toDomainModel()
    )

/**
 * конвертация [FavoriteListResponse] в модель domain слоя
 *
 * @return [FavoriteListModel]
 */
fun FavoriteListResponse.toDomainModel() =
    FavoriteListModel(
        animes = animes.map { it.toDomainModel() },
        mangas = mangas.map { it.toDomainModel() },
        characters = characters.map { it.toDomainModel() },
        people = people.map { it.toDomainModel() },
        mangakas = mangakas.map { it.toDomainModel() },
        seyu = seyu.map { it.toDomainModel() },
        producers = producers.map { it.toDomainModel() }
    )

/**
 * конвертация [FavoriteResponse] в модель domain слоя
 *
 * @return [FavoriteModel]
 */
fun FavoriteResponse.toDomainModel() =
    FavoriteModel(
        id = id,
        name = name,
        nameRu = nameRu,
        image = image,
        url = url
    )

/**
 * конвертация [MessageResponse] в модель domain слоя
 *
 * @return [MessageModel]
 */
fun MessageResponse.toDomainModel() =
    MessageModel(
        id = id,
        type = type.toDomainEnumMessageType(),
        read = read,
        body = body,
        htmlBody = htmlBody,
        dateCreated = dateCreated,
        linkedId = linkedId,
        linkedType = linkedType,
        linked = linked?.toDomainModel(),
        userFrom = userFrom.toDomainModel(),
        userTo = userTo.toDomainModel()
    )

/**
 * конвертация [UserUnreadMessagesCountResponse] в модель domain слоя
 *
 * @return [UserUnreadMessagesCountModel]
 */
fun UserUnreadMessagesCountResponse.toDomainModel() =
    UserUnreadMessagesCountModel(
        messages = messages,
        news = news,
        notifications = notifications
    )

/**
 * конвертация [UserHistoryResponse] в модель domain слоя
 *
 * @return [UserHistoryModel]
 */
fun UserHistoryResponse.toDomainModel() =
    UserHistoryModel(
        id = id,
        dateCreated = dateCreated,
        description = description,
        target = target?.toDomainModel()
    )

/**
 * конвертация [UserRateResponse] в модель domain слоя
 *
 * @return [UserRateModel]
 */
fun UserRateResponse.toDomainModel() =
    UserRateModel(
        id = id,
        userId = userId,
        targetId = targetId,
        targetType = targetType?.toDomainEnumSectionType(),
        score = score,
        status = status?.toDomainEnumRateStatus(),
        rewatches = rewatches,
        episodes = episodes,
        volumes = volumes,
        chapters = chapters,
        text = text,
        textHtml = textHtml,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated
    )

/**
 * конвертация [StatisticResponse] в модель domain слоя
 *
 * @return [StatisticModel]
 */
fun StatisticResponse.toDomainModel() =
    StatisticModel(
        name = name,
        value = value
    )

/**
 * конвертация [IncrementResponse] в модель domain слоя
 *
 * @return [IncrementModel]
 */
fun IncrementResponse.toDomainModel() =
    IncrementModel(
        id = id,
        userId = userId,
        targetId = targetId,
        targetType = targetType?.toDomainEnumSectionType(),
        score = score,
        status = status?.toDomainEnumRateStatus(),
        rewatches = rewatches,
        episodes = episodes,
        volumes = volumes,
        chapters = chapters,
        text = text,
        textHtml = textHtml,
        createdDateTime = createdDateTime,
        updatedDateTime = updatedDateTime
    )