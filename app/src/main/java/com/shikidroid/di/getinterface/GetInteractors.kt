package com.shikidroid.di.getinterface

import com.shikidroid.domain.interactors.*
import com.shikidroid.domain.interactors.TokenLocalInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor

/**
 * Интерфейс с методами получения интеракторов для Dagger компонента
 */
internal interface GetInteractors {

    fun getTokenInteractor(): TokenLocalInteractor

    fun getAuthInteractor(): AuthInteractor

    fun getUserInteractor(): UserInteractor

    fun getUserProfileStorageInteractor(): UserLocalInteractor

    fun getAnimeInteractor(): AnimeInteractor

    fun getMangaInteractor(): MangaInteractor

    fun getRanobeInteractor(): RanobeInteractor

    fun getCharacterInteractor(): CharacterInteractor

    fun getPeopleInteractor(): PeopleInteractor

    fun getShimoriVideoInteractor(): ShimoriVideoInteractor

    fun getDownloadVideoInteractor(): DownloadVideoInteractor

    fun getCalendarInteractor(): CalendarInteractor

    fun getCommentsInteractor(): CommentsInteractor

    fun getMyAnimeListInteractor(): MyAnimeListInteractor
}