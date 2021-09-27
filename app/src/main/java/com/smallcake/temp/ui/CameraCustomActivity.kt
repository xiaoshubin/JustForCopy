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
 */
class CameraCustomActivity : BaseBindActivity<ActivityCameraCustomBinding>() {
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("自定义相机")
        bind.btnTakePhoto.setOnClickListener{
            TakePhotoUtils.takePhoto(this@CameraCustomActivity,{picPath->
                if (TextUtils.isEmpty(picPath))return@takePhoto
                bind.ivPhoto.setImageURI(Uri.fromFile(File(picPath)))
            })
        }
    }
}