package com.shikidroid.data.network.converters

import com.shikidroid.data.network.entity.rates.UserRateCreateOrUpdateRequest
import com.shikidroid.data.network.entity.rates.UserRateResponse
import com.shikidroid.domain.models.rates.UserRateCreateOrUpdateModel
import com.shikidroid.domain.models.rates.UserRateModel

/**
 * Конвертация [UserRateCreateOrUpdateModel] в сущность data-слоя
 */
fun UserRateCreateOrUpdateModel.toDataModel() =
    UserRateCreateOrUpdateRequest(
        userRate = userRate.toDataModel()
    )

/**
 * Конвертация [UserRateModel] в сущность data-слоя
 */
fun UserRateModel.toDataModel() =
    UserRateResponse(
        id = id,
        userId = userId,
        targetId = targetId,
        targetType = targetType?.toStringRequest(),
        score = score,
        status = status?.toStringRequest(),
        rewatches = rewatches,
        episodes = episodes,
        volumes = volumes,
        chapters = chapters,
        text = text,
        textHtml = textHtml,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated
    )