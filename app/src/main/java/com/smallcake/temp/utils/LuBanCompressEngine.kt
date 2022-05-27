package com.smallcake.temp.utils

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.smallcake.smallutils.FileUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.ArrayList

class LuBanCompressEngine:CompressEngine {
    override fun onStartCompress(
        context: Context?,
        list: ArrayList<LocalMedia>,
        listener: OnCallbackListener<ArrayList<LocalMedia>>
    ) {
        // 1、构造可用的压缩数据源
        val compress: MutableList<Uri> = ArrayList()
        for (i in 0 until list.sizeNull()) {
            val media = list[i]
            val availablePath = media.availablePath
            val uri =
                if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(
                        availablePath
                    )
                ) Uri.parse(availablePath) else Uri.fromFile(
                    File(availablePath)
                )
            compress.add(uri)
        }
        if (compress.size == 0) {
            listener.onCall(list)
            return
        }
        // 2、调用Luban压缩
        Luban.with(context)
            .load(compress)
            .ignoreBy(100)
            .filter { path -> PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)}
            .setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_").toString() + postfix
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {}
                override fun onSuccess(index: Int, compressFile: File) {
                    Log.e("压缩后的图片大小：","${FileUtils.getFileSize(compressFile)}")
                    // 压缩完构造LocalMedia对象
                    val media = list[index]
                    if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                        media.isCompressed = true
                        media.compressPath = compressFile.absolutePath
                        media.sandboxPath = if (SdkVersionUtils.isQ()) media.compressPath else null
                    }
                    // 因为是多图压缩，所以判断压缩到最后一张时返回结果
                    if (index == list.sizeNull() - 1) {
                        listener.onCall(list)
                    }
                }

                override fun onError(index: Int, e: Throwable?) {
                    // 压缩失败
                    if (index != -1) {
                        val media = list[index]
                        media.isCompressed = false
                        media.compressPath = null
                        media.sandboxPath = null
                        if (index == list.sizeNull() - 1) {
                            listener.onCall(list)
                        }
                    }
                }
            }).launch()
    }
}