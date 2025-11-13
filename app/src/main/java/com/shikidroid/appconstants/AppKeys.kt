package com.shikidroid.appconstants

/**
 * Константы для использования в приложении
 */
object AppKeys {

    ///////////////////////////////////////////////////////////////////////////
    // COMMON APP KEYS
    ///////////////////////////////////////////////////////////////////////////

    /** ключ темы */
    const val IS_DARK_THEME = "IS_DARK_THEME"

    /** ключ типа экранов приложени Android / AndroidTV */
    const val IS_ANDROID_TV = "IS_ANDROID_TV"

    /** ключ для получения/сохранения токена */
    const val ARGUMENT_TOKEN = "ARGUMENT_TOKEN"

    /** ключ для фоновой задачи уведомления о новых сообщениях */
    const val MESSAGE_NOTIFICATION = "MESSAGE_NOTIFICATION"

    ///////////////////////////////////////////////////////////////////////////
    // DETAILS SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ получения ID элемента для экрана детальной информации */
    const val DETAILS_SCREEN_ID = "DETAILS_SCREEN_ID"

    /** ключ типа экрана детальной информации */
    const val DETAILS_SCREEN_TYPE = "DETAILS_SCREEN_TYPE"

    /** ключ позиции плавающей кнопки экрана детальной информации по оси X */
    const val DETAIL_SCREEN_FLOATING_BUTTON_X = "DETAIL_SCREEN_FLOATING_BUTTON_X"

    /** ключ позиции плавающей кнопки экрана детальной информации по оси Y */
    const val DETAIL_SCREEN_FLOATING_BUTTON_Y = "DETAIL_SCREEN_FLOATING_BUTTON_Y"

    ///////////////////////////////////////////////////////////////////////////
    // CHARACTER PEOPLE SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ получения ID персонажа или человека */
    const val CHARACTER_PEOPLE_SCREEN_ID = "CHARACTER_PEOPLE_SCREEN_ID"

    /** ключ типа экрана информации персонажа/человека */
    const val CHARACTER_PEOPLE_SCREEN_TYPE = "CHARACTER_PEOPLE_SCREEN_TYPE"

    ///////////////////////////////////////////////////////////////////////////
    // SEARCH SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ позиции плавающей кнопки экрана поиска по оси X */
    const val SEARCH_SCREEN_FLOATING_BUTTON_X = "SEARCH_SCREEN_FLOATING_BUTTON_X"

    /** ключ позиции плавающей кнопки экрана поиска по оси Y */
    const val SEARCH_SCREEN_FLOATING_BUTTON_Y = "SEARCH_SCREEN_FLOATING_BUTTON_Y"

    /** тип экрана поиска */
    const val SEARCH_SCREEN_TYPE = "SEARCH_SCREEN_TYPE"

    /** ID жанра для поиска */
    const val SEARCH_SCREEN_GENRE_ID = "SEARCH_SCREEN_GENRE_ID"

    /** ID студии для поиска */
    const val SEARCH_SCREEN_STUDIO_ID = "SEARCH_SCREEN_STUDIO_ID"

    /**
     * ключ включения цензуры
     */
    const val SEARCH_SCREEN_CENSORED = "SEARCH_SCREEN_CENSORED"

    ///////////////////////////////////////////////////////////////////////////
    // WEBVIEW SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ для получения ссылки для браузера */
    const val WEBVIEW_SCREEN_URL = "WEBVIEW_SCREEN_URL"

    /** ключ для использования iframe внутри ВебВью */
    const val WEBVIEW_SCREEN_I_FRAME = "WEBVIEW_SCREEN_I_FRAME"

    /** ключ флага для указания получения тела ответа HTML ВебВью для TV */
    const val WEBVIEW_SCREEN_IS_GET_HTML_BODY_FOR_TV = "WEBVIEW_SCREEN_IS_GET_HTML_BODY_FOR_TV"

    /** ключ для получения тела ответа HTML ВебВью для TV */
    const val WEBVIEW_SCREEN_GET_HTML_BODY_FOR_TV = "WEBVIEW_SCREEN_GET_HTML_BODY_FOR_TV"

    ///////////////////////////////////////////////////////////////////////////
    // EPISODE SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ для получения id аниме для видео */
    const val EPISODE_SCREEN_ANIME_ID = "EPISODE_SCREEN_ANIME_ID"

    /** ключ для получения названия аниме для видео */
    const val EPISODE_SCREEN_ANIME_TITLE = "EPISODE_SCREEN_ANIME_TITLE"

    /** ключ для получения названия аниме на русском языке для видео */
    const val EPISODE_SCREEN_ANIME_RU_TITLE = "EPISODE_SCREEN_ANIME_RU_TITLE"

    /** ключ для получения id аниме в пользовательском списке */
    const val EPISODE_SCREEN_ANIME_ID_USER_RATE = "EPISODE_SCREEN_ANIME_ID_USER_RATE"

    /** ключ для получения ссылки для загрузки фоновой картинки */
    const val EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE_URL = "EPISODE_SCREEN_ANIME_BACKGROUND_IMAGE"

    ///////////////////////////////////////////////////////////////////////////
    // VIDEO SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ для получения id аниме для видео */
    const val VIDEOS_SCREEN_ANIME_ID = "VIDEOS_SCREEN_ANIME_ID"

    /** ключ для получения строки заголовка с названием аниме на английском */
    const val VIDEO_SCREEN_ANIME_ENG_TITLE = "VIDEO_SCREEN_ANIME_ENG_TITLE"

    /** ключ для получения строки заголовка с названием аниме на русском */
    const val VIDEO_SCREEN_ANIME_RU_TITLE = "VIDEO_SCREEN_ANIME_RU_TITLE"

    /** ключ для получения модели с данными видео */
    const val VIDEO_SCREEN_DATA_MODEL = "VIDEO_SCREEN_DATA_MODEL"

    /** ключ для получения модели данных трансляции эпизода */
    const val VIDEO_SCREEN_TRANSLATION_MODEL = "VIDEO_SCREEN_TRANSLATION_MODEL"

    /** ключ интента нажатий кнопки плеера перемотки Назад в режиме Картина-в-картинке */
    const val PLAYER_BACK_BUTTON_P_I_P_INTENT_KEY = "PLAYER_BACK_BUTTON_P_I_P_INTENT_KEY"

    /** ключ интента нажатий кнопки Play плеера в режиме Картина-в-картинке */
    const val PLAYER_PLAY_BUTTON_P_I_P_INTENT_KEY = "PLAYER_PLAY_BUTTON_P_I_P_INTENT_KEY"

    /** ключ интента нажатий кнопки плеера перемотки Вперёд в режиме Картина-в-картинке */
    const val PLAYER_NEXT_BUTTON_P_I_P_INTENT_KEY = "PLAYER_NEXT_BUTTON_P_I_P_INTENT_KEY"

    ///////////////////////////////////////////////////////////////////////////
    // PROFILE SCREEN
    ///////////////////////////////////////////////////////////////////////////

    /** ключ для получения ID пользователя */
    const val USER_ID_PROFILE_KEY = "USER_ID_PROFILE_KEY"
}