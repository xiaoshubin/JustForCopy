package com.smallcake.temp.utils

import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.client.android.MNScanManager
import com.google.zxing.client.android.model.MNScanConfig
import com.smallcake.temp.R

/**
 * Date: 2020/1/14
 * author: SmallCake
 * 1.需要引入
        implementation 'com.google.zxing:core:3.3.3'
        implementation 'com.github.maning0303:MNZXingCode:V2.1.9'
 * 2.如果需要自定义遮罩层
        需要引入 layout_scan_custom_view.xml布局文件
 * 3.单独解析图片中的二维码
        String code = ZXingUtils.syncDecodeQRCode(String picturePath);
        String code = ZXingUtils.syncDecodeQRCode(Bitmap bitmap);

 参考：
https://github.com/maning0303/MNZXingCode
 注意：1.需要申请相机（扫描）和存储权限（从相册读取图片识别二维码）
        如果targetSdkVersion >=29且需要从相册识别二维码的功能，则还需要在application中加入
        android:requestLegacyExternalStorage="true"

        使用XXPermissions申请权限,然后再使用此扫码功能
        XXPermissions.with(this)
            .permission(Permission.CAMERA,Permission.MANAGE_EXTERNAL_STORAGE)
            .request { permissions, all ->
                if (all)ZxingUtils.scanQRCode(this@MainActivity)
            }

 */
object ZxingUtils {

    /**
     *  cb:(Boolean,String?) 是否解析成功，解析成功的返回数据
     *  获得扫描二维码的结果和识别二维码的结果
     */
     fun scanQRCode(context: AppCompatActivity,cb:(String) -> Unit){
        //自定义扫描
        val scanConfig = MNScanConfig.Builder()
            //设置完成震动
            .isShowVibrate(false)
            //扫描完成声音
            .isShowBeep(false)
            //显示相册功能
            .isShowPhotoAlbum(true)
            //显示闪光灯
            .isShowLightController(true)
            //打开扫描页面的动画
            .setScanHintText("请将二维码放入框中")
            //自定义文案颜色
            .setScanHintTextColor("#FFFFFF")
            //自定义文案大小（单位sp）
            .setScanHintTextSize(14)
            //扫描线的颜色
            .setScanColor("#03A9F4")
            //是否显示缩放控制器
            .isShowZoomController(false)
            //显示缩放控制器位置
            .setZoomControllerLocation(MNScanConfig.ZoomControllerLocation.Bottom)
            //扫描线样式
            .setLaserStyle(MNScanConfig.LaserStyle.Grid)
            //背景颜色
            .setBgColor("#33000000")
            //网格扫描线的列数
            .setGridScanLineColumn(50)
            //网格高度
            .setGridScanLineHeight(250)
            //高度偏移值（单位px）+向上偏移，-向下偏移
            .setScanFrameHeightOffsets(0)
            //是否全屏范围扫描
            .setFullScreenScan(false)
            //二维码标记点
            .isShowResultPoint(true)
            //二维码标记点样式
            .setResultPointConfigs(8, 4, 1, "#03A9F4", "#03A9F4")
            //状态栏设置：颜色，是否黑色字体
            .setStatusBarConfigs("#00000000", true)
            //是否支持多二维码同时扫出,默认false,多二维码状态不支持条形码
            .setSupportMultiQRCode(true)

            //自定义遮罩，如果无需自定义，可以用默认的，把这部分注释即可
            .setCustomShadeViewLayoutID(R.layout.layout_scan_custom_view) { view ->
                view?.apply {
                    findViewById<ImageView>(R.id.iv_back).setOnClickListener { MNScanManager.closeScanPage()}
                    findViewById<ImageView>(R.id.iv_photo).setOnClickListener { MNScanManager.openAlbumPage()}
                    val ivLigth = findViewById<ImageView>(R.id.iv_scan_light)
                    ivLigth.setOnClickListener {
                        if (MNScanManager.isLightOn()) {
                            MNScanManager.closeScanLight()
                            ivLigth.setImageResource(com.google.zxing.client.android.R.drawable.mn_icon_scan_flash_light_off)
                        } else {
                            MNScanManager.openScanLight()
                            ivLigth.setImageResource(com.google.zxing.client.android.R.drawable.mn_icon_scan_flash_light_on)
                        }
                    }

                }
            }

            .builder()
        MNScanManager.startScan(context,scanConfig) { resultCode, data ->
            when (resultCode) {
                MNScanManager.RESULT_SUCCESS ->{
                    val resultSuccess : String? = data.getStringExtra(MNScanManager.INTENT_KEY_RESULT_SUCCESS)
                    if (!TextUtils.isEmpty(resultSuccess))cb(resultSuccess!!)
                }
                MNScanManager.RESULT_FAIL ->{
                    val resultError: String? = data.getStringExtra(MNScanManager.INTENT_KEY_RESULT_ERROR)
                    Toast.makeText(context,"扫码失败:$resultError", Toast.LENGTH_LONG).show()
                }
                MNScanManager.RESULT_CANCLE -> Toast.makeText(context,"取消扫码", Toast.LENGTH_LONG).show()
            }
        }
    }

}
