package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.shikidroid.domain.interactors.AuthInteractor
import com.shikidroid.domain.interactors.TokenLocalInteractor
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.domain.models.user.UserAuthorizationErrorModel
import com.shikidroid.presentation.navigation.sealedscreens.StartScreens
import com.shikidroid.uikit.viewmodel.BaseViewModel
import com.shikidroid.utils.getHttpErrorModel
import com.shikidroid.utils.getHttpStatusCode
import io.reactivex.rxjava3.core.Completable
import retrofit2.HttpException

/**
 * Вью модель для экрана заставки
 *
 *
 */
internal class SplashScreenViewModel(
    private val authInteractor: AuthInteractor,
    private val tokenLocalInteractor: TokenLocalInteractor,
    private val userInteractor: UserInteractor,
    private val userLocalInteractor: UserLocalInteractor,
) : BaseViewModel() {

    /** ключ экрана для следующей навигации */
    val navigateScreen = MutableLiveData<String>("")

    /** флаг показа экрана входа с использование сервера MyAnimeList */
    val showEnterMalScreen = MutableLiveData<Boolean>(false)

    /** Метод для загрузки данных */
    fun loadData() {
        if (tokenLocalInteractor.isTokenExists()) {
            userInteractor.getCurrentUserBriefInfo()
                .compose(doAsyncSingleWithProgress())
                .subscribe({ userBriefModel ->
                    userLocalInteractor.setUserId(
                        userBriefModel.id
                    )
                    userLocalInteractor.setUserAuthStatus(UserAuthStatus.AUTHORIZED)
                    navigateScreen.value = StartScreens.Bottom.route
                }, {
                    if (it is HttpException) {
                        val statusCode = it.getHttpStatusCode()
                        when (statusCode) {
                            HTTP_401_ERROR_CODE -> {

                                val errorModel = it.getHttpErrorModel<UserAuthorizationErrorModel>()

                                if (errorModel != null) {

                                    showToast(
                                        message = "${errorModel.error_description} | ${errorModel.state}"
                                    )

                                    if (
                                        errorModel.error == INVALID_TOKEN &&
                                        errorModel.error_description == ERROR_DESCRIPTION &&
                                        errorModel.state == AUTHORIZATION_STATE
                                    ) {
                                        val refreshToken: String? = tokenLocalInteractor.getToken()?.refreshToken
                                        tokenLocalInteractor.removeToken()
                                        loadDataIfTokenExpired(refreshToken = refreshToken)
                                    } else {
                                        navigateScreen.value = StartScreens.WebViewAuth.route
                                    }

                                } else {
                                    navigateScreen.value = StartScreens.WebViewAuth.route
                                }

                            }
                            else -> {
                                showEnterMalScreen.value = true
                            }
                        }
                    } else {
                        showEnterMalScreen.value = true
                    }
                }).addToDisposable()
        } else {
            userLocalInteractor.setUserAuthStatus(userAuthStatus = UserAuthStatus.GUEST)
            userInteractor.getCurrentUserBriefInfo()
                .compose(doAsyncSingleWithProgress())
                .subscribe({
                    navigateScreen.value = StartScreens.Enter.route
                }, {
                    if (it is NoSuchElementException) {
                        navigateScreen.value = StartScreens.Enter.route
                    } else {
                        showEnterMalScreen.value = true
                    }
                }).addToDisposable()
        }
    }

    private fun loadDataIfTokenExpired(refreshToken: String?) {
        refreshToken?.let { refresh ->
            tokenLocalInteractor.removeToken()
            authInteractor.refreshToken(
                refreshToken = refresh
            )
                .flatMapCompletable { tokenModel ->
                    tokenLocalInteractor.saveToken(token = tokenModel)
                }
                .andThen(userInteractor.getCurrentUserBriefInfo())
                .flatMapCompletable { userBriefModel ->
                    Completable.fromAction {
                        userLocalInteractor.setUserId(
                            userBriefModel.id
                        )
                        userLocalInteractor.setUserAuthStatus(UserAuthStatus.AUTHORIZED)
                    }
                }
                .compose(doAsyncCompletableWithProgress())
                .subscribe({
                    showToast(message = "Refresh COMPLETE")
                    navigateScreen.value = StartScreens.Bottom.route
                }, {
                    showToast(message = "$it")
                    navigateScreen.value = StartScreens.WebViewAuth.route
                }).addToDisposable()
        }
    }

    companion object {

        // код ошибки, если истёк срок действия основного токена
        private const val HTTP_401_ERROR_CODE = 401

        // строка ошибки недействительного токена
        private const val INVALID_TOKEN = "invalid_token"

        // описание ошибки с недействительным токеном
        private const val ERROR_DESCRIPTION = "The access token is invalid"

        // состояние авторизации с недействительным токеном
        private const val AUTHORIZATION_STATE = "unauthorized"
    }
}