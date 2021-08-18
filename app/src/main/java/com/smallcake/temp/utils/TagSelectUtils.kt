package com.smallcake.temp.utils

import android.content.Context
import android.view.Gravity
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.smallcake.smallutils.DpUtils
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.custom.AutoNewLineLayout
import com.smallcake.temp.R

/**
 * Date:2021/7/22 9:12
 * Author:SmallCake
 * Desc:
 **/
object TagSelectUtils {

    /**
     * 单选标签
     * @param context Context
     * @param autoNewLine AutoNewLineLayout
     * @param list List<String>
     * @param tabNum Int 每横排显示的数量
     * @param margins Int 外部包裹的布局外边距
     * @param cb Function2<Int, String, Unit> 位置 和 文本
     */
     fun selectSingleTag(context: Context, autoNewLine: AutoNewLineLayout,list:List<String>,tabNum:Int=4,margins:Int=0, cb:(Int,String)->Unit) {
        val listBtns = ArrayList<CheckBox>()

        val dpMargin = DpUtils.dp2px(margins)//layout左右margin
        val pL = autoNewLine.paddingLeft
        val pR = autoNewLine.paddingRight
        val spaceWidth = autoNewLine.horizontalSpace*(tabNum-1)
        val tabWidth = ((Screen.width-dpMargin- pL-pR-spaceWidth)/tabNum).toInt()
        val tabHeight = DpUtils.dp2px( 33f)
        val layoutParams = LinearLayout.LayoutParams(tabWidth, tabHeight)

        val dp8 = DpUtils.dp2px( 8f).toInt()//checkBox间隙
        list.forEachIndexed {index,tagName->
            val btn = CheckBox(context)
            btn.apply {
                this.layoutParams = layoutParams
                text = tagName
                gravity = Gravity.CENTER
                setButtonDrawable(0)
                setPadding(dp8, 0, dp8, 0)
                setTextColor(ContextCompat.getColorStateList(context,R.color.gray_red_text_selector))
                setBackgroundResource(R.drawable.gray_lightred_bg_selector)
                setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
                    if (b) {
                        for (i in 0 until listBtns.size){
                            if (index!=i){
                                listBtns[i].apply {
                                    isEnabled = false
                                    isChecked = false
                                    isEnabled = true
                                }
                            }
                        }
                    }
                    cb.invoke(index,tagName)
                }
            }
            listBtns.add(btn)
            autoNewLine.addView(btn)
        }

    }

    /**
     * 多选标签
     * @param context Context
     * @param autoNewLine AutoNewLineLayout
     * @param list List<String>
     * @param cb Function1<String, Unit>
     */
     fun selectTags(context: Context, autoNewLine: AutoNewLineLayout,list:List<String>,tabNum:Int=4,margins:Int=0, cb:(List<Int>,List<String>)->Unit) {
        val listBtns = ArrayList<CheckBox>()
        val selectTags = ArrayList<String>()
        val selectTagsPosition = ArrayList<Int>()
        val dpMargin = DpUtils.dp2px(margins)//layout左右margin
        val dp8 = DpUtils.dp2px( 8f)//checkBox左右间距
        val pL = autoNewLine.paddingLeft
        val pR = autoNewLine.paddingRight
        val spaceWidth = autoNewLine.horizontalSpace*(tabNum-1)
        val tabWidth = ((Screen.width - pL-pR-spaceWidth)/tabNum).toInt()
        val tabHeight = DpUtils.dp2px( 33f)
        val layoutParams = LinearLayout.LayoutParams(tabWidth, tabHeight)
        list.forEachIndexed {index,tagName->
            val btn = CheckBox(context)
            btn.apply {
                this.layoutParams = layoutParams
                text = tagName
                gravity = Gravity.CENTER
                setButtonDrawable(0)
                setPadding(dp8, 0, dp8, 0)
                setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.gray_red_text_selector
                    )
                )
                setBackgroundResource(R.drawable.gray_lightred_bg_selector)
                setOnCheckedChangeListener{ _: CompoundButton, b: Boolean ->
                    if (b) {
                        selectTags.add(tagName)
                        selectTagsPosition.add(index)
                    }else{
                        selectTags.remove(tagName)
                        selectTagsPosition.remove(index)
                    }
                    cb.invoke(selectTagsPosition,selectTags)
                }
            }
            listBtns.add(btn)
            autoNewLine.addView(btn)
        }

    }
}