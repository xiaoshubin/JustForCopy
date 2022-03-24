package com.smallcake.temp

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import androidx.activity.viewModels
import com.smallcake.smallutils.buildSpannableString
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityTestBinding
import com.smallcake.temp.module.MobileViewModule
import com.smallcake.temp.utils.showToast


class TestActivity : BaseBindActivity<ActivityTestBinding>() {


    override fun onCreate(savedInstanceState: Bundle?,bar: NavigationBar) {
        bar.setTitle("测试")
        val mobileViewModule:MobileViewModule by viewModels()
        bind.lifecycleOwner = this
        bind.viewmodel = mobileViewModule
        bind.btnGet1.setOnClickListener {
//            mobileViewModule.getPhoneResponse("18324138218","c95c37113391b9fff7854ce0eafe496d")
            bind.tvMsg.buildSpannableString {
                addText("我已详细阅读并同意"){setColor(Color.RED)}
                addText("《隐私政策》"){
                    setColor(Color.BLUE)
                    onClick{showToast("正在打开《隐私政策》")}
                }
                addText("\n背景颜色设置"){setBgColor(Color.RED)}
                addText("\n圆角背景颜色设置"){setRadiusBgColor(Color.BLUE,Color.WHITE,8)}
                addText("\n引用线颜色"){setQuoteColor(Color.RED,20,20)}
                addText("\n缩小一半"){ proportion=0.5f}
                addText("\nX方向放大一倍"){xProportion=2f}
                addText("设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进设置缩进\n"){setLeadingMargin(100,0)}
                addText("粗体"){isBold=true}
                addText("斜体"){isItalic=true}
                addText("粗斜体"){
                    isBold=true
                    isItalic=true
                }
                addText("\n对齐正常"){
                    align = Layout.Alignment.ALIGN_CENTER
                }
                addText("\n对齐中部"){
                    align = Layout.Alignment.ALIGN_CENTER
                }
                addText("\n对齐相反"){
                    align = Layout.Alignment.ALIGN_OPPOSITE
                }
                addText("\n正常模糊字体"){
                    setBlur(8F, BlurMaskFilter.Blur.NORMAL)
                }
                addText("\n边框模糊字体"){
                    setBlur(8F, BlurMaskFilter.Blur.SOLID)
                }
                addText("\n外部模糊字体"){
                    setBlur(8F, BlurMaskFilter.Blur.OUTER)
                }
                addText("\n内部模糊字体"){
                    setBlur(8F, BlurMaskFilter.Blur.INNER)
                }
                addText("图片"){
                    resourceId = R.mipmap.ic_add_img
                }
                addText("百度网址"){
                    linkUrl = "http://www.baidu.com"
                }
                addText("电话13800138000"){
                    linkUrl = "tel:13800138000"
                }

            }

        }
        bind.refreshLayout.setOnRefreshListener {
            mobileViewModule.getPhoneResponse("18324138218","c95c37113391b9fff7854ce0eafe496d")
        }

    }


}


