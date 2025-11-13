package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.appconstants.HttpStatusCode
import com.shikidroid.data.network.converters.toDomainModel
import com.shikidroid.domain.interactors.CalendarInteractor
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.calendar.CalendarJsonModel
import com.shikidroid.domain.models.calendar.CalendarModel
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.getHttpStatusCode
import com.shikidroid.utils.getHttpUrl
import com.shikidroid.utils.getListFromJson
import com.shikidroid.utils.withTime
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewModel для экрана с календарём выхода эпизодов
 *
 * @property calendarInteractor интерактор загрузки данных графика выхода аниме
 */
internal class CalendarViewModel(
    private val calendarInteractor: CalendarInteractor
): BaseViewModel() {

    /** список аниме календаря */
    val calendarAnimeList = MutableLiveData<List<AnimeModel?>>(listOf())

    /** список уже вышедших эпизодов */
    val releasedToday = MutableLiveData<MutableList<CalendarModel>>(mutableListOf())

    /** словарь дата выхода - список аниме */
    val dateAnimeMap = MutableLiveData<SortedMap<Date, MutableList<CalendarModel>>>(sortedMapOf())

    /** флаг состояния нижнего меню - открыто/закрыто */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** выбранная дата в виде строки */
    val currentDateString = MutableLiveData<String>()

    /** список выбранного расписания выхода эпизодов */
    val currentCalendar = MutableLiveData<List<CalendarModel>>(listOf())

    /** строка для поиска */
    val searchValue = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            value?.let {
                searchSubject.onNext(it)
            }
        }
    }

    /** ссылка для перехода в WebView и получения ответа, если ошибка 403 */
    val urlWebViewIf403 = MutableLiveData<String>("")

    private val currentTime = Calendar.getInstance().time

    private val releasedList = mutableListOf<CalendarModel>()
    private val animeMap = sortedMapOf<Date, MutableList<CalendarModel>>()

    private val searchSubject = PublishSubject.create<String>()

    init {
        loadData()
        initSearchSubject()
    }

    /** загрузка данных */
    fun loadData() {
        calendarInteractor.getCalendar()
            .compose(doAsyncSingleWithProgress())
            .subscribe({ calendarList ->
                setCalendarList(calendarList = calendarList)
            }, {
                if (it.getHttpStatusCode() == HttpStatusCode.HTTP_403_FORBIDDED) {
                    urlWebViewIf403.value = it.getHttpUrl()
                }
            }).addToDisposable()
    }

    /**
     *
     */
    fun setCalendarList(calendarList: List<CalendarModel>) {
        val animeList = mutableListOf<AnimeModel?>()

        calendarList.forEach { calendarModel ->

            animeList.add(calendarModel.anime)

            DateUtils.fromString(dateString = calendarModel.nextEpisodeDate)?.let { nextEpisodeDate ->
                if (currentTime > nextEpisodeDate) {
                    releasedList.add(calendarModel)
                } else {
                    val keyDate = Calendar.getInstance().withTime(nextEpisodeDate).time
                    if (animeMap.containsKey(key = keyDate)) {
                        animeMap.get(key = keyDate)?.add(element = calendarModel)
                    } else {
                        animeMap.set(key = keyDate, value = mutableListOf(calendarModel))
                    }
                }
            }
        }

        releasedToday.postValue(releasedList)
        dateAnimeMap.postValue(animeMap)

        calendarAnimeList.postValue(animeList)
    }

    /**
     *
     */
    fun setCalendarListFromJson(jsonString: String?) {
        showLoading()
        urlWebViewIf403.postValue("")
        val dataList = jsonString.getListFromJson<CalendarJsonModel>()?.map { it.toDomainModel() }
        dataList?.let { setCalendarList(calendarList = it) }
        hideLoading()
    }

    /** */
    fun reset() {
        releasedList.clear()
        animeMap.clear()
        releasedToday.value?.clear()
        dateAnimeMap.value?.clear()
        loadData()
        searchValue.value = ""
    }

    private fun searchInCalendar(input: String) {
        if (input.isNotEmpty()) {
            val releasedSearch = releasedList.filter {
                it.anime?.name?.contains(input, ignoreCase = true) == true ||
                        it.anime?.nameRu?.contains(input, ignoreCase = true) == true
            }
            val dateMap = sortedMapOf<Date, MutableList<CalendarModel>>()
            animeMap.forEach { map ->
                map.value?.forEach {
                    if (it.anime?.name?.contains(input, ignoreCase = true) == true ||
                        it.anime?.nameRu?.contains(input, ignoreCase = true) == true
                    ) {
                        if (dateMap.containsKey(key = map.key)) {
                            dateMap.get(key = map.key)?.add(element = it)
                        } else {
                            dateMap.set(key = map.key, value = mutableListOf(it))
                        }
                    }
                }
            }
            releasedToday.value = releasedSearch.toMutableList()
            dateAnimeMap.value = dateMap
        } else {
            releasedToday.value = releasedList
            dateAnimeMap.value = animeMap
        }
    }

    private fun initSearchSubject() {
        searchSubject
            .debounce(SEARCH_INPUT_VALUE_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .compose(doAsync())
            .onErrorReturnItem("")
            .subscribe({
                searchInCalendar(it)
            }, {}).addToDisposable()
    }

    companion object {

        private const val SEARCH_INPUT_VALUE_DEBOUNCE_TIME = 500L
    }
}