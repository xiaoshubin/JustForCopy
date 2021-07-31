package com.smallcake.temp.ui

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.smallcake.smallutils.DpPxUtils
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityReportRepairBinding
import com.smallcake.temp.utils.L
import com.smallcake.temp.utils.SelectImgUtils
import com.smallcake.temp.utils.TagSelectUtils
import com.smallcake.temp.utils.ldd
import com.yx.jiading.adapter.GridImageAdapter
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
            imgs?.forEachIndexed{i,imgBean->
                L.d("$i =="+imgBean.path)
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
            "车库清洁", "家具维修", " 开水开电", " 清理保洁"
        )
        TagSelectUtils.selectSingleTag(this,bind.autoNewLine,list){i,s->
            bind.etDesc.setText("$i == $s")
        }
        TagSelectUtils.selectTags(this,bind.autoNewLine2,list){i,s->
            bind.etDesc.setText("$i == $s")
        }

    }
}