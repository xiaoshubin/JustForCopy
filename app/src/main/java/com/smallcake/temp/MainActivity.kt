package com.smallcake.temp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.smallcake.smallutils.Base64Utils
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.ToastUtil
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMainBinding
import com.smallcake.temp.utils.BottomNavUtils
import com.smallcake.temp.utils.L
import com.xuexiang.xqrcode.XQRCode


/**
 * @see com.smallcake.temp.fragment.PageFragment 页面
 * @see com.smallcake.temp.fragment.ListFragment 控件
 * @see com.smallcake.temp.fragment.MineFragment 我的
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        initView()
        onEvent()
    }

    private fun onEvent() {
        val key = "1111wwww2222uuuu"
        val str = "yx888888"

        val encryptStr = Base64Utils.encryptAES(key,str)
        val decryptStr = Base64Utils.decryptAES(key,encryptStr)
        L.e("加密：$encryptStr  解密：$decryptStr")

    }

    private fun initView() {
        BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("REQUEST_CODE","requestCode:$requestCode"+"resultCode==$resultCode")
        //处理二维码扫描结果
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data)
        }

    }

    /**
     * 处理二维码扫描结果
     * @param data
     */
    private fun handleScanResult(data: Intent?) {
        if (data != null) {
            val bundle = data.extras
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    val result = bundle.getString(XQRCode.RESULT_DATA)
                    ToastUtil.showLong("解析结果:$result")
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    ToastUtil.showLong("解析二维码失败")
                }
            }
        }
    }




}


