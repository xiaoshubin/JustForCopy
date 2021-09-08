package com.smallcake.temp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.smallcake.smallutils.BitmapUtils.getBitmapPath
import com.smallcake.smallutils.ToastUtil.Companion.showLong
import com.smallcake.temp.R
import com.smallcake.temp.utils.GlideEngine
import com.smallcake.temp.utils.L
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
        mIvFlashLight!!.setImageResource(if(mIsOpen)R.drawable.picture_ic_flash_on else R.drawable.picture_ic_flash_off)
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
            .openGallery(PictureMimeType.ofImage()) //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
            .isWeChatStyle(true) // 是否开启微信图片选择风格
            .maxSelectNum(1) // 最大图片选择数量
            .imageSpanCount(3) // 每行显示个数
            .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
            .compressQuality(80) // 图片压缩后输出质量 0~ 100
            .cutOutQuality(90) // 裁剪输出质量 默认100
            .minimumCompressSize(1000) // 小于多少kb的图片不压缩
            //.isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);不压缩不裁剪有效,默认处理
            .isCompress(true) // 是否压缩
            .isEnableCrop(false) // 是否裁剪
            .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
            .synOrAsy(false) //同步true或异步false 压缩 默认同步
            .isPreviewImage(false) // 是否可预览图片
            .isCamera(true) // 是否显示拍照按钮
            .withAspectRatio(1, 1) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
            .hideBottomControls(true) // 是否显示uCrop工具栏，默认不显示
            .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
            .circleDimmedLayer(false) // 是否圆形裁剪
            .showCropFrame(false) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
            //.cropImageWideHigh(120,120)// 裁剪宽高比，设置如果大于图片本身宽高则无效
            .rotateEnabled(false) // 裁剪是否可旋转图片
            .scaleEnabled(false) // 裁剪是否可放大缩小图片
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    val media = result[0]
                    printMedia(media)
                    val androidQToPath = media.androidQToPath
                    var path: String? = ""
                    path = if (TextUtils.isEmpty(androidQToPath)) {
                        if (media.isCompressed) media.compressPath else media.realPath
                    } else {
                        androidQToPath
                    }
                    val bitmapPath = getBitmapPath(path)
                    XQRCode.analyzeQRCode(bitmapPath, object : AnalyzeCallback {
                        override fun onAnalyzeSuccess(bitmap: Bitmap, result: String) {
                            L.e("解析结果：$result")
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
                    L.e("没有选择图片")
                }
            })
    }

    private fun printMedia(media: LocalMedia) {
        L.e("是否压缩:" + media.isCompressed+
        "\n压缩:" + media.compressPath+
        "\n原图:" + media.path+
        "\n绝对路径:" + media.realPath+
        "\n是否裁剪:" + media.isCut+
        "\n裁剪:" + media.cutPath+
        "\n是否开启原图:" + media.isOriginal+
        "\n原图路径:" + media.originalPath+
        "\nAndroid Q 特有Path:" + media.androidQToPath+
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