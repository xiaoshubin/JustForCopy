package com.smallcake.temp.weight

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.collection.ArrayMap
import androidx.collection.SparseArrayCompat
import java.io.IOException
import java.util.*

class CameraPreview(context: Context, private val mCamera: Camera?) : SurfaceView(
    context
), SurfaceHolder.Callback {
    private val mHolder: SurfaceHolder
    private var isPreview = false

    /**
     * 预览尺寸集合
     */
    private val mPreviewSizes = SizeMap()

    /**
     * 图片尺寸集合
     */
    private val mPictureSizes = SizeMap()

    /**
     * 屏幕旋转显示角度
     */
    private val mDisplayOrientation: Int

    /**
     * 设备屏宽比
     */
    private var mAspectRatio: AspectRatio
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            //设置设备高宽比
            mAspectRatio = getDeviceAspectRatio(context as Activity)
            //设置预览方向
            mCamera!!.setDisplayOrientation(90)
            val parameters = mCamera.parameters
            //获取所有支持的预览尺寸
            mPreviewSizes.clear()
            for (size in parameters.supportedPreviewSizes) {
                mPreviewSizes.add(Size(size.width, size.height))
            }
            //获取所有支持的图片尺寸
            mPictureSizes.clear()
            for (size in parameters.supportedPictureSizes) {
                mPictureSizes.add(Size(size.width, size.height))
            }
            val previewSize = chooseOptimalSize(mPreviewSizes.sizes(mAspectRatio))
            val pictureSize = mPictureSizes.sizes(mAspectRatio)!!
                .last()
            //设置相机参数
            parameters.setPreviewSize(previewSize!!.width, previewSize.height)
            parameters.setPictureSize(pictureSize.width, pictureSize.height)
            parameters.pictureFormat = ImageFormat.JPEG
            parameters.setRotation(90)
            mCamera.parameters = parameters
            //把这个预览效果展示在SurfaceView上面
            mCamera.setPreviewDisplay(holder)
            //开启预览效果
            mCamera.startPreview()
            isPreview = true
        } catch (e: IOException) {
            Log.e("CameraPreview", "相机预览错误: " + e.message)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (holder.surface == null) {
            return
        }
        //停止预览效果
        mCamera!!.stopPreview()
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(mHolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mCamera.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (mCamera != null) {
            if (isPreview) {
                //正在预览
                mCamera.stopPreview()
                mCamera.release()
            }
        }
    }

    /**
     * 注释：获取设备屏宽比
     */
    private fun getDeviceAspectRatio(activity: Activity): AspectRatio {
        val width = activity.window.decorView.width
        val height = activity.window.decorView.height
        return AspectRatio.of(height, width)
    }

    /**
     * 注释：选择合适的预览尺寸
     *
     * @param sizes
     * @return
     */
    private fun chooseOptimalSize(sizes: SortedSet<Size>?): Size? {
        val desiredWidth: Int
        val desiredHeight: Int
        val surfaceWidth = width
        val surfaceHeight = height
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight
            desiredHeight = surfaceWidth
        } else {
            desiredWidth = surfaceWidth
            desiredHeight = surfaceHeight
        }
        var result: Size? = null
        for (size in sizes!!) {
            if (desiredWidth <= size.width && desiredHeight <= size.height) {
                return size
            }
            result = size
        }
        return result
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private fun isLandscape(orientationDegrees: Int): Boolean {
        return orientationDegrees == 90 ||
                orientationDegrees == 270
    }

    /**
     * 注释：构造函数
     *
     * @param context
     * @param mCamera
     */
    init {
        mHolder = holder
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mDisplayOrientation = (context as Activity).windowManager.defaultDisplay.rotation
        mAspectRatio = AspectRatio.of(16, 9)
    }
}

/**
 * 注释：尺寸对象
 */
class Size(val width: Int, val height: Int) : Comparable<Size> {
    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        if (this === o) {
            return true
        }
        if (o is Size) {
            val size = o
            return width == size.width && height == size.height
        }
        return false
    }

    override fun toString(): String {
        return width.toString() + "x" + height
    }

    override fun hashCode(): Int {
        return height xor (width shl Integer.SIZE / 2 or (width ushr Integer.SIZE / 2))
    }

    override fun compareTo(another: Size): Int {
        return width * height - another.width * another.height
    }
}

class SizeMap {
    private val mRatios = ArrayMap<AspectRatio, SortedSet<Size>?>()
    fun add(size: Size): Boolean {
        for (ratio in mRatios.keys) {
            if (ratio.matches(size)) {
                val sizes = mRatios[ratio]
                return if (sizes!!.contains(size)) {
                    false
                } else {
                    sizes.add(size)
                    true
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        val sizes: SortedSet<Size> = TreeSet()
        sizes.add(size)
        mRatios[AspectRatio.of(size.width, size.height)] = sizes
        return true
    }

    fun remove(ratio: AspectRatio) {
        mRatios.remove(ratio)
    }

    fun ratios(): Set<AspectRatio> {
        return mRatios.keys
    }

    fun sizes(ratio: AspectRatio): SortedSet<Size>? {
        if (mRatios[ratio] != null) {
            return mRatios[ratio]
        }
        //如果找不到合适屏宽比，找最接近屏幕的
        var retRatio = ratio
        var diff = 1f
        for (size in ratios()) {
            if (Math.abs(ratio.toFloat() - size.toFloat()) < diff) {
                retRatio = size
                diff = Math.abs(ratio.toFloat() - size.toFloat())
            }
        }
        return mRatios[retRatio]
    }

    fun clear() {
        mRatios.clear()
    }

    val isEmpty: Boolean
        get() = mRatios.isEmpty
}

/**
 * 注释:屏宽比
 */
class AspectRatio private constructor(val x: Int, val y: Int) : Comparable<AspectRatio>,
    Parcelable {
    fun matches(size: Size): Boolean {
        val gcd = gcd(size.width, size.height)
        val x = size.width / gcd
        val y = size.height / gcd
        return this.x == x && this.y == y
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        if (this === o) {
            return true
        }
        if (o is AspectRatio) {
            val ratio = o
            return x == ratio.x && y == ratio.y
        }
        return false
    }

    override fun toString(): String {
        return "$x:$y"
    }

    fun toFloat(): Float {
        return x.toFloat() / y
    }

    override fun hashCode(): Int {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return y xor (x shl Integer.SIZE / 2 or (x ushr Integer.SIZE / 2))
    }

    override fun compareTo(another: AspectRatio): Int {
        if (equals(another)) {
            return 0
        } else if (toFloat() - another.toFloat() > 0) {
            return 1
        }
        return -1
    }

    /**
     * @return The inverse of this [AspectRatio].
     */
    fun inverse(): AspectRatio {
        return of(y, x)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(x)
        dest.writeInt(y)
    }

    companion object {
        private val sCache = SparseArrayCompat<SparseArrayCompat<AspectRatio>>(16)
        fun of(x: Int, y: Int): AspectRatio {
            var x = x
            var y = y
            val gcd = gcd(x, y)
            x /= gcd
            y /= gcd
            var arrayX = sCache[x]
            return if (arrayX == null) {
                val ratio = AspectRatio(x, y)
                arrayX = SparseArrayCompat()
                arrayX.put(y, ratio)
                sCache.put(x, arrayX)
                ratio
            } else {
                var ratio = arrayX[y]
                if (ratio == null) {
                    ratio = AspectRatio(x, y)
                    arrayX.put(y, ratio)
                }
                ratio
            }
        }

        fun parse(s: String): AspectRatio {
            val position = s.indexOf(':')
            require(position != -1) { "Malformed aspect ratio: $s" }
            return try {
                val x = s.substring(0, position).toInt()
                val y = s.substring(position + 1).toInt()
                of(x, y)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Malformed aspect ratio: $s", e)
            }
        }

        private fun gcd(a: Int, b: Int): Int {
            var a = a
            var b = b
            while (b != 0) {
                val c = b
                b = a % b
                a = c
            }
            return a
        }
        @JvmField val CREATOR: Parcelable.Creator<AspectRatio?> = object :
            Parcelable.Creator<AspectRatio?> {
            override fun createFromParcel(source: Parcel): AspectRatio? {
                val x = source.readInt()
                val y = source.readInt()
                return of(x, y)
            }

            override fun newArray(size: Int): Array<AspectRatio?> {
                return arrayOfNulls(size)
            }
        }
    }
}