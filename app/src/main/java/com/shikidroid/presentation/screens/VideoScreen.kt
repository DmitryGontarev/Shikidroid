package com.shikidroid.presentation.screens

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.shikidroid.appconstants.AppKeys
import com.shikidroid.domain.models.video.TranslationSpeed
import com.shikidroid.domain.models.video.getRequestHeaderForHosting
import com.shikidroid.getVideoActivity
import com.shikidroid.presentation.converters.toPlayerSpeed
import com.shikidroid.presentation.converters.toScreenString
import com.shikidroid.presentation.converters.toSourceResolution
import com.shikidroid.presentation.navigation.navigateWebViewTvScreen
import com.shikidroid.presentation.screens.items.Loader
import com.shikidroid.presentation.screens.items.Text16SemiBold
import com.shikidroid.presentation.viewmodels.VideoScreenViewModel
import com.shikidroid.ui.theme.ShikidroidTheme
import com.shikidroid.ui.theme.ShowHideSystemUi
import com.shikidroid.uikit.*
import com.shikidroid.uikit.components.CountdownTimer
import com.shikidroid.uikit.components.KeepScreenOn
import com.shikidroid.uikit.components.RoundedIconButton
import com.shikidroid.utils.LongUtils.toVideoTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun VideoScreen(
    viewModel: VideoScreenViewModel
) {
    /** флаг загрузки данных */
    val isLoading by viewModel.isLoading.observeAsState(initial = true)

    /** флаг включения режима Картинка-в-картинке */
    val isPictureInPicture by viewModel.isPictureInPicture.observeAsState(initial = false)

    /** флаг нажатия кнопки перемотки Назад в режиме Картинка-в-картинке */
    val backButtonPress by viewModel.backButtonPress.observeAsState(initial = false)

    /** флаг нажатия кнопки Play в режиме Картинка-в-картинке */
    val playButtonPress by viewModel.playButtonPress.observeAsState(initial = false)

    /** флаг нажатия кнопки Play в режиме Картинка-в-картинке */
    val nextButtonPress by viewModel.nextButtonPress.observeAsState(initial = false)

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

    /** контекст представления */
    val context = LocalContext.current

    ShowHideSystemUi()

    KeepScreenOn(activity = context.getVideoActivity())

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
                setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
                setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
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

    LaunchedEffect(key1 = backButtonPress) {
        if (backButtonPress) {
            exoPlayer.seekBack()
            viewModel.backButtonPress.value = false
        }
    }

    LaunchedEffect(key1 = playButtonPress) {
        if (playButtonPress) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            viewModel.playButtonPress.value = false
        }
    }

    LaunchedEffect(key1 = nextButtonPress) {
        if (nextButtonPress) {
            exoPlayer.seekForward()
            viewModel.nextButtonPress.value = false
        }
    }

    LaunchedEffect(key1 = currentEpisode) {
        exoPlayer.seekTo(0L)
    }

//    LaunchedEffect(key1 = exoPlayer.contentPosition) {
//        viewModel.playerItemIndex.value = exoPlayer.currentMediaItemIndex
//        viewModel.playerPosition.value = 0L.coerceAtLeast(exoPlayer.contentPosition)
//    }

    /** флаг показа кнопок плеера */
    var isControlsShow by remember { mutableStateOf(false) }

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

    if (isControlsShow) {
        if (playbackState != ExoPlayer.STATE_ENDED) {
            CountdownTimer(targetTime = 5L) {
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

    /** флаг увеличения размер окна с видео */
    val isScaleScreen = remember {
        mutableStateOf<Boolean?>(null)
    }

    /** величина увеличения размер окна с видео */
    val scaleValue = remember {
        mutableStateOf(1f)
    }

    val videoHeight = remember {
        mutableStateOf(0)
    }

    val videoWidth = remember {
        mutableStateOf(0)
    }

    GetIncreaseValueByScreenRatio(
        scaleState = isScaleScreen.value ?: false,
        videoHeight = videoHeight.value,
        videoWidth = videoWidth.value,
        ratio = {
            scaleValue.value = it
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .scale(
                    scale = scaleValue.value
                )
                .onSizeChanged {
                    videoHeight.value = it.height
                    videoWidth.value = it.width
                },
            factory = {
                playerView
            }
        )
    }

    if (showLoaderBuffering || isLoading) {
        Loader()
    }

    fun playerSeekTo(percent: Float, isDragEnd: Boolean): Triple<String, String, String> {
        var targetTime = currentTime + (percent.toLong() * 100L)

        if (targetTime <= 0L) {
            targetTime = 0L
        }

        if (targetTime >= totalDuration) {
            targetTime = totalDuration
        }

        if (isDragEnd) {
            exoPlayer.seekTo(
                targetTime
            )
        }

        return Triple(
            targetTime.toVideoTime(),
            currentTime.toVideoTime(),
            totalDuration.toVideoTime()
        )
    }

    GesturesControls(
        onReplayClick = { exoPlayer.seekBack() },
        onForwardClick = { exoPlayer.seekForward() },
        onTapClick = { isControlsShow = !isControlsShow },
        onSeekChanged = { time, isDragEnd ->
            playerSeekTo(
                percent = time,
                isDragEnd = isDragEnd
            )
        },
        isScaleScreen = { isScale ->
            isScaleScreen.value = isScale
        }
    )

    /** кнопки и слайдер для плеера */
    val playerControls: @Composable () -> Unit = {
        PlayerControls(
            modifier = Modifier
                .fillMaxSize(),
            isVisible = isControlsShow,
            isPlaying = isPlaying,
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> exoPlayer.pause()
                    exoPlayer.isPlaying.not() && playbackState == ExoPlayer.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> exoPlayer.play()
                }
                isPlaying = isPlaying.not()
            },
            totalDuration = totalDuration,
            currentTime = currentTime,
            bufferedPercentage = bufferedPercentage,
            playBackState = playbackState,
            onSeekChanged = { timeMs: Float ->
                exoPlayer.seekTo(timeMs.toLong())
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isPlaying: Boolean,
    onPauseToggle: () -> Unit,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    playBackState: Int,
    onSeekChanged: (timeMs: Float) -> Unit,
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
            TopControls(
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

            CenterControls(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                isPlaying = isPlaying,
                playBackState = playBackState,
                onPauseToggle = { onPauseToggle() },
                viewModel = viewModel
            )

            BottomControls(
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
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged
            )
        }
    }
}

/**
 * Заголовок с переключателям скорости воспроизведения и разрешения видео
 */
@Composable
internal fun TopControls(
    modifier: Modifier = Modifier,
    viewModel: VideoScreenViewModel
) {

    /** название аниме на русском */
    val nameRu by viewModel.nameRu.observeAsState(initial = "")

    /** текущий номер эпизода */
    val currentEpisode by viewModel.currentEpisode.observeAsState(initial = 1)

    /** скорость воспроизведения видео */
    val videoSpeed by viewModel.videoSpeed.observeAsState()

    /** флаг показа меню с выбором скорости воспроизведения видео */
    val isVideoSpeedShow by viewModel.isVideoSpeedShow.observeAsState(false)

    /** текущее разрешение видео */
    val videoResolution by viewModel.videoResolution.observeAsState()

    /** флаг показа меню выбора разрешения видео */
    val isResolutionsShow by viewModel.isResolutionsShow.observeAsState(initial = false)

    /** контекст экрана */
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = sevenDP, vertical = sevenDP),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .weight(weight = 0.95f)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RoundedIconButton(
                icon = R.drawable.ic_arrow_back,
                backgroundColor = Color.Transparent,
                onClick = {
                    context.getVideoActivity()?.finish()
                },
                tint = ShikidroidTheme.colors.onPrimary
            )

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = threeDP),
                    text = nameRu,
                    style = ShikidroidTheme.typography.body12sp,
                    color = ShikidroidTheme.colors.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .width(
                            if (context.isHorizontalOrientation()) {
                                twoHundredFiftyDP
                            } else {
                                fiftyDP
                            }
                        )
                        .padding(top = threeDP),
                    text = "$currentEpisode эпизод",
                    style = ShikidroidTheme.typography.body12sp,
                    color = ShikidroidTheme.colors.onPrimary,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable {
                            viewModel.isVideoSpeedShow.value =
                                viewModel.isVideoSpeedShow.value != true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = sevenDP),
                        text = videoSpeed?.toScreenString().orEmpty(),
                        style = ShikidroidTheme.typography.body13sp,
                        color = ShikidroidTheme.colors.onPrimary
                    )
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = sevenDP)
                            .rotate(
                                if (isVideoSpeedShow) 180f else 0f
                            ),
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        tint = ShikidroidTheme.colors.onPrimary,
                        contentDescription = null
                    )
                }
                VideoSpeedMenu(viewModel = viewModel)
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable {
                            viewModel.isResolutionsShow.value =
                                viewModel.isResolutionsShow.value != true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = sevenDP),
                        text = videoResolution.toSourceResolution(),
                        style = ShikidroidTheme.typography.body13sp,
                        color = ShikidroidTheme.colors.onPrimary
                    )
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = sevenDP)
                            .rotate(
                                if (isResolutionsShow) 180f else 0f
                            ),
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        tint = ShikidroidTheme.colors.onPrimary,
                        contentDescription = null
                    )
                }
                VideoResolutionMenu(viewModel = viewModel)
            }
        }
    }
}

@Composable
internal fun VideoSpeedMenu(
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
            ),
        expanded = isVideoSpeedShow,
        onDismissRequest = { viewModel.isVideoSpeedShow.value = false }
    ) {
        speeds.forEach {
            Text(
                modifier = Modifier
                    .padding(fourteenDP)
                    .clickable {
                        viewModel.videoSpeed.value = it
                        viewModel.isVideoSpeedShow.value = false
                    },
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

@Composable
internal fun VideoResolutionMenu(
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
            ),
        expanded = isResolutionsShow,
        onDismissRequest = { viewModel.isResolutionsShow.value = false }
    ) {
        resolutions?.forEach {
            Text(
                modifier = Modifier
                    .padding(fourteenDP)
                    .clickable {
                        viewModel.videoResolution.value = it
                        viewModel.isResolutionsShow.value = false
                    },
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

/**
 * Центральные кнопки контроля плеера Назад/Воспроизведение/Вперёд
 */
@Composable
internal fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    playBackState: Int,
    onPauseToggle: () -> Unit,
    viewModel: VideoScreenViewModel
) {
    /** текущий номер эпизода */
    val currentEpisode by viewModel.currentEpisode.observeAsState(initial = 1)

    /** общее количество доступных к просмотру эпизодов */
    val totalEpisodes by viewModel.totalEpisodes.observeAsState(initial = 1)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        // кнопка переключения на предыдущий эпизод
        RoundedIconButton(
            modifier = Modifier
                .padding(horizontal = fourteenDP),
            enabled = currentEpisode > 1,
            iconBoxSize = fortyDP,
            icon = R.drawable.ic_prev,
            tint =
            if (currentEpisode > 1) {
                ShikidroidTheme.colors.onPrimary
            } else {
                ShikidroidTheme.colors.onBackground
            },
            onClick = {
                viewModel.loadTranslations(episode = currentEpisode.minus(1))
            }
        )

        // кнопка Воспроизведения
        RoundedIconButton(
            iconBoxSize = fortyDP,
            icon =
            when {
                isPlaying -> R.drawable.ic_pause
                !isPlaying && playBackState == ExoPlayer.STATE_ENDED -> R.drawable.ic_replay
                else -> R.drawable.ic_play
            },
            tint = ShikidroidTheme.colors.onPrimary,
            onClick = {
                onPauseToggle()
            }
        )

        // кнопка переключения на следующий эпизод
        RoundedIconButton(
            modifier = Modifier
                .padding(horizontal = fourteenDP),
            enabled = currentEpisode < totalEpisodes,
            iconBoxSize = fortyDP,
            icon = R.drawable.ic_next,
            tint =
            if (currentEpisode < totalEpisodes) {
                ShikidroidTheme.colors.onPrimary
            } else {
                ShikidroidTheme.colors.onBackground
            },
            onClick = {
                viewModel.loadTranslations(episode = currentEpisode.plus(1))
            }
        )
    }
}

/**
 * Нижние кнопки контроля плеера и слайдер длительности
 */
@Composable
internal fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    /** контектс экрана */
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = fourteenDP,
                end = fourteenDP,
                bottom = threeDP
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            modifier = Modifier,
            text = "${currentTime.toVideoTime()} / ${totalDuration.toVideoTime()}",
            color = ShikidroidTheme.colors.onPrimary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(0.95f)
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
                        inactiveTrackColor = ShikidroidTheme.colors.onPrimary
                    )
                )
                Slider(
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

            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (context.isHasPip()) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                context.getVideoActivity()?.enterPictureInPictureMode()
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val intentBack = Intent(AppKeys.PLAYER_BACK_BUTTON_P_I_P_INTENT_KEY)
                                val intentPlay = Intent(AppKeys.PLAYER_PLAY_BUTTON_P_I_P_INTENT_KEY)
                                val intentNext = Intent(AppKeys.PLAYER_NEXT_BUTTON_P_I_P_INTENT_KEY)
                                val params = PictureInPictureParams.Builder()
                                    .setActions(
                                        listOf(
                                            RemoteAction(
                                                android.graphics.drawable.Icon.createWithResource(
                                                    context, R.drawable.ic_replay_5
                                                ),
                                                "Back",
                                                "",
                                                PendingIntent.getBroadcast(
                                                    context,
                                                    0,
                                                    intentBack,
                                                    PendingIntent.FLAG_IMMUTABLE
                                                )
                                            ),
                                            RemoteAction(
                                                android.graphics.drawable.Icon.createWithResource(
                                                    context, R.drawable.ic_play_pause
                                                ),
                                                "Play",
                                                "",
                                                PendingIntent.getBroadcast(
                                                    context,
                                                    0,
                                                    intentPlay,
                                                    PendingIntent.FLAG_IMMUTABLE
                                                )
                                            ),
                                            RemoteAction(
                                                android.graphics.drawable.Icon.createWithResource(
                                                    context, R.drawable.ic_forward_10
                                                ),
                                                "Next",
                                                "",
                                                PendingIntent.getBroadcast(
                                                    context,
                                                    0,
                                                    intentNext,
                                                    PendingIntent.FLAG_IMMUTABLE
                                                )
                                            )
                                        )
                                    )
                                    .build()
                                context.getVideoActivity()?.enterPictureInPictureMode(params)
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = threeDP),
                            painter = painterResource(
                                id = R.drawable.ic_pip
                            ),
                            tint = ShikidroidTheme.colors.onPrimary,
                            contentDescription = null
                        )
                    }
                }

                IconButton(
                    modifier = Modifier,
                    onClick = {
                        context.changeScreenOrientation()
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = threeDP),
                        painter = painterResource(
                            id = R.drawable.ic_screen_rotation
                        ),
                        tint = ShikidroidTheme.colors.onPrimary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
internal fun GesturesControls(
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onTapClick: () -> Unit,
    onSeekChanged: (time: Float, isDragEnd: Boolean) -> Triple<String, String, String>,
    isScaleScreen: (Boolean?) -> Unit
) {
    /** центр ширины контейнера */
    val screenCenterX = remember { mutableStateOf(0.dp) }

    /** флаг нажатия на левую часть экрана */
    val leftTap = remember {
        mutableStateOf(false)
    }

    /** цвет левой части экрана */
    val leftBoxColor: Color by animateColorAsState(
        targetValue =
        if (leftTap.value) {
            Color.Black.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 500
        )
    )

    /** цвет левой иконки экрана */
    val leftIconColor: Color by animateColorAsState(
        targetValue =
        if (leftTap.value) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    /** флаг нажатия на правую часть экрана */
    val rightTap = remember {
        mutableStateOf(false)
    }

    /** цвет правой части экрана */
    val rightBoxColor: Color by animateColorAsState(
        targetValue =
        if (rightTap.value) {
            Color.Black.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 500
        )
    )

    /** цвет правой иконки экрана */
    val rightIconColor: Color by animateColorAsState(
        targetValue =
        if (rightTap.value) {
            ShikidroidTheme.colors.onPrimary
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 100
        )
    )

    /** област выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** контекст */
    val context = LocalContext.current

    /** флаг изменения яркости */
    val isBrightnessChange = remember {
        mutableStateOf(false)
    }

    /** флаг изменения громкости */
    val isVolumeChange = remember {
        mutableStateOf(false)
    }

    /** строка с текущим количеством процентов яркости */
    val brightnessText = remember {
        mutableStateOf("")
    }

    /** строка с текущим количеством процентов громкости */
    val volumeText = remember {
        mutableStateOf("")
    }

    /** флаг горизонтальной прокрутки с левой стороны */
    val leftHorizontalScroll = remember {
        mutableStateOf(false)
    }

    /** флаг горизонтальной прокрутки с правой стороны */
    val rightHorizontalScroll = remember {
        mutableStateOf(false)
    }

    /** флаг жеста увеличения окна видео с левой стороны */
    val isIncreaseLeftScale = remember {
        mutableStateOf(false)
    }

    /** флаг жеста увеличения окна видео с правой стороны */
    val isIncreaseRightScale = remember {
        mutableStateOf(false)
    }

    /** время перемотки */
    val seekTime = remember {
        mutableStateOf(0f)
    }

    /** флаг показа времени перемотки */
    val showCenterTime = remember {
        mutableStateOf(false)
    }

    /** строка текста с временем перемотки */
    val timeText = remember {
        mutableStateOf(Triple<String, String, String>("", "", ""))
    }

    if (leftHorizontalScroll.value && rightHorizontalScroll.value) {
        when {
            isIncreaseLeftScale.value && isIncreaseRightScale.value -> {
                isScaleScreen(true)
            }

            !isIncreaseLeftScale.value && !isIncreaseRightScale.value -> {
                isScaleScreen(false)
            }

            else -> {
                isScaleScreen(null)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                screenCenterX.value = it.width.dp / 2
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        when {
                            it.x.dp < screenCenterX.value -> {
                                coroutineScope.launch {
                                    leftTap.value = true
                                    delay(500)
                                    leftTap.value = false
                                    onReplayClick()
                                }
                            }

                            it.x.dp > screenCenterX.value -> {
                                coroutineScope.launch {
                                    rightTap.value = true
                                    delay(500)
                                    rightTap.value = false
                                    onForwardClick()
                                }
                            }

                            else -> Unit
                        }
                    },
                    onTap = { onTapClick() }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ////////////////////////////////////////////////////////////////////////////////////////////
        // ЛЕВАЯ ЧАСТЬ ЭКРАНА
        ////////////////////////////////////////////////////////////////////////////////////////////
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 0.25f)
                .background(
                    color = leftBoxColor,
                    shape = ShikidroidTheme.shapes.absoluteRounded50dp
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            leftHorizontalScroll.value = true
                        },
                        onDragEnd = {
                            leftHorizontalScroll.value = false
                        },
                        onDragCancel = {
                            leftHorizontalScroll.value = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            isIncreaseLeftScale.value = dragAmount < 0
                            change.consume()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .width(
                        width = if (isComposableHorizontalOrientation()) {
                            oneHundredFiftyDP
                        } else {
                            fiftyDP
                        }
                    )
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isBrightnessChange.value = true
                            },
                            onDragEnd = {
                                isBrightnessChange.value = false
                            },
                            onDragCancel = {
                                isBrightnessChange.value = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                context
                                    .getVideoActivity()
                                    ?.changeBrightness(
                                        percent = -dragAmount.toInt()
                                    ) { text ->
                                        brightnessText.value = text
                                    }
                                change.consume()
                            }
                        )
                    }
            )

            if (isBrightnessChange.value) {
                Row(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.TopCenter
                        )
                        .wrapContentSize()
                        .padding(top = fiftyDP)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = ShikidroidTheme.shapes.absoluteRounded50dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(sevenDP),
                        painter = painterResource(id = R.drawable.ic_sun),
                        tint = ShikidroidTheme.colors.onPrimary,
                        contentDescription = null
                    )
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = brightnessText.value
                    )
                }
            }

            Icon(
                painter = painterResource(
                    id = R.drawable.ic_replay_5
                ),
                contentDescription = null,
                tint = leftIconColor
            )
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // ЦЕНТРАЛЬНАЯ ЧАСТЬ ЭКРАНА
        ////////////////////////////////////////////////////////////////////////////////////////////
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 0.5f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { },
                        onDragEnd = {
                            timeText.value = onSeekChanged(seekTime.value, true)
                            seekTime.value = 0f
                            showCenterTime.value = false
                        },
                        onDragCancel = {
                            seekTime.value = 0f
                            showCenterTime.value = false
                        },
                        onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                            seekTime.value += dragAmount.toLong()
                            timeText.value = onSeekChanged(seekTime.value, false)
                            showCenterTime.value = true
                            change.consume()
                        }
                    )
                }
        ) {
            if (showCenterTime.value) {
                Row(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.TopCenter
                        )
                        .wrapContentSize()
                        .padding(top = fiftyDP)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = ShikidroidTheme.shapes.absoluteRounded50dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(
                                start = sevenDP,
                                top = sevenDP,
                                bottom = sevenDP
                            ),
                        text = timeText.value.first
                    )
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = "| ${timeText.value.second} |",
                        color = ShikidroidTheme.colors.onBackground
                    )
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(
                                top = sevenDP,
                                bottom = sevenDP,
                                end = sevenDP
                            ),
                        text = timeText.value.third
                    )
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // ПРАВАЯ ЧАСТЬ ЭКРАНА
        ////////////////////////////////////////////////////////////////////////////////////////////
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 0.25f)
                .background(
                    color = rightBoxColor,
                    shape = ShikidroidTheme.shapes.absoluteRounded50dp
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            rightHorizontalScroll.value = true
                        },
                        onDragEnd = {
                            rightHorizontalScroll.value = false
                        },
                        onDragCancel = {
                            rightHorizontalScroll.value = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            isIncreaseRightScale.value = dragAmount > 0
                            change.consume()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterEnd)
                    .width(
                        width = if (isComposableHorizontalOrientation()) {
                            oneHundredFiftyDP
                        } else {
                            fiftyDP
                        }
                    )
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isVolumeChange.value = true
                            },
                            onDragEnd = {
                                isVolumeChange.value = false
                            },
                            onDragCancel = {
                                isVolumeChange.value = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                context
                                    .getVideoActivity()
                                    ?.changeVolume(
                                        percent = -dragAmount.roundToInt()
                                    ) { text ->
                                        volumeText.value = text
                                    }
                                change.consume()
                            }
                        )
                    }
            )

            if (isVolumeChange.value) {
                Row(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.TopCenter
                        )
                        .wrapContentSize()
                        .padding(top = fiftyDP)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = ShikidroidTheme.shapes.absoluteRounded50dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(sevenDP),
                        painter = painterResource(id = R.drawable.ic_volume),
                        tint = ShikidroidTheme.colors.onPrimary,
                        contentDescription = null
                    )
                    Text16SemiBold(
                        modifier = Modifier
                            .padding(sevenDP),
                        text = volumeText.value
                    )
                }
            }

            Icon(
                painter = painterResource(
                    id = R.drawable.ic_forward_10
                ),
                contentDescription = null,
                tint = rightIconColor
            )
        }
    }
}

/** 5 секунд перемотка Назад */
private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L

/** 10 секунд перемотка Вперёд */
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L