package com.shikidroid.presentation.viewmodels.mal

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.*
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.RankingType
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.StringUtils.deleteEmptySpaces
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
internal class SearchScreenMalViewModel(
    private val malInteractor: MyAnimeListInteractor,
) : BaseViewModel() {

    /** флаг открытия бокового меню */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** строка для поиска */
    val searchValue = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            value?.let {
                searchSubject.onNext(it)
            }
        }
    }

    /** список аниме из поиска */
    val animeSearch = MutableLiveData<SnapshotStateList<AnimeMalModel>>()

    private val animeList = mutableListOf<AnimeMalModel>()

    private val searchSubject = PublishSubject.create<String>()

    init {
        loadAnimeByRank()
        initSearchSubject()
    }

    fun loadAnimeByRank(
        limit: Int? = 500,
        rankingType: RankingType = RankingType.ALL,
        fields: String? = "start_date, end_date, media_type, status, num_episodes, mean"
    ) {
        malInteractor.getAnimeRankingList(
            limit = limit,
            rankingType = rankingType,
            fields = fields
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                animeSearch.value?.clear()
                animeList.clear()
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
    fun searchAnime(
        limit: Int? = 100,
        search: String? = null,
        fields: String? = "start_date, end_date, media_type, status, num_episodes, mean"
    ) {
        malInteractor.getAnimeListByParameters(
            limit = limit,
            search = search,
            fields = fields
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                animeSearch.value?.clear()
                animeList.clear()
                it.forEach { anime ->
                    if (!animeList.contains(anime)) {
                        animeList.add(anime)
                    }
                }
                animeSearch.value = animeList.toMutableStateList()
            }, {

            }).addToDisposable()
    }


    fun reset() {
        searchValue.value = ""
        loadAnimeByRank()
    }

    private fun initSearchSubject() {
        searchSubject
            .debounce(SEARCH_INPUT_VALUE_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .compose(doAsync())
            .onErrorReturnItem("")
            .subscribe({
                if (it.isEmpty()) {
                    loadAnimeByRank()
                }
                if (it.deleteEmptySpaces().length >= 3) {
                    searchAnime(search = it)
                }
            }, {}).addToDisposable()
    }

    companion object {

        private const val SEARCH_INPUT_VALUE_DEBOUNCE_TIME = 500L
    }
}