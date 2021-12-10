package com.smallcake.temp.ui

import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.opensource.svgaplayer.SVGACache
import com.smallcake.smallutils.BitmapUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.smallutils.custom.GridItemDecoration
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.TestBean
import com.smallcake.temp.databinding.ActivityNinePatchBinding
import com.smallcake.temp.utils.NinePatchBuilder
import com.smallcake.temp.utils.coilImageLoader
import com.smallcake.temp.utils.sizeNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * 网络.9图片处理
 *
 */
class NinePatchActivity : BaseBindActivity<ActivityNinePatchBinding>() {

    private val bgs = listOf(
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/god.png",//热吻
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/13x.png",//金玉满堂
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/23x.png",//怦然心动
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/33x.png",//晚安
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/43x.png",//秋枫
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/53x.png",//海底世界
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/63x.png",//可爱云
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/73x.png",//春意盎然
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/83x.png",//春意盎然
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/93x.png",//春意盎然
        "https://biubiu-static-1306772580.file.myqcloud.com/kuang/chat/3x.png", //爱神
    )
    private val mAdapter = NinePatchAdapter(bgs)

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle(".9图片")
        bind.recyclerView.apply {
            addItemDecoration(GridItemDecoration(1))
            layoutManager = LinearLayoutManager(this@NinePatchActivity)
            adapter = mAdapter
        }
        bind.btnSendMsg.setOnClickListener{
            val msg = bind.etMsg.text.toString()
            if (!TextUtils.isEmpty(msg)){
                val time = TimeUtils.today(TimeUtils.YYYY_MM_DD_H24_MM_SS)
                mAdapter.addData("$time\n$msg")
                bind.recyclerView.smoothScrollToPosition(mAdapter.data.size-1)
            }
        }
    }
}

class NinePatchAdapter(val bgs:List<String>):BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_nine_patch){
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_name,item)
        val view = holder.getView<View>(R.id.layout)
        val layoutPosition = holder.layoutPosition
        if (layoutPosition<bgs.sizeNull()){
            NinePatchBuilder.loadNinePatchBg(view,bgs[layoutPosition])
        }else{
            NinePatchBuilder.loadNinePatchBg(view,bgs[layoutPosition%bgs.sizeNull()])
        }
    }

}
