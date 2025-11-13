package com.shikidroid.domain.models.video

/**
 * Возможные видео хостинги
 */
enum class VideoHosting {

    /** ВКонтакте */
    VK,

    /** Сибнет */
    SIBNET,

    /** Совет Романтика */
    SOVET_ROMANTICA,

    /** Сервер Смотреть Аниме */
    SMOTRET_ANIME,

    /** Кодик */
    KODIK,

    /** Аниме Джой */
    ANIME_JOY,

    /** Яндекс Дзен */
    DZEN,

    /** Неизвестный сервер */
    UNKNOWN
}

/**
 * Функция перевода строки в тип видеохостинга
 */
fun String.toVideoHosting(): VideoHosting {
//    return when (this) {
//        "vk.com", "vk" -> VideoHosting.VK
//        "video.sibnet.ru", "sibnet", "sibnet.ru" -> VideoHosting.SIBNET
//        "sovetromantica.com", "sovetromantica" -> VideoHosting.SOVET_ROMANTICA
//        "smotretanime.ru", "smotretanime", "smotret-anime.online", "smotret-anime.com" -> VideoHosting.SMOTRET_ANIME
//        "aniqit.com" -> VideoHosting.KODIK
//        "animejoy.ru" -> VideoHosting.ANIME_JOY
//        else -> VideoHosting.UNKNOWN
//    }

    return when {
        this.contains("vk") -> VideoHosting.VK
        this.contains("sibnet") -> VideoHosting.SIBNET
        this.contains("sovetromantica") -> VideoHosting.SOVET_ROMANTICA
        this.contains("smotret") -> VideoHosting.SMOTRET_ANIME
        this.contains("aniqit") -> VideoHosting.KODIK
        this.contains("animejoy") -> VideoHosting.ANIME_JOY
        this.contains("dzen") -> VideoHosting.DZEN
        else -> VideoHosting.UNKNOWN
    }
}

/**
 * Возвращает флаг, поддерживается ли видеохостинг внутренним плеером
 */
fun String.isHostingSupports(): Boolean {
    val hosting = this.toVideoHosting()
    return when (hosting) {
        VideoHosting.SIBNET, VideoHosting.VK,
        VideoHosting.SMOTRET_ANIME, VideoHosting.SOVET_ROMANTICA,
        VideoHosting.KODIK, VideoHosting.ANIME_JOY, VideoHosting.DZEN -> true
        else -> false
    }
}

/**
 * Функция для добавления заголовков в запрос для загрузки видео
 */
fun VideoModel?.getRequestHeaderForHosting(): Map<String, String> {
    val hosting = this?.hosting?.toVideoHosting()
    return when (hosting) {
        VideoHosting.SOVET_ROMANTICA, VideoHosting.UNKNOWN -> {
            mapOf("Referrer" to (this?.player ?: ""))
        }
        VideoHosting.SIBNET -> {
            mapOf("Referer" to (this?.player ?: ""))
        }
        else -> {
            emptyMap()
        }
    }
}

/**
 * Функция для возврата флага, нужно ли использовать тег <iframe> в WebView
 */
fun String?.checkNeedIFrame(): Boolean {

    if (this?.contains("aparat") == true) {
        return false
    }

    if (this?.contains("ebd") == true) {
        return false
    }

    if (this?.contains("arven") == true) {
        return false
    }

    if (this?.contains("animaunt") == true) {
        return false
    }

    return true;
}