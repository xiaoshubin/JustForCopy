package com.smallcake.temp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.smallcake.smallutils.BitmapUtils.getBitmapPath
import com.smallcake.smallutils.ToastUtil.Companion.showLong
import com.smallcake.temp.R
import com.smallcake.temp.utils.GlideEngine
import com.xuexiang.xqrcode.XQRCode
import com.xuexiang.xqrcode.ui.CaptureActivity
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils.AnalyzeCallback

/**
 * 自定义二维码扫描界面，基于zxing和XQRCode库
 * implementation 'com.google.zxing:core:3.3.3'
 * implementation 'com.github.xuexiangjys:XQRCode:1.1.0'
 * 需要相机权限，存储权限
 * <uses-permission android:name="android.permission.CAMERA" />
 * <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
 */
class CustomCaptureActivity : CaptureActivity(), View.OnClickListener {

    private var mIvFlashLight: AppCompatImageView? = null//闪光灯开关
    private var mPic: AppCompatImageView? = null//图片选择
    private var mIsOpen = false//闪光灯是否开启

    override fun getCaptureLayoutId(): Int {
        return R.layout.activity_custom_capture
    }



    /**
     * 开始拍摄前
     */
    override fun beforeCapture() {
        findViewById<View>(R.id.iv_back).setOnClickListener(this)
        mIvFlashLight = findViewById(R.id.iv_flash_light)
        mPic = findViewById(R.id.iv_pic)
    }

    override fun onCameraInitSuccess() {
        mIvFlashLight!!.visibility = View.VISIBLE
        mPic!!.visibility = View.VISIBLE
        mIvFlashLight!!.setOnClickListener(this)
        mPic!!.setOnClickListener(this)
        mIsOpen = XQRCode.isFlashLightOpen()
        refreshFlashIcon()


    }

    override fun onCameraInitFailed() {
        mIvFlashLight!!.visibility = View.GONE
        mPic!!.visibility = View.GONE
    }


    /**
     * 打开关闭闪光灯
     */
    private fun switchFlashLight() {
        mIsOpen = !mIsOpen
        try {
            XQRCode.switchFlashLight(mIsOpen)
            refreshFlashIcon()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            showLong("设备不支持闪光灯!")
        }
    }
    private fun refreshFlashIcon() {
        mIvFlashLight!!.setImageResource(if(mIsOpen)R.mipmap.flash_close else R.mipmap.flash_open)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> finish()
            R.id.iv_flash_light -> switchFlashLight()
            R.id.iv_pic -> getPhoto(this)
        }
    }

    private fun getPhoto(activity: AppCompatActivity) {
        PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage()) //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .setImageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
            .setMaxSelectNum(1)
            .setImageSpanCount(3)// 每行显示个数
            .isDirectReturnSingle(false) // 未选择数据时点击按钮是否可以返回
            .isPreviewImage(false) // 是否可预览图片
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val media = result[0]
                    printMedia(media)
                    val path = if (media.isCompressed) media.compressPath else media.realPath
                    val bitmapPath = getBitmapPath(path)
                    XQRCode.analyzeQRCode(bitmapPath, object : AnalyzeCallback {
                        override fun onAnalyzeSuccess(bitmap: Bitmap, result: String) {
                            Log.e("TAG","解析结果：$result")
                            val resultIntent = Intent()
                            val bundle = Bundle()
                            bundle.putInt(XQRCode.RESULT_TYPE, XQRCode.RESULT_SUCCESS)
                            bundle.putString(XQRCode.RESULT_DATA, result)
                            resultIntent.putExtras(bundle)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                        override fun onAnalyzeFailed() {}
                    })
                }

                override fun onCancel() {
                    showLong("取消选择")
                    Log.e("TAG","没有选择图片")
                }


            })
    }

    private fun printMedia(media: LocalMedia) {
        Log.e("TAG","是否压缩:" + media.isCompressed+
        "\n压缩:" + media.compressPath+
        "\n原图:" + media.path+
        "\n绝对路径:" + media.realPath+
        "\n是否裁剪:" + media.isCut+
        "\n裁剪:" + media.cutPath+
        "\n是否开启原图:" + media.isOriginal+
        "\n原图路径:" + media.originalPath+
        "\n宽高: " + media.width + "x" + media.height+
        "\nSize: " + media.size)
    }

    companion object {
        /**
         * 开始二维码扫描
         * @param fragment
         * @param requestCode 请求码
         * @param theme       主题
         */
        fun start(fragment: Fragment, requestCode: Int, theme: Int) {
            val intent = Intent(fragment.context, CustomCaptureActivity::class.java)
            intent.putExtra(KEY_CAPTURE_THEME, theme)
            fragment.startActivityForResult(intent, requestCode)
        }

        /**
         * 开始二维码扫描
         * @param activity
         * @param requestCode 请求码
         * @param theme       主题
         */
        fun start(activity: Activity, requestCode: Int, theme: Int) {
            val intent = Intent(activity, CustomCaptureActivity::class.java)
            intent.putExtra(KEY_CAPTURE_THEME, theme)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}