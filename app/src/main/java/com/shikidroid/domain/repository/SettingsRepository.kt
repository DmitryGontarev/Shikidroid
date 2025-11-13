package com.shikidroid.domain.repository

import com.shikidroid.domain.models.video.TranslationType

/**
 * Интерфейс с Настройками приложения
 */
internal interface SettingsRepository {

//    var isAutoStatus : Boolean
//
//    var isAutoIncrement : Boolean
//
//    var isRussianNaming: Boolean
//
//    var isAskForPlayer : Boolean
//
//    var isNotificationsEnabled : Boolean
//
//    var translationType : TranslationType
//
//    var useLocalTranslationSettings : Boolean

    /**
     * Папка для загрузки видео
     */
    var downloadFolder : String

//    var isExternalBestQuality : Boolean

//    var rateSwipeToLeftAction : RateSwipeAction
//
//    var rateSwipeToRightAction : RateSwipeAction
//
//    var chronologyType : ChronologyType
}