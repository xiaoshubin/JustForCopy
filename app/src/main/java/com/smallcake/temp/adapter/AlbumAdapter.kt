package com.smallcake.temp.adapter

import android.util.Log
import android.widget.CheckBox
import androidx.annotation.Keep
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R


/**
 * @CLassName AlbumAdapter
 * @Author xiao
 * @Date 2024/6/15
 * @Description
 */
 class AlbumAdapter :BaseNodeAdapter() {
    init {
        addFullSpanNodeProvider(HeaderProvider())
        addNodeProvider(ChildProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        return when (node) {
            is RootNode -> HeaderProvider.HEADER_TYPE
            is ChildNode -> ChildProvider.CHILD_TYPE
            else -> -1
        }
    }
}

class HeaderProvider :BaseNodeProvider() {
    companion object{
        const val HEADER_TYPE = 0x001
    }
    override val itemViewType: Int
        get() = HEADER_TYPE
    override val layoutId: Int
        get() = R.layout.item_time
    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val node = item as? RootNode ?: return
        helper.setText(R.id.tv_title,node.title)
        Log.d("convert2","父节点=${node.isSelect}")
        helper.getView<CheckBox>(R.id.checkBox).isChecked = node.isSelect
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        super.convert(helper, item, payloads)
        val payload =  payloads.firstOrNull() as? RefreshEvent ?: return
        if (item is RootNode){
            if (payload is RefreshCheck){
                Log.d("convert3","父节点=${item.isSelect} payload=${payload.isCheck}")
                helper.getView<CheckBox>(R.id.checkBox).isChecked = payload.isCheck
            }
        }
    }
}
class ChildProvider :BaseNodeProvider() {
    companion object{
        const val CHILD_TYPE = 0x002
    }
    override val itemViewType: Int
        get() = CHILD_TYPE
    override val layoutId: Int
        get() = R.layout.item_photo
    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val node =  item as? ChildNode ?: return
        helper.getView<CheckBox>(R.id.checkBox).isChecked = node.isSelect
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode, payloads: List<Any>) {
        super.convert(helper, item, payloads)
       val payload =  payloads.firstOrNull() as? RefreshEvent ?: return
        if (item is ChildNode){
            if (payload is RefreshCheck){
                helper.getView<CheckBox>(R.id.checkBox).isChecked = payload.isCheck
            }
        }
    }
}

open class Node: BaseExpandNode(){
    private val parent: BaseNode? = null
    override val childNode: MutableList<BaseNode>? =null
    fun getParent(): BaseNode? {
        return parent
    }
}
class  RootNode(val title:String, private val childNodes: MutableList<BaseNode>?) : Node(){
   override val childNode: MutableList<BaseNode>? = childNodes
    //是否选中
    var isSelect:Boolean = false


}
class  ChildNode :Node(){
    //是否选中
    var isSelect:Boolean = false

}

sealed interface RefreshEvent

@Keep
data class RefreshCheck(val isCheck:Boolean):RefreshEvent