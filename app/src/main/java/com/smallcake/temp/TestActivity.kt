package com.smallcake.temp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import com.smallcake.smallutils.buildSpannableString
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityTestBinding
import com.smallcake.temp.module.MobileViewModule
import com.smallcake.temp.utils.showToast


class TestActivity : BaseBindActivity<ActivityTestBinding>() {


    override fun onCreate(savedInstanceState: Bundle?,bar: NavigationBar) {
        bar.setTitle("测试")
        val mobileViewModule:MobileViewModule by viewModels()
        bind.lifecycleOwner = this
        bind.viewmodel = mobileViewModule
        bind.btnGet1.setOnClickListener {
//            mobileViewModule.getPhoneResponse("18324138218","c95c37113391b9fff7854ce0eafe496d")
            bind.tvMsg.buildSpannableString {
                for (i in 1..200){
                    addText("惊爆价：")
                    addText("￥"){
                        textColor = Color.RED
                        backgroundColor = Color.YELLOW
                        scale=0.8f
                    }
                    addText(i.toString()){
                        textColor =Color.RED
                        backgroundColor = Color.YELLOW
                        scale=1.8f
                            onClick {
                                showToast("只要 $i 块钱")
                            }
                    }
                    addText("\t\t\t原价：")
                    addText("￥"){
                        textColor =Color.GRAY
                        scale=0.5f
                    }
                    addText("998"){
                        isDelLine = true
                        textColor =Color.GRAY

                    }
                }

            }

        }
        bind.refreshLayout.setOnRefreshListener {
            mobileViewModule.getPhoneResponse("18324138218","c95c37113391b9fff7854ce0eafe496d")
        }

    }


}


