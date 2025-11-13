package com.shikidroid.presentation.converters

import androidx.compose.ui.graphics.Color
import com.shikidroid.R
import com.shikidroid.domain.models.anime.AnimeDurationType
import com.shikidroid.domain.models.anime.AnimeSearchType
import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.rates.SortBy
import com.shikidroid.domain.models.search.SearchType
import com.shikidroid.domain.models.search.SeasonType
import com.shikidroid.domain.models.video.TranslationSpeed
import com.shikidroid.ui.theme.*
import com.shikidroid.uikit.*

/**
 * Конвертация типа аниме [AnimeType] в строку для показа на экране
 */
fun AnimeType.toScreenString(): String {
    return when (this) {
        AnimeType.TV -> "TV"
        AnimeType.MOVIE -> "Фильм"
        AnimeType.SPECIAL -> "Спешл"
        AnimeType.MUSIC -> "Клип"
        AnimeType.OVA -> "OVA"
        AnimeType.ONA -> "ONA"
        AnimeType.TV_13 -> "TV"
        AnimeType.TV_24 -> "TV"
        AnimeType.TV_48 -> "TV"
        AnimeType.NONE -> ""
        AnimeType.UNKNOWN -> ""
    }
}

/**
 * Конвертация типа аниме [AnimeType] в строку для показа на экране
 */
fun AnimeType.toScreenFilterString(): String {
    return when (this) {
        AnimeType.TV -> "TV"
        AnimeType.MOVIE -> "Фильм"
        AnimeType.SPECIAL -> "Спешл"
        AnimeType.MUSIC -> "Клип"
        AnimeType.OVA -> "OVA"
        AnimeType.ONA -> "ONA"
        AnimeType.TV_13 -> "TV 13"
        AnimeType.TV_24 -> "TV 24"
        AnimeType.TV_48 -> "TV 48"
        AnimeType.NONE -> ""
        AnimeType.UNKNOWN -> ""
    }
}

fun AnimeDurationType.toScreenString(): String {
    return when (this) {
        AnimeDurationType.S -> "< 10 мин"
        AnimeDurationType.D -> "< 30 мин"
        AnimeDurationType.F -> "> 30 мин"
    }
}

/**
 * Конвертация типа манги [MangaType] в строку для показа на экране
 */
fun MangaType.toScreenString(): String {
    return when (this) {
        MangaType.MANGA -> "Манга"
        MangaType.MANHWA -> "Манхва"
        MangaType.MANHUA -> "Маньхуа"
        MangaType.LIGHT_NOVEL -> "Ранобэ"
        MangaType.NOVEL -> "Ранобэ"
        MangaType.ONE_SHOT -> "Ваншот"
        MangaType.DOUJIN -> "Додзинси"
        MangaType.UNKNOWN -> ""
    }
}

/**
 * Конвертация статуса выхода [AiredStatus] в строку для показа на экране
 */
fun AiredStatus.toScreenString(): String {
    return when (this) {
        AiredStatus.ANONS -> "Анонс"
        AiredStatus.ONGOING -> "Онгоинг"
        AiredStatus.RELEASED -> "Релиз"
        AiredStatus.LATEST -> "Недавно вышедшее"
        AiredStatus.PAUSED -> "Приостановлен"
        AiredStatus.DISCONTINUED -> "Прекращен"
        AiredStatus.NONE -> ""
        AiredStatus.UNKNOWN -> ""
    }
}

/**
 * Конвертация статуса просмотра [RateStatus] в строку для показа статус релиза аниме на экране
 */
fun RateStatus.toAnimePresentationString(): String {
    return when (this) {
        RateStatus.WATCHING -> "Смотрю"
        RateStatus.PLANNED -> "Запланировано"
        RateStatus.REWATCHING -> "Пересматриваю"
        RateStatus.COMPLETED -> "Просмотрено"
        RateStatus.ON_HOLD -> "Отложено"
        RateStatus.DROPPED -> "Брошено"
        else -> ""
    }
}

/**
 * Конвертация статуса просмотра [RateStatus] в строку для показа статус манги на экране
 */
fun RateStatus.toMangaPresentationString(): String {
    return when (this) {
        RateStatus.WATCHING -> "Читаю"
        RateStatus.PLANNED -> "Запланировано"
        RateStatus.REWATCHING -> "Перечитываю"
        RateStatus.COMPLETED -> "Прочитано"
        RateStatus.ON_HOLD -> "Отложено"
        RateStatus.DROPPED -> "Брошено"
        else -> ""
    }
}

/**
 * Конвертация строки статуса просмотра аниме в [RateStatus]
 */
fun String.toAnimeRateStatus(): RateStatus {
    return when (this) {
        "Смотрю" -> RateStatus.WATCHING
        "Запланировано" -> RateStatus.PLANNED
        "Пересматриваю" -> RateStatus.REWATCHING
        "Просмотрено" -> RateStatus.COMPLETED
        "Отложено" -> RateStatus.ON_HOLD
        "Брошено" -> RateStatus.DROPPED
        else -> RateStatus.PLANNED
    }
}

/**
 * Конвертация строки статуса чтения манги в [RateStatus]
 */
fun String.toMangaRateStatus(): RateStatus {
    return when (this) {
        "Читаю" -> RateStatus.WATCHING
        "Запланировано" -> RateStatus.PLANNED
        "Перечитываю" -> RateStatus.REWATCHING
        "Прочитано" -> RateStatus.COMPLETED
        "Отложено" -> RateStatus.ON_HOLD
        "Брошено" -> RateStatus.DROPPED
        else -> RateStatus.PLANNED
    }
}

/**
 * Конвертация статуса просмотра [RateStatus] в цвет
 */
fun RateStatus?.toColor(): Color {
    return when (this) {
        RateStatus.WATCHING -> WatchingColor
        RateStatus.PLANNED -> PlannedColor
        RateStatus.REWATCHING -> ReWatchingColor
        RateStatus.COMPLETED -> CompletedColor
        RateStatus.ON_HOLD -> OnHoldColor
        RateStatus.DROPPED -> DroppedColor
        else -> Color.Transparent
    }
}

/**
 * Конвертация статуса просмотра [RateStatus] в цвет заднего фона
 */
fun RateStatus?.toBackgroundColor(): Color {
    return when (this) {
        RateStatus.WATCHING -> BackgroundWatchingColor
        RateStatus.PLANNED -> BackgroundPlannedColor
        RateStatus.REWATCHING -> BackgroundReWatchingColor
        RateStatus.COMPLETED -> BackgroundCompletedColor
        RateStatus.ON_HOLD -> BackgroundOnHoldColor
        RateStatus.DROPPED -> BackgroundDroppedColor
        null -> BackgroundLightGray
        else -> Color.Transparent
    }
}

/**
 * Конвертация статуса просмотра [RateStatus] в изображение
 */
fun RateStatus?.toDrawable(): Int {
    return when (this) {
        RateStatus.WATCHING -> R.drawable.ic_play_rate
        RateStatus.PLANNED -> R.drawable.ic_planned
        RateStatus.REWATCHING -> R.drawable.ic_replay
        RateStatus.COMPLETED -> R.drawable.ic_check
        RateStatus.ON_HOLD -> R.drawable.ic_pause_rate
        RateStatus.DROPPED -> R.drawable.ic_close
        null -> R.drawable.ic_plus
        else -> R.drawable.ic_play_rate
    }
}

/**
 * Конвертация статуса выхода [AiredStatus] в цвет
 */
fun AiredStatus.toColor(): Color {
    return when (this) {
        AiredStatus.ANONS -> AnonsColor
        AiredStatus.ONGOING -> OngoingColor
        AiredStatus.RELEASED -> ReleasedColor
        AiredStatus.PAUSED -> OnHoldColor
        AiredStatus.DISCONTINUED -> DroppedColor
        else -> Color.Transparent
    }
}

/**
 * Конвертация типа поиска [SortBy] в строку
 */
fun SortBy.toScreenString(): String {
    return when (this) {
        SortBy.BY_NAME -> "По названию"
        SortBy.BY_PROGRESS -> "По прогрессу"
        SortBy.BY_RELEASE_DATE -> "По дате выхода"
        SortBy.BY_ADD_DATE -> "По дате добавления"
        SortBy.BY_REFRESH_DATE -> "По дате обновления"
        SortBy.BY_SCORE -> "По оценке"
        SortBy.BY_EPISODES -> "По эпизодам"
        SortBy.BY_CHAPTERS -> "По главам"
    }
}

/**
 * Конвертация строки в тип поиска [SortBy]
 */
fun String.toSortBy(): SortBy {
    return when (this) {
        "По названию" -> SortBy.BY_NAME
        "По прогрессу" -> SortBy.BY_PROGRESS
        "По дате выхода" -> SortBy.BY_RELEASE_DATE
        "По дате добавления" -> SortBy.BY_ADD_DATE
        "По дате обновления" -> SortBy.BY_REFRESH_DATE
        "По оценке" -> SortBy.BY_SCORE
        "По эпизодам" -> SortBy.BY_EPISODES
        "По главам" -> SortBy.BY_CHAPTERS
        else -> SortBy.BY_NAME
    }
}

/**
 * Конвертация [SectionType] в строку для показа на экране
 */
fun SectionType.toScreenString(): String {
    return when (this) {
        SectionType.ANIME -> "Аниме"
        SectionType.MANGA -> "Манга"
        SectionType.RANOBE -> "Ранобэ"
        SectionType.CHARACTER -> "Персонаж"
        SectionType.PERSON -> "Личность"
        SectionType.USER -> "Пользователь"
        SectionType.CLUB -> "Клуб"
        SectionType.CLUB_PAGE -> "Страница Клуба"
        SectionType.COLLECTION -> "Коллекция"
        SectionType.REVIEW -> "Рецензия"
        SectionType.COSPLAY -> "Косплей"
        SectionType.CONTEST -> "Конкурс"
        SectionType.TOPIC -> "Топик"
        SectionType.COMMENT -> "Комментарии"
        SectionType.UNKNOWN -> ""
    }
}

/**
 * Конвертация строки в [SectionType]
 */
fun String.toSectionType(): SectionType {
    return when (this) {
        "Аниме" -> SectionType.ANIME
        "Манга" -> SectionType.MANGA
        "Ранобэ" -> SectionType.RANOBE
        "Персонаж" -> SectionType.CHARACTER
        "Личность" -> SectionType.PERSON
        "Пользователь" -> SectionType.USER
        "Клуб" -> SectionType.CLUB
        "Страница Клуба" -> SectionType.CLUB_PAGE
        "Коллекция" -> SectionType.COLLECTION
        "Рецензия" -> SectionType.REVIEW
        "Косплей" -> SectionType.COSPLAY
        "Конкурс" -> SectionType.CONTEST
        "Топик" -> SectionType.TOPIC
        "Комментарии" -> SectionType.COMMENT
        else -> SectionType.ANIME
    }
}

/**
 * Конвертация оценки [RatingBar] в строку для пока
 */
fun Float?.toScoreString(): String {
    return when (this?.toInt()) {
        0 -> "Нет оценки"
        1 -> "Хуже некуда"
        2 -> "Ужасно"
        3 -> "Очень плохо"
        4 -> "Плохо"
        5 -> "Неплохо"
        6 -> "Нормально"
        7 -> "Хорошо"
        8 -> "Отлично"
        9 -> "Великолепно"
        10 -> "Шедевр!"
        null -> "Нет оценки"
        else -> "Нет оценки"
    }
}

/**
 * Конвертация возрастного рейтинга [AgeRatingType] в строку для показа на экране
 */
fun AgeRatingType.toScreenString(): String {
    return when (this) {
        AgeRatingType.G -> "0+"
        AgeRatingType.PG -> "7+"
        AgeRatingType.PG_13 -> "13+"
        AgeRatingType.R -> "17+"
        AgeRatingType.R_PLUS -> "18+"
        AgeRatingType.RX -> "18++"
        else -> ""
    }
}

fun String?.toSourceResolution(): String {
    return if (this == "unknown") {
        "src"
    } else {
        this ?: ""
    }
}

/**
 * Конвертация скорости воспроизведения [TranslationSpeed] в строку для показа на экране
 */
fun TranslationSpeed.toScreenString(): String {
    return when (this) {
        TranslationSpeed.X0_25 -> "0.25x"
        TranslationSpeed.X0_5 -> "0.5x"
        TranslationSpeed.X0_75 -> "0.75x"
        TranslationSpeed.X1 -> "1x"
        TranslationSpeed.X1_25 -> "1.25x"
        TranslationSpeed.X1_5 -> "1.5x"
        TranslationSpeed.X1_75 -> "1.75x"
        TranslationSpeed.X2 -> "2x"
    }
}

/**
 * Конвертация скорости воспроизведения [TranslationSpeed] в значение для плеера
 */
fun TranslationSpeed.toPlayerSpeed(): Float {
    return when (this) {
        TranslationSpeed.X0_25 -> 0.25f
        TranslationSpeed.X0_5 -> 0.5f
        TranslationSpeed.X0_75 -> 0.75f
        TranslationSpeed.X1 -> 1f
        TranslationSpeed.X1_25 -> 1.25f
        TranslationSpeed.X1_5 -> 1.5f
        TranslationSpeed.X1_75 -> 1.75f
        TranslationSpeed.X2 -> 2f
    }
}

/**
 * Конвертация типа поиска [SearchType] в строку для показа на экране
 */
fun SearchType.toScreenString(): String {
    return when (this) {
        SearchType.ANIME -> "Аниме"
        SearchType.MANGA -> "Манга"
        SearchType.RANOBE -> "Ранобэ"
        SearchType.CHARACTER -> "Персонаж"
        SearchType.PEOPLE -> "Человек"
    }
}

/**
 * Конвертация типа поиска [SearchType] в строку для показа на экране
 */
fun SeasonType.toScreenString(useEng: Boolean = false): String {
    return when (this) {
        SeasonType.WINTER -> if (!useEng) "Зима" else "winter"
        SeasonType.SPRING -> if (!useEng) "Весна" else "spring"
        SeasonType.SUMMER -> if (!useEng) "Лето" else "summer"
        SeasonType.AUTUMN -> if (!useEng) "Осень" else "fall"
    }
}

/**
 * Конвертация строки сезона в enum [SeasonType]
 */
fun String.toSeasonType(): SeasonType {
    return when (this) {
        "Зима", "winter" -> SeasonType.WINTER
        "Весна", "spring" -> SeasonType.SPRING
        "Лето", "summer" -> SeasonType.SUMMER
        "Осень", "fall" -> SeasonType.AUTUMN
        else -> SeasonType.AUTUMN
    }
}

/**
 * Конвертация типа поиска [AnimeSearchType] в строку для показа на экране
 */
fun AnimeSearchType.toScreenString(): String {
    return when (this) {
        AnimeSearchType.ID -> "По id (по возрастанию)"
        AnimeSearchType.ID_DESC -> "По id (по убыванию)"
        AnimeSearchType.RANKED -> "По оценке"
        AnimeSearchType.KIND -> "По типу"
        AnimeSearchType.POPULARITY -> "По популярности"
        AnimeSearchType.NAME -> "По названию"
        AnimeSearchType.AIRED_ON -> "По дате выхода"
        AnimeSearchType.EPISODES -> "По эпизодам"
        AnimeSearchType.STATUS -> "По статусу"
        AnimeSearchType.RANDOM -> "Вразброс"
    }
}

/**
 * Конвертация типа поиска [MangaSearchType] в строку для показа на экране
 */
fun MangaSearchType.toScreenString(): String {
    return when (this) {
        MangaSearchType.ID -> "По id (по возрастанию)"
        MangaSearchType.ID_DESC -> "По id (по убыванию)"
        MangaSearchType.RANKED -> "По оценке"
        MangaSearchType.KIND -> "По типу"
        MangaSearchType.POPULARITY -> "По популярности"
        MangaSearchType.NAME -> "По названию"
        MangaSearchType.AIRED_ON -> "По дате выхода"
        MangaSearchType.VOLUMES -> "По томам"
        MangaSearchType.CHAPTERS -> "По главам"
        MangaSearchType.STATUS -> "По статусу"
        MangaSearchType.RANDOM -> "Вразброс"
    }
}