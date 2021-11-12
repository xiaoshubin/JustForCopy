package com.smallcake.temp.music

/**
 *
 * @property name String?     歌曲名
 * @property singer String?   歌手
 * @property size Long        歌曲所占空间大小
 * @property duration Int     歌曲时间长度 毫秒
 * @property path String?     歌曲地址
 * @property albumId Long     图片id
 * @property id Long          歌曲id
 * @constructor
 */
data class Song (
    var name : String? = null,
    var singer : String? = null,
    var size : Long = 0,
    var duration:Int  = 0,
    var path : String? = null,
    var albumId  : Long = 0,
    var id: Long = 0,
)