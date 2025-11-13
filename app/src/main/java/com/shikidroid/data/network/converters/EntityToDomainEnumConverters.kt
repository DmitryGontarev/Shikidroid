package com.shikidroid.data.network.converters

import com.shikidroid.domain.models.anime.AnimeType
import com.shikidroid.domain.models.anime.AnimeVideoType
import com.shikidroid.domain.models.club.ClubCommentPolicy
import com.shikidroid.domain.models.club.ClubPolicy
import com.shikidroid.domain.models.comment.CommentableType
import com.shikidroid.domain.models.common.*
import com.shikidroid.domain.models.manga.MangaType
import com.shikidroid.domain.models.rates.RateStatus
import com.shikidroid.domain.models.user.MessageType
import com.shikidroid.domain.models.video.TranslationQuality
import com.shikidroid.domain.models.video.TranslationType

/**
 * Конвертация строки ответа в значение domain слоя [AnimeType]
 */
fun String.toDomainEnumAnimeType(): AnimeType {
    return when (this) {
        "tv" -> AnimeType.TV
        "tv_special" -> AnimeType.SPECIAL
        "movie" -> AnimeType.MOVIE
        "special" -> AnimeType.SPECIAL
        "music" -> AnimeType.MUSIC
        "ova" -> AnimeType.OVA
        "ona" -> AnimeType.ONA
        "tv_13" -> AnimeType.TV_13
        "tv_24" -> AnimeType.TV_24
        "tv_48" -> AnimeType.TV_48
        "none" -> AnimeType.NONE
        "" -> AnimeType.NONE
        else -> AnimeType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [AnimeType]
 */
fun String.toDomainEnumMangaType(): MangaType {
    return when (this) {
        "manga" -> MangaType.MANGA
        "manhwa" -> MangaType.MANHWA
        "manhua" -> MangaType.MANHUA
        "light_novel" -> MangaType.LIGHT_NOVEL
        "novel" -> MangaType.NOVEL
        "one_shot" -> MangaType.ONE_SHOT
        "doujin" -> MangaType.DOUJIN
        else -> MangaType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [LinkedContentProductType]
 */
fun String.toDomainEnumLinkedContentProductType(): LinkedContentProductType {
    return when (this) {
        "tv" -> LinkedContentProductType.TV
        "movie" -> LinkedContentProductType.MOVIE
        "special" -> LinkedContentProductType.SPECIAL
        "music" -> LinkedContentProductType.MUSIC
        "ova" -> LinkedContentProductType.OVA
        "ona" -> LinkedContentProductType.ONA
        "tv_13" -> LinkedContentProductType.TV_13
        "tv_24" -> LinkedContentProductType.TV_24
        "tv_48" -> LinkedContentProductType.TV_48

        "manga" -> LinkedContentProductType.MANGA
        "manhwa" -> LinkedContentProductType.MANHWA
        "manhua" -> LinkedContentProductType.MANHUA
        "light_novel" -> LinkedContentProductType.NOVEL
        "one_shot" -> LinkedContentProductType.ONE_SHOT
        "doujin" -> LinkedContentProductType.DOUJIN

        "none" -> LinkedContentProductType.NONE
        "" -> LinkedContentProductType.NONE
        else -> LinkedContentProductType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [RateStatus]
 */
fun String.toDomainEnumRateStatus(): RateStatus {
    return when (this) {
        "watching" -> RateStatus.WATCHING
        "planned" -> RateStatus.PLANNED
        "rewatching" -> RateStatus.REWATCHING
        "completed" -> RateStatus.COMPLETED
        "on_hold" -> RateStatus.ON_HOLD
        "dropped" -> RateStatus.DROPPED
        else -> RateStatus.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [SectionType]
 */
fun String.toDomainEnumSectionType(): SectionType {
    return when (this) {
        "Anime" -> SectionType.ANIME
        "Manga" -> SectionType.MANGA
        "Ranobe" -> SectionType.RANOBE
        "Character" -> SectionType.CHARACTER
        "Person" -> SectionType.PERSON
        "User" -> SectionType.USER
        "Club" -> SectionType.CLUB
        "ClubPage" -> SectionType.CLUB_PAGE
        "Collection" -> SectionType.COLLECTION
        "Review" -> SectionType.REVIEW
        "CosplayGallery" -> SectionType.COSPLAY
        "Contest" -> SectionType.CONTEST
        "Topic" -> SectionType.TOPIC
        "Comment" -> SectionType.COMMENT
        else -> SectionType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [ClubPolicy]
 */
fun String.toDomainEnumClubPolicy(): ClubPolicy {
    return when (this) {
        "free" -> ClubPolicy.FREE
        "admin_invite" -> ClubPolicy.ADMIN_INVITE
        "owner_invite" -> ClubPolicy.OWNER_INVITE
        else -> ClubPolicy.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [ClubCommentPolicy]
 */
fun String.toDomainEnumClubCommentPolicy(): ClubCommentPolicy {
    return when (this) {
        "free" -> ClubCommentPolicy.FREE
        "members" -> ClubCommentPolicy.MEMBERS
        else -> ClubCommentPolicy.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [AiredStatus]
 */
fun String.toDomainEnumAiredStatus(): AiredStatus {
    return when (this) {
        "anons" -> AiredStatus.ANONS
        "ongoing" -> AiredStatus.ONGOING
        "released" -> AiredStatus.RELEASED
        "latest" -> AiredStatus.LATEST
        "paused" -> AiredStatus.PAUSED
        "discontinued" -> AiredStatus.DISCONTINUED

        "finished_airing" -> AiredStatus.RELEASED
        "currently_airing" -> AiredStatus.ONGOING
        "not_yet_aired" -> AiredStatus.ANONS

        "none" -> AiredStatus.NONE
        else -> AiredStatus.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [MessageType]
 */
fun String.toDomainEnumMessageType(): MessageType {
    return when (this) {
        "inbox" -> MessageType.INBOX
        "private" -> MessageType.PRIVATE
        "sent" -> MessageType.SENT
        "news" -> MessageType.NEWS
        "notifications" -> MessageType.NOTIFICATIONS
        "episode" -> MessageType.EPISODE
        "released" -> MessageType.RELEASED
        "anons" -> MessageType.ANONS
        "ongoing" -> MessageType.ANONS
        else -> MessageType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [AgeRatingType]
 */
fun String.toDomainEnumAgeRating(): AgeRatingType {
    return when (this) {
        "none" -> AgeRatingType.NONE
        "g" -> AgeRatingType.G
        "pg" -> AgeRatingType.PG
        "pg_13" -> AgeRatingType.PG_13
        "r" -> AgeRatingType.R
        "r_plus" -> AgeRatingType.R_PLUS
        "r+" -> AgeRatingType.R_PLUS
        "rx" -> AgeRatingType.RX
        else -> AgeRatingType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [AgeRatingType]
 */
fun String.toDomainEnumAnimeVideoType(): AnimeVideoType {
    return when (this) {
        "op" -> AnimeVideoType.OPENING
        "ed" -> AnimeVideoType.ENDING
        "pv" -> AnimeVideoType.PROMO
        "cm" -> AnimeVideoType.COMMERCIAL
        "episode_preview" -> AnimeVideoType.EPISODE_PREVIEW
        "op_ed_clip" -> AnimeVideoType.OP_ED_CLIP
        "character_trailer" -> AnimeVideoType.CHARACTER_TRAILER
        "other" -> AnimeVideoType.OTHER
        else -> AnimeVideoType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [RelationType]
 */
fun String.toDomainEnumRelationType(): RelationType {
    return when (this) {
        "adaptation" -> RelationType.ADAPTATION
        "sequel" -> RelationType.SEQUEL
        "prequel" -> RelationType.PREQUEL
        "summary" -> RelationType.SUMMARY
        "full_story" -> RelationType.FULL_STORY
        "side_story" -> RelationType.SIDE_STORY
        "parent_story" -> RelationType.PARENT_STORY
        "alternative_setting" -> RelationType.ALTERNATIVE_SETTING
        "alternative_version" -> RelationType.ALTERNATIVE_VERSION
        "other" -> RelationType.OTHER
        "none" -> RelationType.NONE
        else -> RelationType.UNKNOWN
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [TranslationType]
 */
fun String.toDomainTranslationType(): TranslationType {
    return when (this) {
        "subs" -> TranslationType.SUB_RU
        "dub" -> TranslationType.VOICE_RU
        else -> TranslationType.RAW
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [TranslationQuality]
 */
fun String.toDomainTranslationQuality(): TranslationQuality {
    return when (this) {
        "bd" -> TranslationQuality.BD
        else -> TranslationQuality.TV
    }
}

/**
 * Конвертация строки ответа в значение domain слоя [CommentableType]
 */
fun String.toDomainCommentableType(): CommentableType {
    return when (this) {
        "Topic" -> CommentableType.TOPIC
        "User" -> CommentableType.USER
        else -> CommentableType.TOPIC
    }
}