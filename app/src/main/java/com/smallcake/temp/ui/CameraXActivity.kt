package com.smallcake.temp.ui

import android.os.Bundle
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCameraXactivityBinding

/**
 * CameraX 1.1 有哪些新的特性发布？: https://www.jianshu.com/p/f50753ea7257
 *
 * 官方Demo      https://github.com/android/camera-samples
 * CameraX 架构  https://developer.android.google.cn/training/camerax/architecture
 *
 *
 *1.引入
// CameraX core library
def camerax_version = '1.1.0-beta01'
implementation "androidx.camera:camera-core:$camerax_version"
// CameraX Camera2 extensions
implementation "androidx.camera:camera-camera2:$camerax_version"
// CameraX Lifecycle library
implementation "androidx.camera:camera-lifecycle:$camerax_version"
// CameraX View class
implementation "androidx.camera:camera-view:$camerax_version"
 */
class CameraXActivity : BaseBindActivity<ActivityCameraXactivityBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("CameraX")
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    fun bindPreview(cameraProvider : ProcessCameraProvider) {
        // 使用 CameraX 创建 Preview 用例
        var preview : Preview = Preview.Builder().build()
        // 创建 cameraSelector，它会在设备上搜索所需的相机
        var cameraSelector : CameraSelector = CameraSelector.Builder()
            // 在本例中，我们选择搜索后置相机
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        // 从 CameraX 的 CameraView 包中获取 previewView 的句柄
        // 利用此方法可以轻松的将相机内容添加到视图
        preview.setSurfaceProvider(bind.previewView.surfaceProvider)
        // 将 preview 与其生命周期绑定
        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

    }
}