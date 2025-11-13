package com.shikidroid.appconstants

/**
 * Базовые URL-адреса для запросов
 */
object BaseUrl {

    ///////////////////////////////////////////////////////////////////////////
    // URL-адресы сайта
    ///////////////////////////////////////////////////////////////////////////

    /** Базовый URL-адрес сайта Shikimori */
    const val SHIKIMORI_BASE_URL = "https://shikimori.one/"

    /** Базовый URL-адрес сайта Shikimori для загрузки картинок */
    const val SHIKIMORI_IMAGE_URL = "https://kawai.shikimori.one/"

    /** Базовый URL-адрес для просмотра видео */
    const val VIDEO_BASE_URL = "https://us-central1-shikimori-fbf37.cloudfunctions.net/"

    /** Второй базовый URL-адрес для просмотра видео */
    const val SHIMORI_VIDEO_URL = "https://shimori-us.herokuapp.com/"

    /** Третий базовый URL-адрес для просмотра видео */
    const val SHIKIMORI_VIDEO_URL = "https://play.shikimori.org"

    ///////////////////////////////////////////////////////////////////////////
    // Адресы и токены приложения
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Стандартное URI сайта Shikimori для перенаправления
     */
    const val REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"

    /**
     * Секретный ID приложения (клиента)
     */
    const val CLIENT_ID = "t_wuix2_x_4PypSGg5aH2qM1qqWRJWQnWWOag3EyGtY"

    /**
     * Секретный ключ приложения (клиента)
     */
    const val CLIENT_SECRET = "4NtXSjFbNQyx9oYE5orO24IzM_rx_9iWNLvn5CtLjMQ"

    /**
     * URL-адрес для запроса авторизации на сайте через WebView
     */
    const val AUTH_URL =
        "${SHIKIMORI_BASE_URL}oauth/authorize?client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URI&response_type=code&scope=user_rates+comments+topics"

    ///////////////////////////////////////////////////////////////////////////
    // Адресы и токены MyAnimeList
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Базовый URL-адрес сайта MyAnimeList
     */
    const val MY_ANIME_LIST_BASE_URL = "https://api.myanimelist.net/"

    /**
     * Секретный ID приложения (клиента)
     */
    const val MY_ANIME_LIST_CLIENT_ID = "97bb91d1c4507140e37895d3a9d6c530"
}