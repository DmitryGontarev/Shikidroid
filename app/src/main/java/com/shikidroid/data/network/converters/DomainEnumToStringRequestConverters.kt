package com.shikidroid.data.network.converters

import com.shikidroid.domain.models.anime.*
import com.shikidroid.domain.models.comment.CommentableType
import com.shikidroid.domain.models.common.AiredStatus
import com.shikidroid.domain.models.common.AgeRatingType
import com.shikidroid.domain.models.common.RelationType
import com.shikidroid.domain.models.common.SectionType
import com.shikidroid.domain.models.manga.MangaSearchType
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.roles.RoleType
import com.shikidroid.domain.models.user.MessageType
import com.shikidroid.domain.models.video.TranslationQuality
import com.shikidroid.domain.models.video.TranslationType

/**
 * Конвертация [SectionType] в строку для передачи в запрос
 */
fun SectionType.toStringRequest(): String {
    return when (this) {
        SectionType.ANIME -> "Anime"
        SectionType.MANGA -> "Manga"
        SectionType.RANOBE -> "Ranobe"
        SectionType.CHARACTER -> "Character"
        SectionType.PERSON -> "Person"
        SectionType.USER -> "User"
        SectionType.CLUB -> "Club"
        SectionType.CLUB_PAGE -> "ClubPage"
        SectionType.COLLECTION -> "Collection"
        SectionType.REVIEW -> "Review"
        SectionType.COSPLAY -> "CosplayGallery"
        SectionType.CONTEST -> "Contest"
        SectionType.TOPIC -> "Topic"
        SectionType.COMMENT -> "Comment"
        SectionType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [MessageType] в строку для передачи в запрос
 */
fun MessageType.toStringRequest(): String {
    return when (this) {
        MessageType.INBOX -> "inbox"
        MessageType.PRIVATE -> "private"
        MessageType.SENT -> "sent"
        MessageType.NEWS -> "news"
        MessageType.NOTIFICATIONS -> "notifications"
        MessageType.EPISODE -> "episode"
        MessageType.RELEASED -> "released"
        MessageType.ANONS -> "anons"
        MessageType.ONGOING -> "ongoing"
        MessageType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [AnimeSearchType] в строку для передачи в запрос
 */
fun AnimeSearchType.toStringRequest(): String {
    return when (this) {
        AnimeSearchType.ID -> "id"
        AnimeSearchType.ID_DESC -> "id_desc"
        AnimeSearchType.RANKED -> "ranked"
        AnimeSearchType.KIND -> "kind"
        AnimeSearchType.POPULARITY -> "popularity"
        AnimeSearchType.NAME -> "name"
        AnimeSearchType.AIRED_ON -> "aired_on"
        AnimeSearchType.EPISODES -> "episodes"
        AnimeSearchType.STATUS -> "status"
        AnimeSearchType.RANDOM -> "random"
    }
}

/**
 * Конвертация [MangaSearchType] в строку для передачи в запрос
 */
fun MangaSearchType.toStringRequest(): String {
    return when (this) {
        MangaSearchType.ID -> "id"
        MangaSearchType.ID_DESC -> "id_desc"
        MangaSearchType.RANKED -> "ranked"
        MangaSearchType.KIND -> "kind"
        MangaSearchType.POPULARITY -> "popularity"
        MangaSearchType.NAME -> "name"
        MangaSearchType.AIRED_ON -> "aired_on"
        MangaSearchType.VOLUMES -> "volumes"
        MangaSearchType.CHAPTERS -> "chapters"
        MangaSearchType.STATUS -> "status"
        MangaSearchType.RANDOM -> "random"
    }
}

/**
 * Конвертация [AnimeType] в строку для передачи в запрос
 */
fun AnimeType.toStringRequest(): String {
    return when (this) {
        AnimeType.TV -> "tv"
        AnimeType.MOVIE -> "movie"
        AnimeType.OVA -> "ova"
        AnimeType.ONA -> "ona"
        AnimeType.SPECIAL -> "special"
        AnimeType.MUSIC -> "music"
        AnimeType.TV_13 -> "tv_13"
        AnimeType.TV_24 -> "tv_24"
        AnimeType.TV_48 -> "tv_48"
        AnimeType.NONE -> ""
        AnimeType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [MangaType] в строку для передачи в запрос
 */
fun MangaType.toStringRequest(): String {
    return when (this) {
        MangaType.MANGA -> "manga"
        MangaType.MANHWA -> "manhwa"
        MangaType.MANHUA -> "manhua"
        MangaType.LIGHT_NOVEL -> "light_novel"
        MangaType.NOVEL -> "novel"
        MangaType.ONE_SHOT -> "one_shot"
        MangaType.DOUJIN -> "doujin"
        MangaType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [AnimeDurationType] в строку для передачи в запрос
 */
fun AnimeDurationType.toStringRequest(): String {
    return when (this) {
        AnimeDurationType.S -> "S"
        AnimeDurationType.D -> "D"
        AnimeDurationType.F -> "F"
    }
}

/**
 * Конвертация [AnimeVideoType] в строку для передачи в запрос
 */
fun AnimeVideoType.toStringRequest(): String {
    return when (this) {
        AnimeVideoType.OPENING -> "op"
        AnimeVideoType.ENDING -> "ed"
        AnimeVideoType.PROMO -> "pv"
        AnimeVideoType.COMMERCIAL -> "cm"
        AnimeVideoType.EPISODE_PREVIEW -> "episode_preview"
        AnimeVideoType.OP_ED_CLIP -> "op_ed_clip"
        AnimeVideoType.CHARACTER_TRAILER -> "character_trailer"
        AnimeVideoType.OTHER -> "other"
        AnimeVideoType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [RateStatus] в строку для передачи в запрос
 */
fun RateStatus.toStringRequest(): String {
    return when (this) {
        RateStatus.PLANNED -> "planned"
        RateStatus.WATCHING -> "watching"
        RateStatus.REWATCHING -> "rewatching"
        RateStatus.COMPLETED -> "completed"
        RateStatus.ON_HOLD -> "on_hold"
        RateStatus.DROPPED -> "dropped"
        RateStatus.UNKNOWN -> ""
    }
}

/**
 * Конвертация [AiredStatus] в строку для передачи в запрос
 */
fun AiredStatus.toStringRequest(): String {
    return when (this) {
        AiredStatus.ANONS -> "anons"
        AiredStatus.ONGOING -> "ongoing"
        AiredStatus.RELEASED -> "released"
        AiredStatus.LATEST -> "latest"
        AiredStatus.PAUSED -> "paused"
        AiredStatus.DISCONTINUED -> "discontinued"
        AiredStatus.NONE -> ""
        AiredStatus.UNKNOWN -> ""
    }
}

/**
 * Конвертация [AgeRatingType] в строку для передачи в запрос
 */
fun AgeRatingType.toStringRequest(): String {
    return when (this) {
        AgeRatingType.NONE -> "none"
        AgeRatingType.G -> "g"
        AgeRatingType.PG -> "pg"
        AgeRatingType.PG_13 -> "pg_13"
        AgeRatingType.R -> "r"
        AgeRatingType.R_PLUS -> "r_plus"
        AgeRatingType.RX -> "rx"
        AgeRatingType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [RelationType] в строку для передачи в запрос
 */
fun RelationType.toStringRequest(): String {
    return when (this) {
        RelationType.ADAPTATION -> "adaptation"
        RelationType.SEQUEL -> "sequel"
        RelationType.PREQUEL -> "prequel"
        RelationType.SUMMARY -> "summary"
        RelationType.SIDE_STORY -> "side_story"
        RelationType.PARENT_STORY -> "parent_story"
        RelationType.ALTERNATIVE_SETTING -> "alternative_setting"
        RelationType.ALTERNATIVE_VERSION -> "alternative_version"
        RelationType.OTHER -> "other"
        RelationType.FULL_STORY -> "full_story"
        RelationType.NONE -> "none"
        RelationType.UNKNOWN -> ""
    }
}

/**
 * Конвертация [RoleType] в строку для передачи в запрос
 */
fun RoleType.toStringRequest(): String {
    return when (this) {
        RoleType.SEYU -> "seyu"
        RoleType.MANGAKA -> "mangaka"
        RoleType.PRODUCER -> "producer"
    }
}

/**
 * Конвертация [TranslationType] в строку для передачи в запрос
 */
fun TranslationType.toStringRequest(): String {
    return when (this) {
        TranslationType.SUB_RU -> "subs"
        TranslationType.VOICE_RU -> "dub"
        TranslationType.RAW -> "raw"
        TranslationType.ALL -> "all"
    }
}

/**
 * Конвертация [TranslationQuality] в строку для передачи в запрос
 */
fun TranslationQuality.toStringRequest(): String {
    return when (this) {
        TranslationQuality.BD -> "bd"
        TranslationQuality.TV -> "tv"
        TranslationQuality.DVD -> "dvd"
    }
}

/**
 * Конвертация [CommentableType] в строку для передачи в запрос
 */
fun CommentableType.toStringRequest(): String {
    return when (this) {
        CommentableType.TOPIC -> "Topic"
        CommentableType.USER -> "User"
    }
}