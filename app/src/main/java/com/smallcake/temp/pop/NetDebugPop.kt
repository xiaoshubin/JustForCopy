package com.smallcake.temp.pop

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.smallcake.smallutils.ClipboardUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.utils.MMKVUtils
import com.smallcake.temp.utils.setSpaceView
import com.yx.jiading.utils.sizeNull
import kotlinx.parcelize.Parcelize
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

/**
 * Date:2021/8/13 10:34
 * Author:SmallCake
 * Desc:
 * @see R.layout.pop_net_debug  弹框布局
 * @see R.layout.item_net_debug 列表布局
 * 0.基类Activity中使用悬浮按钮来开启网络日志弹框
/日志悬浮按钮
if (BuildConfig.DEBUG)EasyFloat.with(this)
    .setLayout(R.layout.debug_text,invokeView = {
        it.findViewById<View>(R.id.btn_debug).setOnClickListener{
            XPopup.Builder(this@BaseBindActivity)
            .popupHeight(Screen.height- Screen.statusHeight)
            .asCustom(NetDebugPop(this@BaseBindActivity))
            .show()
    }
    })
.setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 0)
.setSidePattern(SidePattern.RESULT_SIDE)
.show()
 * 1.在网络拦截器中保存网络访问数据
 * @see  saveNetLog(response, request, tookMs, jsonBody,bodySize)
 * 2.在调试按钮中点击后出发弹框
    XPopup.Builder(mContext)
    .asCustom(NetDebugPop(mContext))
    .show()
 *
 **/
class NetDebugPop(context: Context):BottomPopupView(context) {
    private val mAdapter = NetDebugAdapter()
    override fun getImplLayoutId(): Int {
        return R.layout.pop_net_debug
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.tv_close).setOnClickListener{
            dismiss()
        }
        findViewById<TextView>(R.id.tv_clear).setOnClickListener{
            MMKVUtils.mmkv.remove("netLogList")
            dismiss()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        val netLogList: NetLogList = MMKVUtils.mmkv.decodeParcelable("netLogList", NetLogList::class.java)?: NetLogList(arrayListOf())
        mAdapter.setList(netLogList.list)

        mAdapter.setOnItemClickListener{adapter, view, position ->
            val item = adapter.getItem(position) as NetLog
            val str = "【${item.meth}】${item.code} ${item.msg} ${item.size}\n${item.url}(${item.requestTime})\n${item.params}\n${item.body}"
            ClipboardUtils.copy(context,str)
        }

        //只保留前10条
        if (netLogList.list.sizeNull() > 20) {
            val list5 = netLogList.list.take(10) as ArrayList
            netLogList.list = list5
            MMKVUtils.mmkv.encode("netLogList", netLogList)
        }
    }
}

class NetDebugAdapter:BaseQuickAdapter<NetLog,BaseViewHolder>(R.layout.item_net_debug){
    override fun convert(holder: BaseViewHolder, item: NetLog) {
        holder.setText(R.id.tv_url,"【${item.meth}】${item.code} ${item.msg} ${item.size}\n${item.url}(${item.requestTime})")
            .setText(R.id.tv_params,if (TextUtils.isEmpty(item.params))"无参" else item.params)
            .setText(R.id.tv_body,item.body)
    }

}

/**
 * 在网络拦截器HttpLogInterceptor中使用
 * 保存页面网络日志，方便查看
 * @param response Response
 * @param request Request
 * @param tookMs Long
 * @param responseBody ResponseBody
 */
 fun saveNetLog(response: Response, request: Request, tookMs: Long, jsonBody: String, bodySize:String) {
    val netLogList: NetLogList = MMKVUtils.mmkv.decodeParcelable("netLogList", NetLogList::class.java)?: NetLogList(arrayListOf())
    val item = NetLog().apply {
        url = response.request.url.toString()
        params =  logParams(request)
        meth = request.method
        code = response.code.toString()
        msg = response.message
        requestTime = "${tookMs}ms"
        body = jsonBody
        size = bodySize
    }
    netLogList.list.add(0, item)
    MMKVUtils.mmkv.encode("netLogList", netLogList)
}
private fun logParams(request: Request): String {
    val method = request.method
    if ("POST" == method) {
        val requestBody = request.body
        val buffer = Buffer()
        requestBody!!.writeTo(buffer)
        return buffer.readString(Charset.forName("UTF-8"))
    }
    return "no params"
}

@Parcelize
data class NetLogList(var list:ArrayList<NetLog>): Parcelable
@Parcelize
data class NetLog(var url:String="",var params:String="",var meth:String="",var code:String="",var msg:String="",var requestTime:String="",var body:String="",var size:String="",private val createTime:String= TimeUtils.currentTimeYmdhms):
    Parcelable
