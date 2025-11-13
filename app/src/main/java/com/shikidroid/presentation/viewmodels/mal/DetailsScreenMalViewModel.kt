package com.shikidroid.presentation.viewmodels.mal

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.MyAnimeListInteractor
import com.shikidroid.domain.models.myanimelist.AnimeMalModel
import com.shikidroid.domain.models.myanimelist.MainPictureModel
import com.shikidroid.domain.models.myanimelist.RelatedAnimeModel
import com.shikidroid.presentation.DetailsScreenType
import com.shikidroid.presentation.DetailScreenDrawerType
import com.shikidroid.uikit.viewmodel.BaseViewModel

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
internal class DetailsScreenMalViewModel(
    val id: Long?,
    private val screenType: DetailsScreenType?,
    private val malInteractor: MyAnimeListInteractor,
) : BaseViewModel() {

    /** детальная информации об аниме */
    val animeDetails = MutableLiveData<AnimeMalModel>()

    /** список произведений связанных с текущим */
    val relatedList = MutableLiveData<List<RelatedAnimeModel>>(listOf())

    /** скриншоты из аниме */
    val animeScreenshots = MutableLiveData<List<MainPictureModel>>(listOf())

    /** список с похожими аниме */
    val animeSimilar = MutableLiveData<List<AnimeMalModel>>()

    /** состояние нижнего выдвижного меню открыто/закрыто */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownMenu = MutableLiveData<Boolean>(false)

    /** тип выдвижного меню */
    val drawerType = MutableLiveData<DetailScreenDrawerType>()

    init {
        id?.let { id ->
            loadDetails(id = id)
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
        malInteractor.getAnimeDetailsById(
            id = id,
            fields = "id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,popularity,num_list_users,num_scoring_users,nsfw,created_at,updated_at,media_type,status,genres,my_list_status,num_episodes,start_season,broadcast,source,average_episode_duration,rating,pictures,background,related_anime,related_manga,recommendations,studios,statistics"
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                animeDetails.value = it

                relatedList.value = it.relatedAnime ?: listOf()

                animeScreenshots.value = it.pictures ?: listOf()

                animeSimilar.value = it.recommendations ?: listOf()
            }, {
                showErrorScreen()
            })
            .addToDisposable()
    }
}