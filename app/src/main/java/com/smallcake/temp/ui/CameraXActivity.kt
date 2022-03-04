package com.smallcake.temp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.MainActivity
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCameraXactivityBinding
import com.smallcake.temp.utils.showToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * CameraX 1.1 有哪些新的特性发布？: https://www.jianshu.com/p/f50753ea7257
 *
 * 官方Demo         https://github.com/android/camera-samples
 * CameraX 架构     https://developer.android.google.cn/training/camerax/architecture
 * CameraX 扩展插件 https://developer.android.google.cn/training/camerax/vendor-extensions
 *
 * 预览：接受用于显示预览的 Surface 如 PreviewView
 * 视频拍摄：通过 VideoCapture 拍摄视频和音频：https://developer.android.google.cn/reference/androidx/camera/video/VideoCapture
 *
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

    private val TAG = "CameraXActivity"
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var camera:Camera?=null              //选择相机并绑定当前页面生命周期后的相机实例
    private var imageCapture:ImageCapture?=null  //拍照需要
    private var imageAnalysis:ImageAnalysis?=null  //拍照需要
    private var executor = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("CameraX")
        bar.hide()
        bar.setImmersed(true)
        //图片分析
         imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        //图片分析监听
        imageAnalysis?.setAnalyzer(executor) {

        }
        //（可选）配置
        val cameraConfig = CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig()).build()
        //1.请求 CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        //2.检查 CameraProvider 可用性
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))



        onClick()

    }

    private fun onClick(){
        bind.apply {
            takePhotoButton.setOnClickListener{takePic()}
        }
    }


    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        // 1. 创建 Preview 用例
        val preview : Preview = Preview.Builder().build()
        // 2.指定所需的相机 LensFacing 选项 在本例中，我们选择搜索后置相机
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        // 3.将 Preview 连接到 PreviewView
        preview.setSurfaceProvider(bind.previewView.surfaceProvider)
        // 4.将所选相机和当前页面生命周期绑定
//        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)

        //5.拍照需要设置
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(bind.previewView.display.rotation)
            .build()
        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector,imageCapture,imageAnalysis, preview)

    }

    /**
     * 图片拍摄
     */
    private fun takePic(){
        //设置照片输出路径
        val outputDirectory =getOutputDirectory(this)
        val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .build()
        //开始拍照，并保存
        val executor = Executor({})
        imageCapture?.takePicture(outputOptions,executor,object :ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.e(TAG,"已保存到：${outputFileResults}")
            }
            override fun onError(exception: ImageCaptureException) {
                showToast("拍照出错：${exception.message}")
            }

        })
    }

    companion object {

        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }

    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }
}