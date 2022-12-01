package com.smallcake.temp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.SystemUtils
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentMineBinding
import com.smallcake.temp.utils.ZxingUtils
import com.smallcake.temp.utils.showToast


class MineFragment: BaseBindFragment<FragmentMineBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEvent()
    }

    @SuppressLint("SetTextI18n")
    private fun onEvent() {
        bind.tvScan.setOnClickListener{
            XXPermissions.with(this)
                .permission(arrayListOf(Permission.CAMERA,Permission.MANAGE_EXTERNAL_STORAGE) )
                .request { _, all ->
                    if (all){
                        ZxingUtils.scanQRCode(requireActivity() as AppCompatActivity){str->
                            showToast("扫码结果：$str")
                        }
                    }else{
                        //相机用于扫描二维码，存储用于读取手机相册图片识别二维码
                        showToast("需要相机和存储权限")
                    }
                }

        }
        bind.tvScan.text = "系统版本号:${SystemUtils.systemVersion}" +
                "\n手机型号:${SystemUtils.model}" +
                "\n手机厂商：${SystemUtils.deviceBrand}"+
                "\nAndroidId：${SystemUtils.androidId}"+
                "\nIMEI：${SystemUtils.imei}"+
                "\nSdk版本：${SystemUtils.systemSdk}"+
                "\n产品名：${SystemUtils.deviceProduct}"+
                "\n手机主板名：${SystemUtils.deviceBoard}"+
                "\n设备名：${SystemUtils.deviceName}"+
                "\nfingerprit：${SystemUtils.fingerprit}"
    }




}