package com.smallcake.temp.ui

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCameraCustomBinding
import com.smallcake.temp.utils.TakePhotoUtils
import java.io.File

/**
 * 自定义相机
 *
 * CameraX相机 兼容至 Android 5.0 插件(Extensions https://developer.android.google.cn/training/camerax/vendor-extensions)
 *
 */
class CameraCustomActivity : BaseBindActivity<ActivityCameraCustomBinding>() {
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("自定义相机")
        //只有拍照按钮的相机:CameraActivity 普通Camera相机
        bind.btnTakePhoto.setOnClickListener{
            TakePhotoUtils.takePhoto(this@CameraCustomActivity,{picPath->
                if (TextUtils.isEmpty(picPath))return@takePhoto
                bind.ivPhoto.setImageURI(Uri.fromFile(File(picPath)))
            })
        }
        //CameraX相机
        bind.btnCamerax.setOnClickListener{
            goActivity(CameraXActivity::class.java)
        }
    }
}