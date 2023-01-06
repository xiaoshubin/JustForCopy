package com.smallcake.temp.ui

import android.os.Bundle
import com.lxj.xpopup.XPopup
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityListSelectBinding
import com.smallcake.temp.pop.ListSelectPop
import com.smallcake.temp.utils.showToast

/**
 * 无限极列表选择器
 */
class ListSelectActivity : BaseBindActivity<ActivityListSelectBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("列表选择")
        bind.btnSelect.setOnClickListener {
            XPopup.Builder(this@ListSelectActivity)
                .enableDrag(false)
                .asCustom(ListSelectPop(this@ListSelectActivity){
                    showToast(it)
                }).show()
        }
    }
}