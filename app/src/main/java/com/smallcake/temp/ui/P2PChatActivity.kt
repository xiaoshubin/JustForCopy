package com.smallcake.temp.ui

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smallcake.smallutils.EditTextUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.MsgBean
import com.smallcake.temp.databinding.ActivityP2pChatBinding
import com.smallcake.temp.databinding.ItemChatMsgImgBinding
import com.smallcake.temp.databinding.ItemChatMsgTextBinding
import com.smallcake.temp.utils.sizeNull

class P2PChatActivity : BaseBindActivity<ActivityP2pChatBinding>() {
    private val mAdapter = ChatMsgAdapter()
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("王红军")
        initView()
        onEvent()
    }

    private fun onEvent() {
        EditTextUtils.setOnSend(this,bind.etContent){content->
            if (TextUtils.isEmpty(content))return@setOnSend
            val msg =if (content?.contains("图片")==true){
                MsgBean(1,content!!)
            }else {
               MsgBean(0,content!!)
            }
            mAdapter.addDataLast(msg)
        }
    }

    private fun initView() {
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@P2PChatActivity)
            adapter = mAdapter
        }
        mAdapter.apply {
            animationEnable = true
            setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom)
        }

    }
}

class ChatMsgAdapter: BaseDelegateMultiAdapter<MsgBean, BaseDataBindingHolder<*>>(){
    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<MsgBean>(){
            override fun getItemType(data: List<MsgBean>, position: Int): Int {
                return data[position].type
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()?.apply {
            addItemType(0, R.layout.item_chat_msg_text)
            addItemType(1, R.layout.item_chat_msg_img)
        }
    }
    override fun convert(holder: BaseDataBindingHolder<*>, item: MsgBean) {
        when(holder.itemViewType){
            0->(holder.dataBinding as ItemChatMsgTextBinding).apply {
                tvMsg.text = item.message
                val tvLayoutParams = tvMsg.layoutParams as FrameLayout.LayoutParams
                val ivLayoutParams = ivAvatar.layoutParams as FrameLayout.LayoutParams
                //自己
                if (holder.layoutPosition%2==0){
                    tvLayoutParams.gravity = Gravity.RIGHT
                    ivLayoutParams.gravity = Gravity.RIGHT
                    tvMsg.setTextColor(Color.WHITE)
                    tvMsg.setBackgroundResource(R.drawable.right_blue_msg_bg)
                }else{
                    tvLayoutParams.gravity = Gravity.LEFT
                    ivLayoutParams.gravity = Gravity.LEFT
                    tvMsg.setTextColor(Color.BLACK)
                    tvMsg.setBackgroundResource(R.drawable.left_white_msg_bg)
                }
            }
            1->(holder.dataBinding as ItemChatMsgImgBinding).apply {
                val imgLayoutParams = iv.layoutParams as FrameLayout.LayoutParams
                val ivLayoutParams = ivAvatar.layoutParams as FrameLayout.LayoutParams
                //自己
                if (holder.layoutPosition%2==0){
                    imgLayoutParams.gravity = Gravity.RIGHT
                    ivLayoutParams.gravity = Gravity.RIGHT
                }else{
                    imgLayoutParams.gravity = Gravity.LEFT
                    ivLayoutParams.gravity = Gravity.LEFT
                }
            }

        }
    }

    fun addDataLast(msg:MsgBean){
        addData(msg)
        recyclerView.smoothScrollToPosition(this.data.sizeNull()-1)
    }

}