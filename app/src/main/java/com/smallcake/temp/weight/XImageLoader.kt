package com.smallcake.temp.weight

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lxj.xpopup.interfaces.XPopupImageLoader
import java.io.File

/**
 * Date:2021/8/2 8:46
 * Author:SmallCake
 * Desc:
 **/
 class XImageLoader : XPopupImageLoader {
 override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
     Glide.with(imageView).load(uri).into(imageView)
 }

 override fun getImageFile(context: Context, uri: Any): File {
     return Glide.with(context).downloadOnly().load(uri).submit().get()
 }
}