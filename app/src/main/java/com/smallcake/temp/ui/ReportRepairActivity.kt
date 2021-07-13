package com.smallcake.temp.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.smallcake.smallutils.DpPxUtils
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityReportRepairBinding
import com.smallcake.temp.utils.GlideEngine
import com.smallcake.temp.utils.L
import com.smallcake.temp.utils.ldd
import com.yx.jiading.adapter.GridImageAdapter

class ReportRepairActivity : BaseBindActivity<ActivityReportRepairBinding>() {
    private val imageMaxCount = 3 //图片最大上传数量
    private val mInsertImageAdapter = GridImageAdapter(imageMaxCount)
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("报事报修")
        bar.menuTextView.apply {
            text ="历史记录"
            setTextColor(ContextCompat.getColor(this@ReportRepairActivity,R.color.titleColor))

        }
        initView()
        onEvent()
    }

    private fun onEvent() {
        bind.btnReport.setOnClickListener{
            val imgs = mInsertImageAdapter.getDataList()
            ldd("要上传的图片：${imgs.size}")

        }

    }

    private fun initView() {
        initQuestionType()
        initImg()
    }

    private fun initImg() {
        mInsertImageAdapter.setOnInsertImageListener(object :
            GridImageAdapter.OnInsertImageListener {
            override fun onInsertImage() {
                checkPermission()
            }
        })
        bind.recyclerView.apply {
            layoutManager = GridLayoutManager(this@ReportRepairActivity,3)
            adapter = mInsertImageAdapter
        }


    }
    fun checkPermission() {
        if (XXPermissions.isGrantedPermission(this, Permission.MANAGE_EXTERNAL_STORAGE)) {
            getPhoto()
        } else {
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            getPhoto()
                        } else {
                            ldd("获取部分权限成功,但部分权限未正常授予")
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(
                                this@ReportRepairActivity,
                                permissions
                            )
                        } else {
                            ldd("获取权限失败")
                        }
                    }

                })
        }
    }
    fun getPhoto() {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage()) //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .loadImageEngine(GlideEngine.createGlideEngine()) //// 外部传入图片加载引擎，必传项
            .isWeChatStyle(true) // 是否开启微信图片选择风格
            .maxSelectNum(imageMaxCount - mInsertImageAdapter.getDataList().size) // 最大图片选择数量
            .imageSpanCount(3) // 每行显示个数
            .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
            .compressQuality(80) // 图片压缩后输出质量 0~ 100
            .cutOutQuality(90) // 裁剪输出质量 默认100
            .minimumCompressSize(100) // 小于多少kb的图片不压缩
            //.isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);不压缩不裁剪有效,默认处理
            .isCompress(true) // 是否压缩
            .isEnableCrop(false) // 是否裁剪
            .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
            .synOrAsy(false) //同步true或异步false 压缩 默认同步
            .isPreviewImage(false) // 是否可预览图片
            .isCamera(true) // 是否显示拍照按钮
            .withAspectRatio(1, 1) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
            .hideBottomControls(true) // 是否显示uCrop工具栏，默认不显示
            .freeStyleCropEnabled(true) // 裁剪框是否可拖拽
            .circleDimmedLayer(false) // 是否圆形裁剪
            .showCropFrame(false) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
            //                .cropImageWideHigh(120,120)// 裁剪宽高比，设置如果大于图片本身宽高则无效
            .rotateEnabled(false) // 裁剪是否可旋转图片
            .scaleEnabled(false) // 裁剪是否可放大缩小图片
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    // 结果回调
                    L.e("选择图片结果回调")
                    for (media in result) {
                        L.e("是否压缩:" + media.isCompressed)
                        L.e("压缩:" + media.compressPath)
                        L.e("原图:" + media.path)
                        L.e("绝对路径:" + media.realPath)
                        L.e("是否裁剪:" + media.isCut)
                        L.e("裁剪:" + media.cutPath)
                        L.e("是否开启原图:" + media.isOriginal)
                        L.e("原图路径:" + media.originalPath)
                        L.e("Android Q 特有Path:" + media.androidQToPath)
                        L.e("宽高: " + media.width + "x" + media.height)
                        L.e("Size: " + media.size)
                        var url1: String = "" //裁剪后的头像
                        url1 = if (TextUtils.isEmpty(media.androidQToPath)) {
                            if (TextUtils.isEmpty(media.compressPath)) {
                                L.e("没有压缩用绝对路径图：" + media.realPath)
                                media.realPath
                            } else {
                                L.e("压缩了,用压缩图：" + media.compressPath)
                                media.compressPath
                            }
                        } else {
                            L.e("收到 Android q 路径 =" + media.androidQToPath)
                            media.androidQToPath
                        }
                        mInsertImageAdapter.insertImage(url1)
                    }
                }

                override fun onCancel() {
                    // 取消
                    L.e("没有选择图片")
                }
            })
    }

    private fun initQuestionType() {
        val list = listOf(
            "安装空调", "维修下水", " 维修水阀", " 疏通管道",
            "安装空调", "维修下水", " 维修水阀", " 疏通管道"
        )
        val dp8 = DpPxUtils.dp2px( 8f).toInt()
        val tabWidth = (Screen.width - DpPxUtils.dp2px( 48f)) / 4
        val layoutParams =
            LinearLayout.LayoutParams(tabWidth.toInt(), DpPxUtils.dp2px( 33f).toInt())
        list.forEach {
            val btn = CheckBox(this)
            btn.apply {
                this.layoutParams = layoutParams
                text = it
                setButtonDrawable(0)
                setPadding(dp8, 0, dp8, 0)
                setTextColor(
                    ContextCompat.getColorStateList(
                        this@ReportRepairActivity,
                        R.color.gray_red_text_selector
                    )
                )
                setBackgroundResource(R.drawable.gray_lightred_bg_selector)
            }
            bind.autoNewLine.addView(btn)
        }
    }
}