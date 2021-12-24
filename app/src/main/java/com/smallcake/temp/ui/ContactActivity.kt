package com.smallcake.temp.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.smallutils.CharacterParser
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityContactBinding
import com.smallcake.temp.weight.IndexBar

class ContactActivity : BaseBindActivity<ActivityContactBinding>() {

    private val mAdapter = ContactAdapter()
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("通讯录")
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContactActivity)
            adapter = mAdapter
        }
        //模拟测试数据
        val list = arrayListOf(
            ContactBean("阿拉善"),
            ContactBean("安安"),
            ContactBean("阿大"),
            ContactBean("宝瑞"),
            ContactBean("博云"),
            ContactBean("包小包"),
            ContactBean("蔡阳"),
            ContactBean("曹值"),
            ContactBean("曹天然"),

            ContactBean("肖尔布拉克"),
            ContactBean("加多久"),
            ContactBean("看看"),

            ContactBean("光见"),
            ContactBean("可靠的"),
            ContactBean("杨志"),
            ContactBean("大大"),
            ContactBean("大热加工"),
            ContactBean("额挂机的"),
            ContactBean("发哥"),
        )
        //对数据进行排序，从字母A-Z
        list.sortBy { it.firstLetter }
        mAdapter.setList(list)
        //根据数据过滤出不重复的字母索引列表
        val letters = ArrayList<String>()
        list.forEach {
            val letter = it.firstLetter
            if (!letters.contains(letter))letters.add(letter)
        }
        initLetter(letters)
    }
    private fun initLetter(letters: ArrayList<String>) {
        bind.indexBar.setLetters(letters)
        bind.indexBar.setMyOnLetterChangeListener(object : IndexBar.OnLetterChangeListener{
            override fun onLetterChange(position: Int, letter: String?) {
                mAdapter.data.forEachIndexed{index, cityBean ->
                    if ( letters.size > 0 && letters[0] == letter) {
                        bind.recyclerView.scrollToPosition(0)
                        return
                    }
                    val le: String = cityBean.firstLetter
                    if (
                        TextUtils.equals(letters[0],letter)
                        && TextUtils.equals(letters[letters.size - 1],le)
                        || TextUtils.equals(letter,le)
                    ){
                        val mLayoutManager = bind.recyclerView.layoutManager as LinearLayoutManager
                        mLayoutManager.isSmoothScrollbarEnabled = true
                        mLayoutManager.scrollToPositionWithOffset(index, 0)
                        return
                    }
                }
            }
            override fun onLetterNone() {}
        })

    }
}
//联系人数据
data class ContactBean(val name:String){
    var firstLetter= CharacterParser.getFirstLetter(name)//回去名字首字母
}

/**
 * 联系人适配器
 */
class ContactAdapter:BaseQuickAdapter<ContactBean,BaseViewHolder>(R.layout.item_contact){
    override fun convert(holder: BaseViewHolder, item: ContactBean) {
        holder.setText(R.id.tv_name,item.name)
        var str: String? = null
        val curIndex: String = item.firstLetter
        if (getItemPosition(item) == 0) {
            str = curIndex
        } else {
            val preIndex: String =getItem(getItemPosition(item)-1).firstLetter
            if (!TextUtils.equals(curIndex, preIndex)) str = curIndex
        }
        holder.getView<TextView>(R.id.tv_letter).visibility = if (str == null) View.GONE else View.VISIBLE
        holder.setText(R.id.tv_letter,if ("z" == curIndex) "#" else curIndex)
    }

}