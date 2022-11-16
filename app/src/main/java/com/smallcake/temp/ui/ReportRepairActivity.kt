package com.smallcake.temp.ui

import android.os.Bundle
import android.util.Log
import com.smallcake.smallutils.TimeUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityReportRepairBinding
import com.smallcake.temp.utils.*
import com.smallcake.temp.utils.sizeNull

/**
 * 问题反馈模板页面
 * @property imgs MutableList<Bean>? 要上传的图片集合
 * @see GridImageAdapter 图片选择适配器
 * @see TagSelectUtils 单选多选工具类
 *
 */
class ReportRepairActivity : BaseBindActivity<ActivityReportRepairBinding>() {
    private var imgs: List<ImgSelectBean>?=null
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("报事报修")
        initView()
        onEvent()
    }

    private fun onEvent() {
        bind.btnReport.setOnClickListener{
            ldd("要上传的图片：${imgs.sizeNull()}")
            imgs?.forEachIndexed{i,path->
                Log.d("ReportRepairActivity","$i =="+path)
            }
        }
        bind.tvStartTime.setOnClickListener{
            PopShowUtils.showTimePicker(this){
                val showStr = TimeUtils.timeToStr((it.time/1000).toInt(),"yyyy-MM-dd HH:mm")
                bind.tvStartTime.text = showStr
            }
        }
        bind.tvEndTime.setOnClickListener{
            PopShowUtils.showTimePicker(this){
                val showStr = TimeUtils.timeToStr((it.time/1000).toInt(),"yyyy-MM-dd HH:mm")
                bind.tvEndTime.text = showStr
            }
        }
    }

    private fun initView() {
        initQuestionType()
        initImg()
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
    private fun initImg() {
        SelectImgUtils.bindRecyclerView(this,bind.recyclerView,9){
            imgs = it
        }

    }
}