package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.models.rates.RateModel
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.presentation.BottomSheetType

/**
 * Интерфейс для использования полей [RateScreenViewModel] в других вью моделях
 */
interface RateHolder {

    /** список аниме для показа статуса в календаре */
    var animeRatesForCalendar: List<RateModel>

    /** тип нижней шторки */
    val bottomSheetType: MutableLiveData<BottomSheetType>

    /** состояние показа нижней шторки */
    val isBottomSheetVisible: MutableLiveData<Boolean>

    /** выбранный элемента списка */
    val currentItem: MutableLiveData<RateModel?>

    /** статус выбранного элемента */
    val currentItemStatus: MutableLiveData<RateStatus?>

    /** оценка аниме/манги для компонента RatingBar в нижней шторке */
    val ratingBarScore: MutableLiveData<Float?>

    /** флаг показа ошибки ввода просмотренных/прочитанных эпизодов/глав */
    val isEpisodeChapterError: MutableLiveData<Boolean>

    /** количество просмотренных эпизодов/прочитанных глав */
    val episodeChapterCount: MutableLiveData<String>

    /** флаг показа ошибки ввода количества пересмотров/перечитываний */
    val isReWatchesError: MutableLiveData<Boolean>

    /** количество повторных просмотров/повторных чтений */
    val reWatchReReadCount: MutableLiveData<String>

    /** комментарий к списку в нижней шторке */
    val itemListComment: MutableLiveData<String>

    /**
     * показать шторку
     *
     * @param bottomType тип шторки
     */
    fun showBottomSheet(bottomType: BottomSheetType)

    /** скрыть нижнюю шторку */
    fun hideBottomSheet()

    /** изменить элемент списка */
    fun changeRateModel(rateModel: RateModel)

    /** добавить новый элемент в список */
    fun createRateStatus(rateModel: RateModel)

    /**
     * переместить элемент в другой список
     */
    fun changeRateStatus(rateModel: RateModel, newStatus: RateStatus)

    /** удалить элемент из списка */
    fun deleteRateModel(rateModel: RateModel)

    /** найти элемент в словаре статус-список и установить текущий элемент */
    fun findAnimeMangaAndSetCurrentItem(rateModel: RateModel?)
}