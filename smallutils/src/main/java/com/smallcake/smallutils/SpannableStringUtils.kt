package com.smallcake.smallutils

import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.smallcake.smallutils.SpannableStringUtils.Builder

/**
 * Date: 2019/11/20
 * author: SmallCake
 * 多媒体文本工具类
 */
class SpannableStringUtils private constructor() {
    class Builder(private var text: CharSequence) {
        private val defaultValue = 0x12000000
        private var flag: Int

        @ColorInt
        private var foregroundColor: Int

        @ColorInt
        private var backgroundColor: Int

        @ColorInt
        private var radiusBackgroundColor: Int

        @ColorInt
        private var radiusBgTxtColor = 0

        @ColorInt
        private var quoteColor: Int
        private var isLeadingMargin = false
        private var first = 0
        private var rest = 0
        private var isBullet = false
        private var gapWidth = 0
        private var bulletColor = 0
        private var bgRadius = 0
        private var proportion: Float
        private var xProportion: Float
        private var isStrikethrough = false
        private var isUnderline = false
        private var isSuperscript = false
        private var isSubscript = false
        private var isBold = false
        private var isItalic = false
        private var isBoldItalic = false
        private var fontFamily: String? = null
        private var align: Layout.Alignment? = null
        private var imageIsBitmap = false
        private var bitmap: Bitmap? = null
        private var imageIsDrawable = false
        private var drawable: Drawable? = null
        private var imageIsUri = false
        private var uri: Uri? = null
        private var imageIsResourceId = false

        @DrawableRes
        private var resourceId = 0
        private var clickSpan: ClickableSpan? = null
        private var url: String? = null
        private var isBlur = false
        private var radius = 0f
        private var style: Blur? = null
        private val mBuilder: SpannableStringBuilder

        /**
         * 设置标识
         *
         * @param flag
         *
         * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
         *
         * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
         *
         * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
         *
         * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
         *
         * @return [Builder]
         */
        fun setFlag(flag: Int): Builder {
            this.flag = flag
            return this
        }

        /**
         * 设置前景色
         *
         * @param color 前景色
         * @return [Builder]
         */
        fun setForegroundColor(@ColorInt color: Int): Builder {
            foregroundColor = color
            return this
        }

        /**
         * 设置背景色
         *
         * @param color 背景色
         * @return [Builder]
         */
        fun setBackgroundColor(@ColorInt color: Int): Builder {
            backgroundColor = color
            return this
        }

        /**
         * 设置圆角背景色
         *
         * @param color 背景色
         * @param txtColor 文本颜色
         * @param radius 圆角幅度
         * @return [Builder]
         */
        fun setRadiusBgColor(
            @ColorInt color: Int,
            @ColorInt txtColor: Int,
            radius: Int
        ): Builder {
            radiusBackgroundColor = color
            bgRadius = radius
            radiusBgTxtColor = txtColor
            return this
        }

        /**
         * 设置引用线的颜色
         *
         * @param color 引用线的颜色
         * @return [Builder]
         */
        fun setQuoteColor(@ColorInt color: Int): Builder {
            quoteColor = color
            return this
        }

        /**
         * 设置缩进
         *
         * @param first 首行缩进
         * @param rest  剩余行缩进
         * @return [Builder]
         */
        fun setLeadingMargin(
            first: Int,
            rest: Int
        ): Builder {
            this.first = first
            this.rest = rest
            isLeadingMargin = true
            return this
        }

        /**
         * 设置列表标记
         *
         * @param gapWidth 列表标记和文字间距离
         * @param color    列表标记的颜色
         * @return [Builder]
         */
        fun setBullet(
            gapWidth: Int,
            color: Int
        ): Builder {
            this.gapWidth = gapWidth
            bulletColor = color
            isBullet = true
            return this
        }

        /**
         * 设置字体比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setProportion(proportion: Float): Builder {
            this.proportion = proportion
            return this
        }

        /**
         * 设置字体横向比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setXProportion(proportion: Float): Builder {
            xProportion = proportion
            return this
        }

        /**
         * 设置删除线
         *
         * @return [Builder]
         */
        fun setStrikethrough(): Builder {
            isStrikethrough = true
            return this
        }

        /**
         * 设置下划线
         *
         * @return [Builder]
         */
        fun setUnderline(): Builder {
            isUnderline = true
            return this
        }

        /**
         * 设置上标
         *
         * @return [Builder]
         */
        fun setSuperscript(): Builder {
            isSuperscript = true
            return this
        }

        /**
         * 设置下标
         *
         * @return [Builder]
         */
        fun setSubscript(): Builder {
            isSubscript = true
            return this
        }

        /**
         * 设置粗体
         *
         * @return [Builder]
         */
        fun setBold(): Builder {
            isBold = true
            return this
        }

        /**
         * 设置斜体
         *
         * @return [Builder]
         */
        fun setItalic(): Builder {
            isItalic = true
            return this
        }

        /**
         * 设置粗斜体
         *
         * @return [Builder]
         */
        fun setBoldItalic(): Builder {
            isBoldItalic = true
            return this
        }

        /**
         * 设置字体
         *
         * @param fontFamily 字体
         *
         *
         * monospace
         *
         * serif
         *
         * sans-serif
         *
         * @return [Builder]
         */
        fun setFontFamily(fontFamily: String?): Builder {
            this.fontFamily = fontFamily
            return this
        }

        /**
         * 设置对齐
         *
         *
         * [Layout.Alignment.ALIGN_NORMAL]正常
         *
         * [Layout.Alignment.ALIGN_OPPOSITE]相反
         *
         * [Layout.Alignment.ALIGN_CENTER]居中
         *
         *
         * @return [Builder]
         */
        fun setAlign(align: Layout.Alignment?): Builder {
            this.align = align
            return this
        }

        /**
         * 设置图片
         *
         * @param bitmap 图片位图
         * @return [Builder]
         */
        fun setBitmap(bitmap: Bitmap): Builder {
            this.bitmap = bitmap
            imageIsBitmap = true
            return this
        }

        /**
         * 设置图片
         *
         * @param drawable 图片资源
         * @return [Builder]
         */
        fun setDrawable(drawable: Drawable): Builder {
            this.drawable = drawable
            imageIsDrawable = true
            return this
        }

        /**
         * 设置图片
         *
         * @param uri 图片uri
         * @return [Builder]
         */
        fun setUri(uri: Uri): Builder {
            this.uri = uri
            imageIsUri = true
            return this
        }

        /**
         * 设置图片
         *
         * @param resourceId 图片资源id
         * @return [Builder]
         */
        fun setResourceId(@DrawableRes resourceId: Int): Builder {
            this.resourceId = resourceId
            imageIsResourceId = true
            return this
        }

        /**
         * 设置点击事件
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         * @param clickSpan 点击事件
         * @return [Builder]
         */
        fun setClickSpan(clickSpan: ClickableSpan): Builder {
            this.clickSpan = clickSpan
            return this
        }

        /**
         * 设置超链接
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         *
         * @param url 超链接
         * @return [Builder]
         */
        fun setUrl(url: String): Builder {
            this.url = url
            return this
        }

        /**
         * 设置模糊
         *
         * 尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊
         *
         * 推荐还是把所有字体都模糊这样使用
         *
         * @param radius 模糊半径（需大于0）
         * @param style  模糊样式
         *
         * @return [Builder]
         */
        fun setBlur(
            radius: Float,
            style: Blur?
        ): Builder {
            this.radius = radius
            this.style = style
            isBlur = true
            return this
        }

        /**
         * 追加样式字符串
         *
         * @param text 样式字符串文本
         * @return [Builder]
         */
        fun append(text: CharSequence): Builder {
            setSpan()
            this.text = text
            return this
        }

        /**
         * 创建样式字符串
         *
         * @return 样式字符串
         */
        fun create(): SpannableStringBuilder {
            setSpan()
            return mBuilder
        }

        /**
         * 设置样式
         */
        private fun setSpan() {
            val start = mBuilder.length
            mBuilder.append(text)
            val end = mBuilder.length
            if (foregroundColor != defaultValue) {
                mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
                foregroundColor = defaultValue
            }
            if (backgroundColor != defaultValue) {
                mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
                backgroundColor = defaultValue
            }
            if (radiusBackgroundColor != defaultValue) {
                mBuilder.setSpan(
                    RadiusBackgroundSpan(
                        radiusBackgroundColor,
                        radiusBgTxtColor,
                        bgRadius
                    ), start, end, flag
                )
                radiusBackgroundColor = defaultValue
            }
            if (isLeadingMargin) {
                mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
                isLeadingMargin = false
            }
            if (quoteColor != defaultValue) {
                mBuilder.setSpan(QuoteSpan(quoteColor), start, end, 0)
                quoteColor = defaultValue
            }
            if (isBullet) {
                mBuilder.setSpan(BulletSpan(gapWidth, bulletColor), start, end, 0)
                isBullet = false
            }
            if (proportion != -1f) {
                mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
                proportion = -1f
            }
            if (xProportion != -1f) {
                mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
                xProportion = -1f
            }
            if (isStrikethrough) {
                mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
                isStrikethrough = false
            }
            if (isUnderline) {
                mBuilder.setSpan(UnderlineSpan(), start, end, flag)
                isUnderline = false
            }
            if (isSuperscript) {
                mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
                isSuperscript = false
            }
            if (isSubscript) {
                mBuilder.setSpan(SubscriptSpan(), start, end, flag)
                isSubscript = false
            }
            if (isBold) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
                isBold = false
            }
            if (isItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
                isItalic = false
            }
            if (isBoldItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
                isBoldItalic = false
            }
            if (fontFamily != null) {
                mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
                fontFamily = null
            }
            if (align != null) {
                mBuilder.setSpan(AlignmentSpan.Standard(align!!), start, end, flag)
                align = null
            }
            if (imageIsBitmap || imageIsDrawable || imageIsUri || imageIsResourceId) {
                if (imageIsBitmap) {
                    mBuilder.setSpan(ImageSpan(SmallUtils.context!!, bitmap!!), start, end, flag)
                    bitmap = null
                    imageIsBitmap = false
                } else if (imageIsDrawable) {
                    mBuilder.setSpan(ImageSpan(drawable!!), start, end, flag)
                    drawable = null
                    imageIsDrawable = false
                } else if (imageIsUri) {
                    mBuilder.setSpan(ImageSpan(SmallUtils.context!!, uri!!), start, end, flag)
                    uri = null
                    imageIsUri = false
                } else {
                    mBuilder.setSpan(
                        ImageSpan(SmallUtils.context!!, resourceId),
                        start,
                        end,
                        flag
                    )
                    resourceId = 0
                    imageIsResourceId = false
                }
            }
            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag)
                clickSpan = null
            }
            if (url != null) {
                mBuilder.setSpan(URLSpan(url), start, end, flag)
                url = null
            }
            if (isBlur) {
                mBuilder.setSpan(MaskFilterSpan(BlurMaskFilter(radius, style)), start, end, flag)
                isBlur = false
            }
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        }

        init {
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            foregroundColor = defaultValue
            backgroundColor = defaultValue
            quoteColor = defaultValue
            radiusBackgroundColor = defaultValue
            proportion = -1f
            xProportion = -1f
            mBuilder = SpannableStringBuilder()
        }
    }



    companion object {
        /**
         * 获取建造者
         *
         * @return [Builder]
         */
        fun getBuilder(text: CharSequence): Builder {
            return Builder(text)
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}


/**
 * 背景带圆角，可设置颜色，角度
 * @param mColor     背景颜色
 * @param mTxtColor  文字颜色
 * @param mRadius    圆角半径
 */
class RadiusBackgroundSpan(@param:ColorInt private val mColor: Int, @param:ColorInt private val mTxtColor: Int, private val mRadius: Int) : ReplacementSpan() {
    private var mSize = 0
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        mSize = (paint.measureText(text, start, end) + 2 * mRadius).toInt()
        //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
        //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
        //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
        return mSize
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.color = mColor //设置背景颜色
        paint.isAntiAlias = true // 设置画笔的锯齿效果
        val oval = RectF(x, y + paint.ascent(), x + mSize, y + paint.descent())
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(oval,mRadius.toFloat(),mRadius.toFloat(), paint) //绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.color = mTxtColor //恢复画笔的文字颜色
        canvas.drawText(text, start, end, x + mRadius, y.toFloat(), paint) //绘制文字
    }

}
/**
 * 使用DSL扩展来实现富文本
 * 1.它是TextView的一个扩展函数
 * 2.它的内部是 DSL 风格的代码
 * 3.它的每段文字都有设置颜色 & 点击事件的函数
 *
 * 包含：
 * 三个实现类
 * @see DslSpannableStringBuilderImpl
 * @see DslSpanBuilderImpl
 *
 * 参考：
 * https://blog.csdn.net/benhuo931115/article/details/51069373
 *
 */
class DslSpannableStringBuilderImpl  {
    private val builder = SpannableStringBuilder()
    //记录上次添加文字后最后的索引值
    private var lastIndex: Int = 0

     fun addText(text: String, method: (DslSpanBuilderImpl.() -> Unit)?=null) {
        val start = lastIndex
        builder.append(text)
        lastIndex += text.length
        val spanBuilder = DslSpanBuilderImpl()
        method?.let { spanBuilder.it() }
        spanBuilder.apply {
            //默认超链接也不显示下划线
            onClickSpan?.let {
                builder.setSpan(it, start, lastIndex, flag)
                if (!isUnderLine) {builder.setSpan(object : UnderlineSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ds.linkColor
                        ds.isUnderlineText = false
                    }}, start, lastIndex, flag)}
            }

            //前景色,背景色，圆角背景色，引用线颜色
            foregroundColorSpan?.let {builder.setSpan(it, start, lastIndex, flag)}
            backgroundColorSpan?.let {builder.setSpan(it, start, lastIndex, flag)}
            radiusBgColorSpan?.let {builder.setSpan(it, start, lastIndex, flag)}
            quoteSpan?.let {builder.setSpan(it, start, lastIndex, flag)}

            //缩进
            leadingMarginSpan?.let { builder.setSpan(it, start, lastIndex, flag) }
            //列表标记
            bulletSpan?.let { builder.setSpan(it, start, lastIndex, flag) }
            //删除线，下划线，上标，下标
            if (isDelLine) builder.setSpan(StrikethroughSpan(), start, lastIndex, flag)
            if (isUnderLine) builder.setSpan(UnderlineSpan(), start, lastIndex, flag)
            if (isTopTag) builder.setSpan(SuperscriptSpan(), start, lastIndex, flag)
            if (isBottomTag) builder.setSpan(SubscriptSpan(), start, lastIndex, flag)

            //缩小与放大
            if (proportion != -1f) builder.setSpan(RelativeSizeSpan(proportion), start, lastIndex, flag)
            if (xProportion != -1f) builder.setSpan(ScaleXSpan(xProportion), start, lastIndex, flag)
            //粗体，斜体，字体,对其方式,模糊
            if (isBold)builder.setSpan(StyleSpan(Typeface.BOLD), start, lastIndex, flag)
            if (isItalic)builder.setSpan(StyleSpan(Typeface.ITALIC), start, lastIndex, flag)
            fontFamily?.let { builder.setSpan(TypefaceSpan(fontFamily), start, lastIndex, flag) }
            align?.let { builder.setSpan(AlignmentSpan.Standard(it), start, lastIndex, flag) }
            blurFilter?.let { builder.setSpan(MaskFilterSpan(it), start, lastIndex, flag) }
            //图片
            bitmap?.let { builder.setSpan(ImageSpan(it), start, lastIndex, flag) }
            drawable?.let { builder.setSpan(ImageSpan(it), start, lastIndex, flag) }
            uri?.let { builder.setSpan(ImageSpan(SmallUtils.context!!,it), start, lastIndex, flag) }
            resourceId?.let { builder.setSpan(ImageSpan(SmallUtils.context!!,it), start, lastIndex, flag) }
            //超连接
            linkUrl?.let { builder.setSpan(URLSpan(it), start, lastIndex, flag) }

        }
    }

    fun build(): SpannableStringBuilder {
        return builder
    }
}
 class DslSpanBuilderImpl  {
     /**
      * 文本标记
      * 它是用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
      * @see Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)、
      * @see Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)、
      * @see Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)、
      * @see Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)
      */
     var flag: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
     /**
      * 前景色：文本颜色
      */
     var foregroundColorSpan: ForegroundColorSpan? = null
     /**
      * 文本背景颜色
      */
     var backgroundColorSpan: BackgroundColorSpan? = null
     /**
      * 圆角背景色
      */
      var radiusBgColorSpan: RadiusBackgroundSpan? = null
     /**
      * 引用线条色:会添加文字前面的一条竖线
      */
      var quoteSpan: QuoteSpan? = null
     /**
      * 列表标记
      */
      var bulletSpan: BulletSpan? = null
     /**
      * 点击事件
      */
      var onClickSpan: ClickableSpan? = null
     /**
      * 缩进
      */
      var leadingMarginSpan: LeadingMarginSpan? = null
     /**
      * 是否需要下划线
      */
      var isUnderLine = false
     /**
      * 是否需要删除线
      */
      var isDelLine = false
     /**
      * 是否需要上标
      * 效果：文字移动到上面
      * 从中线开始上移
      */
      var isTopTag = false
     /**
      * 是否需要下标
      * 效果：文字移动到下面
      * 从最下面开始下移
      */
      var isBottomTag = false

     /**
      * 整体缩放比例 0.5f（缩小到原来的一半） 2f（放大到原来的一倍）
      */
      var proportion: Float =-1f
     /**
      * X轴方向缩放比例 0.5f（缩小到原来的一半） 2f（放大到原来的一倍）
      * 效果：变扁
      */
      var xProportion: Float =-1f

     /**
      * 粗体
      */
     var isBold = false
     /**
      * 斜体
      */
     var isItalic = false
     /**
      * 字体
      */
     var fontFamily: String? = null
     /**
      * 对齐方式
      * [Layout.Alignment.ALIGN_NORMAL]正常
      * [Layout.Alignment.ALIGN_OPPOSITE]相反
      * [Layout.Alignment.ALIGN_CENTER]居中
      */
     var align: Layout.Alignment?=null

     /**
      * 添加图片
      * 缺点：图片的大小无法控制
      */
     var bitmap: Bitmap? = null
     var drawable: Drawable? = null
     var uri: Uri? = null
     @DrawableRes
     var resourceId:Int?=null

     /**
      * 超连接(网址，电话)
      * 类型一：网址，应该是以http开头的网址如：http://www.baidu.com
      * 类型二：电话，应该是以tel开头的电话如：tel:13800138000
      */
     var linkUrl: String? = null

     /**
      * 模糊字体:调用如下方法设置
      * @see setBlur
      */
     var blurFilter:BlurMaskFilter?=null



    fun setColor(@ColorInt color: Int) {
        foregroundColorSpan = ForegroundColorSpan(color)
    }
    fun setBgColor(@ColorInt color: Int) {
        backgroundColorSpan = BackgroundColorSpan(color)
    }
    fun setRadiusBgColor(@ColorInt color: Int,@ColorInt txtColor: Int,radius: Int) {
        radiusBgColorSpan = RadiusBackgroundSpan(color,txtColor,radius)
    }
    fun setQuoteColor(@ColorInt color: Int) {
        quoteSpan = QuoteSpan(color)
    }
     /**
      *
      * @param color Int        线条颜色
      * @param stripeWidth Int  线条宽度  （Android9及以上有效）
      * @param gapWidth Int     间隙宽度：线条和文本之间的间隙（Android9及以上有效）
      */
    fun setQuoteColor(@ColorInt color: Int, stripeWidth:Int, gapWidth:Int) {
         quoteSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             QuoteSpan(color,stripeWidth,gapWidth)
         }else{
             QuoteSpan(color)
         }
    }
    fun onClick( onClick: (View) -> Unit) {
        onClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick(widget)
            }
        }
    }

     /**
      * 设置缩进:写文章有用
      * @param first 首行缩进
      * @param rest  剩余行缩进
      */
     fun setLeadingMargin(first: Int, rest: Int){
         leadingMarginSpan = LeadingMarginSpan.Standard(first, rest)
     }

     /**
      * 设置列表标记
      * @param gapWidth        列表标记和文字间距离
      * @param color           列表标记的颜色
      * @param bulletRadius    列表标记的圆角（Android9及以上有效）
      * 注意：如果文本包含换行符，标记无效
      * 如果拼接的文本包含换行符，标记无效
      * 效果：文字的前面加个圆
      */
     fun setBullet(gapWidth: Int, color: Int, bulletRadius:Int) {
         bulletSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             BulletSpan(gapWidth,color,bulletRadius)
         }else{
             BulletSpan(gapWidth,color)
         }

     }

     /**
      * 设置模糊
      * 尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊
      * 推荐还是把所有字体都模糊这样使用
      * @param radius 模糊半径（需大于0）
      * @param style  模糊样式
      */
     fun setBlur(radius: Float, style: Blur?) {
         blurFilter = BlurMaskFilter(radius,style)
     }

}

//为TextView添加富文本扩展函数
fun TextView.buildSpannableString(init:DslSpannableStringBuilderImpl.()->Unit){
    //具体实现类
    val spanStringBuilderImpl = DslSpannableStringBuilderImpl()
    spanStringBuilderImpl.init()
    //此方法在需要响应用户事件时使用
    movementMethod = LinkMovementMethod.getInstance()
    //通过实现类返回SpannableStringBuilder
    text = spanStringBuilderImpl.build()
}