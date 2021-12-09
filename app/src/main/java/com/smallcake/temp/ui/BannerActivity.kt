package com.smallcake.temp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityBannerBinding
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.transformer.*

class BannerActivity : BaseBindActivity<ActivityBannerBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("轮播图")
        val listImgs = listOf(
            "https://upload-images.jianshu.io/upload_images/11345863-09245cb3a4d26c49.png?imageMogr2/auto-orient/strip|imageView2/2/format/webp",
            "https://upload-images.jianshu.io/upload_images/21509097-ea4e13f39b78f6b8.jpg",
            "https://upload-images.jianshu.io/upload_images/11424289-1b0aa5f78fdd497b.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/1079/format/webp",
            "https://upload-images.jianshu.io/upload_images/9134822-2bfc105bd50f3f54.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp",
        )
        //礼品展示效果
        bind.banner1.apply {
            setBannerGalleryEffect(100,16)
            setPageTransformer(ZoomOutPageTransformer())
            adapter = BoxBannerAdapter(listImgs)
            addBannerLifecycleObserver(this@BannerActivity)
        }
    }
}

/**
 * 自定义轮播适配器
 * 注意：里面的布局必须match_parent
 */
class BoxBannerAdapter(datas: List<String>?) : BannerAdapter<String, BaseViewHolder>(datas) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.banner_box_prize,parent,false)
        return BaseViewHolder(view)
    }

    override fun onBindView(holder: BaseViewHolder, item: String, position: Int, size: Int) {
        holder.setText(R.id.tv,holder.layoutPosition.toString())
        holder.getView<ImageView>(R.id.iv).load(item){transformations(RoundedCornersTransformation(8f))}
    }
}