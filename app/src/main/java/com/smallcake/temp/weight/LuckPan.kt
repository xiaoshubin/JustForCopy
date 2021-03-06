package com.smallcake.temp.weight

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import com.smallcake.smallutils.px
import com.smallcake.temp.utils.showToast
import kotlin.math.roundToInt

/**
 * 抽奖大转盘
 * 参考：
 * https://github.com/Nipuream/LuckPan/blob/master/app/src/main/java/com/hr/nipuream/luckpan/view/RotatePan.java
 * https://blog.csdn.net/YanghuiNipurean/article/details/52251107
 * http://inloop.github.io/interpolator/
 * 【文字绘制】https://www.cnblogs.com/duoshou/articles/9071237.html
 *
 * 注意：大小最好在200dp-300dp之间
 */
open class LuckPan: View {


    private val panNum       = 6               //奖盘数量
    private var initAngle    = -120f           //初始化开始绘制角度（-60指向最后一块的中心位置，如果每块的角度为60）
    private val panRadius    = 360f / panNum   //圆盘根据盘数量平分后的角度（60）
    private val turnTime     = 3000L           //旋转所需要的时间
    private var panSize      = 200             //抽奖盘的大小，默认200           注意：设置宽高后会重新测量
    private var turnNum      = 6               //旋转的圈数                     注意：不能小于2圈
    private var luckPosition = 5               //中奖的位置(3代表转动到第四个盘)  注意：不能大于panNum-1

    private val lampNum      = 36                                   //灯泡个数
    private val lampSize     = 10f                                  //灯泡半径
    private val lampMargin   = 8f                                   //灯泡距离边框距离
    private val lampPadding  = 16f                                  //灯泡距离抽奖区块距离
    private val lampSpace    = lampSize*2+lampMargin+lampPadding    //灯泡位置占用
    private val lampAngle   = 360f/lampNum                          //灯泡之间的角度

    private val bg2Size      = 8f                                   //第二层背景宽度


    private val paintBg      = Paint(Paint.ANTI_ALIAS_FLAG)          //背景大圆盘（黄色）
    private val paintBg2     = Paint(Paint.ANTI_ALIAS_FLAG)          //背景大圆盘2（橙色）
    private val paintLamp    = Paint(Paint.ANTI_ALIAS_FLAG)          //背景大圆盘灯泡（黄白灯）
    private val paintArc     = Paint(Paint.ANTI_ALIAS_FLAG)          //奖品区块（红白色）
    private val paintText    = Paint(Paint.ANTI_ALIAS_FLAG)          //奖品区块文字（黄色）
    private val paintBtnText = TextPaint(Paint.ANTI_ALIAS_FLAG)      //中心按钮文字（白色）

    private val isOpenLamp = true                             //是否需要打开了霓虹灯交替
    private var isLight = false                               //灯泡是否点亮

    private var canClickCenter = true                         //是否可以点击中心区域，点击后禁用，避免转动中再次点击触发转盘
    private var isTouchDown = false                           //是否按下了中心按钮


    constructor(context: Context?):super(context){init()}
    constructor(context: Context?,attrs: AttributeSet?):super(context,attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {init()}


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initSize()
    }

    private fun init(){
        paintBg.apply {
            color = Color.parseColor("#FFBE04")//黄
        }
        paintBg2.apply {
            color = Color.parseColor("#FF9000")//橙
        }
        paintLamp.apply {
            strokeWidth=1f
            color = Color.parseColor("#ffffff")
        }
        paintArc.apply {
            color = Color.parseColor("#ffffff")
        }
        paintText.apply {
            color = Color.parseColor("#FFBE04")
            textSize = 18f.px
            letterSpacing = 0.2f
            isFakeBoldText = true
        }
        paintBtnText.apply {
            color = Color.WHITE
            textSize = 22f.px
            isFakeBoldText = true
        }
        isClickable = true
        //是否开灯
        if (isOpenLamp)mHandler.sendEmptyMessage(0)

    }
    private val mHandler = Handler(Looper.getMainLooper()){
        isLight = !isLight
        it.target.sendEmptyMessageDelayed(0,800)
        postInvalidate()
        false
    }

    private fun initSize() {
        panSize = width.coerceAtMost(height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width/2f
        val cy = height/2f
        val radius = panSize /2f
        val radius2 = radius-lampSpace+bg2Size
        //1.绘制背景圆环
        canvas.drawCircle(cx,cy,radius,paintBg)
        canvas.drawCircle(cx,cy,radius2,paintBg2)
        //2.绘制霓虹灯
        drawLamp(canvas)
        //3.绘制奖品扇形区块和奖品名称
        drawLuckArc(canvas)
        //4.绘制向上的三角形
        drawTriangle(canvas)
        //5.绘制中间圆圈按钮
        drawLuckCenter(canvas)
    }

    /**
     * 绘制灯泡
     */
    private fun drawLamp(canvas: Canvas) {
        val cx = width/2f
        val cy = lampSize+lampMargin
        //评分的份数
        for (i in 0 until lampNum) {
            paintLamp.color = Color.parseColor(if (isLight)"#ffffff" else "#F53030")
            paintLamp.style = if (i%2==0) Paint.Style.FILL else Paint.Style.STROKE
            canvas.drawCircle(cx,cy,if (i%2==0) lampSize else lampSize/3*2, paintLamp)
            canvas.rotate(lampAngle, width / 2f, height / 2f)
        }
    }

    /**
     * 绘制奖品区块
     */
    private fun drawLuckArc(canvas: Canvas) {
        val top = 0f+lampSpace
        val bottom = height.toFloat()-lampSpace
        val left = width/2f-height/2f+lampSpace
        val right = width/2f+height/2f-lampSpace
        val rectF = RectF(left,top, right,bottom)
        var angle: Float = initAngle
        for (i in 0 until panNum) {
            paintArc.color = Color.parseColor(if (i%2==0)"#F53030" else "#ffffff")
            canvas.drawArc(rectF, angle, panRadius, true, paintArc)

            paintText.color = Color.parseColor(if (i%2==0)"#ffffff" else "#F53030")
            drawText(angle,tranLunkName(i),canvas,rectF)
            angle += panRadius
        }
    }
    private fun tranLunkName(position:Int):String{
        return when(position){
            0 -> "一等奖"
            1 -> "二等奖"
            2 -> "三等奖"
            3 -> "四等奖"
            4 -> "五等奖"
            else -> "谢谢参与"
        }
    }

    /**
     * 绘制奖品文字
     */
    private  fun drawText(startAngle: Float,string: String, mCanvas: Canvas,mRange: RectF) {
        val path = Path()
        path.addArc(mRange, startAngle, panRadius)
        val textWidth = paintText.measureText(string)
        //圆弧的水平偏移
        val hOffset = (panSize * Math.PI / panNum / 2 - textWidth/2-8f ).toFloat()
        //圆弧的垂直偏移
        val vOffset = (panSize / 12).toFloat()

        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, paintText)
    }

    /**
     * 绘制三角形箭头
     */
    private fun drawTriangle(canvas: Canvas) {
        val pathLeft = Path()
        pathLeft.moveTo(width/2-30f,height/2f)
        pathLeft.lineTo(width/2f,lampSpace+60f)
        pathLeft.lineTo(width/2f,height/2f)
        pathLeft.close()
        canvas.drawPath(pathLeft,paintBg)
        val pathRight = Path()
        pathRight.moveTo(width/2+30f,height/2f)
        pathRight.lineTo(width/2f,lampSpace+60f)
        pathRight.lineTo(width/2f,height/2f)
        pathRight.close()
        canvas.drawPath(pathRight,paintBg2)
    }

    /**
     * 绘制中心按钮
     */
    private fun drawLuckCenter(canvas: Canvas) {
        val cx = width/2f
        val cy = height/2f
        val radius = panSize /6f
        //绘制橙黄圆
        canvas.drawCircle(cx,cy,radius,paintBg2)
        canvas.drawCircle(cx,cy,radius-4,paintBg)
        //绘制小灯泡
        val cxS = width/2f
        val cyS =  width.coerceAtMost(height) /2f-radius+8
        //绘制小灯泡，平分的份数
        for (i in 0 until lampNum) {
            paintLamp.color = Color.parseColor("#ffffff")
            paintLamp.style = Paint.Style.FILL
            canvas.drawCircle(cxS,cyS,2f, paintLamp)
            canvas.rotate(lampAngle, width / 2f, height / 2f)
        }
        //绘制白橙圆
        canvas.drawCircle(cx,cy,radius-14,paintLamp)
        canvas.drawCircle(cx,cy,radius-16,if (isTouchDown)paintBg else paintBg2)
        //绘制中心文字
        val str1 = "幸运"
        val str2 = "转盘"
        val textWidth = paintBtnText.measureText(str1)
        val fm = paintBtnText.fontMetricsInt
        val textHeight = fm.top -(fm.top-fm.ascent)-(fm.bottom-fm.descent)

        canvas.drawText(str1,cx-textWidth/2,cy-14,paintBtnText)
        canvas.drawText(str2,cx-textWidth/2,cy-textHeight,paintBtnText)

    }

    /**
     * 开始转动
     */
    private fun startRotate() {
        luckPosition = (Math.random() *panNum).toInt()

        initAngle=0f
        val randomJd = (Math.random() * (panRadius-5)).toInt() + 3
        val angle = luckPosition*panRadius//中奖区域需要转动的角度
        val pointDiffRotate: Int = (270-panRadius).roundToInt()+randomJd-angle.toInt()//开始位置和指针位置的偏移角度
        val desRotate: Int = turnNum * 360+pointDiffRotate
        val animator = ValueAnimator.ofInt(initAngle.toInt(), desRotate)
        animator.apply {
            interpolator = DecelerateInterpolator(1.1f)
            duration = turnTime
            addUpdateListener { animation ->
                val updateValue = animation.animatedValue as Int
                initAngle = ((updateValue % 360 + 360) % 360).toFloat()
                ViewCompat.postInvalidateOnAnimation(this@LuckPan)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    canClickCenter=true
                    val str="恭喜获得第${(luckPosition+1)}块扇形内的奖品"
                    showToast(str)
                }
            })
            start()
        }

    }

    /**
     * 触摸事件
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cx = width/2f
        val cy = height/2f
        val radius = panSize /6f
        val rectF = RectF(cx - radius/2,cy-radius/2,cx + radius/2,cy+radius/2)
        when(event.action){
            MotionEvent.ACTION_DOWN -> {//点击中心区域，开始抽奖
                if (rectF.contains(event.x,event.y)&&canClickCenter){
                    isTouchDown=true
                    canClickCenter = false
                    startRotate()

                }
            }

            MotionEvent.ACTION_UP -> {//抬起了手指
                isTouchDown=false
                postInvalidate()
            }
        }
        return super.onTouchEvent(event)
    }

}
