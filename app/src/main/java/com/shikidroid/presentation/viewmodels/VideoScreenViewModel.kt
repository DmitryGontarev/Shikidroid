package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.ShimoriVideoInteractor
import com.shikidroid.domain.models.video.*
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.uikit.viewmodel.BaseViewModel

/**
 * ViewModel для экрана видеоплеера
 *
 * @property animeId идентификационный номер аниме
 * @property animeNameEng название аниме на английском
 * @property animeNameEng название аниме на русском
 * @property translation данные трансляции для загрузки видео
 * @property prefs внутреннее хранилище приложения
 * @property shimoriVideoInteractor интерактор для загрузки видео
 */
internal class VideoScreenViewModel(
    private val animeId: Long?,
    private val animeNameEng: String?,
    private val animeNameRu: String?,
    val translation: ShimoriTranslationModel? = null,
    private val prefs: SharedPreferencesProvider,
    private val shimoriVideoInteractor: ShimoriVideoInteractor,
) : BaseViewModel() {

    /** флаг включения режима Картинка-в-картинке */
    val isPictureInPicture = MutableLiveData(false)

    /** флаг нажатия кнопки перемотки Назад в режиме Картинка-в-картинке */
    val backButtonPress = MutableLiveData(false)

    /** флаг нажатия кнопки Play в режиме Картинка-в-картинке */
    val playButtonPress = MutableLiveData(false)

    /** флаг нажатия кнопки перемотки Вперёд в режиме Картинка-в-картинке */
    val nextButtonPress = MutableLiveData(false)

    /** название аниме на русском */
    val nameRu = MutableLiveData<String>("")

    /** текущий номер эпизода */
    val currentEpisode = MutableLiveData<Int>()

    /** общее количество доступных к просмотру эпизодов */
    val totalEpisodes = MutableLiveData<Int>()

    /** ошибка загрузки видео */
    val videoError = MutableLiveData<Boolean>(false)

    /** информация о видео для загрузки */
    val video = MutableLiveData<VideoModel>()

    /** скорость воспроизведения видео */
    val videoSpeed = MutableLiveData<TranslationSpeed>(TranslationSpeed.X1)

    /** флаг показа меню с выбором скорости воспроизведения видео */
    val isVideoSpeedShow = MutableLiveData<Boolean>(false)

    /** тип разрешения видео */
    val videoResolution: MutableLiveData<String?> =
        object : MutableLiveData<String?>() {
            override fun setValue(value: String?) {
                super.setValue(value)
                value?.let { resolution ->
                    videoUrl.value = qualityUrlMap.get(key = resolution)
                    prefs.putString(
                        key = VIDEO_RESOLUTION,
                        s = resolution
                    )
                }
            }
        }

    /** список доступных разрешений видео */
    val resolutions = MutableLiveData<MutableList<String>>(mutableListOf())

    /** флаг показа меню с выбором разрешения видео */
    val isResolutionsShow = MutableLiveData<Boolean>(false)

    /** ссылка на видео */
    val videoUrl = MutableLiveData<String>()

    /** текущее видео для плеера */
    val playerItemIndex = MutableLiveData<Int>(0)

    /** текущая позиции плеера */
    val playerPosition = MutableLiveData<Long>(0L)

    // словарь с разрешением видео файла и ссылкой на него
    private val qualityUrlMap = mutableMapOf<String, String>()

    init {
        nameRu.value = animeNameRu.orEmpty()

        translation?.let {

            it.episodesTotal?.let { episodes ->
                totalEpisodes.value = episodes
            }

            loadVideo(
                malId = it.targetId ?: 1,
                episode = it.episode ?: 1,
                translationType = it.kind,
                author = it.author,
                hosting = it.hosting.orEmpty(),
                hostingId = 1,
                videoId = it.id,
                url = it.url
            )
        }
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
        id: Long = animeId ?: 0,
        name: String? = animeNameEng.orEmpty(),
        episode: Int = 1,
        hostingId: Int = 1,
        translationType: TranslationType = translation?.kind ?: TranslationType.SUB_RU
    ) {
        shimoriVideoInteractor.getTranslations(
            malId = id,
            name = name.orEmpty(),
            episode = episode,
            hostingId = hostingId,
            translationType = translationType
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({ translations ->
                val translation = translations.find {
                    if (translation?.author != null) {
                        it.author == translation.author && it.hosting == translation.hosting
                    } else {
                        it.hosting == translation?.hosting
                    }
                }
                (translation ?: translations.getOrNull(index = 0))?.let {
                    loadVideo(
                        malId = it.targetId ?: 1,
                        episode = it.episode ?: 1,
                        translationType = it.kind,
                        author = it.author,
                        hosting = it.hosting.orEmpty(),
                        hostingId = 1,
                        videoId = it.id,
                        url = it.url
                    )
                }
            }, {
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
            author = author ?: hosting,
            hosting = hosting,
            hostingId = hostingId,
            videoId = videoId,
            url = url,
            accessToken = accessToken
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                video.value = it
                resolutions.value?.clear()
                qualityUrlMap.values.clear()

                if (it.tracks.isNullOrEmpty()) {
                    videoError.value = true
                    return@subscribe
                }

                it.tracks?.forEach { video ->
                    video?.quality?.let { quality ->
                        resolutions.value?.add(quality)
                        video.url?.let { url ->
                            qualityUrlMap.set(
                                key = quality,
                                value = url
                            )
                        }
                    }
                }

                if (resolutions.value?.isNotEmpty() == true) {
                    val savedResolution = prefs.getString(
                        key = VIDEO_RESOLUTION,
                        default = resolutions.value?.maxOrNull()
                    )

                    if (resolutions.value?.contains(savedResolution) == true) {
                        videoResolution.value =
                            savedResolution
                    } else {
                        videoResolution.value =
                            resolutions.value?.first()
                    }
                }

                currentEpisode.value = episode
            }, {
                videoError.value = true
            }).addToDisposable()
    }

    companion object {

        // ключ для сохранения выбранного разрешения видео
        private const val VIDEO_RESOLUTION = "VIDEO_RESOLUTION"
    }
}