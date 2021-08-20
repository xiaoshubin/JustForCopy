package com.smallcake.temp.fragment

import android.os.Bundle
import android.view.View
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