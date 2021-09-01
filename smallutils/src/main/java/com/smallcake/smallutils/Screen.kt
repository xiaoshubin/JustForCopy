package com.smallcake.smallutils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity


/**
 * 屏幕工具
 */
object Screen {

    /**
     * 获取屏幕实际高度（也包含虚拟导航栏）
     * @return
     */
    val height: Int
        get() {
            val displayMetrics = DisplayMetrics()
            val windowManager =
                SmallUtils.context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    /**
     * 获取屏幕实际宽度
     * @return
     */
    val width: Int
        get() {
            val displayMetrics = DisplayMetrics()
            val windowManager =
                SmallUtils.context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

    /**
     * 获取电量栏高度
     *
     * @return
     */
    val statusHeight: Int
        get() {
            var result = 0
            val resourceId: Int = SmallUtils.context?.resources?.getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
            ) ?: 24
            if (resourceId > 0) {
                result = SmallUtils.context?.resources?.getDimensionPixelSize(resourceId) ?: 0
            }
            return result
        }

    /**
     * 屏幕截图
     * @param activity
     * @param hasStatus 是否包含状态栏，默认：是
     * @return bitmap
     * 注意：禁止某些页面截图，可以设置安全标记
     *  window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
     */
    fun screenShot(activity: Activity, hasStatus: Boolean = true): Bitmap? {
        val view = activity.window.decorView
        val width: Int = width
        val height: Int = height
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache ?: return null
        var bp: Bitmap = if (!hasStatus) {
            val frame = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        } else Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bp
    }

    /**
     * 截取控件视图
     * @param view View
     * @return Bitmap?
     */
    fun screenShotView(view: View): Bitmap? {
        view.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        val b = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        view.buildDrawingCache()
        return b
    }


    /**
     * 设置屏幕透明度
     * @param context
     * @param alpha
     */
    fun setWindowBackgroundAlpha(
        context: Activity,
        @FloatRange(from = 0.0, to = 1.0) alpha: Float
    ) {
        val lp = context.window.attributes
        lp.alpha = alpha //0.0-1.0
        context.window.attributes = lp
    }

    /**
     * 获取底部菜单栏高度
     */
    val navigationBarHeight: Int
        get() {
            var result = 0
            val res: Resources = SmallUtils.context!!.resources
            val resourceId: Int = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
            return result
        }

    /**
     * 判断是否显示了导航栏
     * @param context  一定要是activity的context 否则类型转换失败
     * @return
     */
    fun isShowNavBar(activity: Activity): Boolean {
        //获取应用区域高度
        val outRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(outRect)
        val activityHeight = outRect.height()
        //屏幕物理高度 减去 状态栏高度
        val remainHeight: Int = height - statusHeight
        //剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
        return activityHeight != remainHeight
    }

}