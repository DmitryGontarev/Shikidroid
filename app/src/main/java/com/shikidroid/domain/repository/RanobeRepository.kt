package com.shikidroid.domain.repository

import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.rates.RateStatus
import io.reactivex.rxjava3.core.Single

/**
 * Интерфейс репозитория получения данных о ранобэ
 */
internal interface RanobeRepository {

    /**
     * Получение списка манги по указанным параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, volumes, chapters, status, random, created_at, created_at_desc) (необязательно)
     * @param status тип релиза (anons, ongoing, released, paused, discontinued) (необязательно)
     * @param season сезон выхода манги (summer_2017, spring_2016,fall_2016, 2016,!winter_2016, 2016, 2014_2016, 199x) (необязательно)
     * @param score минимальная оценка манги (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param publisher список и издателями манги
     * @param franchise список с названиями франшиз манги (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param ids список id номеров манги (необязательно)
     * @param excludeIds список id номеров манги (необязательно)
     * @param search поисковая фраза для фильтрации манги по имени (name) (необязательно)
     */
    fun getRanobeListByParameters(
        page: Int?,
        limit: Int?,
        order: MangaSearchType?,
        status: List<AiredStatus>?,
        season: List<Pair<String?, String?>>?,
        score: Double?,
        genre: List<Long>?,
        publisher: List<String>?,
        franchise: List<String>?,
        censored: Boolean?,
        myList: List<RateStatus>?,
        ids: List<String>?,
        excludeIds: List<String>?,
        search: String?
    ): Single<List<MangaModel>>

    /**
     * Получить информацию о ранобэ по ID
     *
     * @param id id номер манги
     */
    fun getRanobeDetailsById(id: Long): Single<MangaDetailsModel>

    /**
     * Получить информацию о людях, принимавших участие в создании ранобэ по id
     *
     * @param id id аниме
     */
    fun getRanobeRolesById(id: Long): Single<List<RolesModel>>

    /**
     * Получить список похожих ранобэ по ID
     *
     * @param id id аниме
     */
    fun getSimilarRanobe(id: Long): Single<List<MangaModel>>

    /**
     * Получить список ранобэ, связанным с текущим
     *
     * @param id id аниме
     */
    fun getRelatedRanobe(id: Long): Single<List<RelatedModel>>

    /**
     * Получить список франшизы
     *
     * @param id id манги
     */
    fun getRanobeFranchise(id: Long): Single<FranchiseModel>

    /**
     * Получить внешние ссылки на произведение
     *
     * @param id id манги
     */
    fun getRanobeExternalLinksById(id: Long): Single<List<LinkModel>>

}