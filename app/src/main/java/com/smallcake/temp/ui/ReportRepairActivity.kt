package com.smallcake.temp.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.smallcake.smallutils.DpPxUtils
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityReportRepairBinding
import com.smallcake.temp.utils.GlideEngine
import com.smallcake.temp.utils.L
import com.smallcake.temp.utils.ldd
import com.yx.jiading.adapter.GridImageAdapter
import com.yx.jiading.utils.SelectImgUtils
import com.yx.jiading.utils.sizeNull

class ReportRepairActivity : BaseBindActivity<ActivityReportRepairBinding>() {
    private var imgs: MutableList<GridImageAdapter.Bean>?=null
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("报事报修")
        bar.menuTextView.apply {
            text ="历史记录"
            setTextColor(ContextCompat.getColor(this@ReportRepairActivity,R.color.titleColor))

        }
        initView()
        onEvent()
    }

    private fun onEvent() {
        bind.btnReport.setOnClickListener{
            ldd("要上传的图片：${imgs.sizeNull()}")
            imgs?.forEachIndexed{i,it->
                L.d("$i =="+it.path)
            }

        }

    }

    private fun initView() {
        initQuestionType()
        initImg()
    }

    private fun initImg() {
        SelectImgUtils.bindRecyclerView(this,bind.recyclerView){
            imgs = it
        }


    }

    private fun initQuestionType() {
        val list = listOf(
            "安装空调", "维修下水", " 维修水阀", " 疏通管道",
            "安装空调", "维修下水", " 维修水阀", " 疏通管道"
        )
        val dp8 = DpPxUtils.dp2px( 8f).toInt()
        val tabWidth = (Screen.width - DpPxUtils.dp2px( 48f)) / 4
        val layoutParams =
            LinearLayout.LayoutParams(tabWidth.toInt(), DpPxUtils.dp2px( 33f).toInt())
        list.forEach {
            val btn = CheckBox(this)
            btn.apply {
                this.layoutParams = layoutParams
                text = it
                setButtonDrawable(0)
                setPadding(dp8, 0, dp8, 0)
                setTextColor(
                    ContextCompat.getColorStateList(
                        this@ReportRepairActivity,
                        R.color.gray_red_text_selector
                    )
                )
                setBackgroundResource(R.drawable.gray_lightred_bg_selector)
            }
            bind.autoNewLine.addView(btn)
        }
    }
}