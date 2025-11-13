package com.shikidroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Lifecycle
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.domain.models.video.ShimoriTranslationModel
import com.shikidroid.presentation.screens.VideoScreen
import com.shikidroid.presentation.screens.items.NoInternetConnectionMessage
import com.shikidroid.presentation.screens.tv.VideoTvScreen
import com.shikidroid.presentation.viewmodels.VideoScreenViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.uikit.viewmodel.componentActivityViewModelFactory
import com.shikidroid.utils.audioManager

/**
 * Activity для воспроизведения видео
 */
internal class VideoActivity : ComponentActivity() {

    /** слушатель событий нажатий на кнопкие плеера в режиме Картинка-в-картинке */
    var playerReceiver: BroadcastReceiver? = null

    val viewModel: VideoScreenViewModel by componentActivityViewModelFactory(
        viewModelCreator = {
            VideoScreenViewModel(
                animeId = intent.getLongExtra(AppKeys.VIDEOS_SCREEN_ANIME_ID, 0L),
                animeNameEng = intent.getStringExtra(AppKeys.VIDEO_SCREEN_ANIME_ENG_TITLE),
                animeNameRu = intent.getStringExtra(AppKeys.VIDEO_SCREEN_ANIME_RU_TITLE),
                translation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        AppKeys.VIDEO_SCREEN_TRANSLATION_MODEL,
                        ShimoriTranslationModel::class.java
                    )
                } else {
                    intent.getParcelableExtra<ShimoriTranslationModel>(AppKeys.VIDEO_SCREEN_TRANSLATION_MODEL)
                },
                prefs = appComponent.getSharedPreferencesProvider(),
                shimoriVideoInteractor = appComponent.getShimoriVideoInteractor()
            )
        }
    )

    // текущая громкость
    private var currentVolume: Int = 0

    // текущая яркость
    private var currentBrightness: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDisplayCutoutMode()

        /** флаг запуска приложения на Android TV */
        val isAndroidTv = appComponent.getSharedPreferencesProvider().getBoolean(
            key = AppKeys.IS_ANDROID_TV,
            default = false
        )

        // значение максимальной громкости устройства для установки текущего значения в процентах
        val maxVolume = audioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // установка текущих значений громкости и яркости экрана
        currentVolume =
            (audioManager().getStreamVolume(AudioManager.STREAM_MUSIC) * 100) / maxVolume
        currentBrightness = (window.attributes.screenBrightness / 2.55f).toInt()

        setContent {
            /** состояние подключения к интернету */
            val isNetworkConnect =
                appComponent.getNetworkConnectionObserver().observeAsState(initial = true)

            ShikidroidTheme(
                isEdgeToEdge = true,
                darkTheme = true
            ) {
                if (isAndroidTv) {
                    VideoTvScreen(
                        viewModel = viewModel
                    )
                } else {
                    VideoScreen(
                        viewModel = viewModel
                    )
                }

                NoInternetConnectionMessage(showMessage = !isNetworkConnect.value)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.isPictureInPicture.value = isInPictureInPictureMode

        if (isInPictureInPictureMode) {
            // интент фильтр обработки событий нажатия кнопок плеера в режиме Картинка-в-картинке
            val filter = IntentFilter()
            filter.apply {
                addAction(AppKeys.PLAYER_BACK_BUTTON_P_I_P_INTENT_KEY)
                addAction(AppKeys.PLAYER_PLAY_BUTTON_P_I_P_INTENT_KEY)
                addAction(AppKeys.PLAYER_NEXT_BUTTON_P_I_P_INTENT_KEY)
            }

            playerReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        AppKeys.PLAYER_BACK_BUTTON_P_I_P_INTENT_KEY -> {
                            viewModel.backButtonPress.value = true
                        }

                        AppKeys.PLAYER_PLAY_BUTTON_P_I_P_INTENT_KEY -> {
                            viewModel.playButtonPress.value = true
                        }

                        AppKeys.PLAYER_NEXT_BUTTON_P_I_P_INTENT_KEY -> {
                            viewModel.nextButtonPress.value = true
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(playerReceiver, filter, RECEIVER_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                registerReceiver(playerReceiver, filter)
            }
        } else {
            playerReceiver?.let {
                unregisterReceiver(it)
            }
        }

        // в это состояние переходит активити, если нажать закрыть "Закрыть" в режиме Картинка-в-картинке
        if (lifecycle.currentState == Lifecycle.State.CREATED) {
            finishAndRemoveTask()
        }

        // в это состояние переходит активити, если нажать "Увеличить" в режиме Картинка-в-картинке
        if (lifecycle.currentState == Lifecycle.State.STARTED) {

        }
    }

    /** Функция изменения яркости экрана
     *
     * @param percent процент изменения
     * @param callback возврат текста с текущим значением яркости в процентах
     */
    fun changeBrightness(percent: Int, callback: (String) -> Unit) {
        var currentPercent = percent
        when {
            currentPercent < -1 -> {
                currentPercent = -1
            }

            currentPercent > 1 -> {
                currentPercent = 1
            }
        }

        currentBrightness += currentPercent

        when {
            currentBrightness > 100 -> {
                currentBrightness = 100
            }

            currentBrightness < 1 -> {
                currentBrightness = 1
            }

            else -> Unit
        }

        val text = "$currentBrightness%"

        window.attributes = window.attributes.apply {
            screenBrightness = 2.55f * currentBrightness / 255f
        }

        callback(text)
    }

    /**
     * Функция изменения громкости
     *
     * через пропорцию переводить шкалу 0..100% в шаг громкости,
     * так как в устройствах шкала громкости может быть меньше или больше 100
     *
     * @param percent процент изменения
     * @param callback возврат текста с текущим значением громкости в процентах
     */
    fun changeVolume(percent: Int, callback: (String) -> Unit) {
        val manager = audioManager()

        val max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        var currentPercent = percent

        when {
            currentPercent < -1 -> {
                currentPercent = -1
            }

            currentPercent > 1 -> {
                currentPercent = 1
            }
        }

        currentVolume += currentPercent

        when {
            currentVolume > 100 -> {
                currentVolume = 100
            }

            currentVolume < 0 -> {
                currentVolume = 0
            }

            else -> Unit
        }

        // через пропорцию переводим проценты в шаг громкости
        val volume = (max * currentVolume) / 100

        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

        val text = "$currentVolume%"
        callback(text)
    }
}

/**
 * Функция расширение для получения Activity
 */
internal fun Context.getVideoActivity(): VideoActivity? {
    return when (this) {
        is VideoActivity -> this
        is ContextWrapper -> baseContext.getVideoActivity()
        else -> null
    }
}

/** Функция переворота экрана в горизонтальное положение */
internal fun Context.changeScreenOrientation() {
    val activity = this.getVideoActivity() ?: return
    if (this.isHorizontalOrientation()) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    } else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

/**
 * Возвращает true, если поддерживается режим Картинка-в-картинке
 */
internal fun Context.isHasPip(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(
        PackageManager.FEATURE_PICTURE_IN_PICTURE
    )
}