package com.smallcake.temp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCamreLayoutBinding
import com.smallcake.temp.weight.CameraPreview
import com.smallcake.temp.weight.OverCameraView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author
 */
class CameraActivity : BaseBindActivity<ActivityCamreLayoutBinding>(), View.OnClickListener {
    /**
     * 相机预览
     */
    private var mPreviewLayout: FrameLayout? = null

    /**
     * 拍摄按钮视图
     */
    private var mPhotoLayout: RelativeLayout? = null

    /**
     * 确定按钮视图
     */
    private var mConfirmLayout: RelativeLayout? = null

    /**
     * 闪光灯
     */
    private var mFlashButton: ImageView? = null

    /**
     * 拍照按钮
     */
    private var mPhotoButton: ImageView? = null

    /**
     * 取消保存按钮
     */
    private var mCancleSaveButton: ImageView? = null

    /**
     * 保存按钮
     */
    private var mSaveButton: ImageView? = null

    /**
     * 聚焦视图
     */
    private var mOverCameraView: OverCameraView? = null

    /**
     * 相机类
     */
    private var mCamera: Camera? = null

    /**
     * Handle
     */
    private val mHandler = Handler()
    private var mRunnable: Runnable? = null

    /**
     * 取消按钮
     */
    private var mCancleButton: Button? = null

    /**
     * 是否开启闪光灯
     */
    private var isFlashing = false

    /**
     * 图片流暂存
     */
    private var imageData: ByteArray?=null

    /**
     * 拍照标记
     */
    private var isTakePhoto = false

    /**
     * 是否正在聚焦
     */
    private var isFoucing = false

    /**
     * 蒙版类型
     */
    private var mMongolianLayerType: MongolianLayerType? = null

    /**
     * 蒙版图片
     */
    private var mMaskImage: ImageView? = null

    /**
     * 护照出入境蒙版
     */
    private var mPassportEntryAndExitImage: ImageView? = null

    /**
     * 提示文案容器
     */
    private var rlCameraTip: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camre_layout)
        initView()
        setOnclickListener()
    }

    override fun onResume() {
        super.onResume()
        mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT)
        val preview = CameraPreview(this, mCamera)
        mOverCameraView = OverCameraView(this)
        mPreviewLayout!!.addView(preview)
        mPreviewLayout!!.addView(mOverCameraView)
    }

    /**
     * 注释：获取蒙版图片
     *
     * @return
     */
    private val maskImage: Int
        private get() {
            if (mMongolianLayerType == MongolianLayerType.BANK_CARD) {
                return R.mipmap.bank_card
            } else if (mMongolianLayerType == MongolianLayerType.HK_MACAO_TAIWAN_PASSES_POSITIVE) {
                return R.mipmap.hk_macao_taiwan_passes_positive
            } else if (mMongolianLayerType == MongolianLayerType.HK_MACAO_TAIWAN_PASSES_NEGATIVE) {
                return R.mipmap.hk_macao_taiwan_passes_negative
            } else if (mMongolianLayerType == MongolianLayerType.IDCARD_POSITIVE) {
                return R.mipmap.idcard_positive
            } else if (mMongolianLayerType == MongolianLayerType.IDCARD_NEGATIVE) {
                return R.mipmap.idcard_negative
            } else if (mMongolianLayerType == MongolianLayerType.PASSPORT_PERSON_INFO) {
                return R.mipmap.passport_person_info
            }
            return 0
        }

    /**
     * 注释：设置监听事件
     */
    private fun setOnclickListener() {
        mCancleButton!!.setOnClickListener(this)
        mCancleSaveButton!!.setOnClickListener(this)
        mFlashButton!!.setOnClickListener(this)
        mPhotoButton!!.setOnClickListener(this)
        mSaveButton!!.setOnClickListener(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                val x = event.x
                val y = event.y
                isFoucing = true
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView!!.setTouchFoucusRect(mCamera, autoFocusCallback, x, y)
                }
                mRunnable = Runnable {
                    Toast.makeText(this@CameraActivity, "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_SHORT)
                    isFoucing = false
                    mOverCameraView!!.isFoucuing = false
                    mOverCameraView!!.disDrawTouchFocusRect()
                }
                //设置聚焦超时
                mHandler.postDelayed(mRunnable!!, 3000)
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 注释：自动对焦回调
     */
    private val autoFocusCallback = Camera.AutoFocusCallback { success, camera ->
        isFoucing = false
        mOverCameraView!!.isFoucuing = false
        mOverCameraView!!.disDrawTouchFocusRect()
        //停止聚焦超时回调
        mHandler.removeCallbacks(mRunnable!!)
    }

    /**
     * 注释：拍照并保存图片到相册
     */
    private fun takePhoto() {
        isTakePhoto = true
        //调用相机拍照
        mCamera!!.takePicture(null, null, null, { data: ByteArray?, camera1: Camera? ->
            //视图动画
            mPhotoLayout!!.visibility = View.GONE
            mConfirmLayout!!.visibility = View.VISIBLE
            imageData = data
            //停止预览
            mCamera!!.stopPreview()
        })
    }

    /**
     * 注释：切换闪光灯
     */
    private fun switchFlash() {
        isFlashing = !isFlashing
        mFlashButton!!.setImageResource(if (isFlashing) R.mipmap.flash_open else R.mipmap.flash_close)
        try {
            val parameters = mCamera!!.parameters
            parameters.flashMode =
                if (isFlashing) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF
            mCamera!!.parameters = parameters
        } catch (e: Exception) {
            Toast.makeText(this, "该设备不支持闪光灯", Toast.LENGTH_SHORT)
        }
    }

    /**
     * 注释：取消保存
     */
    private fun cancleSavePhoto() {
        mPhotoLayout!!.visibility = View.VISIBLE
        mConfirmLayout!!.visibility = View.GONE
        //开始预览
        mCamera!!.startPreview()
        imageData = null
        isTakePhoto = false
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.cancle_button) {
            finish()
        } else if (id == R.id.take_photo_button) {
            if (!isTakePhoto) {
                takePhoto()
            }
        } else if (id == R.id.flash_button) {
            switchFlash()
        } else if (id == R.id.save_button) {
            savePhoto()
        } else if (id == R.id.cancle_save_button) {
            cancleSavePhoto()
        }
    }

    /**
     * 注释：蒙版类型
     */
    enum class MongolianLayerType {
        /**
         * 护照个人信息
         */
        PASSPORT_PERSON_INFO,

        /**
         * 护照出入境
         */
        PASSPORT_ENTRY_AND_EXIT,

        /**
         * 身份证正面
         */
        IDCARD_POSITIVE,

        /**
         * 身份证反面
         */
        IDCARD_NEGATIVE,

        /**
         * 港澳通行证正面
         */
        HK_MACAO_TAIWAN_PASSES_POSITIVE,

        /**
         * 港澳通行证反面
         */
        HK_MACAO_TAIWAN_PASSES_NEGATIVE,

        /**
         * 银行卡
         */
        BANK_CARD
    }

    /**
     * 注释：初始化视图
     */
    private fun initView() {
        mCancleButton = findViewById(R.id.cancle_button)
        mPreviewLayout = findViewById(R.id.camera_preview_layout)
        mPhotoLayout = findViewById(R.id.ll_photo_layout)
        mConfirmLayout = findViewById(R.id.ll_confirm_layout)
        mPhotoButton = findViewById(R.id.take_photo_button)
        mCancleSaveButton = findViewById(R.id.cancle_save_button)
        mSaveButton = findViewById(R.id.save_button)
        mFlashButton = findViewById(R.id.flash_button)
        mMaskImage = findViewById(R.id.mask_img)
        rlCameraTip = findViewById(R.id.camera_tip)
        mPassportEntryAndExitImage = findViewById(R.id.passport_entry_and_exit_img)
        if (mMongolianLayerType == null) {
            bind.maskImg.visibility = View.GONE
            bind.cameraTip.visibility = View.GONE
            return
        }
        //设置蒙版,护照出入境蒙版特殊处理
        if (mMongolianLayerType != MongolianLayerType.PASSPORT_ENTRY_AND_EXIT) {
            Glide.with(this).load(maskImage).into(bind.maskImg)
        } else {
            bind.maskImg.visibility = View.GONE
            bind.passportEntryAndExitImg.visibility = View.VISIBLE
        }

        mCancleButton?.visibility = View.GONE
        bind.flashButton.visibility = View.GONE
    }

    /**
     * 注释：保持图片
     */
    private fun savePhoto() {
        var fos: FileOutputStream? = null
        val cameraPath =
            Environment.getExternalStorageDirectory().path + File.separator + "DCIM" + File.separator + "Camera"
        //相册文件夹
        val cameraFolder = File(cameraPath)
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs()
        }
        //保存的图片文件
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val imagePath =
            cameraFolder.absolutePath + File.separator + "IMG_" + simpleDateFormat.format(
                Date()
            ) + ".jpg"
        val imageFile = File(imagePath)
        try {
            fos = FileOutputStream(imageFile)
            fos.write(imageData)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                    var retBitmap = BitmapFactory.decodeFile(imagePath)
                    retBitmap = setTakePicktrueOrientation(CameraInfo.CAMERA_FACING_FRONT, retBitmap)
                    saveBitmap(retBitmap, imagePath)
                    val intent = Intent()
                    intent.putExtra(KEY_IMAGE_PATH, imagePath)
                    setResult(RESULT_OK, intent)
                } catch (e: IOException) {
                    setResult(RESULT_FIRST_USER)
                    e.printStackTrace()
                }
            }
            finish()
        }
    }

    companion object {
        const val KEY_IMAGE_PATH = "imagePath"

        /**
         * 启动拍照界面
         *
         * @param activity
         * @param requestCode
         * @param type
         */
        fun startMe(activity: Activity,fragment: Fragment, requestCode: Int) {
            XXPermissions.with(activity)
                .permission(arrayListOf(Permission.CAMERA,Permission.MANAGE_EXTERNAL_STORAGE))
                .request(object :OnPermissionCallback{
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (!all)return
                        val intent = Intent(activity, CameraActivity::class.java)
                        fragment.startActivityForResult(intent, requestCode)
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        XXPermissions.startPermissionActivity(activity,permissions)
                    }

                })
        }

        /**
         * 注释：设置拍照图片正确方向
         *
         * @param id
         * @param bitmap
         * @return
         */
        fun setTakePicktrueOrientation(id: Int, bitmap: Bitmap): Bitmap {
            //如果返回的图片宽度小于高度，说明FrameWork层已经做过处理直接返回即可
            var bitmap = bitmap
            if (bitmap.width < bitmap.height) {
                return bitmap
            }
            val info = CameraInfo()
            Camera.getCameraInfo(id, info)
            bitmap = rotaingImageView(id, info.orientation, bitmap)
            return bitmap
        }

        /**
         * 把相机拍照返回照片转正
         *
         * @param angle 旋转角度
         * @return bitmap 图片
         */
        private fun rotaingImageView(id: Int, angle: Int, bitmap: Bitmap): Bitmap {
            //矩阵
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            //加入翻转 把相机拍照返回照片转正
            if (id == 1) {
                matrix.postScale(-1f, 1f)
            }
            // 创建新的图片
            return Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true
            )
        }

        /**
         * 注释：保存图片
         *
         * @param bitmap
         * @param path
         * @return
         */
        fun saveBitmap(bitmap: Bitmap, path: String?): Boolean {
            try {
                val file = File(path)
                val parent = file.parentFile
                if (!parent.exists()) {
                    parent.mkdirs()
                }
                val fos = FileOutputStream(file)
                val b = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                return b
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        bar.setImmersed(true)
    }
}