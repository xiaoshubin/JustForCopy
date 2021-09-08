package com.smallcake.temp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            ZxingUtils.scanQRCode(requireActivity() as AppCompatActivity){scanSuccess,str->
                showToast("是否扫码成功$scanSuccess 扫码结果：$str")
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