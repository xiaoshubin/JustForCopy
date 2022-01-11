package com.smallcake.temp.utils

import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.compress.OnCompressListener
import com.luck.picture.lib.entity.LocalMedia
import com.smallcake.smallutils.SmallUtils


class LubanUtils {
    fun compress(filePath:String,cb:(String)->Unit){
        Luban.with(SmallUtils.context)
            .load(filePath)
            .ignoreBy(100) // 忽略不压缩图片的大小
            .setTargetDir(SmallUtils.context?.externalCacheDir?.path) // 设置压缩后文件存储位置
            .setCompressListener(object : OnCompressListener {
                //设置回调
                override fun onStart() {}
                override fun onSuccess(list: MutableList<LocalMedia>?) {
                    cb.invoke(filePath)
                }
                override fun onError(e: Throwable) {}
            }).launch() //启动压缩
    }
}