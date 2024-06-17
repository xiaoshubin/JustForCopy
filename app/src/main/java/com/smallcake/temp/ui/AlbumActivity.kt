package com.smallcake.temp.ui

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.entity.node.BaseNode
import com.smallcake.smallutils.custom.GridItemDecoration
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.adapter.AlbumAdapter
import com.smallcake.temp.adapter.ChildNode
import com.smallcake.temp.adapter.RefreshCheck
import com.smallcake.temp.adapter.RootNode
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityAlbumBinding

class AlbumActivity : BaseBindActivity<ActivityAlbumBinding>() {
    private val TAG = "AlbumActivity"
    private val mAdapter = AlbumAdapter()
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("节点列表")
        bind.apply {
            recyclerView.addItemDecoration(GridItemDecoration(3))
            recyclerView.layoutManager  = GridLayoutManager(this@AlbumActivity,3)
            recyclerView.adapter = mAdapter
        }
        loadData()
        mAdapter.setOnItemClickListener{adapter,view,position->
            val item = adapter.getItem(position)
            when(item){
                //点击根节点，选中所有子节点
                is RootNode-> {
                    val currentSelect = item.isSelect
                    item.childNode?.forEach {childBaseNode->
                        (childBaseNode as ChildNode).isSelect = !currentSelect
                    }
                    item.isSelect = !currentSelect
                    val positionEnd = position+(item.childNode?.size?:0)
                    adapter.notifyItemRangeChanged(position,positionEnd,RefreshCheck(!currentSelect))

                }
                //点击子节点，判断：是否全部选中
                is ChildNode->{
                    item.isSelect = !item.isSelect
                    val currentSelectState = item.isSelect
                    adapter.notifyItemChanged(position, RefreshCheck(currentSelectState))


                    val parentPosition =  mAdapter.findParentNode(item)
                    val timeNode = mAdapter.getItem(parentPosition) as RootNode

                    //更新父节点
                    val hasSelectAll = timeNode.childNode?.find { !(it as ChildNode).isSelect } == null
                    Log.d(TAG,"父节点（$parentPosition:${timeNode.isSelect}）--> 子节点(${timeNode.childNode?.size})选中=$hasSelectAll")
                    if (timeNode.isSelect != hasSelectAll){
                        timeNode.isSelect = hasSelectAll
                        adapter.notifyItemChanged(parentPosition, RefreshCheck(currentSelectState))
                    }

                }
            }

        }
    }

    private fun loadData() {
        val childs = mutableListOf<BaseNode>()
        for (i in 0..37) {
            childs.add(ChildNode())
        }
        val list = arrayListOf(
            RootNode("2024年6月6日", childs),
            RootNode("2024年6月3日", arrayListOf(ChildNode(), ChildNode()))
        )
        mAdapter.addDiffList(list)
    }
}