package com.smallcake.temp.bean

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 *
 */
data class PageBean(val name:String,val clz: Class<*>,val keywork:String="")

data class Song(@Column(nullable = false)val name: String,val duration:Int): LitePalSupport()