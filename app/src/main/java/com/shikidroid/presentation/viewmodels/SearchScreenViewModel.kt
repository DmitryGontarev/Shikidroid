package com.shikidroid.presentation.viewmodels

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.appconstants.SettingsExtras
import com.shikidroid.domain.interactors.*
import com.shikidroid.domain.interactors.AnimeInteractor
import com.shikidroid.domain.interactors.CharacterInteractor
import com.shikidroid.domain.interactors.MangaInteractor
import com.shikidroid.domain.interactors.RanobeInteractor
import com.shikidroid.domain.models.anime.AnimeDurationType
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeSearchType
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.models.roles.PersonModel
import com.shikidroid.domain.models.roles.RoleType
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.domain.models.search.SeasonType
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.StringUtils.getNullIfEmpty
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Вью модель экрана Поиск
 *
 * @property screenSearchType тип поиска при открытии экрана
 * @property genreId идентификационный номер жанра
 * @property studioId идентификационный номер студии
 * @property animeInteractor интерактор получения списка аниме
 * @property mangaInteractor интерактор получения списка манги
 * @property ranobeInteractor интерактор получения списка ранобэ
 * @property characterInteractor интерактор получения списка персонажей
 * @property peopleInteractor интерактор получения списка людей
 * @property prefs доступ к локальному хранилищу системы Android
 */
internal class SearchScreenViewModel(
    private val screenSearchType: SearchType? = null,
    private val genreId: Long? = null,
    private val studioId: Long? = null,
    private val animeInteractor: AnimeInteractor,
    private val mangaInteractor: MangaInteractor,
    private val ranobeInteractor: RanobeInteractor,
    private val characterInteractor: CharacterInteractor,
    private val peopleInteractor: PeopleInteractor,
    private val prefs: SharedPreferencesProvider
) : BaseViewModel() {

    /** флаг включена ли цензура */
    val isCensored = MutableLiveData<Boolean>(
        prefs.getBoolean(
            key = AppKeys.SEARCH_SCREEN_CENSORED,
            default = true
        )
    )

    /** статус авторизации пользователя */
    val userAuthorizationStatus = MutableLiveData<String>(
        prefs.getString(
            key = SettingsExtras.USER_STATUS
        )
    )

    /** флаг переключения типа экрана через UI */
    val isSearchTypeChangesByUI = MutableLiveData<Boolean>(false)

    /** тип поиска */
    val searchType = object : MutableLiveData<SearchType>(SearchType.ANIME) {
        override fun setValue(value: SearchType?) {
            super.setValue(value)
            if (isSearchTypeChangesByUI.value == true) {
                fullReset()
            }
        }
    }

    /** флаг открытия бокового меню */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** флаг открытия фильтров поиска */
    val isFilterOpen = MutableLiveData<Boolean>(false)

    /** флаг открытия выпадающего меню фильтров поиска */
    val isSearchTypeOpen = MutableLiveData<Boolean>(false)

    /** флаг очистки фильтров поиска */
    val clearFilters = MutableLiveData<Boolean>(false)

    /** флаг изменения настроек поиска */
    val isSearchSettingsChanged = MutableLiveData(false)

    /** строка для поиска */
    val searchValue = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            value?.let {
                searchSubject.onNext(it)
            }
        }
    }

    /** статус выхода аниме */
    val airedStatus = MutableLiveData<List<AiredStatus>?>(listOf())

    /** выбранный тип фильтра даты выхода: [0 - квартал/год], [1 - год/год] */
    val seasonTab = MutableLiveData<Int>(0)

    /** тип сезонов выход (зима, весна, лето, осень) */
    val seasonTypes = MutableLiveData<List<SeasonType>?>()

    /** год сезона */
    val yearSeason = MutableLiveData<String>("")

    /** год начала периода выхода */
    val yearStart = MutableLiveData<String>("")

    /** год конца периода выхода */
    val yearEnd = MutableLiveData<String>("")

    /** оценка */
    val score = MutableLiveData<Double?>()

    /** возрастной рейтинг */
    val ageRating = MutableLiveData<List<AgeRatingType>?>(listOf())

    /** список жанров */
    val genre = MutableLiveData<List<Long>?>(listOf())

    /** список статусов пользовательского списка */
    val myList = MutableLiveData<List<RateStatus>?>(listOf())

    /** страница аниме для загрузки */
    val nextPage = object : MutableLiveData<Int>() {
        override fun setValue(value: Int?) {
            super.setValue(value)
            value?.let { page ->
                when (searchType.value) {
                    SearchType.ANIME -> {
                        loadAnime(
                            page = page,
                            order = orderAnime.value,
                            kind = kindAnime.value,
                            status = airedStatus.value,
                            season = getSeasonsListPairs(),
                            score = score.value,
                            duration = durationAnime.value,
                            rating = ageRating.value,
                            genre = genre.value,
                            studio = studioAnime.value,
                            censored = isCensored.value,
                            myList = myList.value,
                            search = searchValue.value?.getNullIfEmpty()
                        )
                    }
                    SearchType.MANGA -> {
                        loadManga(
                            page = page,
                            order = orderManga.value,
                            kind = kindManga.value,
                            status = airedStatus.value,
                            season = getSeasonsListPairs(),
                            score = score.value,
                            genre = genre.value,
                            censored = isCensored.value,
                            myList = myList.value,
                            search = searchValue.value?.getNullIfEmpty()
                        )
                    }
                    SearchType.RANOBE -> {
                        loadRanobe(
                            page = page,
                            order = orderManga.value,
                            status = airedStatus.value,
                            season = getSeasonsListPairs(),
                            score = score.value,
                            genre = genre.value,
                            censored = isCensored.value,
                            myList = myList.value,
                            search = searchValue.value?.getNullIfEmpty()
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    /** флаг загрузки полного списка */
    val endReached = MutableLiveData(false)

    ///////////////////////////////////////////////////////////////////////////
    // ANIME SEARCH
    ///////////////////////////////////////////////////////////////////////////

    /** список аниме из поиска */
    val animeSearch = MutableLiveData<SnapshotStateList<AnimeModel>>()

    /** порядок сортировки аниме */
    val orderAnime = MutableLiveData(AnimeSearchType.RANKED)

    /** тип аниме */
    val kindAnime = MutableLiveData<List<AnimeType>?>(listOf())

    /** длительность аниме */
    val durationAnime = MutableLiveData<List<AnimeDurationType>?>(listOf())

    /** список студий */
    val studioAnime = MutableLiveData<List<Long>?>(listOf())

    ///////////////////////////////////////////////////////////////////////////
    // MANGA RANOBE SEARCH
    ///////////////////////////////////////////////////////////////////////////

    /** список манги из поиска */
    val mangaSearch = MutableLiveData<SnapshotStateList<MangaModel>>()

    /** порядок сортировки манги */
    val orderManga = MutableLiveData(MangaSearchType.RANKED)

    /** тип манги */
    val kindManga = MutableLiveData<List<MangaType>?>(listOf())

    ///////////////////////////////////////////////////////////////////////////
    // MANGA RANOBE SEARCH
    ///////////////////////////////////////////////////////////////////////////

    /** список манги из поиска */
    val ranobeSearch = MutableLiveData<SnapshotStateList<MangaModel>>()

    ///////////////////////////////////////////////////////////////////////////
    // CHARACTER SEARCH
    ///////////////////////////////////////////////////////////////////////////

    /** список персонажей из поиска */
    val characterSearch = MutableLiveData<List<CharacterModel>>(listOf())

    ///////////////////////////////////////////////////////////////////////////
    // PEOPLE SEARCH
    ///////////////////////////////////////////////////////////////////////////

    /** роль человека при создании произведения */
    val peopleRole = MutableLiveData<RoleType>(RoleType.SEYU)

    /** список персонажей из поиска */
    val peopleSearch = MutableLiveData<List<PersonModel>>(listOf())

    ///////////////////////////////////////////////////////////////////////////
    // CLEAR BUTTON STATE
    ///////////////////////////////////////////////////////////////////////////

    val genresNotEmpty = MutableLiveData<Boolean>(false)
    val airedStatusNotEmpty = MutableLiveData<Boolean>(false)
    val rateListsNotEmpty = MutableLiveData<Boolean>(false)
    val animeTypesNotEmpty = MutableLiveData<Boolean>(false)
    val animeDurationsNotEmpty = MutableLiveData<Boolean>(false)
    val animeAgeRatingNotEmpty = MutableLiveData<Boolean>(false)
    val mangaTypesNotEmpty = MutableLiveData<Boolean>(false)
    val seasonsNotEmpty = MutableLiveData<Boolean>(false)
    val yearSeasonNotEmpty = MutableLiveData<Boolean>(false)
    val yearStartNotEmpty = MutableLiveData<Boolean>(false)
    val yearEndNotEmpty = MutableLiveData<Boolean>(false)

    /** активность кнопки Сбросить в фильтрах поиска */
    val clearButtonActive = MediatorLiveData<Boolean>().apply {

        fun isClearBtnEnabled(): Boolean {
            return genresNotEmpty.value == true || airedStatusNotEmpty.value == true ||
                    rateListsNotEmpty.value == true || animeTypesNotEmpty.value == true ||
                    animeDurationsNotEmpty.value == true || animeAgeRatingNotEmpty.value == true ||
                    mangaTypesNotEmpty.value == true || seasonsNotEmpty.value == true ||
                    yearSeasonNotEmpty.value == true || yearStartNotEmpty.value == true ||
                    yearEndNotEmpty.value == true || orderAnime.value != AnimeSearchType.RANKED ||
                    orderManga.value != MangaSearchType.RANKED || studioAnime.value.isNullOrEmpty().not()
        }

        addSource(genresNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(airedStatusNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(rateListsNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(animeTypesNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(animeDurationsNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(animeAgeRatingNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(mangaTypesNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(seasonsNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(yearSeasonNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(yearStartNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(yearEndNotEmpty) { postValue(isClearBtnEnabled()) }
        addSource(orderAnime) { postValue(isClearBtnEnabled()) }
        addSource(orderManga) { postValue(isClearBtnEnabled()) }
    }

    /** флаг пустых фильтров */
    val isFiltersClears = MutableLiveData<Boolean>(true)

    private val animeList = mutableListOf<AnimeModel>()
    private val mangaList = mutableListOf<MangaModel>()
    private val ranobeList = mutableListOf<MangaModel>()

    private val animeGenreList = mutableListOf<Int>()

    private val searchSubject = PublishSubject.create<String>()

    init {
        prefs.putBoolean(key = AppKeys.SEARCH_SCREEN_CENSORED, boolean = true)
        genreId?.let {
            genre.value = listOf(it)
            isFiltersClears.value = false
        }
        studioId?.let {
            studioAnime.value = listOf(it)
            isFiltersClears.value = false
        }
        screenSearchType?.let {
            searchType.value = it
        }
        nextPage.value = 1
        initSearchSubject()
    }

    /**
     * Загрузка списка аниме по поисковым параметрам
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
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param search поисковая фраза для фильтрации аниме по имени (name) (необязательно)
     */
    fun loadAnime(
        page: Int? = 1,
        limit: Int? = 30,
        order: AnimeSearchType? = null,
        kind: List<AnimeType>? = null,
        status: List<AiredStatus>? = null,
        season: List<Pair<String?, String?>>? = null,
        score: Double? = null,
        duration: List<AnimeDurationType>? = null,
        rating: List<AgeRatingType>? = null,
        genre: List<Long>? = null,
        studio: List<Long>? = null,
        censored: Boolean? = null,
        myList: List<RateStatus>? = null,
        search: String? = null
    ) {
        animeInteractor.getAnimeListByParameters(
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
            censored = censored,
            myList = myList,
            search = search
        )
            .compose(
                if (isSearchSettingsChanged.value == true) {
                    doAsyncSingleWithProgress()
                } else {
                    doAsyncSingleWithoutBlocking()
                }
            )
            .subscribe({
                when {
                    it.isNullOrEmpty() && nextPage.value == 1 -> {
                        animeList.clear()
                        endReached.value = true
                    }
                    it.isNullOrEmpty() && (nextPage.value ?: 0) > 1 -> {
                        endReached.value = true
                    }
                }
                if (isSearchSettingsChanged.value == true) {
                    animeList.clear()
                    isSearchSettingsChanged.value = false
                }
                it.forEach { anime ->
                    if (!animeList.contains(anime)) {
                        animeList.add(anime)
                    }
                }
                animeSearch.value = animeList.toMutableStateList()
            }, {

            }).addToDisposable()
    }

    /**
     * Загрузка списка манги по поисковым параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, volumes, chapters, status, random, created_at, created_at_desc) (необязательно)
     * @param kind тип манги (manga, manhwa, manhua, light_novel, novel, one_shot, doujin) (необязательно)
     * @param status тип релиза (anons, ongoing, released, paused, discontinued) (необязательно)
     * @param season сезон выхода манги (summer_2017, spring_2016,fall_2016, 2016,!winter_2016, 2016, 2014_2016, 199x) (необязательно)
     * @param score минимальная оценка манги (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param publisher список и издателями манги (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param search поисковая фраза для фильтрации манги по имени (name) (необязательно)
     */
    fun loadManga(
        page: Int? = 1,
        limit: Int? = 30,
        order: MangaSearchType? = null,
        kind: List<MangaType>? = null,
        status: List<AiredStatus>? = null,
        season: List<Pair<String?, String?>>? = null,
        score: Double? = null,
        genre: List<Long>? = null,
        publisher: List<String>? = null,
        censored: Boolean? = null,
        myList: List<RateStatus>? = null,
        search: String? = null
    ) {
        mangaInteractor.getMangaListByParameters(
            page = page,
            limit = limit,
            order = order,
            kind = kind,
            status = status,
            season = season,
            score = score,
            genre = genre,
            publisher = publisher,
            censored = censored,
            myList = myList,
            search = search
        )
            .compose(
                if (isSearchSettingsChanged.value == true) {
                    doAsyncSingleWithProgress()
                } else {
                    doAsyncSingleWithoutBlocking()
                }
            )
            .subscribe({
                when {
                    it.isNullOrEmpty() && nextPage.value == 1 -> {
                        mangaList.clear()
                        endReached.value = true
                    }
                    it.isNullOrEmpty() && (nextPage.value ?: 0) > 1 -> {
                        endReached.value = true
                    }
                }
                if (isSearchSettingsChanged.value == true) {
                    mangaList.clear()
                    isSearchSettingsChanged.value = false
                }
                it.forEach { manga ->
                    if (!mangaList.contains(manga)) {
                        mangaList.add(manga)
                    }
                }
                mangaSearch.value = mangaList.toMutableStateList()
            }, {

            }).addToDisposable()

    }

    /**
     * Загрузка списка ранобэ по поисковым параметрам
     *
     * @param page номер страницы, должно быть числом между 1 и 100000 (необязательно)
     * @param limit лимит списка, число максимум 50 (необязательно)
     * @param order порядок сортировки (id, id_desc, ranked, kind, popularity, name, aired_on, volumes, chapters, status, random, created_at, created_at_desc) (необязательно)
     * @param kind тип манги (manga, manhwa, manhua, light_novel, novel, one_shot, doujin) (необязательно)
     * @param status тип релиза (anons, ongoing, released, paused, discontinued) (необязательно)
     * @param season сезон выхода манги (summer_2017, spring_2016,fall_2016, 2016,!winter_2016, 2016, 2014_2016, 199x) (необязательно)
     * @param score минимальная оценка манги (необязательно)
     * @param genre список с id жанров аниме  (необязательно)
     * @param publisher список и издателями манги (необязательно)
     * @param censored включить цензуру (Set to false to allow hentai, yaoi and yuri) (необязательно)
     * @param myList статус манги в списке пользователя (planned, watching, rewatching, completed, on_hold, dropped) (необязательно)
     * @param search поисковая фраза для фильтрации манги по имени (name) (необязательно)
     */
    fun loadRanobe(
        page: Int? = 1,
        limit: Int? = 30,
        order: MangaSearchType? = null,
        status: List<AiredStatus>? = null,
        season: List<Pair<String?, String?>>? = null,
        score: Double? = null,
        genre: List<Long>? = null,
        publisher: List<String>? = null,
        censored: Boolean? = null,
        myList: List<RateStatus>? = null,
        search: String? = null
    ) {
        ranobeInteractor.getRanobeListByParameters(
            page = page,
            limit = limit,
            order = order,
            status = status,
            season = season,
            score = score,
            genre = genre,
            publisher = publisher,
            censored = censored,
            myList = myList,
            search = search
        )
            .compose(
                if (isSearchSettingsChanged.value == true) {
                    doAsyncSingleWithProgress()
                } else {
                    doAsyncSingleWithoutBlocking()
                }
            )
            .subscribe({
                when {
                    it.isNullOrEmpty() && nextPage.value == 1 -> {
                        ranobeList.clear()
                        endReached.value = true
                    }
                    it.isNullOrEmpty() && (nextPage.value ?: 0) > 1 -> {
                        endReached.value = true
                    }
                }
                if (isSearchSettingsChanged.value == true) {
                    ranobeList.clear()
                    isSearchSettingsChanged.value = false
                }
                it.forEach { manga ->
                    if (!ranobeList.contains(manga)) {
                        ranobeList.add(manga)
                    }
                }
                ranobeSearch.value = ranobeList.toMutableStateList()
            }, {

            }).addToDisposable()
    }

    /**
     * Загрузка списка персонажей по поисковым параметрам
     *
     * @param characterName имя персонажа
     */
    fun loadCharacter(characterName: String?) {
        characterName?.let { name ->
            characterInteractor.getCharacterList(characterName = name)
                .compose(doAsyncSingleWithoutBlocking())
                .subscribe({
                    characterSearch.value = it
                }, {

                }).addToDisposable()
        }
    }

    /**
     * Загрузка списка людей по поисковым параметрам
     *
     * @param peopleName имя человека
     */
    fun loadPeople(peopleName: String?) {
        peopleName?.let { name ->
            peopleInteractor.getPersonList(peopleName = name)
                .compose(doAsyncSingleWithoutBlocking())
                .subscribe({
                    peopleSearch.value = it
                }, {

                }).addToDisposable()
        }
    }

    fun reset() {
        isSearchSettingsChanged.value = true
        endReached.value = false
        nextPage.value = 1
    }

    fun fullReset() {
        airedStatus.value = null
        ageRating.value = null
        score.value = null
        ageRating.value = null
        genre.value = null
        myList.value = null
        orderAnime.value = AnimeSearchType.RANKED
        kindAnime.value = null
        durationAnime.value = null
        studioAnime.value = null
        orderManga.value = MangaSearchType.RANKED
        kindManga.value = null
        seasonTypes.value = null
        yearSeason.value = ""
        yearStart.value = ""
        yearEnd.value = ""
        seasonTab.value = 0
        searchValue.value = ""
        reset()
    }

    private fun initSearchSubject() {
        searchSubject
            .debounce(SEARCH_INPUT_VALUE_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .compose(doAsync())
            .onErrorReturnItem("")
            .subscribe({
                when (searchType.value) {
                    SearchType.CHARACTER -> {
                        loadCharacter(characterName = it)
                    }
                    SearchType.PEOPLE -> {
                        loadPeople(peopleName = it)
                    }
                    else -> {
                        reset()
                    }
                }
            }, {}).addToDisposable()
    }

    private fun getSeasonsListPairs(): List<Pair<String?, String?>>? {
        val seasons = mutableListOf<Pair<String?, String?>>()
        if (seasonTab.value == 0) {

            val seasonT = if (seasonTypes.value?.isNotEmpty() == true) seasonTypes.value else null

            val year = if (yearSeason.value?.length == 4) yearSeason.value else null

            when {
                seasonT.isNullOrEmpty().not() && year.isNullOrEmpty() -> {
                    return null
                }
                seasonT.isNullOrEmpty() && year.isNullOrEmpty().not() -> {
                    seasons.add(
                        null to year
                    )
                }
                seasonT.isNullOrEmpty().not() && year.isNullOrEmpty().not() -> {
                    year?.let { y ->
                        seasonT?.forEach { season ->
                            seasons.add(
                                season.toScreenString(useEng = true) to y
                            )
                        }
                    }
                }
                else -> {
                    return null
                }
            }
        } else {

            val yearS = if (yearStart.value?.length == 4) yearStart.value else null

            val yearE = if (yearEnd.value?.length == 4) yearEnd.value else null

            when {
                yearS.isNullOrEmpty().not() && yearE.isNullOrEmpty() -> {
                    seasons.add(
                        yearS to null
                    )
                }
                yearS.isNullOrEmpty() && yearE.isNullOrEmpty().not() -> {
                    seasons.add(
                        null to yearE
                    )
                }
                yearS.isNullOrEmpty().not() && yearE.isNullOrEmpty().not() -> {
                    seasons.add(
                        yearS to yearE
                    )
                }
                else -> {
                    return null
                }
            }
        }

        return seasons.ifEmpty { null }
    }

    companion object {

        private const val SEARCH_INPUT_VALUE_DEBOUNCE_TIME = 500L
    }
}