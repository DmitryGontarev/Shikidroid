package com.shikidroid.domain.interactors

import com.shikidroid.domain.models.anime.*
import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.repository.AnimeRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Интерфейс интерактора получения данных об аниме
 */
internal interface AnimeInteractor {

    /**
     * Получение списка аниме по указанным параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, episodes, status, random) (необязательно)
     * @param kind тип аниме (tv, movie, ova, ona, special, music, tv_13, tv_24, tv_48) (необязательно)
     * @param status тип релиза (anons, ongoing, released) (необязательно)
     * @param season сезон выхода аниме (summer_2017, 2016, 2014_2016) (необязательно)
     * @param score оценка аниме (необязательно)
     * @param duration длительность аниме (S, D, F) (необязательно)
     * @param rating возрастной рейтинг (none, g, pg, pg_13, r, r_plus, rx) (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param studio список студий, работавших над аниме (необязательно)
     * @param franchise список с названиями франшиз аниме (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param ids список id номеров аниме (необязательно)
     * @param excludeIds список id номеров аниме (необязательно)
     * @param search поисковая фраза для фильтрации аниме по имени (name) (необязательно)
     */
    fun getAnimeListByParameters(
        page: Int? = null,
        limit: Int? = null,
        order: AnimeSearchType? = null,
        kind: List<AnimeType>? = null,
        status: List<AiredStatus>? = null,
        season: List<Pair<String?, String?>>? = null,
        score: Double? = null,
        duration: List<AnimeDurationType>? = null,
        rating: List<AgeRatingType>? = null,
        genre: List<Long>? = null,
        studio: List<Long>? = null,
        franchise: List<String>? = null,
        censored: Boolean? = null,
        myList: List<RateStatus>? = null,
        ids: List<String>? = null,
        excludeIds: List<String>? = null,
        search: String? = null
    ): Single<MutableList<AnimeModel>>

    /**
     * Получить информациб об аниме по ID
     *
     * @param id id номер аниме
     */
    fun getAnimeDetailsById(id : Long) : Single<AnimeDetailsModel>

    /**
     * Получить информацию о людях, принимавших участие в создании аниме по id
     *
     * @param id id аниме
     */
    fun getAnimeRolesById(id: Long): Single<List<RolesModel>>

    /**
     * Получить список похожих аниме по ID
     *
     * @param id id аниме
     */
    fun getSimilarAnime(id: Long): Single<List<AnimeModel>>

    /**
     * Получить список аниме, связанных с текущим
     *
     * @param id id аниме
     */
    fun getRelatedAnime(id: Long): Single<List<RelatedModel>>

    /**
     * Получить скриншоты текущего аниме по id
     *
     * @param id id аниме
     */
    fun getAnimeScreenshotsById(id: Long): Single<List<ScreenshotModel>>

    /**
     * Получить список франшизы
     *
     * @param id id аниме
     */
    fun getAnimeFranchise(id: Long): Single<FranchiseModel>

    /**
     * Получить внешние ссылки на произведение
     *
     * @param id id аниме
     */
    fun getAnimeExternalLinksById(id: Long): Single<List<LinkModel>>

    /**
     * Получить список видео относящихся к аниме
     *
     * @param id id аниме
     */
    fun getAnimeVideos(id: Long): Single<List<AnimeVideoModel>>
}

/**
 * Реализация интерактора [AnimeInteractor]
 *
 * @property animeRepository репозиторий для получения данных об аниме
 */
internal class AnimeInteractorImpl @Inject constructor(
    private val animeRepository: AnimeRepository
) : AnimeInteractor {

    override fun getAnimeListByParameters(
        page: Int?,
        limit: Int?,
        order: AnimeSearchType?,
        kind: List<AnimeType>?,
        status: List<AiredStatus>?,
        season: List<Pair<String?, String?>>?,
        score: Double?,
        duration: List<AnimeDurationType>?,
        rating: List<AgeRatingType>?,
        genre: List<Long>?,
        studio: List<Long>?,
        franchise: List<String>?,
        censored: Boolean?,
        myList: List<RateStatus>?,
        ids: List<String>?,
        excludeIds: List<String>?,
        search: String?
    ): Single<MutableList<AnimeModel>> {
        return animeRepository.getAnimeListByParameters(
            page = page,
            limit = limit,
            order = order,
            kind = kind,
            status = status,
            season = season,
            score = score,
            duration = duration,
            rating = rating,
            genre = genre,
            studio = studio,
            franchise = franchise,
            censored = censored,
            myList = myList,
            ids = ids,
            excludeIds = excludeIds,
            search = search
        )
    }

    override fun getAnimeDetailsById(id: Long): Single<AnimeDetailsModel> {
        return animeRepository.getAnimeDetailsById(id = id)
    }

    override fun getAnimeRolesById(id: Long): Single<List<RolesModel>> {
        return animeRepository.getAnimeRolesById(id = id)
    }

    override fun getSimilarAnime(id: Long): Single<List<AnimeModel>> {
        return animeRepository.getSimilarAnime(id = id)
    }

    override fun getRelatedAnime(id: Long): Single<List<RelatedModel>> {
        return animeRepository.getRelatedAnime(id = id)
    }

    override fun getAnimeScreenshotsById(id: Long): Single<List<ScreenshotModel>> {
        return animeRepository.getAnimeScreenshotsById(id = id)
    }

    override fun getAnimeFranchise(id: Long): Single<FranchiseModel> {
        return animeRepository.getAnimeFranchise(id = id)
    }

    override fun getAnimeExternalLinksById(id: Long): Single<List<LinkModel>> {
        return animeRepository.getAnimeExternalLinksById(id = id)
    }

    override fun getAnimeVideos(id: Long): Single<List<AnimeVideoModel>> {
        return animeRepository.getAnimeVideos(id = id)
    }
}