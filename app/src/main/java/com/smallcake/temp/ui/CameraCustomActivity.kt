package com.smallcake.temp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.MainActivity
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCameraCustomBinding
import com.smallcake.temp.weight.CameraActivity
import java.io.File

/**
 * 自定义相机
 */
class CameraCustomActivity : BaseBindActivity<ActivityCameraCustomBinding>() {
    val KEY_IMAGE_PATH = "imagePath"
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("自定义相机")
        bind.btnTakePhoto.setOnClickListener{
            CameraActivity.startMe(this,1002, CameraActivity.MongolianLayerType.BANK_CARD)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            bind.ivPhoto.setImageURI(Uri.fromFile(File(data.getStringExtra(KEY_IMAGE_PATH))))
        }
    }
}