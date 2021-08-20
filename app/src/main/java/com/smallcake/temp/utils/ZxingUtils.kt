package com.smallcake.temp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.smallcake.temp.ui.CustomCaptureActivity
import com.xuexiang.xqrcode.XQRCode

/**
 * Date: 2020/1/14
 * author: SmallCake
 * 需要引入
implementation 'com.google.zxing:core:3.3.3'
 https://github.com/xuexiangjys/XQRCode

 */
object ZxingUtils {
    private var IMAGE_HALFWIDTH = 50 //宽度值，影响中间图片大小

    /**
     * 生成二维码,默认500大小
     * @param contents 需要生成二维码的文字、网址等
     * @return bitmap
     */
    fun createQRCode(contents: String?): Bitmap? {
        return XQRCode.createQRCodeWithLogo(contents,500, 500,null)

    }

    fun scanQRCode(context: Activity){
        val intent = Intent(context, CustomCaptureActivity::class.java)
        context.startActivityForResult(intent,1002)
    }


}