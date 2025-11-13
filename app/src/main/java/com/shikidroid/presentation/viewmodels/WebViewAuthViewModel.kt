package com.shikidroid.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.shikidroid.domain.interactors.AuthInteractor
import com.shikidroid.domain.interactors.TokenLocalInteractor
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor
import com.shikidroid.domain.models.user.UserAuthStatus
import com.shikidroid.uikit.viewmodel.BaseViewModel
import io.reactivex.rxjava3.core.Completable
import java.util.regex.Pattern

/**
 * Вью модель для экрана авторизации
 *
 *
 */
internal class WebViewAuthViewModel(
    private val authInteractor: AuthInteractor,
    private val tokenLocalInteractor: TokenLocalInteractor,
    private val userInteractor: UserInteractor,
    private val userLocalInteractor: UserLocalInteractor
) : BaseViewModel() {

    /**
     * Флаг получения токена
     */
    val isTokenReceived = MutableLiveData<Boolean>(false)

    /**
     * Метод обработки строки адреса в браузере для получения кода авторизации
     *
     * @param url ссылка
     */
    fun interceptUrlCode(url: String?) {
        url?.let {
            val matcher = Pattern.compile(SHIKIMORI_AUTH_PATTERN).matcher(it)
            if (matcher.find()) {
                val authCode =
                    if (matcher.group().isNullOrEmpty()) {
                        ""
                    } else {
                        it.let { urlCode ->
                            urlCode.substring(
                                urlCode.lastIndexOf("/")
                            )
                                .replaceFirst("/", "")
                        }
                    }
                signIn(authCode = authCode)
            }
        }
    }

    /**
     * Метод для входа в систему
     */
    private fun signIn(authCode: String?) {
        if (authCode != null && authCode != "") {
            tokenLocalInteractor.removeToken()
            authInteractor.signIn(authCode)
                .flatMapCompletable { tokenModel ->
                    tokenLocalInteractor.saveToken(tokenModel)
                }
                .andThen(userInteractor.getCurrentUserBriefInfo())
                .flatMapCompletable {
                    Completable.fromAction {
                        userLocalInteractor.setUserId(
                            it.id
                        )
                        userLocalInteractor.setUserAuthStatus(UserAuthStatus.AUTHORIZED)
                    }
                }
                .compose(doAsyncCompletable())
                .subscribe({
                    isTokenReceived.value = true
                }, {

                }).addToDisposable()
        }
    }

    companion object {

        private const val SHIKIMORI_AUTH_PATTERN = "https?://(?:www\\.)?shikimori\\.one/oauth/authorize/(?:.*)"
        private const val SHIKIMORI_SIGN_UP_URL = "https://shikimori.one/users/sign_up"
        private const val SHIKIMORI_SIGN_IN_URL = "https://shikimori.one/users/sign_in"
    }
}