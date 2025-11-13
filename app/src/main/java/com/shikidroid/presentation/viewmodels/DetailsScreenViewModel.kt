package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shikidroid.domain.interactors.AnimeInteractor
import com.shikidroid.domain.interactors.CommentsInteractor
import com.shikidroid.domain.interactors.MangaInteractor
import com.shikidroid.domain.interactors.RanobeInteractor
import com.shikidroid.domain.models.anime.AnimeDetailsModel
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.anime.AnimeVideoModel
import com.shikidroid.domain.models.anime.ScreenshotModel
import com.shikidroid.domain.models.comment.CommentModel
import com.shikidroid.domain.models.comment.CommentableType
import com.shikidroid.domain.models.common.FranchiseModel
import com.shikidroid.domain.models.common.LinkModel
import com.shikidroid.domain.models.common.RelatedModel
import com.shikidroid.domain.models.common.RolesModel
import com.shikidroid.domain.models.manga.MangaDetailsModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.DateUtils
import com.shikidroid.utils.StringUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * View Model экрана детальной информации об аниме/манге
 *
 * @property id идентификтор элемента для получения детальной информации
 * @property screenType тип экрана для показа информации
 * @property animeInteractor интерактор информации об аниме
 * @property mangaInteractor интерактор информации о манге
 * @property ranobeInteractor интерактор информации о ранобэ
 * @property commentsInteractor интерактор получения комментариев
 */
internal class DetailsScreenViewModel(
    val id: Long?,
    private val screenType: DetailsScreenType?,
    private val animeInteractor: AnimeInteractor,
    private val mangaInteractor: MangaInteractor,
    private val ranobeInteractor: RanobeInteractor,
    private val commentsInteractor: CommentsInteractor
) : BaseViewModel() {

    /** детальная информации об аниме */
    val animeDetails = MutableLiveData<AnimeDetailsModel>()

    /** детальная информации о манге и ранобэ */
    val mangaDetails = MutableLiveData<MangaDetailsModel>()

    /** список создателей */
    val rolesList = MutableLiveData<List<RolesModel>>(listOf())

    /** список персонажей */
    val charactersList = MutableLiveData<List<CharacterModel?>>(listOf())

    /** список главных персонажей */
    val mainCharactersList = MutableLiveData<List<CharacterModel?>>(listOf())

    /** список второстепенных персонажей */
    val supportingCharactersList = MutableLiveData<List<CharacterModel?>>(listOf())

    /** флаг наличия персонажей в списке */
    val isCharactersExist = MutableLiveData<Boolean>(false)

    /** список произведений связанных с текущим */
    val relatedList = MutableLiveData<List<RelatedModel>>(listOf())

    /** скриншоты из аниме */
    val animeScreenshots = MutableLiveData<List<ScreenshotModel>>(listOf())

    /** видео, относящееся к аниме */
    val animeVideos = MutableLiveData<List<AnimeVideoModel>>(listOf())

    /** список ссылок сайтов с информацией */
    val externalLinks = MutableLiveData<List<LinkModel>>(listOf())

    /** данные о франшизе аниме/манги */
    val franchiseModel = MutableLiveData<FranchiseModel>()

    /** список с похожими аниме */
    val animeSimilar = MutableLiveData<List<AnimeModel>>()

    /** список с похожей мангой */
    val mangaSimilar = MutableLiveData<List<MangaModel>>()

    /** список комментариев */
    val commentsList = MutableLiveData<List<CommentModel>>(listOf())

    /** страница комментариев */
    val commentsPage = object : MutableLiveData<Int>(1) {
        override fun setValue(value: Int?) {
            super.setValue(value)
            value?.let { page ->
                loadComments(
                    page = page
                )
            }
        }
    }

    /** имя персонажа для поиска */
    val searchCharacter: MutableLiveData<String> =
        object : MutableLiveData<String>(StringUtils.EMPTY) {
            override fun setValue(value: String?) {
                super.setValue(value)
                value?.let {
                    searchCharacter(value = it)
                }
            }
        }

    /** состояние нижнего выдвижного меню открыто/закрыто */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownMenu = MutableLiveData<Boolean>(false)

    /** тип выдвижного меню */
    val drawerType = MutableLiveData<DetailScreenDrawerType>()

    private var characters = listOf<CharacterModel?>()
    private var mutableComments = mutableListOf<CommentModel>()

    init {
        id?.let { id ->
            viewModelScope.launch {
                // если вызывать все запросы сразу, то некоторые могут упасть с ошибкой,
                // поэтому используется задержка в 200 миллисекунд
                loadDetails(id = id)
                delay(200)

                loadRoles(id = id)
                delay(200)

                loadRelated(id = id)
                delay(200)

                if (screenType == DetailsScreenType.ANIME) {
                    loadScreenshots(id = id)
                    delay(200)

                    loadAnimeVideos(id = id)
                    delay(200)
                }

                loadSimilar(id = id)
                delay(200)

                loadLinks(id = id)
                delay(200)

                loadComments()
            }
        }
    }

    /** показать выдвижное меню */
    fun showDrawer(type: DetailScreenDrawerType) {
        drawerType.value = type
        isDrawerOpen.value = isDrawerOpen.value != true
    }

    /**
     * загрузка данных детальной информации
     *
     * @param id идентификационный номер произведения
     */
    fun loadDetails(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getAnimeDetailsById(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getMangaDetailsById(id = id)
            }
            else -> {
                ranobeInteractor.getRanobeDetailsById(id = id)
            }
        }
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                if (screenType == DetailsScreenType.ANIME) {
                    animeDetails.value = it as AnimeDetailsModel
                } else {
                    mangaDetails.value = it as MangaDetailsModel
                }
            }, {
                showErrorScreen()
            })
            .addToDisposable()
    }

    private fun loadRoles(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getAnimeRolesById(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getMangaRolesById(id = id)
            }
            else -> {
                ranobeInteractor.getRanobeRolesById(id = id)
            }
        }
            .compose(doAsyncSingle())
            .subscribe({
                rolesList.value = it
                val mainCharacters = mutableListOf<CharacterModel?>()
                val supportingCharacters = mutableListOf<CharacterModel?>()
                it.filter { role ->
                    role.character != null
                }.forEach { role ->
                    when {
                        role.roles?.contains(MAIN_CHARACTER) == true -> {
                            mainCharacters.add(role.character)
                        }
                        role.roles?.contains(SUPPORTING_CHARACTER) == true -> {
                            supportingCharacters.add(role.character)
                        }
                        else -> {
                            supportingCharacters.add(role.character)
                        }
                    }
                }
                mainCharacters.sortBy { character ->
                    character?.nameRu
                }
                supportingCharacters.sortBy { character ->
                    character?.nameRu
                }
                characters = mainCharacters + supportingCharacters
                if (characters.isEmpty().not()) {
                    isCharactersExist.value = true
                }
                charactersList.value = mainCharacters + supportingCharacters
                mainCharactersList.value = mainCharacters
                supportingCharactersList.value = supportingCharacters
            }, {
                loadRoles(id = id)
            }).addToDisposable()
    }

    private fun loadRelated(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getRelatedAnime(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getRelatedManga(id = id)
            }
            else -> {
                ranobeInteractor.getRelatedRanobe(id = id)
            }
        }
            .compose(doAsyncSingle())
            .subscribe({
                relatedList.value = it
            }, {
                loadRelated(id = id)
            }).addToDisposable()
    }

    private fun loadScreenshots(id: Long) {
        animeInteractor.getAnimeScreenshotsById(id = id)
            .compose(doAsyncSingle())
            .subscribe({
                animeScreenshots.value = it
            }, {
                loadScreenshots(id = id)
            }).addToDisposable()
    }

    private fun loadAnimeVideos(id: Long) {
        animeInteractor.getAnimeVideos(id = id)
            .compose(doAsyncSingle())
            .subscribe({
                animeVideos.value = it
            }, {
                loadAnimeVideos(id = id)
            }).addToDisposable()
    }

    private fun loadFranchise(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getAnimeFranchise(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getMangaFranchise(id = id)
            }
            else -> {
                ranobeInteractor.getRanobeFranchise(id = id)
            }
        }
            .compose(doAsyncSingle())
            .subscribe({
                franchiseModel.value = it
            }, {

            }).addToDisposable()
    }

    private fun loadSimilar(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getSimilarAnime(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getSimilarManga(id = id)
            }
            else -> {
                ranobeInteractor.getSimilarRanobe(id = id)
            }
        }
            .compose(doAsyncSingle())
            .subscribe({
                if (screenType == DetailsScreenType.ANIME) {
                    animeSimilar.value = it as List<AnimeModel>
                } else {
                    mangaSimilar.value = it as List<MangaModel>
                }
            }, {
                loadSimilar(id = id)
            }).addToDisposable()
    }

    private fun loadLinks(id: Long) {
        when (screenType) {
            DetailsScreenType.ANIME -> {
                animeInteractor.getAnimeExternalLinksById(id = id)
            }
            DetailsScreenType.MANGA -> {
                mangaInteractor.getMangaExternalLinksById(id = id)
            }
            else -> {
                ranobeInteractor.getRanobeExternalLinksById(id = id)
            }
        }
            .compose(doAsyncSingle())
            .subscribe({
                externalLinks.value = it
            }, {
                loadLinks(id = id)
            }).addToDisposable()
    }

    fun loadComments(
        id: Long = (animeDetails.value?.topicId ?: mangaDetails.value?.topicId) ?: 0,
        type: CommentableType = CommentableType.TOPIC,
        page: Int = 1,
        limit: Int = 10,
        desc: Int? = 1
    ) {
        commentsInteractor.getComments(
            id = id,
            type = type,
            page = page,
            limit = limit,
            desc = desc
        )
            .compose(doAsyncSingleWithoutBlocking())
            .subscribe({ comments ->
                comments.forEach { comment ->
                    if (!mutableComments.contains(comment)) {
                        mutableComments.add(comment)
                    }
                }
                mutableComments.sortBy {
                    DateUtils.fromString(
                        dateString = it.dateCreated
                    )
                }
                commentsList.value = mutableComments
            }, {
                loadComments(
                    id = (animeDetails.value?.topicId ?: mangaDetails.value?.topicId) ?: 0,
                    type = type,
                    page = page,
                    limit = limit,
                    desc = desc
                )
            }).addToDisposable()
    }

    private fun searchCharacter(value: String?) {
        if (value.isNullOrEmpty()) {
            charactersList.value = characters
        } else {
            charactersList.value = characters.filter {
                it?.name?.contains(value, ignoreCase = true) == true ||
                        it?.nameRu?.contains(value, ignoreCase = true) == true
            }
        }
    }

    companion object {

        private const val MAIN_CHARACTER = "Main"
        private const val SUPPORTING_CHARACTER = "Supporting"
    }
}