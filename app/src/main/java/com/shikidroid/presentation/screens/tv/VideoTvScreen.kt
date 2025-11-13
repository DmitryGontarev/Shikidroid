package com.shikidroid.presentation.screens.tv

import android.app.PictureInPictureParams
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.shikidroid.*
import com.shikidroid.R
import com.shikidroid.domain.models.video.TranslationSpeed
import com.shikidroid.domain.models.video.getRequestHeaderForHosting
import com.shikidroid.getVideoActivity
import com.shikidroid.presentation.converters.toPlayerSpeed
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.converters.toSourceResolution
import com.shikidroid.presentation.navigation.navigateWebViewTvScreen
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.viewmodels.VideoScreenViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.ui.theme.ShowHideSystemBars
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.CountdownTimer
import com.shikidroid.uikit.components.RoundedIconButton
import com.shikidroid.uikit.components.RoundedTextButton
import com.shikidroid.uikit.components.TvSelectable
import com.shikidroid.utils.LongUtils.toVideoTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Экран показа видео для AndroidTV
 *
 * @param viewModel вью модель экрана
 */
@Composable
internal fun VideoTvScreen(
    viewModel: VideoScreenViewModel
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** флаг включения режима Картинка-в-картинке */
    val isPictureInPicture by viewModel.isPictureInPicture.observeAsState(initial = false)

    /** ошибка загрузки видео */
    val videoError by viewModel.videoError.observeAsState(initial = false)

    /** информация о видео для загрузки */
    val video by viewModel.video.observeAsState()

    /** словарь разрешением видео и ссылкой на это разрешение */
    val videoUrl by viewModel.videoUrl.observeAsState()

    /** скорость воспроизведения видео */
    val videoSpeed by viewModel.videoSpeed.observeAsState()

    /** текущий номер эпизода */
    val currentEpisode by viewModel.currentEpisode.observeAsState(initial = 1)

    /** текущий элемент для проигрывания */
    val playerItemIndex by viewModel.playerItemIndex.observeAsState(0)

    /** текущая позиция плеера */
    val playerPosition by viewModel.playerPosition.observeAsState(0L)

    /** флаг показа кнопок плеера */
    var isControlsShow by remember { mutableStateOf(false) }

    /** область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** контекст представления */
    val context = LocalContext.current

    ShowHideSystemBars()

    LaunchedEffect(key1 = videoError) {
        if (videoError) {
            navigateWebViewTvScreen(
                url = viewModel.translation?.url,
                useIFrame = true,
                context = context
            )
            context.getVideoActivity()?.finish()
        }
    }

    /** плеер */
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .apply {
                setSeekBackIncrementMs(
                    if (isControlsShow) {
                        PLAYER_SEEK_FAST_BACK_INCREMENT
                    } else {
                        PLAYER_SEEK_BACK_INCREMENT
                    }
                )
                setSeekForwardIncrementMs(
                    if (isControlsShow) {
                        PLAYER_SEEK_FAST_FORWARD_INCREMENT
                    } else {
                        PLAYER_SEEK_FORWARD_INCREMENT
                    }
                )
            }
            .build()
    }

    val playerView = remember {
        StyledPlayerView(context).apply {
            player = exoPlayer
            useController = false
            setShowBuffering(StyledPlayerView.SHOW_BUFFERING_NEVER)
            setKeepContentOnPlayerReset(true)
        }
    }

    LaunchedEffect(key1 = videoUrl) {
        videoUrl?.let { url ->

            viewModel.playerItemIndex.value = exoPlayer.currentMediaItemIndex
            viewModel.playerPosition.value = 0L.coerceAtLeast(exoPlayer.contentPosition)

            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            val dataSource = DefaultHttpDataSource.Factory()
            dataSource.setDefaultRequestProperties(
                video.getRequestHeaderForHosting()
            )
            val sourceFactory =
                if (videoUrl.orEmpty().contains("m3u8")) {
                    HlsMediaSource.Factory(dataSource)
                } else {
                    ProgressiveMediaSource.Factory(dataSource)
                }
            val source = sourceFactory.createMediaSource(mediaItem)

            exoPlayer.apply {
                clearMediaItems()
                addMediaSource(source)
                prepare()
                playWhenReady = true
            }
            exoPlayer.seekTo(playerItemIndex, playerPosition)
            exoPlayer.play()
        }
    }

    LaunchedEffect(key1 = videoSpeed) {
        videoSpeed?.toPlayerSpeed()?.let { speed ->
            exoPlayer.setPlaybackSpeed(speed)
        }
    }

    LaunchedEffect(key1 = currentEpisode) {
        exoPlayer.seekTo(0L)
    }

    /** флаг проигрывания/паузы */
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    /** общая длительность в миллисекундах */
    var totalDuration by remember { mutableStateOf(0L) }

    /** текущее время просмотра */
    var currentTime by remember { mutableStateOf(0L) }

    /** процент загрузки */
    var bufferedPercentage by remember { mutableStateOf(0) }

    /** состояние проигрывателя */
    var playbackState by remember { mutableStateOf(exoPlayer.playbackState) }

    /** флаг показа загрузки буферизации */
    var showLoaderBuffering by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            isControlsShow = true
        }
        showLoaderBuffering = playbackState == ExoPlayer.STATE_BUFFERING
    }

    /** счётчик таймера для скрытия кнопок плеера */
    val timerState = remember {
        mutableStateOf(0L)
    }

    if (isControlsShow) {
        if (playbackState != ExoPlayer.STATE_ENDED) {
            CountdownTimer(
                targetTime = 5L,
                timerState = timerState
            ) {
                viewModel.isVideoSpeedShow.value = false
                viewModel.isResolutionsShow.value = false
                isControlsShow = false
            }
        }
    }

    BackHandler {
        if (isControlsShow) {
            isControlsShow = false
        } else {
            context.getVideoActivity()?.finish()
        }
    }

    ComposableLifecycle { lifecycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (context.getVideoActivity()?.isInPictureInPictureMode == true) {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.play()
                        }
                    } else {
                        exoPlayer.pause()
                        viewModel.playerItemIndex.value = exoPlayer.currentMediaItemIndex
                        viewModel.playerPosition.value = 0L.coerceAtLeast(exoPlayer.contentPosition)
                    }
                } else {
                    exoPlayer.pause()
                    viewModel.playerItemIndex.value = exoPlayer.currentMediaItemIndex
                    viewModel.playerPosition.value = 0L.coerceAtLeast(exoPlayer.contentPosition)
                }
            }

            Lifecycle.Event.ON_START -> {
//                exoPlayer.seekTo(playerItemIndex, playerPosition)
                exoPlayer.play()
            }

            else -> {}
        }
    }

    DisposableEffect(Unit) {

        val playerListener =
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    totalDuration = player.duration.coerceAtLeast(0L)
                    bufferedPercentage = player.bufferedPercentage
                    currentTime = player.currentPosition.coerceAtLeast(0L)
                    isPlaying = player.isPlaying
                    playbackState = player.playbackState
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    viewModel.videoError.value = true
                }
            }

        exoPlayer.addListener(playerListener)

        onDispose {
            exoPlayer.pause()
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }

    if (isPlaying) {
        LaunchedEffect(Unit) {
            while (true) {
                currentTime = exoPlayer.currentPosition.coerceAtLeast(0L)
                delay(1.seconds / 30)
            }
        }
    }

    /** флаги показа иконок на экране */
    val replay = remember { mutableStateOf(false) }
    val play = remember { mutableStateOf(false) }
    val pause = remember { mutableStateOf(false) }
    val forward = remember { mutableStateOf(false) }

    /** функция показа иконки */
    fun showIcon(icon: MutableState<Boolean>) {
        coroutineScope.launch {
            icon.value = true
            delay(500)
            icon.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        TvSelectable { interactionSource, isFocused, scale ->
            AndroidView(
                modifier = Modifier
                    .focusable(
                        !isControlsShow,
                        interactionSource
                    )
                    .onKeyEvent {
                        if (!isControlsShow) {
                            when {
                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_UP -> {
                                    isControlsShow = true
                                    timerState.value = 0L
                                }

                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT -> {
                                    exoPlayer.seekBack()
                                    showIcon(icon = replay)
                                }

                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT -> {
                                    exoPlayer.seekForward()
                                    showIcon(icon = forward)
                                }

                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_CENTER &&
                                        it.type == KeyEventType.KeyDown -> {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        showIcon(icon = pause)
                                    } else {
                                        exoPlayer.play()
                                        showIcon(icon = play)
                                    }
                                }
                            }
                        }
                        false
                    },
                factory = {
                    playerView
                }
            )
        }
    }

    if (showLoaderBuffering || isLoading) {
        Loader()
    }

    PlayerTvScreenIcons(
        isReplay = replay.value,
        isPlay = play.value,
        isPause = pause.value,
        isForward = forward.value
    )

    /** кнопки и слайдер для плеера */
    val playerControls: @Composable () -> Unit = {
        PlayerTvControls(
            modifier = Modifier
                .fillMaxSize()
                .onKeyEvent {
                    timerState.value = 0L
                    false
                },
            isVisible = isControlsShow,
            playEnded = playbackState == ExoPlayer.STATE_ENDED,
            onReplayClick = {
                exoPlayer.seekBack()
                showIcon(icon = replay)
            },
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> {
                        exoPlayer.pause()
                        showIcon(icon = pause)
                    }

                    exoPlayer.isPlaying.not() && playbackState == ExoPlayer.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> {
                        exoPlayer.play()
                        showIcon(icon = play)
                    }
                }
                isPlaying = isPlaying.not()
            },
            onForwardClick = {
                exoPlayer.seekForward()
                showIcon(icon = forward)
            },
            totalDuration = totalDuration,
            currentTime = currentTime,
            bufferedPercentage = bufferedPercentage,
            onSeekChanged = { timeMs: Float ->
                exoPlayer.seekTo(timeMs.toLong())
            },
            speedResClickCallback = {
                timerState.value = 0L
            },
            viewModel = viewModel
        )
    }

    if (isPictureInPicture) {
        isControlsShow = false
    } else {
        playerControls()
    }
}

@Composable
internal fun PlayerTvScreenIcons(
    isReplay: Boolean,
    isPlay: Boolean,
    isPause: Boolean,
    isForward: Boolean
) {
    /** цвет левой иконки экрана */
    val replayColor: Color by animateColorAsState(
        targetValue =
        if (isReplay) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    /** цвет центральной иконки проигрывания */
    val playColor: Color by animateColorAsState(
        targetValue =
        if (isPlay) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    /** цвет центральной иконки паузы */
    val pauseColor: Color by animateColorAsState(
        targetValue =
        if (isPause) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    /** цвет правой иконки экрана */
    val forwardColor: Color by animateColorAsState(
        targetValue =
        if (isForward) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_replay_5),
            contentDescription = null,
            tint = replayColor
        )
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(
                    id = R.drawable.ic_play
                ),
                contentDescription = null,
                tint = playColor
            )
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(
                    id = R.drawable.ic_pause
                ),
                contentDescription = null,
                tint = pauseColor
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_forward_10),
            contentDescription = null,
            tint = forwardColor
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun PlayerTvControls(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    playEnded: Boolean,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    speedResClickCallback: () -> Unit,
    viewModel: VideoScreenViewModel
) {

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            TopTvControls(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                -fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                -fullHeight
                            }
                        )
                    ),
                viewModel = viewModel
            )

            BottomTvControls(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                playEnded = playEnded,
                onReplayClick = onReplayClick,
                onPauseToggle = onPauseToggle,
                onForwardClick = onForwardClick,
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged,
                speedResClickCallback = {
                    speedResClickCallback()
                },
                viewModel = viewModel
            )
        }
    }
}

/**
 * Заголовок с переключателям скорости воспроизведения и разрешения видео
 */
@Composable
internal fun TopTvControls(
    modifier: Modifier = Modifier,
    viewModel: VideoScreenViewModel
) {
    /** название аниме на русском */
    val nameRu by viewModel.nameRu.observeAsState(initial = "")

    /** текущий номер эпизода */
    val currentEpisode by viewModel.currentEpisode.observeAsState(initial = 1)

    Column(
        modifier = modifier
            .wrapContentWidth()
            .height(56.dp)
            .padding(horizontal = sevenDP, vertical = sevenDP),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = nameRu,
            style = ShikidroidTheme.typography.body12sp,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "$currentEpisode эпизод",
            style = ShikidroidTheme.typography.body12sp,
            color = ShikidroidTheme.colors.onPrimary,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )
    }
}

/**
 * Нижние кнопки контроля плеера и слайдер длительности
 */
@Composable
internal fun BottomTvControls(
    modifier: Modifier = Modifier,
    playEnded: Boolean,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    speedResClickCallback: () -> Unit,
    viewModel: VideoScreenViewModel
) {

    /** скорость воспроизведения видео */
    val videoSpeed by viewModel.videoSpeed.observeAsState()

    /** текущее разрешение видео */
    val videoResolution by viewModel.videoResolution.observeAsState()

    /** текущий номер эпизода */
    val currentEpisode by viewModel.currentEpisode.observeAsState(initial = 1)

    /** общее количество доступных к просмотру эпизодов */
    val totalEpisodes by viewModel.totalEpisodes.observeAsState(initial = 1)

    /** контекст экрана */
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(bottom = tenDP)
    ) {

        Box(
            modifier = Modifier
                .wrapContentWidth()
        ) {
            Slider(
                value = bufferedPercentage.toFloat(),
                enabled = false,
                onValueChange = { },
                valueRange = 0f..100f,
                colors =
                SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Black,
                    inactiveTrackColor = Color.White
                )
            )
            TvSelectable(scaleAnimation = 1.01f) { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .focusable(true, interactionSource)
                        .background(
                            color =
                            if (isFocused) {
                                ShikidroidTheme.colors.tvSelectable
                            } else {
                                Color.Transparent
                            },
                            shape = ShikidroidTheme.shapes.roundedCorner30dp
                        )
                        .onKeyEvent {
                            when {
                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_LEFT -> {
                                    onReplayClick()
                                }

                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_CENTER
                                        && it.type == KeyEventType.KeyDown -> {
                                    onPauseToggle()
                                }

                                it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_DPAD_RIGHT -> {
                                    onForwardClick()
                                }
                            }
                            false
                        }
                ) {
                    Slider(
                        modifier = Modifier
                            .focusable(true, interactionSource),
                        value = currentTime.toFloat(),
                        enabled = true,
                        onValueChange = onSeekChanged,
                        valueRange = 0f..totalDuration.toFloat(),
                        colors =
                        SliderDefaults.colors(
                            thumbColor = ShikidroidTheme.colors.secondary,
                            activeTrackColor = ShikidroidTheme.colors.secondaryLightVariant,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = fourteenDP),
                text = "${currentTime.toVideoTime()} / ${totalDuration.toVideoTime()}",
                color = ShikidroidTheme.colors.onPrimary
            )

            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                if (playEnded) {
                    TvSelectable() { interactionSource, isFocused, scale ->
                        // кнопка Воспроизведения
                        RoundedIconButton(
                            modifier = Modifier
                                .focusable(true, interactionSource)
                                .scale(scale = scale.value),
                            iconBoxModifier = Modifier
                                .focusable(true, interactionSource)
                                .scale(scale = scale.value),
                            icon = R.drawable.ic_replay,
                            tint = ShikidroidTheme.colors.onPrimary,
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                onPauseToggle()
                            }
                        )
                    }
                }

                TvSelectable() { interactionSource, isFocused, scale ->
                    // кнопка перемотки Назад
                    RoundedIconButton(
                        modifier = Modifier
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        iconBoxModifier = Modifier
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        enabled = currentEpisode > 1,
                        icon = R.drawable.ic_prev,
                        tint =
                        if (currentEpisode > 1) {
                            ShikidroidTheme.colors.onPrimary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        },
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            ShikidroidTheme.colors.tvSelectable
                        },
                        onClick = {
                            viewModel.loadTranslations(episode = currentEpisode.minus(1))
                        }
                    )
                }

                TvSelectable() { interactionSource, isFocused, scale ->
                    // кнопка перемотки Вперёд
                    RoundedIconButton(
                        modifier = Modifier
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        iconBoxModifier = Modifier
                            .focusable(true, interactionSource)
                            .scale(scale = scale.value),
                        enabled = currentEpisode < totalEpisodes,
                        icon = R.drawable.ic_next,
                        tint =
                        if (currentEpisode < totalEpisodes) {
                            ShikidroidTheme.colors.onPrimary
                        } else {
                            ShikidroidTheme.colors.onBackground
                        },
                        backgroundColor =
                        if (isFocused) {
                            ShikidroidTheme.colors.secondaryVariant
                        } else {
                            ShikidroidTheme.colors.tvSelectable
                        },
                        onClick = {
                            viewModel.loadTranslations(episode = currentEpisode.plus(1))
                        }
                    )
                }

                // кнопка переключения Скорости воспроизведения
                TvSelectable() { interactionSource, isFocused, scale ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        RoundedTextButton(
                            modifier = Modifier
                                .focusable(true, interactionSource)
                                .scale(scale = scale.value),
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            text = videoSpeed?.toScreenString().orEmpty(),
                            textColor = ShikidroidTheme.colors.onPrimary,
                            onClick = {
                                viewModel.isVideoSpeedShow.value =
                                    viewModel.isVideoSpeedShow.value != true
                            }
                        )
                        VideoSpeedTvMenu(
                            clickCallback = {
                                speedResClickCallback()
                            },
                            viewModel = viewModel
                        )
                    }
                }

                // кнопка переключения Разрешения видео
                TvSelectable() { interactionSource, isFocused, scale ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        RoundedTextButton(
                            modifier = Modifier
                                .focusable(true, interactionSource)
                                .scale(scale = scale.value),
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            text = videoResolution.toSourceResolution(),
                            textColor = ShikidroidTheme.colors.onPrimary,
                            onClick = {
                                viewModel.isResolutionsShow.value =
                                    viewModel.isResolutionsShow.value != true
                            }
                        )
                        VideoResolutionTvMenu(
                            clickCallback = {
                                speedResClickCallback()
                            },
                            viewModel = viewModel
                        )
                    }
                }

//                TvSelectable() { interactionSource, isFocused, scale ->
//                    Box(
//                        modifier = Modifier
//                            .wrapContentSize()
//                    ) {
//                        RoundedIconButton(
//                            icon = R.drawable.ic_download,
//                            iconBoxModifier = Modifier
//                                .focusable(true, interactionSource)
//                                .scale(scale = scale.value),
//                            backgroundColor =
//                            if (isFocused) {
//                                ShikidroidTheme.colors.secondaryVariant
//                            } else {
//                                ShikidroidTheme.colors.tvSelectable
//                            },
//                            onClick = {
//                                context.openLink(link = viewModel.videoUrl.value?.toDownloadLink())
//                            },
//                            tint = ShikidroidTheme.colors.onPrimary,
//                            isIcon = true
//                        )
//                    }
//                }

                // кнопка перехода в режим Картинка-в-картинке
                if (context.isHasPip()) {
                    TvSelectable() { interactionSource, isFocused, scale ->
                        RoundedIconButton(
                            icon = R.drawable.ic_pip,
                            iconBoxModifier = Modifier
                                .focusable(true, interactionSource)
                                .scale(scale = scale.value),
                            backgroundColor =
                            if (isFocused) {
                                ShikidroidTheme.colors.secondaryVariant
                            } else {
                                ShikidroidTheme.colors.tvSelectable
                            },
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                    context.getVideoActivity()?.enterPictureInPictureMode()
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val params = PictureInPictureParams.Builder()
                                        .build()
                                    context.getVideoActivity()?.enterPictureInPictureMode(params)
                                }
                            },
                            tint = ShikidroidTheme.colors.onPrimary,
                            isIcon = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun VideoSpeedTvMenu(
    clickCallback: () -> Unit,
    viewModel: VideoScreenViewModel
) {
    /** список скорости воспроизведения видео */
    val speeds = TranslationSpeed.values()

    /** скорость воспроизведения видео */
    val videoSpeed by viewModel.videoSpeed.observeAsState()

    /** флаг показа меню с выбором скорости воспроизведения видео */
    val isVideoSpeedShow by viewModel.isVideoSpeedShow.observeAsState(false)

    DropdownMenu(
        modifier = Modifier
            .background(
                color = ShikidroidTheme.colors.surface
            )
            .onKeyEvent {
                clickCallback()
                false
            },
        expanded = isVideoSpeedShow,
        onDismissRequest = { viewModel.isVideoSpeedShow.value = false }
    ) {

        speeds.forEach {

            TvSelectable { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .focusable(true, interactionSource)
                        .scale(scale = scale.value)
                        .background(
                            color =
                            when {
                                isFocused -> ShikidroidTheme.colors.secondaryVariant
                                else -> Color.Transparent
                            }
                        )
                        .clickable {
                            viewModel.videoSpeed.value = it
                            viewModel.isVideoSpeedShow.value = false
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(fourteenDP),
                        text = it.toScreenString(),
                        style = ShikidroidTheme.typography.body13sp,
                        color =
                        if (it == videoSpeed) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onPrimary
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun VideoResolutionTvMenu(
    clickCallback: () -> Unit,
    viewModel: VideoScreenViewModel
) {
    /** список доступных разрешений */
    val resolutions by viewModel.resolutions.observeAsState()

    /** текущее разрешение видео */
    val videoResolution by viewModel.videoResolution.observeAsState()

    /** флаг показа меню выбора разрешения видео */
    val isResolutionsShow by viewModel.isResolutionsShow.observeAsState(initial = false)

    DropdownMenu(
        modifier = Modifier
            .background(
                color = ShikidroidTheme.colors.surface
            )
            .onKeyEvent {
                clickCallback()
                false
            },
        expanded = isResolutionsShow,
        onDismissRequest = { viewModel.isResolutionsShow.value = false }
    ) {
        resolutions?.forEach {

            TvSelectable { interactionSource, isFocused, scale ->
                Box(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .clip(shape = ShikidroidTheme.shapes.roundedCornerTopEndBottomEnd30dp)
                        .focusable(true, interactionSource)
                        .scale(scale = scale.value)
                        .background(
                            color =
                            when {
                                isFocused -> ShikidroidTheme.colors.secondaryVariant
                                else -> Color.Transparent
                            }
                        )
                        .clickable {
                            viewModel.videoResolution.value = it
                            viewModel.isResolutionsShow.value = false
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(fourteenDP),
                        text = it.toSourceResolution(),
                        style = ShikidroidTheme.typography.body13sp,
                        color =
                        if (it == videoResolution) {
                            ShikidroidTheme.colors.secondary
                        } else {
                            ShikidroidTheme.colors.onPrimary
                        }
                    )
                }
            }
        }
    }
}

/** 5 секунд перемотка Назад */
private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L

/** 15 секунд перемотка Назад */
private const val PLAYER_SEEK_FAST_BACK_INCREMENT = 15 * 1000L

/** 10 секунд перемотка Вперёд */
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L

/** 30 секунд перемотка Вперёд */
private const val PLAYER_SEEK_FAST_FORWARD_INCREMENT = 30 * 1000L