package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shikidroid.domain.interactors.DownloadVideoInteractor
import com.shikidroid.domain.interactors.ShimoriVideoInteractor
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.models.rates.UserRateModel
import com.shikidroid.domain.models.video.ShimoriEpisodeModel
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.domain.models.video.TranslationType
import com.shikidroid.domain.models.video.VideoModel
import com.shikidroid.uikit.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * View Model экрана эпизодов
 *
 * @property animeId идентификационный номер аниме
 * @property animeUserId идентификационный номер аниме в списке пользователя
 * @property animeNameEng название аниме на английском
 * @property userInteractor интерактор загрузки информации о пользователе
 * @property shimoriVideoInteractor интерактор загрузки данных о видео
 * @property downloadVideoInteractor интерактор загрзуки видео в локальное хранилище
 */
internal class EpisodeScreenViewModel(
    val animeId: Long?,
    private val animeUserId: Long?,
    val animeNameEng: String?,
    private val userInteractor: UserInteractor,
    private val shimoriVideoInteractor: ShimoriVideoInteractor,
    private val downloadVideoInteractor: DownloadVideoInteractor
) : BaseViewModel() {

    /** название аниме на русском */
    val animeNameRu = MutableLiveData<String>("")

    /** данные аниме из пользовательского списка */
    val userRateModel = MutableLiveData<UserRateModel>()

    /** флаг показа выпадающего меню с номерами эпизодов */
    val showEpisodesMenu = MutableLiveData<Boolean>(false)

    /** количество эпизодов */
    val episodes = MutableLiveData<Int>()

    /** список данных эпизодов */
    val series = MutableLiveData<List<ShimoriEpisodeModel>>(listOf())

    /** список данных хостинга для просмотра эпизода */
    val translations = MutableLiveData<List<ShimoriTranslationModel>>(listOf())

    /** номер выбранного для просмотра эпизода */
    val episodeNumber: MutableLiveData<Int> = object : MutableLiveData<Int>() {
        override fun setValue(value: Int?) {
            super.setValue(value)
            value?.let { episode ->
                loadTranslations(
                    id = animeId ?: 0,
                    name = animeNameEng.orEmpty(),
                    episode = episode,
                    translationType = translationType.value ?: TranslationType.SUB_RU
                )
            }
        }
    }

    /** выбранный перевод */
    val translationType: MutableLiveData<TranslationType> =
        object : MutableLiveData<TranslationType>(
            TranslationType.SUB_RU
        ) {
            override fun setValue(value: TranslationType?) {
                super.setValue(value)
                value?.let { translationType ->
                    loadTranslations(
                        id = animeId ?: 0,
                        name = animeNameEng.orEmpty(),
                        episode = episodeNumber.value ?: 1,
                        translationType = translationType
                    )
                }
            }
        }

    /** данные для загрузки видео в видеоплеере */
    val video = MutableLiveData<VideoModel>()

    /** данные для загрузки видео в память */
    val videoForDownload = MutableLiveData<VideoModel>()

    /** флаг показа ошибки, если не удалось получить ссылки для загрузки */
    val videoForDownloadError = MutableLiveData<Boolean>(false)

    /** состояние выпадающего меню загрузки видео открыто/закрыто */
    val showDropdownDownloadMenu = MutableLiveData<Boolean>(false)

    /** выбранный автор для получения ссылки на скачивание */
    val currentAuthor = MutableLiveData<String>("")

    /** выбранный хостинг для получения ссылки на скачивание */
    val currentHosting = MutableLiveData<String>("")

    init {
        viewModelScope.launch {
            animeUserId?.let {
                loadDataUserRate(
                    id = it
                )
                delay(200)
            }
            animeId?.let {
                if (animeUserId == null) {
                    episodeNumber.value = 1
                }
                loadEpisodesSize(id = it)
            }
        }
    }

    /** Загрузка информации об аниме из списка пользователя (количество просмотренных эпизодов) */
    fun loadDataUserRate(id: Long) {
        userInteractor.getRate(id = id)
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                userRateModel.value = it
                episodeNumber.value =
                    if ((it.episodes ?: 0) > 0) {
                        it.episodes
                    } else {
                        1
                    }
            }, {
                loadDataUserRate(id = id)
            }).addToDisposable()
    }

    /** Загрузка информации о количестве эпизодов в аниме */
    fun loadEpisodesSize(id: Long) {
        shimoriVideoInteractor.getEpisodes(
            malId = id,
            name = animeNameEng.orEmpty()
        )
            .compose(doAsyncSingle())
            .subscribe({
                episodes.value = it
            }, {
                loadEpisodesSize(id = id)
            }).addToDisposable()
    }

    fun loadSeries(
        id: Long,
        name: String?
    ) {
        shimoriVideoInteractor.getSeries(
            malId = id,
            name = name.orEmpty()
        )
            .compose(doAsyncSingleWithoutBlocking())
            .subscribe({
                series.value = it
            }, {

            }).addToDisposable()
    }

    /**
     * Загрузка данных трансляции
     *
     * @param id идентификационный номер аниме
     * @param name название аниме на английском
     * @param episode номер эпизода
     * @param hostingId идентификационный номер хостинга
     * @param translationType тип трансляции (оригинал, субтитры, озвучка)
     */
    fun loadTranslations(
        id: Long,
        name: String?,
        episode: Int = 1,
        hostingId: Int = 1,
        translationType: TranslationType = TranslationType.SUB_RU
    ) {
        shimoriVideoInteractor.getTranslations(
            malId = id,
            name = name.orEmpty(),
            episode = episode,
            hostingId = hostingId,
            translationType = translationType
        )
            .compose(doAsyncSingleWithoutBlocking())
            .subscribe({
                translations.value = it
            }, {
                loadTranslations(
                    id = id,
                    name = name,
                    episode = episode,
                    hostingId = hostingId,
                    translationType = translationType
                )
            }).addToDisposable()
    }

    /** Загрузка ссылок на видео для показа в видеоплеере */
    fun loadVideo(
        malId: Long,
        episode: Int = 1,
        translationType: TranslationType?,
        author: String?,
        hosting: String,
        hostingId: Int = 1,
        videoId: Long?,
        url: String?,
        accessToken: String? = null
    ) {
        shimoriVideoInteractor.getVideo(
            malId = malId,
            episode = episode,
            translationType = translationType,
            author = author,
            hosting = hosting,
            hostingId = hostingId,
            videoId = videoId,
            url = url,
            accessToken = accessToken
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                video.value = it
            }, {

            }).addToDisposable()
    }

    /** Загрузка ссылок на видео для загрузки в память */
    fun loadVideoForDownload(
        malId: Long,
        episode: Int = 1,
        translationType: TranslationType?,
        author: String?,
        hosting: String,
        hostingId: Int = 1,
        videoId: Long?,
        url: String?,
        accessToken: String? = null
    ) {
        shimoriVideoInteractor.getVideo(
            malId = malId,
            episode = episode,
            translationType = translationType,
            author = author ?: hosting,
            hosting = hosting,
            hostingId = hostingId,
            videoId = videoId,
            url = url,
            accessToken = accessToken
        )
            .compose(doAsyncSingleWithItemProgress())
            .subscribe({

                if (it.tracks.isNullOrEmpty()) {
                    videoForDownloadError.value = true
                    return@subscribe
                }

                videoForDownload.value = it
            }, {
                videoForDownloadError.value = true
            }).addToDisposable()
    }
}