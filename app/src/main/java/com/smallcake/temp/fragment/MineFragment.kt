package com.smallcake.temp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentMineBinding
import com.smallcake.temp.utils.ZxingUtils


class MineFragment: BaseBindFragment<FragmentMineBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEvent()
    }

    private fun onEvent() {
        bind.tvScan.setOnClickListener{
            ZxingUtils.scanQRCode(requireActivity())
        }
    }




}