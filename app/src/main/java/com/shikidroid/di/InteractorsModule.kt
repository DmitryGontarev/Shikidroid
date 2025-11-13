package com.shikidroid.di

import com.shikidroid.domain.interactors.*
import com.shikidroid.domain.interactors.AuthInteractorImpl
import com.shikidroid.domain.interactors.TokenLocalInteractor
import com.shikidroid.domain.interactors.TokenLocalInteractorImpl
import com.shikidroid.domain.interactors.UserInteractorImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Dagger модуль для интеракторов
 */
@Module
internal interface InteractorsModule {

    @Binds
    @Singleton
    fun bindTokenInteractor(tokenInteractor: TokenLocalInteractorImpl): TokenLocalInteractor

    @Binds
    @Singleton
    fun bindAuthInteractor(authInteractor: AuthInteractorImpl): AuthInteractor

    @Binds
    @Singleton
    fun bindUserInteractor(userInteractor: UserInteractorImpl): UserInteractor

    @Binds
    @Singleton
    fun bindUserProfileStorageInteractor(userProfileStorageInteractor: UserLocalInteractorImpl): UserLocalInteractor

    @Binds
    @Singleton
    fun bindAnimeInteractor(animeInteractor: AnimeInteractorImpl): AnimeInteractor

    @Binds
    @Singleton
    fun bindMangaInteractor(mangaInteractor: MangaInteractorImpl): MangaInteractor

    @Binds
    @Singleton
    fun bindRanobeInteractor(ranobeInteractor: RanobeInteractorImpl): RanobeInteractor

    @Binds
    @Singleton
    fun bindCharacterInteractor(characterInteractor: CharacterInteractorImpl): CharacterInteractor

    @Binds
    @Singleton
    fun bindPeopleInteractor(peopleInteractor: PeopleInteractorImpl): PeopleInteractor

    @Binds
    @Singleton
    fun bindShimoriVideoInteractor(shimoriVideoInteractor: ShimoriVideoInteractorImpl): ShimoriVideoInteractor

    @Binds
    @Singleton
    fun bindDownloadVideoInteractor(downloadVideoInteractor: DownloadVideoInteractorImpl): DownloadVideoInteractor

    @Binds
    @Singleton
    fun bindCalendarInteractor(calendarInteractor: CalendarInteractorImpl): CalendarInteractor

    @Binds
    @Singleton
    fun bindCommentsInteractor(commentsInteractor: CommentsInteractorImpl): CommentsInteractor

    @Binds
    @Singleton
    fun bindMyAnimeListInteractor(myAnimeListInteractor: MyAnimeListInteractorImpl): MyAnimeListInteractor
}