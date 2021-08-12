package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity

/**
 * https://github.com/Cmahjong/ExpandTextView
 */
class TextViewActivity : BaseBindActivity<com.smallcake.temp.databinding.ActivityTextViewBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("各种文本控件")
        onEvent()
    }

    private fun onEvent() {
        bind.tvAdd.setOnClickListener{
            var text = bind.tvAutosize.text.toString()
            text +=text
            bind.tvAutosize.text=text
        }
        bind.tvReset.setOnClickListener{
            bind.tvAutosize.text="大小"
        }
        val str = "雷军用实际行动证明：一个不会蹭热度的程序员不会是一个好的CEO。程序员在大家刻板的印象里就是格子衫、油腻、头发稀疏的形象，然而事实并非如此。绝大多数的程序员都很聪明，更有一些程序员已经成了精，不仅编程厉害，营销方面也是不输任何营销大师，最典型的代表就是雷军。"
        //设置最大显示行数
//        bind.expandTextView.setText(str,false,object :Callback{
//            override fun onExpand() {
//            }
//
//            override fun onCollapse() {
//            }
//
//            override fun onLoss() {
//            }
//
//            override fun onExpandClick() {
//                bind.expandTextView.setChanged(true)
//            }
//
//            override fun onCollapseClick() {
//                bind.expandTextView.setChanged(false)
//            }
//
//        })



    }
}