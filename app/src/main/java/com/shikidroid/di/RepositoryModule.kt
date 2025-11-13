package com.shikidroid.di

import com.shikidroid.data.local.DownloadVideoRepositoryImpl
import com.shikidroid.data.local.SettingsRepositoryImpl
import com.shikidroid.data.local.UserLocalRepositoryImpl
import com.shikidroid.data.network.repository.*
import com.shikidroid.data.network.repository.AnimeRepositoryImpl
import com.shikidroid.data.network.repository.AuthRepositoryImpl
import com.shikidroid.data.network.repository.MangaRepositoryImpl
import com.shikidroid.data.local.TokenLocalRepositoryImpl
import com.shikidroid.data.network.repository.UserRepositoryImpl
import com.shikidroid.domain.repository.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Dagger модуль для репозиториев
 */
@Module
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindTokenRepository(tokenRepository: TokenLocalRepositoryImpl): TokenLocalRepository

    @Binds
    @Singleton
    fun bindSettingsRepository(settingsRepository: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindUserProfileStorage(userProfileStorage: UserLocalRepositoryImpl): UserLocalRepository

    @Binds
    @Singleton
    fun bindAnimeRepository(animeRepository: AnimeRepositoryImpl): AnimeRepository

    @Binds
    @Singleton
    fun bindMangaRepository(mangaRepository: MangaRepositoryImpl): MangaRepository

    @Binds
    @Singleton
    fun bindRanobeRepository(ranobeRepository: RanobeRepositoryImpl): RanobeRepository

    @Binds
    @Singleton
    fun bindCharacterRepository(characterRepository: CharacterRepositoryImpl): CharactersRepository

    @Binds
    @Singleton
    fun bindPeopleRepository(peopleRepository: PeopleRepositoryImpl): PeopleRepository

    @Binds
    @Singleton
    fun bindShimoriVideoRepository(shimoriVideoRepository: ShimoriVideoRepositoryImpl): ShimoriVideoRepository

    @Binds
    @Singleton
    fun bindDownloadVideoRepository(downloadVideoRepository: DownloadVideoRepositoryImpl): DownloadVideoRepository

    @Binds
    @Singleton
    fun bindCalendarRepository(calendarRepository: CalendarRepositoryImpl): CalendarRepository

    @Binds
    @Singleton
    fun bindCommentsRepository(commentsRepository: CommentsRepositoryImpl): CommentsRepository

    @Binds
    @Singleton
    fun bindMyAnimeListRepository(myAnimeListRepository: MyAnimeListRepositoryImpl): MyAnimeListRepository
}