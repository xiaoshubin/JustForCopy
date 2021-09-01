package com.smallcake.temp.base

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.dylanc.viewbinding.inflateBindingWithGeneric
import com.lxj.xpopup.XPopup
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.BuildConfig
import com.smallcake.temp.R
import com.smallcake.temp.pop.NetDebugPop


abstract class BaseBindActivity<VB:ViewBinding>: BaseActivity() {
    lateinit var bind: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = inflateBindingWithGeneric(layoutInflater)
        setContentView(bind.root)
        //导航栏设置
        val bar = NavigationBar(this)
        bar.titleView.setTextColor(ContextCompat.getColor(this,R.color.titleColor))
        bar.setBackgroundColor(Color.WHITE,true)
        bar.backImageView?.setImageResource(R.mipmap.ic_back)
        bar.backImageView?.setOnClickListener{finish()}
        onCreate(savedInstanceState,bar)
        //调试器
        if (BuildConfig.DEBUG) EasyFloat.with(this)
            .setLayout(R.layout.debug_text,invokeView = {
                it.findViewById<View>(R.id.btn_debug).setOnClickListener{
                    XPopup.Builder(this@BaseBindActivity)
                        .popupHeight(Screen.height- Screen.statusHeight-(if (Screen.isShowNavBar(this@BaseBindActivity))Screen.navigationBarHeight else 0 ))
                        .asCustom(NetDebugPop(this@BaseBindActivity))
                        .show()
                }
            })
            .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 0)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .show()

    }
    abstract fun onCreate(savedInstanceState: Bundle?,bar:NavigationBar)

}