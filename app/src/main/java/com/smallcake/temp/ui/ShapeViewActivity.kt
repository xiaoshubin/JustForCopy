package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityShapeViewBinding

class ShapeViewActivity : BaseBindActivity<ActivityShapeViewBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("ShapeView")
    }
}