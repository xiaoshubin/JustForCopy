package com.smallcake.temp.coroutines

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentTestBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Fragment3: BaseBindFragment<FragmentTestBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.tvTitle.text="页面3"
        bind.layoutRoot.setBackgroundColor(Color.BLUE)
        (activity as CoroutinesActivity).viewModel.mobileData.observe(viewLifecycleOwner){
            bind.tvDesc.text = "页面3的result:${it?.result}"
        }
    }
}