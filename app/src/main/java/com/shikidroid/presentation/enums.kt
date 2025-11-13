package com.shikidroid.presentation

/**
 * Типы нижней шторки
 */
enum class BottomSheetType {

    /** Шторка для перемещения элемента пользовательского рейтинга между списками */
    RATE_LIST_EDIT,

    /** Шторка для редактирования элемента пользовательского списка по параметрам */
    RATE_MODEL_EDIT
}

/**
 * Типы экрана детальной информации
 */
enum class DetailsScreenType {

    /** Тип экрана для показа информации об аниме */
    ANIME,

    /** Тип экрана для показа информации о манге */
    MANGA,

    /** Тип экрана для показа информации о ранобэ */
    RANOBE
}

/**
 * Типы меню экрана детальной информации [DetailsScreen]
 */
enum class DetailScreenDrawerType {

    /** Создатели */
    ROLES,

    /** Персонажи */
    CHARACTER,

    /** Связанное */
    RELATED,

    /** Кадры */
    SCREENSHOTS,

    /** Видео */
    VIDEOS,

    /** Похожее Аниме */
    SIMILAR_ANIME,

    /** Похожее Манга */
    SIMILAR_MANGA,

    /** Хронология */
    CHRONOLOGY,

    /** Ссылки */
    EXTERNAL_LINKS,

    /** Статистика */
    STATISTICS,

    /** Комментарии */
    COMMENTS
}

/**
 * Типы экрана информации о персонаже или человеке
 */
enum class CharacterPeopleScreenType {

    /** Тип экрана для показа информации о персонаже */
    CHARACTER,

    /** Тип экрана для показа информации о человеке */
    PEOPLE
}

/**
 * Типы меню экрана информации о человеке или персонаже [CharacterPeopleScreen]
 */
enum class CharacterPeopleScreenDrawerType {

    // CHARACTER SCREEN

    SEYU,

    ANIME,

    MANGA,

    // PEOPLE SCREEN

    BEST_ROLES,

    BEST_WORKS
}