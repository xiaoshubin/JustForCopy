package com.smallcake.temp.utils

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.smallcake.smallutils.AnimUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.fragment.setupDefault
import com.smallcake.temp.weight.XImageLoader
import java.util.*

object PopShowUtils {
    /**
     * 第三方年月日时分秒选择器
     */
     fun showTimePicker(context: Context,cb:(Date)->Unit) {
        val showTypes = booleanArrayOf(true,true,true,true,true,false)
        val types = BooleanArray(6)
        types[0] = true
        types[1] = true
        types[2] = true
        types[3] = true
        types[4] = true
        types[5] = false
        val picker = TimePickerBuilder(context) { date, _ ->
            cb.invoke(date)
        }.setType(showTypes)
            .setupDefault()
            .build()
        picker.setTitleText("时间选择")
        picker.show(true)
    }

    /**
     * 从底部弹出的选择列表
     * @param tv TextView 点击事件，选中后赋值给此文本控件
     * @param title String 标题
     * @param array Array<String> 数据集合
     * @param iv ImageView 用于右侧箭头的翻转动画
     * @param cb Function1<Int, Unit> 回调，只关心位置信息从而确定要传的值
     */
    fun showBottomList(
        title: String = "",
        array: Array<String>,
        iv: ImageView,
        cb: (Int, String) -> Unit
    ){
        XPopup.Builder(iv.context)
            .setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    super.onShow(popupView)
                    AnimUtils.rotateAnimX0To180(iv)
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    AnimUtils.rotateAnimX180To0(iv)
                }
            })
            .asBottomList(title, array){ i: Int, s: String ->
                cb.invoke(i, s)
            }.show()
    }

    /**
     * 点击显示单张大图
     */
    fun showBigPic(iv: ImageView, url: String){
        XPopup.Builder(iv.context)
            .asImageViewer(iv, url, XImageLoader())
            .isShowSaveButton(false)
            .show()
    }

    /**
     * 点击显示多张大图
     * @param iv ImageView 从那个iv开始
     * @param currentPosition Int  弹出弹窗后从第几张图片开始
     * @param list List<String> 图片列表
     * @param recyclerView RecyclerView 拿到对应的item再拿到对应的ImageView
     * layout_imgs 为当前列表中的装载多张图片的布局id
     */
    fun showBigPics(iv: ImageView, currentPosition: Int, list: List<String>, recyclerView: RecyclerView,){
        XPopup.Builder(iv.context).asImageViewer(iv, currentPosition, list,{ popupView, imgPosition ->
            //用于更新回退动画
//            val ivImg = recyclerView.getChildAt(0).findViewById<LinearLayout>(R.id.layout_imgs).getChildAt(imgPosition) as ImageView
//            popupView.updateSrcView(ivImg)

        }, XImageLoader())
            .isShowSaveButton(false)
            .show()
    }
}