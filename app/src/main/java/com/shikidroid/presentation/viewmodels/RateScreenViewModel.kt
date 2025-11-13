package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.appconstants.NetworkConstants
import com.shikidroid.domain.converters.toUserRateModel
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.rates.*
import com.shikidroid.domain.models.user.StatusModel
import com.shikidroid.presentation.BottomSheetType
import com.shikidroid.presentation.converters.*
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.ui.*
import com.shikidroid.ui.ALL_DATA_LOADED_TEXT
import com.shikidroid.ui.ALREADY_IN_LIST_TEXT
import com.shikidroid.ui.DATA_UPDATE_ERROR_TEXT
import com.shikidroid.ui.LISTS_UPDATED_TEXT
import com.shikidroid.ui.PLUS_ONE_EPISODE_TEXT
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.sortedAscendDescend
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.NoSuchElementException
import java.util.concurrent.TimeUnit

/**
 * View Model для экрана [RateScreen] "Списки" пользовательского рейтинга
 * является общим хранилищем пользовательского списка
 * интерфейс [RateHolder] предоставляет доступ и редактирование для других экранов
 *
 * @property userInteractor интерактор информации пользователя
 * @property userLocalInteractor интерактор локального хранилища информации о пользователе
 * @property prefs доступ к локальному хранилищу системы Android
 */
internal class RateScreenViewModel(
    private val userInteractor: UserInteractor,
    private val userLocalInteractor: UserLocalInteractor,
    private val prefs: SharedPreferencesProvider
) : BaseViewModel(), RateHolder {

    /** список аниме/манги для показа */
    val showList = MutableLiveData<MutableList<RateModel>>(mutableListOf())

    /** позиция текущего скролла списка */
    val currentListPosition = MutableLiveData<Int>(0)

    /** словарь пользовательского статуса произведения и модели данных произведения */
    val animeMap = MutableLiveData<MutableMap<RateStatus, MutableList<RateModel>>>(mutableMapOf())

    /** количество аниме в каждой категории */
    val animeRateSize = MutableLiveData<MutableMap<RateStatus, Int>>(mutableMapOf())

    /** словарь пользовательского статуса произведения и модели данных произведения */
    val mangaMap = MutableLiveData<MutableMap<RateStatus, MutableList<RateModel>>>(mutableMapOf())

    /** количества манги/ранобэ в каждой категории */
    val mangaRateSize = MutableLiveData<MutableMap<RateStatus, Int>>(mutableMapOf())

    /** типа списка аниме/манга */
    val listType: MutableLiveData<SectionType> = object : MutableLiveData<SectionType>(
        prefs.getString(key = LIST_TYPE, default = SectionType.ANIME.toScreenString())
            ?.toSectionType()
    ) {
        override fun setValue(value: SectionType?) {
            super.setValue(value)
            value?.let { listType ->
                prefs.putString(
                    key = LIST_TYPE, s = listType.toScreenString()
                )
                if (listType == SectionType.ANIME) {
                    when (isAnimeEmpty()) {
                        true -> {
                            nextAnimePage.value = 1
                            loadData(nextAnimePage.value ?: 1)
                        }

                        else -> {
                            showList.value = animeMap.value?.get(animeRateStatus.value)
                        }
                    }
                } else {
                    when (isMangaEmpty()) {
                        true -> {
                            nextAnimePage.value = 1
                            loadData(nextAnimePage.value ?: 1)
                        }

                        else -> {
                            showList.value = mangaMap.value?.get(mangaRateStatus.value)
                        }
                    }
                }
                setListByOrder()
                setSortListTitles()
            }
        }
    }

    /** поиск названия аниме/манги */
    val searchTitleName: MutableLiveData<String> = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            value?.let { searchString ->
                searchSubject.onNext(searchString)
            }
        }
    }

    /** найдена ли строка в списке */
    val isSearchInList = MutableLiveData<Boolean>(true)

    /** список с типами сортировки для выпадающего меню */
    val sortByTitles = MutableLiveData<List<SortBy>>(listOf())

    /** тип сортировки списка (По названию, по оценке) */
    val sortBy: MutableLiveData<SortBy> = object : MutableLiveData<SortBy>(
        prefs.getString(key = SORT_BY, default = SortBy.BY_NAME.toScreenString())?.toSortBy()
    ) {
        override fun setValue(value: SortBy?) {
            super.setValue(value)
            value?.let { sortBy ->
                prefs.putString(
                    key = SORT_BY, s = sortBy.toScreenString()
                )
                setListByOrder()
            }
        }
    }

    /** флаг для показа выпадающего меню сортировки */
    val showSortByMenu = MutableLiveData<Boolean>(false)

    /** поиск по возрастанию/убыванию */
    val isSortAscend: MutableLiveData<Boolean> = object : MutableLiveData<Boolean>(
        prefs.getBoolean(
            key = SORT_ASCEND_DESCEND, default = true
        )
    ) {
        override fun setValue(value: Boolean?) {
            super.setValue(value)
            value?.let { searchAscend ->
                prefs.putBoolean(
                    key = SORT_ASCEND_DESCEND, searchAscend
                )
                setListByOrder()
            }
        }
    }

    /** выбранный пользовательский статуса для списка аниме (Запланировано, Просмотрено) */
    val animeRateStatus: MutableLiveData<RateStatus> = object : MutableLiveData<RateStatus>(
        prefs.getString(
            key = SELECTED_ANIME_RATE_STATUS,
            default = RateStatus.PLANNED.toAnimePresentationString()
        )?.toAnimeRateStatus()
    ) {
        override fun setValue(value: RateStatus?) {
            super.setValue(value)
            value?.let { rateStatus ->
                prefs.putString(
                    SELECTED_ANIME_RATE_STATUS, rateStatus.toAnimePresentationString()
                )
                setListByRateStatus(rateStatus)
                setListByOrder()
            }
        }
    }

    /** выбранный пользовательского статуса для списка манги (Запланировано, Прочитано) */
    val mangaRateStatus: MutableLiveData<RateStatus> = object : MutableLiveData<RateStatus>(
        prefs.getString(
            key = SELECTED_MANGA_RATE_STATUS,
            default = RateStatus.PLANNED.toMangaPresentationString()
        )?.toMangaRateStatus()
    ) {
        override fun setValue(value: RateStatus?) {
            super.setValue(value)
            value?.let { rateStatus ->
                prefs.putString(
                    SELECTED_MANGA_RATE_STATUS, rateStatus.toMangaPresentationString()
                )
                setListByRateStatus(rateStatus)
                setListByOrder()
            }
        }
    }

    /** флаг открытия бокового меню */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** список аниме для показа статуса в календаре */
    override var animeRatesForCalendar: List<RateModel> = listOf()

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // LiveData-ы для нижней шторки
    //////////////////////////////////////////////////////////////////////////////////////////////'

    /** тип нижней шторки */
    override val bottomSheetType = MutableLiveData<BottomSheetType>(BottomSheetType.RATE_LIST_EDIT)

    /** состояние показа нижней шторки */
    override val isBottomSheetVisible = MutableLiveData<Boolean>(false)

    /** выбранный элемента списка */
    override val currentItem: MutableLiveData<RateModel?> = object : MutableLiveData<RateModel?>() {
        override fun setValue(value: RateModel?) {
            super.setValue(value)
            value?.let { item ->
                currentItemStatus.value = item.status
                ratingBarScore.value = item.score?.toFloat()
                if (listType.value == SectionType.ANIME) {
                    episodeChapterCount.value = (item.episodes ?: 0).toString()
                } else {
                    episodeChapterCount.value = (item.chapters ?: 0).toString()
                }
                reWatchReReadCount.value = (item.rewatches ?: 0).toString()
                itemListComment.value = item.text.orEmpty()
            }
        }
    }

    /** статус выбранного элемента */
    override val currentItemStatus: MutableLiveData<RateStatus?> =
        object : MutableLiveData<RateStatus?>(RateStatus.PLANNED) {
            override fun setValue(value: RateStatus?) {
                super.setValue(value)
            }
        }

    /** оценка аниме/манги для компонента RatingBar в нижней шторке */
    override val ratingBarScore: MutableLiveData<Float?> = object : MutableLiveData<Float?>(0f) {
        override fun setValue(value: Float?) {
            if (value == null) {
                super.setValue(0f)
            } else {
                super.setValue(value)
            }
        }
    }

    /** флаг показа ошибки ввода просмотренных/прочитанных эпизодов/глав */
    override val isEpisodeChapterError = MutableLiveData<Boolean>(false)

    /** количество просмотренных эпизодов/прочитанных глав */
    override val episodeChapterCount = MutableLiveData<String>("")

    /** флаг показа ошибки ввода количества пересмотров/перечитываний */
    override val isReWatchesError = MutableLiveData<Boolean>(false)

    /** количество повторных просмотров/повторных чтений */
    override val reWatchReReadCount = MutableLiveData<String>("")

    /** комментарий к списку в нижней шторке */
    override val itemListComment = MutableLiveData<String>("")

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // LiveData-ы пагинации
    //////////////////////////////////////////////////////////////////////////////////////////////

    /** страница аниме для загрузки */
    val nextAnimePage: MutableLiveData<Int> = object : MutableLiveData<Int>(1) {
        override fun setValue(value: Int?) {
            super.setValue(value)
            value?.let { nextPage ->
                if (isLoading.value == false && listType.value == SectionType.ANIME) {
                    loadData(page = nextPage)
                }
            }
        }
    }

    /** страница манги для загрузки */
    val nextMangaPage: MutableLiveData<Int> = object : MutableLiveData<Int>(1) {
        override fun setValue(value: Int?) {
            super.setValue(value)
            value?.let { nextPage ->
                if (isLoading.value == false && listType.value == SectionType.MANGA) {
                    loadData(page = nextPage)
                }
            }
        }
    }

    /** флаг полной загрузки списка аниме */
    val animeEndReached = MutableLiveData<Boolean>(false)

    /** флаг полной загрузки списка манги */
    val mangaEndReached = MutableLiveData<Boolean>(false)

    val switchTheme = MutableLiveData(false)

    // статусы списка
    private val rateStatuses = RateStatus.values().filter { rateStatus ->
        rateStatus != RateStatus.UNKNOWN
    }

    private val searchSubject = PublishSubject.create<String>()

    init {
        // нужно для инициализации ключей в словаре
        initMaps()

        initSearchSubject()

        loadRateCategoriesSize()

        listType.value = prefs.getString(
            key = LIST_TYPE,
            default = SectionType.ANIME.toScreenString()
        )?.toSectionType()

        animeRateStatus.value = prefs.getString(
            key = SELECTED_ANIME_RATE_STATUS,
            default = RateStatus.PLANNED.toAnimePresentationString()
        )?.toAnimeRateStatus()

        mangaRateStatus.value = prefs.getString(
            key = SELECTED_MANGA_RATE_STATUS,
            default = RateStatus.PLANNED.toMangaPresentationString()
        )?.toMangaRateStatus()

        sortBy.value = prefs.getString(
            key = SORT_BY,
            default = SortBy.BY_NAME.toScreenString()
        )?.toSortBy()

        isSortAscend.value = prefs.getBoolean(
            key = SORT_ASCEND_DESCEND,
            default = true
        )
    }

    override fun showBottomSheet(bottomType: BottomSheetType) {
        bottomSheetType.value = bottomType
        isBottomSheetVisible.value = isBottomSheetVisible.value != true
    }

    override fun hideBottomSheet() {
        isBottomSheetVisible.value = false
        isEpisodeChapterError.value = false
        isReWatchesError.value = false
    }

    /**
     * Загрузка пользовательского списка аниме или манги
     */
    fun loadData(page: Int) {
        if (listType.value == SectionType.ANIME) {
            userInteractor.getUserAnimeRates(
                id = userLocalInteractor.getUserId(),
                page = page,
                limit = NetworkConstants.MAX_LIMIT
            )
        } else {
            userInteractor.getUserMangaRates(
                id = userLocalInteractor.getUserId(),
                page = page,
                limit = NetworkConstants.MAX_LIMIT
            )
        }
            .compose(doAsyncSingleWithProgress())
            .subscribe({ animeManga ->
                if (listType.value == SectionType.ANIME) {
                    animeRatesForCalendar = animeManga
                    processAnimeRateModel(rateList = animeManga)
                } else {
                    processMangaRateModel(rateList = animeManga)
                }
                listType.value?.let { setAnimeMangaListByType(listType = it) }
                setListByOrder()
            }, {
                if (it is NoSuchElementException) {
                    when (listType.value) {
                        SectionType.ANIME -> animeEndReached.value = true
                        else -> mangaEndReached.value = true
                    }
                    showToast(message = ALL_DATA_LOADED_TEXT)
                } else {
//                    showToast(message = DATA_UPDATE_ERROR_TEXT)
                    loadData(page = page)
                }
            })
            .addToDisposable()
    }

    /**
     * Сбросить значения
     */
    fun reset() {
        isDrawerOpen.value = false
        searchTitleName.value = ""
        initMaps()
        animeEndReached.value = false
        mangaEndReached.value = false
        nextAnimePage.value = 1
        nextMangaPage.value = 1
        loadRateCategoriesSize()
        showToast(message = LISTS_UPDATED_TEXT)
    }

    /**
     * Увеличить количество просмотренных/прочитанных эпизодов/глав на 1
     */
    fun plusOneEpisodeChapter(rateModel: RateModel) {
        userInteractor.increment(
            id = rateModel.id ?: -1
        )
            .compose(doAsyncSingle())
            .subscribe({ incrementModel ->
                val updatedRateMotel = rateModel.copy(
                    status = incrementModel.status ?: RateStatus.WATCHING,
                    episodes = incrementModel.episodes,
                    rewatches = incrementModel.rewatches,
                    volumes = incrementModel.volumes,
                    chapters = incrementModel.chapters,
                    createdDateTime = incrementModel.createdDateTime,
                    updatedDateTime = incrementModel.updatedDateTime
                )
                if (incrementModel.status == rateModel.status) {
                    updateItemInList(
                        updatedRateModel = updatedRateMotel
                    )
                    showToast(message = PLUS_ONE_EPISODE_TEXT)
                } else {
                    transferItemFromList(
                        rateModel = rateModel,
                        updatedRateModel = updatedRateMotel
                    )
                    showToast(
                        message = "Перемещено в ${
                            if (rateModel.anime != null) {
                                updatedRateMotel.status?.toAnimePresentationString()
                            } else {
                                updatedRateMotel.status?.toMangaPresentationString()
                            }
                        }"
                    )
                }
                updateScreenState()
            }, {
                currentItem.value = rateModel
                showToast(message = DATA_UPDATE_ERROR_TEXT)
            }).addToDisposable()
    }

    /** добавить новый элемент в список */
    override fun createRateStatus(rateModel: RateModel) {
        val updateRateModel = updateRateModel(rateModel)
        userInteractor.createRate(
            request = UserRateCreateOrUpdateModel(
                userRate = updateRateModel.toUserRateModel(
                    userId = userLocalInteractor.getUserId()
                )
            )
        )
            .compose(doAsyncSingle())
            .subscribe({ userRateModel ->
                val newRateModel = rateModel.copy(
                    id = userRateModel.id,
                    score = userRateModel.score?.toInt(),
                    status = userRateModel.status,
                    rewatches = userRateModel.rewatches,
                    episodes = userRateModel.episodes,
                    volumes = userRateModel.volumes,
                    chapters = userRateModel.chapters,
                    text = userRateModel.text,
                    textHtml = userRateModel.textHtml,
                    createdDateTime = userRateModel.dateCreated,
                    updatedDateTime = userRateModel.dateUpdated
                )
                if (userRateModel.targetType == SectionType.ANIME) {
                    animeMap
                } else {
                    mangaMap
                }.value?.get(userRateModel.status)?.add(newRateModel)
                currentItem.value = newRateModel
                updateScreenState()
            }, {
                currentItem.value = rateModel
                showToast(message = DATA_UPDATE_ERROR_TEXT)
            })
            .addToDisposable()
    }

    /**
     * Переместить элемент в другой список
     */
    override fun changeRateStatus(rateModel: RateModel, newStatus: RateStatus) {
        if (rateModel.status == newStatus) {
            showToast(message = ALREADY_IN_LIST_TEXT)
        } else {
            userInteractor.updateRate(
                id = rateModel.id ?: -1,
                request = UserRateCreateOrUpdateModel(
                    userRate = rateModel.toUserRateModel().copy(status = newStatus)
                )
            )
                .compose(doAsyncSingle())
                .subscribe({
                    val updatedRateModel = rateModel.copy(
                        status = newStatus,
                        updatedDateTime = it.dateUpdated
                    )

                    transferItemFromList(
                        rateModel = rateModel,
                        updatedRateModel = updatedRateModel
                    )
                    currentItem.value = updatedRateModel
                    updateScreenState()
                    showToast(
                        message = "Перемещено в ${
                            if (rateModel.anime != null) {
                                newStatus.toAnimePresentationString()
                            } else {
                                newStatus.toMangaPresentationString()
                            }
                        }"
                    )
                }, {
                    currentItem.value = rateModel
                    showToast(message = DATA_UPDATE_ERROR_TEXT)
                })
                .addToDisposable()
        }
    }

    /** изменить элемент списка */
    override fun changeRateModel(rateModel: RateModel) {
        val updatedUserRateModel = updateUserRateModel(rateModel)

        userInteractor.updateRate(
            id = rateModel.id ?: -1,
            request = UserRateCreateOrUpdateModel(
                userRate = updatedUserRateModel
            )
        )
            .compose(doAsyncSingle())
            .subscribe({
                val updatedRateModel = updateRateModel(rateModel).copy(
                    status = it.status,
                    updatedDateTime = it.dateUpdated
                )

                if (rateModel.anime != null) {
                    if (animeRateStatus.value == updatedRateModel.status) {
                        updateItemInList(updatedRateModel)
                    } else {
                        transferItemFromList(
                            rateModel = rateModel,
                            updatedRateModel = updatedRateModel
                        )
                    }
                } else {
                    if (mangaRateStatus.value == updatedRateModel.status) {
                        updateItemInList(updatedRateModel)
                    } else {
                        transferItemFromList(
                            rateModel = rateModel,
                            updatedRateModel = updatedRateModel
                        )
                    }
                }
                currentItem.value = updatedRateModel
                updateScreenState()
                showToast(message = UPDATED_TEXT)
            }, {
                currentItem.value = rateModel
                showToast(message = DATA_UPDATE_ERROR_TEXT)
            })
            .addToDisposable()
    }

    /** удалить элемент из списка */
    override fun deleteRateModel(rateModel: RateModel) {
        userInteractor.deleteRate(
            id = rateModel.id ?: -1
        )
            .compose(doAsyncCompletable())
            .subscribe({
                if (rateModel.anime != null) {
                    animeMap.value?.get(rateModel.status)?.remove(rateModel)
                } else {
                    mangaMap.value?.get(rateModel.status)?.remove(rateModel)
                }
                currentItem.value = currentItem.value?.copy(status = null)
                updateScreenState()
                showToast(message = DELETED_TEXT)
            }, {
                showToast(message = TRY_ANOTHER_ONE_ERROR_TEXT)
            })
            .addToDisposable()
    }

    /** найти аниме в словаре статус-список и установить текущий элемент */
    override fun findAnimeMangaAndSetCurrentItem(rateModel: RateModel?) {
        val itemsInMap = mutableListOf<RateModel>()

        if (rateModel?.anime != null) {
            animeMap.value?.values?.forEach { list ->
                itemsInMap.addAll(list)
            }
        } else {
            mangaMap.value?.values?.forEach { list ->
                itemsInMap.addAll(list)
            }
        }

        val item =
            if (rateModel?.anime != null) {
                itemsInMap.find {
                    it.anime?.id == rateModel.anime.id
                }
            } else {
                itemsInMap.find {
                    it.manga?.id == rateModel?.manga?.id
                }
            }

        if (item != null) {
            currentItem.value = item
        } else {
            currentItem.value = rateModel?.copy(
                score = null,
                status = null,
                text = null,
                episodes = null,
                chapters = null,
                volumes = null,
                rewatches = null
            )
        }
    }

    private fun updateScreenState() {
        isDrawerOpen.value = false
        loadRateCategoriesSize()
        listType.value?.let { listType ->
            setAnimeMangaListByType(listType)
        }
        setListByOrder()
        searchInList(searchTitleName.value)
    }

    /** Найти элемент в списке по имени */
    private fun searchInList(value: String?) {
        if (value.isNullOrEmpty()) {
            if (listType.value == SectionType.ANIME) {
                showList.value = animeMap.value?.get(animeRateStatus.value)
                setListByOrder()
            } else {
                showList.value = mangaMap.value?.get(mangaRateStatus.value)
                setListByOrder()
            }
        } else {
            if (listType.value == SectionType.ANIME) {
                showList.value = animeMap.value?.get(animeRateStatus.value)?.filter {
                    it.anime?.nameRu?.contains(value, ignoreCase = true) == true ||
                            it.anime?.name?.contains(value, ignoreCase = true) == true
                }?.toMutableList()
            } else {
                showList.value = mangaMap.value?.get(mangaRateStatus.value)?.filter {
                    it.manga?.nameRu?.contains(value, ignoreCase = true) == true ||
                            it.manga?.name?.contains(value, ignoreCase = true) == true
                }?.toMutableList()
            }
        }
        if (showList.value?.isEmpty() == false) {
            isSearchInList.value = true
            setListByOrder()
        } else {
            isSearchInList.value = false
        }
    }

    private fun initSearchSubject() {
        searchSubject
            .debounce(SEARCH_INPUT_VALUE_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .compose(doAsync())
            .onErrorReturnItem("")
            .subscribe({
                searchInList(it)
            }, {}).addToDisposable()
    }

    /** загрузить данные размера категорий списка */
    private fun loadRateCategoriesSize() {
        userInteractor.getUserProfileById(
            id = userLocalInteractor.getUserId()
        )
            .compose(doAsyncSingle())
            .subscribe({
                processAnimeRateSize(it.stats?.fullStatuses?.anime)
                processMangaRateSize(it.stats?.fullStatuses?.manga)
            }, {

            })
            .addToDisposable()
    }

    /** инициализация мап */
    private fun initMaps() {
        rateStatuses.forEach { rateStatus ->
            animeMap.value?.set(rateStatus, mutableListOf())
            animeRateSize.value?.set(rateStatus, 0)
            mangaMap.value?.set(rateStatus, mutableListOf())
            mangaRateSize.value?.set(rateStatus, 0)
        }
    }

    private fun processAnimeRateModel(rateList: List<RateModel>?) {
        rateList?.forEach { rateModel ->
            if (animeMap.value?.get(rateModel.status)?.contains(rateModel) == false) {
                animeMap.value?.get(rateModel.status)?.add(rateModel)
            }
        }
    }

    private fun processMangaRateModel(rateList: List<RateModel>?) {
        rateList?.forEach { rateModel ->
            if (mangaMap.value?.get(rateModel.status)?.contains(rateModel) == false) {
                mangaMap.value?.get(rateModel.status)?.add(rateModel)
            }
        }
    }

    private fun processAnimeRateSize(statusList: List<StatusModel>?) {
        statusList?.forEach { status ->
            animeRateSize.value?.set(status.name ?: RateStatus.PLANNED, status.size ?: 0)
        }
    }

    private fun processMangaRateSize(statusList: List<StatusModel>?) {
        statusList?.forEach { status ->
            mangaRateSize.value?.set(status.name ?: RateStatus.PLANNED, status.size ?: 0)
        }
    }

    private fun isAnimeEmpty(): Boolean {
        return animeMap.value?.get(RateStatus.WATCHING).isNullOrEmpty() &&
                animeMap.value?.get(RateStatus.PLANNED).isNullOrEmpty() &&
                animeMap.value?.get(RateStatus.REWATCHING).isNullOrEmpty() &&
                animeMap.value?.get(RateStatus.COMPLETED).isNullOrEmpty() &&
                animeMap.value?.get(RateStatus.ON_HOLD).isNullOrEmpty() &&
                animeMap.value?.get(RateStatus.DROPPED).isNullOrEmpty()
    }

    private fun isMangaEmpty(): Boolean {
        return mangaMap.value?.get(RateStatus.WATCHING).isNullOrEmpty() &&
                mangaMap.value?.get(RateStatus.PLANNED).isNullOrEmpty() &&
                mangaMap.value?.get(RateStatus.REWATCHING).isNullOrEmpty() &&
                mangaMap.value?.get(RateStatus.COMPLETED).isNullOrEmpty() &&
                mangaMap.value?.get(RateStatus.ON_HOLD).isNullOrEmpty() &&
                mangaMap.value?.get(RateStatus.DROPPED).isNullOrEmpty()
    }

    private fun setAnimeMangaListByType(listType: SectionType) {
        if (animeMap.value?.get(animeRateStatus.value)?.size == 0) {
            animeMap.value?.map {
                if (it.value.size > 0) {
                    when (it.key) {
                        RateStatus.PLANNED -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        RateStatus.WATCHING -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        RateStatus.REWATCHING -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        RateStatus.COMPLETED -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        RateStatus.ON_HOLD -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        RateStatus.DROPPED -> {
                            animeRateStatus.value = it.key
                            return
                        }

                        else -> {
                            animeRateStatus.value = it.key
                            return
                        }
                    }
                }
            }
        }
        if (mangaMap.value?.get(mangaRateStatus.value)?.size == 0) {
            mangaMap.value?.map {
                if (it.value.size > 0) {
                    when (it.key) {
                        RateStatus.PLANNED -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        RateStatus.WATCHING -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        RateStatus.REWATCHING -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        RateStatus.COMPLETED -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        RateStatus.ON_HOLD -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        RateStatus.DROPPED -> {
                            mangaRateStatus.value = it.key
                            return
                        }

                        else -> {
                            mangaRateStatus.value = it.key
                            return
                        }
                    }
                }
            }
        }
        if (listType == SectionType.ANIME) {
            showList.value = animeMap.value?.get(animeRateStatus.value)
        } else {
            showList.value = mangaMap.value?.get(mangaRateStatus.value)
        }
    }

    private fun setListByRateStatus(rateStatus: RateStatus) {
        if (listType.value == SectionType.ANIME) {
            showList.value = animeMap.value?.getValue(rateStatus)
        } else {
            showList.value = mangaMap.value?.getValue(rateStatus)
        }
    }

    /** Установить названия для сортировки в зависимости от типа списка */
    private fun setSortListTitles() {
        if (listType.value == SectionType.ANIME) {
            sortByTitles.value = SortBy.values().filter { it != SortBy.BY_CHAPTERS }
            if (sortBy.value == SortBy.BY_CHAPTERS) {
                sortBy.value = SortBy.BY_EPISODES
            }
        } else {
            sortByTitles.value = SortBy.values().filter { it != SortBy.BY_EPISODES }
            if (sortBy.value == SortBy.BY_EPISODES) {
                sortBy.value = SortBy.BY_CHAPTERS
            }
        }
    }

    /** Отсортировать список */
    private fun setListByOrder() {
        val isAscend = isSortAscend.value ?: false
        when (sortBy.value) {
            SortBy.BY_NAME -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    when (listType.value) {
                        SectionType.ANIME -> it.anime?.nameRu
                        else -> it.manga?.nameRu
                    }
                }?.toMutableList()
            }

            SortBy.BY_PROGRESS -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    when (listType.value) {
                        SectionType.ANIME -> it.episodes
                        else -> it.chapters
                    }
                }?.toMutableList()
            }

            SortBy.BY_RELEASE_DATE -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    when (listType.value) {
                        SectionType.ANIME -> it.anime?.dateReleased ?: it.anime?.dateAired
                        else -> it.manga?.dateReleased ?: it.manga?.dateAired
                    }
                }?.toMutableList()
            }

            SortBy.BY_ADD_DATE -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    it.createdDateTime
                }?.toMutableList()
            }

            SortBy.BY_REFRESH_DATE -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    it.updatedDateTime
                }?.toMutableList()
            }

            SortBy.BY_SCORE -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    when (listType.value) {
                        SectionType.ANIME -> it.anime?.score
                        else -> it.manga?.score
                    }
                }?.toMutableList()
            }

            SortBy.BY_EPISODES -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    it.anime?.episodes
                }?.toMutableList()
            }

            SortBy.BY_CHAPTERS -> {
                showList.value = showList.value?.sortedAscendDescend(isAscend = isAscend) {
                    it.manga?.chapters
                }?.toMutableList()
            }

            else -> return
        }
    }

    /** Получить обновлённую модель пользовательского рейтинга [UserRateModel] */
    private fun updateUserRateModel(rateModel: RateModel): UserRateModel {
        return rateModel.toUserRateModel().copy(
            score = ratingBarScore.value?.toDouble(),
            status = currentItemStatus.value,
            rewatches =
            if (reWatchReReadCount.value?.isNotEmpty() == true) {
                reWatchReReadCount.value?.toInt()
            } else {
                rateModel.rewatches
            },
            episodes =
            if (listType.value == SectionType.ANIME) {
                if (episodeChapterCount.value?.isNotEmpty() == true) {
                    episodeChapterCount.value?.toInt()
                } else {
                    rateModel.episodes
                }
            } else {
                null
            },
            chapters = if (listType.value == SectionType.MANGA) {
                if (episodeChapterCount.value?.isNotEmpty() == true) {
                    episodeChapterCount.value?.toInt()
                } else {
                    rateModel.chapters
                }
            } else {
                null
            },
            text = itemListComment.value
        )
    }

    /** Получить обновлённую модель пользовательского рейтинга [RateModel] */
    private fun updateRateModel(rateModel: RateModel): RateModel {
        return rateModel.copy(
            score = ratingBarScore.value?.toInt() ?: rateModel.score,
            status = currentItemStatus.value ?: (rateModel.status ?: RateStatus.PLANNED),
            rewatches =
            if (reWatchReReadCount.value?.isNotEmpty() == true) {
                reWatchReReadCount.value?.toInt()
            } else {
                rateModel.rewatches
            },
            episodes =
            if (listType.value == SectionType.ANIME) {
                if (episodeChapterCount.value?.isNotEmpty() == true) {
                    episodeChapterCount.value?.toInt()
                } else {
                    rateModel.episodes
                }
            } else {
                null
            },
            chapters =
            if (listType.value == SectionType.MANGA) {
                if (episodeChapterCount.value?.isNotEmpty() == true) {
                    episodeChapterCount.value?.toInt()
                } else {
                    rateModel.chapters
                }
            } else {
                null
            },
            text = itemListComment.value
        )
    }

    /** Обновить элемент списка */
    private fun updateItemInList(updatedRateModel: RateModel) {
        showList.value?.find { it.id == updatedRateModel.id }?.apply {
            score = updatedRateModel.score
            status = updatedRateModel.status
            rewatches = updatedRateModel.rewatches
            episodes = updatedRateModel.episodes
            chapters = updatedRateModel.chapters
            createdDateTime = updatedRateModel.createdDateTime
            updatedDateTime = updatedRateModel.updatedDateTime
            text = updatedRateModel.text
        }
    }

    /** Переместить элемент в другой список локально */
    private fun transferItemFromList(rateModel: RateModel, updatedRateModel: RateModel) {
        if (rateModel.anime != null) {
            animeMap.value?.get(rateModel.status)?.remove(
                animeMap.value?.get(rateModel.status)?.find {
                    it.id == rateModel.id
                }
            )
            animeMap.value?.get(updatedRateModel.status)?.add(updatedRateModel)
        } else {
            mangaMap.value?.get(rateModel.status)?.remove(
                mangaMap.value?.get(rateModel.status)?.find {
                    it.id == rateModel.id
                }
            )
            mangaMap.value?.get(updatedRateModel.status)?.add(updatedRateModel)
        }
    }

    companion object {

        // ключ для сохранения типа списка аниме/манга
        private const val LIST_TYPE = "LIST_TYPE"

        // ключ для сохранения типа пользовательского статуса списка аниме (Запланировано, Просмотрено)
        private const val SELECTED_ANIME_RATE_STATUS = "SELECTED_ANIME_RATE_STATUS"

        // ключ для сохранения типа пользовательского статуса списка манги (Запланировано, Прочитано)
        private const val SELECTED_MANGA_RATE_STATUS = "SELECTED_MANGA_RATE_STATUS"

        // ключ для сохранения выбранного типа сортировки списка
        private const val SORT_BY = "SORT_BY"

        // ключ для сортировки по возрастанию/убыванию
        private const val SORT_ASCEND_DESCEND = "SORT_ASCEND_DESCEND"

        // время задержки для пользовательского ввода с клавиатуры
        private const val SEARCH_INPUT_VALUE_DEBOUNCE_TIME = 500L
    }
}