package com.shikidroid.presentation.viewmodels.mal

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.MyAnimeListInteractor
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RankingType
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.IntUtils.toDayOfWeekString
import com.shikidroid.utils.toDayOfWeekInt
import com.shikidroid.utils.toMonthString
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewModel для экрана с календарём выхода эпизодов
 *
 * @property malInteractor интерактор загрузки данных графика выхода аниме
 */
internal class CalendarMalViewModel(
    private val malInteractor: MyAnimeListInteractor
) : BaseViewModel() {

    /** список уже вышедших эпизодов */
    val releasedToday = MutableLiveData<MutableList<AnimeMalModel>>(mutableListOf())

    /** словарь дата выхода - список аниме */
    val dateAnimeMap =
        MutableLiveData<MutableMap<String, MutableList<AnimeMalModel>>>(mutableMapOf())

    /** флаг состояния нижнего меню - открыто/закрыто */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** выбранная дата в виде строки */
    val currentDateString = MutableLiveData<String>()

    /** список выбранного расписания выхода эпизодов */
    val currentCalendar = MutableLiveData<List<AnimeMalModel>>(listOf())

    /** строка для поиска */
    val searchValue = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            value?.let {
                searchSubject.onNext(it)
            }
        }
    }

    private val animeMap = mutableMapOf<String, MutableList<AnimeMalModel>>()

    private val searchSubject = PublishSubject.create<String>()

    init {
        loadData()
        initSearchSubject()
    }

    /** загрузка данных */
    fun loadData() {
        malInteractor.getAnimeRankingList(
            rankingType = RankingType.AIRING,
            fields = "start_date, end_date, media_type, status, num_episodes, mean, broadcast",
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({ calendarList ->

                val map = mutableMapOf<Int, MutableList<AnimeMalModel>>()

                val currentDayNumber = Calendar.getInstance().toDayOfWeekInt()
                val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                calendarList.forEach { anime ->
                    val key = anime.broadcast?.dayOfTheWeek.dayNameToInt()
                    if (map.containsKey(key = key)) {
                        map.get(key = key)?.add(element = anime)
                    } else {
                        map.set(key = key, value = mutableListOf(anime))
                    }
                }

                // удаляем элементы списка, которые не содержат время выхода
                map.remove(0)

                // сортировка ключей словаря по возрастанию
                val sortKeys = map.keys.sorted()

                // индекс текущего дня в отсортированном списке
                val indexOfCurrentDay = sortKeys.indexOf(currentDayNumber)

                val indexOfLastDay = sortKeys.indexOf(sortKeys.last())

                // отсортированный от текущего дня словарь
                val sortMap = mutableMapOf<String, MutableList<AnimeMalModel>>()

                // внутренняя функция для установки ключ-значение в словарь
                fun setSortMapKeys(keys: List<Int>) {
                    keys.forEach { key ->
                        if (key == currentDayNumber) {
                            map[key]?.let {
                                sortMap["${key.toDayOfWeekString()}, $currentDayOfMonth ${
                                    Calendar.getInstance().toMonthString(infinitive = false)
                                }"] = it
                            }
                        } else {
                            map[key]?.let {
                                sortMap[key.toDayOfWeekString()] = it
                            }
                        }
                    }
                }

                if (indexOfCurrentDay == 0) {
                    setSortMapKeys(sortKeys);
                }

                // список с сортировкой от текущего дня
                val sortByDaysKeys = mutableListOf<Int>()

                if (indexOfCurrentDay == indexOfLastDay) {
                    // добавляем номер текущего дня
                    sortByDaysKeys.add(sortKeys[indexOfCurrentDay])

                    // добавляем номера дней из списка перед текущим
                    sortByDaysKeys.addAll(sortKeys.subList(0, indexOfCurrentDay))

                    setSortMapKeys(sortByDaysKeys);
                }

                if (indexOfCurrentDay != 0 && indexOfCurrentDay != indexOfLastDay) {
                    // добавляем номер текущего дня
                    sortByDaysKeys.add(sortKeys[indexOfCurrentDay]);

                    // добавляем номера дней из списка после текущего
                    sortByDaysKeys.addAll(sortKeys.subList(indexOfCurrentDay + 1, sortKeys.size))

                    // добавляем номера дней из списка перед текущим
                    sortByDaysKeys.addAll(sortKeys.subList(0, indexOfCurrentDay));

                    setSortMapKeys(sortByDaysKeys);
                }

                animeMap.putAll(sortMap)
                dateAnimeMap.value = animeMap
            }, {

            }).addToDisposable()
    }

    fun reset() {
        animeMap.clear()
        dateAnimeMap.value?.clear()
        loadData()
        searchValue.value = ""
    }

    private fun searchInCalendar(input: String) {
        if (input.isNotEmpty()) {
            val dateMap = sortedMapOf<String, MutableList<AnimeMalModel>>()
            animeMap.forEach { map ->
                map.value.forEach { anime ->
                    if (anime.title?.contains(input, ignoreCase = true) == true ||
                        anime.alternativeTitles?.en?.contains(input, ignoreCase = true) == true
                    ) {
                        if (dateMap.containsKey(key = map.key)) {
                            dateMap.get(key = map.key)?.add(element = anime)
                        } else {
                            dateMap.set(key = map.key, value = mutableListOf(anime))
                        }
                    }
                }
            }
            dateAnimeMap.value = dateMap
        } else {
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

    private fun String?.dayNameToInt(): Int {
        return when (this) {
            "monday" -> 1
            "tuesday" -> 2
            "wednesday" -> 3
            "thursday" -> 4
            "friday" -> 5
            "saturday" -> 6
            "sunday" -> 7
            else -> 0
        }
    }

    companion object {

        private const val SEARCH_INPUT_VALUE_DEBOUNCE_TIME = 500L
    }
}