package com.smallcake.temp.coroutines

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentTestBinding

class Fragment1: BaseBindFragment<FragmentTestBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.tvTitle.text="页面1"
        bind.layoutRoot.setBackgroundColor(Color.RED)

        (activity as CoroutinesActivity).viewModel.mobileData.observe(viewLifecycleOwner){
            bind.tvDesc.text = "页面1的result:${it?.result}"
        }
    }
}