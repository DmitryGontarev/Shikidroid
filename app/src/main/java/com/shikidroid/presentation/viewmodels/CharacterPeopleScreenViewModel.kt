package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.CharacterInteractor
import com.shikidroid.domain.interactors.PeopleInteractor
import com.shikidroid.domain.models.anime.AnimeModel
import com.shikidroid.domain.models.manga.MangaModel
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.roles.CharacterDetailsModel
import com.shikidroid.domain.models.roles.CharacterModel
import com.shikidroid.domain.models.roles.PersonDetailsModel
import com.shikidroid.presentation.CharacterPeopleScreenDrawerType
import com.shikidroid.presentation.CharacterPeopleScreenType
import com.shikidroid.uikit.viewmodel.BaseViewModel

/**
 * ViewModel для экрана с персонажами и ролями
 */
internal class CharacterPeopleScreenViewModel(
    val id: Long?,
    private val screenType: CharacterPeopleScreenType?,
    private val characterInteractor: CharacterInteractor,
    private val peopleInteractor: PeopleInteractor
) : BaseViewModel() {

    /** детальная информация о персонаже */
    val characterDetails = MutableLiveData<CharacterDetailsModel>()

    /** детальная информация о человеке */
    val peopleDetails = MutableLiveData<PersonDetailsModel>()

    /** список персонажей, озвученных актёром */
    val seyuBestRoles = MutableLiveData<List<CharacterModel>>()

    /** список лучших работ человек аниме/манга/ранобэ */
    val bestWorksAnimeMangaRanobe =
        MutableLiveData<Triple<List<AnimeModel>, List<MangaModel>, List<MangaModel>>>()

    /** состояние выпадающего меню открыто/закрыто */
    val showDropdownMenu = MutableLiveData<Boolean>(false)

    /** состояние выдвижного меню открыто/закрыто */
    val isDrawerOpen = MutableLiveData<Boolean>(false)

    /** тип выдвижного меню */
    val drawerType = MutableLiveData<CharacterPeopleScreenDrawerType>()

    init {
        id?.let {
            loadData(it)
        }
    }

    /** показать выдвижное меню */
    fun showDrawer(type: CharacterPeopleScreenDrawerType) {
        drawerType.value = type
        isDrawerOpen.value = isDrawerOpen.value != true
    }

    /**
     * Загрузка данных о человек/персонаже
     *
     * @param id идентификационный номер человека/персонажа
     */
    fun loadData(id: Long) {

        if (screenType == CharacterPeopleScreenType.CHARACTER) {
            characterInteractor.getCharacterDetails(id = id)
        } else {
            peopleInteractor.getPersonDetails(peopleId = id)
        }
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                if (screenType == CharacterPeopleScreenType.CHARACTER) {
                    characterDetails.value = it as CharacterDetailsModel
                } else {
                    peopleDetails.value = it as PersonDetailsModel

                    val bestRoles = mutableListOf<CharacterModel>()

                    (it as PersonDetailsModel).roles?.forEach { seyuRoleModel ->
                        seyuRoleModel.characters?.forEach { seyuCharacter ->
                            bestRoles.add(seyuCharacter)
                        }
                    }

                    seyuBestRoles.value = bestRoles

                    val bestAnime = mutableListOf<AnimeModel>()
                    val bestManga = mutableListOf<MangaModel>()
                    val bestRanobe = mutableListOf<MangaModel>()

                    it.works?.forEach { work ->
                        work.anime?.let { anime ->
                            bestAnime.add(anime)
                        }
                        work.manga?.let { manga ->
                            if (manga.type == MangaType.LIGHT_NOVEL || manga.type == MangaType.NOVEL) {
                                bestRanobe.add(manga)
                            } else {
                                bestManga.add(manga)
                            }
                        }
                    }

                    bestWorksAnimeMangaRanobe.value = Triple(
                        first = bestAnime,
                        second = bestManga,
                        third = bestRanobe
                    )
                }
            }, {
                showErrorScreen()
            }).addToDisposable()
    }
}