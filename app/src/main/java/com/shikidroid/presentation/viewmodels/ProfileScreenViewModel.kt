package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor
import com.shikidroid.domain.models.user.MessageType
import com.shikidroid.domain.models.user.UserDetailsModel
import com.shikidroid.sharedprefs.SharedPreferencesProvider
import com.shikidroid.uikit.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * View Model для экрана [ProfileScreen] "Профиль" пользователя
 *
 * @property userInteractor интерактор информации пользователя
 * @property profileStorageInteractor интерактор локального хранилища информации о пользователе
 * @property prefs доступ к локальному хранилищу системы Android
 */
internal class ProfileScreenViewModel(
    private val userId: Long?,
    private val userInteractor: UserInteractor,
    private val profileStorageInteractor: UserLocalInteractor,
    private val prefs: SharedPreferencesProvider
) : BaseViewModel() {

    /** детальная информация о пользователе */
    val userDetails = MutableLiveData<UserDetailsModel>()


    init {
        if (userId != null) {
            loadData(id = userId)
        } else {
            val id = profileStorageInteractor.getUserId()
            viewModelScope.launch {
                loadData(id = id)
                delay(500)
                loadUserHistory(id = id)
                delay(500)
                loadUnreadMessages(id = id)
                delay(500)
                loadMessages(id = id)
            }
        }
    }

    fun loadData(id: Long) {
        userInteractor.getUserProfileById(id = id)
            .compose(doAsyncSingleWithProgress())
            .subscribe({
                userDetails.value = it
            }, {

            }).addToDisposable()
    }

    fun loadUserHistory(id: Long) {
        userInteractor.getUserHistory(
            id = id
        )
            .compose(doAsyncSingleWithProgress())
            .subscribe({

            }, {

            }).addToDisposable()
    }

    fun loadUnreadMessages(id: Long) {
        userInteractor.getUnreadMessages(id = id)
            .compose(doAsyncSingleWithProgress())
            .subscribe({

            }, {

            }).addToDisposable()
    }

    fun loadMessages(id: Long) {
        userInteractor.getUserMessages(
            id = id,
            page = 1,
            type = MessageType.NOTIFICATIONS,
            limit = 30
        )
            .compose(doAsyncSingle())
            .subscribe({

            }, {

            }).addToDisposable()
    }
}