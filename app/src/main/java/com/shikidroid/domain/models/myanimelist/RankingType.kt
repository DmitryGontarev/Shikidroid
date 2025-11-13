package com.shikidroid.domain.models.myanimelist

 /** Тип ранжирования аниме MyAnimeList */
enum class RankingType {

     /** топ аниме сериалов */
     ALL,

     /** топ выходящих аниме */
     AIRING,

     /** топ анонсированных аниме */
     UPCOMING,

     /** топ ТВ-сериалов */
     TV,

     /** топ ОВА */
     OVA,

     /** топ полнометражек */
     MOVIE,

     /** топ специальных выпусков */
     SPECIAL,

     /** топ по популярности */
     BY_POPULARITY,

     /** топ фаворитов */
     FAVORITE
}