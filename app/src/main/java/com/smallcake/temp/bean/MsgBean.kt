package com.smallcake.temp.bean


/**
 *  聊天消息体
 * @property type Int 0文本1图片2语音
 * @property message String
 * @property itemType Int
 * @constructor
 */
data class MsgBean(val type:Int,val message:String)