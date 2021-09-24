package com.smallcake.temp.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.smallutils.custom.GridItemDecoration
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMoreLayoutListBinding
import com.smallcake.temp.databinding.ItemImageViewBinding
import com.smallcake.temp.databinding.ItemImgTextViewBinding
import com.smallcake.temp.databinding.ItemTextViewBinding
import com.smallcake.temp.utils.TabUtils


/**
 * 多布局列表
 * 1、BaseMultiItemQuickAdapter：适用于类型较少，业务不复杂的场景
 *      说明：适用于类型较少，业务不复杂的场景，便于快速使用。
 *      所有的数据类型，都必须实现MultiItemEntity接口（注意，这里不是继承抽象类，而是实现接口，避免对业务的实体类带来影响）
 * 2、BaseDelegateMultiAdapter
 *      说明：通过代理类的方式，返回布局 id 和 item 类型；
 *      适用于:
 *      2.1、实体类不方便扩展，此Adapter的数据类型可以是任意类型，
 *      只需要在BaseMultiTypeDelegate.getItemType中返回对应类型
 *      2.2、item 类型较少 如果类型较多，为了方便隔离各类型的业务逻辑，推荐使用BaseProviderMultiAdapter
 * 3、BaseProviderMultiAdapter
 *      说明：当有多种条目的时候，避免在convert()中做太多的业务逻辑，把逻辑放在对应的 ItemProvider 中。以及最大化自定义VH类型。
 *      3.1、此Adapter的数据类型可以是任意类型，只需要在getItemType中返回对应类型
 *      3.2、Adapter不限定ViewHolder类型。ViewHolder 由 BaseItemProvider 实现，并且每个BaseItemProvider可以拥有自己类型的ViewHolder类型。
 */
class MoreLayoutListActivity : BaseBindActivity<ActivityMoreLayoutListBinding>() {
    private val mAdapter0 = MultiAdapter()
    private val mAdapter1 = DelegateMultiAdapter()
    private val mAdapter2 = ProviderMultiAdapter()
    private var tabType=0//0多布局",1代理类多布局",2复杂多布局
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("多布局列表")
        initView()
        loadData()
    }

    private fun initView() {
        TabUtils.createTabs(bind.tabLayout, listOf("多布局","代理类多布局","复杂多布局")){
            tabType=it
            loadData()
        }
        bind.recyclerView.apply {
            addItemDecoration(GridItemDecoration(1))
            layoutManager = LinearLayoutManager(this@MoreLayoutListActivity)
            adapter = mAdapter0
        }

    }

    private fun loadData() {
        bind.recyclerView.apply {
            adapter= when(tabType){
                0->mAdapter0
                1->mAdapter1
                2->mAdapter2
                else -> mAdapter0
            }
        }
        when(tabType){
            0->{
                val list = listOf(
                    QuickMultipleEntity(0,"我是文本"),
                    QuickMultipleEntity(1,"我是图片",R.mipmap.ic_logo),
                    QuickMultipleEntity(2,"我是文本图片",R.mipmap.ic_logo),
                    QuickMultipleEntity(0,"基督教佛教"),
                    QuickMultipleEntity(1,"我是图片",R.mipmap.ic_add_img),
                    QuickMultipleEntity(2,"文本图片Banner",R.mipmap.ic_default_banner),
                )
                mAdapter0.setList(list)
            }
            1->{
                val list = listOf(
                    DelegateEntity(2,"我是文本图片",R.mipmap.ic_logo),
                    DelegateEntity(1,"我是图片",R.mipmap.ic_logo),
                    DelegateEntity(0,"我是文本"),
                    DelegateEntity(2,"文本图片Banner",R.mipmap.ic_default_banner),
                    DelegateEntity(1,"我是图片",R.mipmap.ic_add_img),
                    DelegateEntity(0,"基督教佛教"),
                )
                mAdapter1.setList(list)
            }
            2->{
                val list = listOf(
                    ProviderMultiEntity(1,"复杂我是图片",R.mipmap.ic_logo),
                    ProviderMultiEntity(2,"复杂我是文本图片",R.mipmap.ic_logo),
                    ProviderMultiEntity(0,"复杂我是文本"),
                    ProviderMultiEntity(1,"复杂我是图片",R.mipmap.ic_add_img),
                    ProviderMultiEntity(2,"复杂文本图片Banner",R.mipmap.icon_selected_tab1),
                    ProviderMultiEntity(0,"复杂基督教佛教"),
                )
                mAdapter2.setList(list)
            }
        }

    }
}

/**
 * 适用于类型较少，业务不复杂的场景
 * 例如：一种数据类型，状态不同，显示的样式和布局就不同的需求
 */
class MultiAdapter: BaseMultiItemQuickAdapter<QuickMultipleEntity, BaseDataBindingHolder<*>>() {
    init {
        addItemType(0, R.layout.item_text_view)
        addItemType(1, R.layout.item_image_view)
        addItemType(2, R.layout.item_img_text_view)
    }
    override fun convert(holder: BaseDataBindingHolder<*>, item: QuickMultipleEntity) {
        when(holder.itemViewType){
            0->(holder.dataBinding as ItemTextViewBinding).tvName.text = item.name
            1->(holder.dataBinding as ItemImageViewBinding).iv.setImageResource(item.img)
            2->(holder.dataBinding as ItemImgTextViewBinding).apply {
                    tvName.text = item.name
                    iv.setImageResource(item.img)
                }
        }
    }
}
data class QuickMultipleEntity(val type:Int,val name:String,val img:Int=0) : MultiItemEntity {
    override val itemType: Int
        get() = type
}

/**
 * 实体类不方便扩展的需求
 * 实现效果和BaseMultiItemQuickAdapter一样
 */
class DelegateMultiAdapter: BaseDelegateMultiAdapter<DelegateEntity, BaseDataBindingHolder<*>>(){
    init {
        // 第一步，设置代理
       setMultiTypeDelegate(object :BaseMultiTypeDelegate<DelegateEntity>(){
            override fun getItemType(data: List<DelegateEntity>, position: Int): Int {
                return data[position].type
            }
        })
        // 第二部，绑定 item 类型
        getMultiTypeDelegate()?.apply {
            addItemType(0, R.layout.item_text_view)
            addItemType(1, R.layout.item_image_view)
            addItemType(2, R.layout.item_img_text_view)
        }
    }
    override fun convert(holder: BaseDataBindingHolder<*>, item: DelegateEntity) {
        when(holder.itemViewType){
            0->(holder.dataBinding as ItemTextViewBinding).tvName.text = item.name
            1->(holder.dataBinding as ItemImageViewBinding).iv.setImageResource(item.img)
            2->(holder.dataBinding as ItemImgTextViewBinding).apply {
                    tvName.text = item.name
                    iv.setImageResource(item.img)
                }
        }
    }
}
data class DelegateEntity(val type:Int,val name:String,val img:Int=0)

/**
 * 数据类型业务逻辑处理比较复杂的场景
 */
class ProviderMultiAdapter: BaseProviderMultiAdapter<ProviderMultiEntity>() {
    init {
        // 注册 Provider
        addItemProvider(TextItemProvider())
        addItemProvider(ImgItemProvider())
        addItemProvider(TextImgItemProvider())
    }

    override fun getItemType(data: List<ProviderMultiEntity>, position: Int): Int {
        return data[position].type
    }

}

data class ProviderMultiEntity(val type:Int,val name:String,val img:Int=0)

class TextItemProvider:BaseItemProvider<ProviderMultiEntity>() {
    override fun convert(helper: BaseViewHolder, item: ProviderMultiEntity) {
        helper.setText(R.id.tv_name,item.name)
    }
    override val itemViewType: Int
        get() = 0
    override val layoutId: Int
        get() = R.layout.item_text_view
}
class ImgItemProvider:BaseItemProvider<ProviderMultiEntity>() {
    override fun convert(helper: BaseViewHolder, item: ProviderMultiEntity) {
        helper.setImageResource(R.id.iv,item.img)
    }
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_image_view
}
class TextImgItemProvider:BaseItemProvider<ProviderMultiEntity>() {
    override fun convert(helper: BaseViewHolder, item: ProviderMultiEntity) {
        helper.setText(R.id.tv_name,item.name).setImageResource(R.id.iv,item.img)
    }
    override val itemViewType: Int
        get() = 2
    override val layoutId: Int
        get() = R.layout.item_img_text_view
}