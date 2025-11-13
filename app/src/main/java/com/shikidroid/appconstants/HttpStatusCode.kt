package com.shikidroid.appconstants

/**
 * Коды HTTP для обработки ошибок
 */
object HttpStatusCode {

    /**
     * Код ответа, если данные получены или запрос обработан
     */
    const val HTTP_200_STATUS_OK = 200

    /**
     * Код ответа, если невозможно обработать сущность
     */
    const val HTTP_422_UNPROCESSABLE_ENTITY = 422

    /**
     * Код ответа, если по запросу ничего не найдено
     */
    const val HTTP_404_NOT_FOUND = 404

    /**
     * Код ответа, если пользователь неавторизован
     */
    const val HTTP_401_UNAUTHORIZED = 401

    /**
     * Код ответа, если такой запрос запрещён
     */
    const val HTTP_403_FORBIDDED = 403

    /**
     * Код ответа, если слишком много запросов
     */
    const val HTTP_429_TOO_MANY_REQUESTS = 429
}