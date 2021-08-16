package com.smallcake.temp.weight

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ScrollerCompat
import com.smallcake.smallutils.DpPxUtils
import com.smallcake.smallutils.Screen
import com.smallcake.temp.utils.L
import kotlin.math.sqrt


open class LuckPan: View {

    private val lampNum = 36//灯泡个数
    private val lampSize = 10f//灯泡半径
    private val lampMargin = 8f//灯泡距离边框距离
    private val lampPadding = 16f//灯泡距离抽奖区块距离
    private val lampSpace = lampSize*2+lampMargin+lampPadding//灯泡位置占用

    private val bg2Size = 8f//第二层背景宽度


    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG)//背景大圆盘
    private val paintBg2 = Paint(Paint.ANTI_ALIAS_FLAG)//背景大圆盘2
    private val paintLamp = Paint(Paint.ANTI_ALIAS_FLAG)//背景大圆盘的灯泡（黄白灯）
    private val paintArc = Paint(Paint.ANTI_ALIAS_FLAG)//奖品区块
    private val panNum = 6
     var InitAngle  = -30f//初始化开始绘制角度
    private var verPanRadius  = 0f
    private var diffRadius  = 0f

    private var mDetector: GestureDetectorCompat? = null//手势触摸
     val scroller: ScrollerCompat? = null//滚动

    //旋转一圈所需要的时间
    private val ONE_WHEEL_TIME: Long = 500
    val FLING_VELOCITY_DOWNSCALE = 4


    constructor(context: Context?):super(context){}
    constructor(context: Context?,attrs: AttributeSet?):super(context,attrs){}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {}


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initView()

    }


    private fun initView() {
        mDetector = GestureDetectorCompat(context, RotatePanGestureListener(this))

        verPanRadius = 360f / panNum
        diffRadius = verPanRadius /2f


        paintBg.apply {
            color = Color.parseColor("#FFBE04")
        }
        paintBg2.apply {
            color = Color.parseColor("#FF9000")
        }
        paintLamp.apply {
            strokeWidth=1f
            color = Color.parseColor("#ffffff")
        }
        paintArc.apply {
            color = Color.parseColor("#ffffff")
        }

        isClickable = true


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width/2f
        val cy = height/2f
        val radius = width.coerceAtMost(height) /2f
        val radius2 = radius-lampSpace+bg2Size
        //1.绘制背景圆环
        canvas.drawCircle(cx,cy,radius,paintBg)
        canvas.drawCircle(cx,cy,radius2,paintBg2)
        //2.绘制霓虹灯
        drawLamp(canvas)
        //3.绘制奖品扇形区块
        drawLuckArc(canvas)
        //4.绘制奖品名称文字

        //5.绘制向上的三角形
        drawTrangle(canvas)
        //6.绘制中间圆圈按钮
        drawLuckCenter(canvas)
    }




    /**
     * 绘制灯泡
     */
    private fun drawLamp(canvas: Canvas) {
        val cx = width/2f
        val cy = lampSize+lampMargin
        val ratate = 360f/lampNum//灯泡之间的角度
        //评分的份数
        for (i in 0 until lampNum) {
            paintLamp.color = Color.parseColor(if (i%2==0)"#ffffff" else "#F53030")
            paintLamp.style = if (i%2==0) Paint.Style.FILL else Paint.Style.STROKE
            canvas.drawCircle(cx,cy,if (i%2==0) lampSize else lampSize/3*2, paintLamp)
            canvas.rotate(ratate, width / 2f, height / 2f)
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
        var angle: Float = if (panNum % 4 == 0) InitAngle else InitAngle - diffRadius
        for (i in 0 until panNum) {
            paintArc.color = Color.parseColor(if (i%2==0)"#ffffff" else "#F53030")
            canvas.drawArc(rectF, angle, verPanRadius, true, paintArc)
            angle += verPanRadius
        }
    }

    /**
     * 绘制三角形箭头
     */
    private fun drawTrangle(canvas: Canvas) {
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
        val radius = width.coerceAtMost(height) /6f
        canvas.drawCircle(cx,cy,radius,paintBg2)
        canvas.drawCircle(cx,cy,radius-4,paintBg)
        //绘制小灯泡
        val cxS = width/2f
        val cyS =  width.coerceAtMost(height) /2f-radius+8
        val ratate = 360f/lampNum//灯泡之间的角度
        //评分的份数
        for (i in 0 until lampNum) {
            paintLamp.color = Color.parseColor("#ffffff")
            paintLamp.style = Paint.Style.FILL
            canvas.drawCircle(cxS,cyS,2f, paintLamp)
            canvas.rotate(ratate, width / 2f, height / 2f)
        }
        canvas.drawCircle(cx,cy,radius-14,paintLamp)
        canvas.drawCircle(cx,cy,radius-16,paintBg2)

    }





    open fun setRotate(rotation: Int) {
        var rotation = rotation
        rotation = (rotation % 360 + 360) % 360
        InitAngle = rotation.toFloat()
        ViewCompat.postInvalidateOnAnimation(this)
    }
    /**
     * 开始转动
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
      fun startRotate(pos: Int) {

        //Rotate lap.
        var lap = (Math.random() * 12).toInt() + 4

        //Rotate angle.
        var angle = 0
        if (pos < 0) {
            angle = (Math.random() * 360).toInt()
        } else {
            val initPos: Int = queryPosition()
            if (pos > initPos) {
                angle = ((pos - initPos) * verPanRadius).toInt()
                lap -= 1
                angle = 360 - angle
            } else if (pos < initPos) {
                angle = ((initPos - pos) * verPanRadius).toInt()
            } else {
                //nothing to do.
            }
        }

        //All of the rotate angle.
        val increaseDegree = lap * 360 + angle
        val time: Long = (lap + angle / 360) * ONE_WHEEL_TIME
        var DesRotate = (increaseDegree + InitAngle).toInt()

        //为了每次都能旋转到转盘的中间位置
        val offRotate = (DesRotate % 360 % verPanRadius).toInt()
        DesRotate -= offRotate
        DesRotate += diffRadius.toInt()
        val animtor = ValueAnimator.ofInt(InitAngle.toInt(), DesRotate)
        animtor.interpolator = AccelerateDecelerateInterpolator()
        animtor.duration = time
        animtor.addUpdateListener { animation ->
            val updateValue = animation.animatedValue as Int
            InitAngle = ((updateValue % 360 + 360) % 360).toFloat()
            ViewCompat.postInvalidateOnAnimation(this)
        }
//        animtor.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {
//                super.onAnimationEnd(animation)
//                if ((parent as LuckPanLayout).getAnimationEndListener() != null) {
//                    (parent as LuckPanLayout).setStartBtnEnable(true)
//                    (parent as LuckPanLayout).setDelayTime(LuckPanLayout.DEFAULT_TIME_PERIOD)
//                    (parent as LuckPanLayout).getAnimationEndListener()
//                        .endAnimation(queryPosition())
//                }
//            }
//        })
        animtor.start()
    }
     fun queryPosition(): Int {
        InitAngle = (InitAngle % 360 + 360) % 360
        var pos = (InitAngle / verPanRadius).toInt()
        if (panNum == 4) pos++
        return calcumAngle(pos)
    }
     fun calcumAngle(pos: Int): Int {
        var pos = pos
        pos = if (pos >= 0 && pos <= panNum / 2) {
            panNum / 2 - pos
        } else {
            panNum - pos + panNum / 2
        }
        return pos
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val consume: Boolean = mDetector?.onTouchEvent(event)?:false
        if (consume) {
            parent.parent.requestDisallowInterceptTouchEvent(true)
            return true
        }
        return super.onTouchEvent(event)
    }

}
class RotatePanGestureListener(private val luckPan: LuckPan) : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return super.onDown(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent,e2: MotionEvent,distanceX: Float,distanceY: Float): Boolean {
        L.e("onScroll x:$distanceX y:$distanceY")
        val centerX: Float = (luckPan.left + luckPan.right) * 0.5f
        val centerY: Float = (luckPan.top + luckPan.bottom) * 0.5f
        val scrollTheta: Float = vectorToScalarScroll(distanceX, distanceY, e2.x - centerX, e2.y -centerY)
        val rotate = (luckPan.InitAngle -scrollTheta.toInt() / luckPan.FLING_VELOCITY_DOWNSCALE).toInt()
        luckPan.setRotate(rotate)
        return true
    }
    //Touch了滑动一点距离后，up时触发。
    override fun onFling(e1: MotionEvent,e2: MotionEvent,velocityX: Float,velocityY: Float): Boolean {
        L.e("onFling x:$velocityX y:$velocityY")
        val centerX: Float = (luckPan.left + luckPan.right) * 0.5f
        val centerY: Float = (luckPan.top + luckPan.bottom) * 0.5f
        val scrollTheta: Float = vectorToScalarScroll(velocityX, velocityY, e2.x - centerX, e2.y -centerY)
        luckPan.scroller?.abortAnimation()
        luckPan.scroller?.fling(
            0, luckPan.InitAngle.toInt(), 0, scrollTheta.toInt() / luckPan.FLING_VELOCITY_DOWNSCALE,
            0, 0, Int.MIN_VALUE, Int.MAX_VALUE
        )
        return true
    }


    private fun vectorToScalarScroll(dx: Float, dy: Float, x: Float, y: Float): Float {
        val l = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        val crossX = -y
        val dot = crossX * dx + x * dy
        val sign = Math.signum(dot)
        return l * sign
    }
}