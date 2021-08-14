package com.smallcake.temp.ui


import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityTextViewBinding


/**
 * https://github.com/Cmahjong/ExpandTextView
 */
class TextViewActivity : BaseBindActivity<ActivityTextViewBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("各种文本控件")
        onEvent()
    }

    private fun onEvent() {


    }
}