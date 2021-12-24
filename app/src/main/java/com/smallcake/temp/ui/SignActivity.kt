package com.smallcake.temp.ui

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.content.ContextCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.px
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivitySignBinding
import com.smallcake.temp.utils.L
import com.smallcake.temp.weight.SignatureView
import java.io.File

/**
 * 手写签名
 * @see SignatureView 签名视图
 */
class SignActivity : BaseBindActivity<ActivitySignBinding>() {
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.backTextView.text = "签名资料"
        bar.backTextView.setPadding(32.px,0,0,0)
        bar.backTextView.setTextColor(ContextCompat.getColor(baseContext, R.color.tv_blue))
        bind.signView.setPenColor(ContextCompat.getColor(baseContext, R.color.black))
        bind.signView.setPentWidth(4)
        bind.signView.setOnTouch(object : SignatureView.OnTouc{
            override fun onTouch() {
                if(bind.tvTips.visibility == View.VISIBLE){
                    bind.tvTips.visibility = View.GONE
                }
            }
        })
        setOnclick()
    }

    private fun setOnclick() {
        bind.tvCancel.setOnClickListener {
            bind.signView.clear()
        }
        bind.tvConfirm.setOnClickListener {
            checkPermission()
        }
    }

    fun checkPermission() {
        if (XXPermissions.isGrantedPermission(this, Permission.MANAGE_EXTERNAL_STORAGE)) {
            L.e("有读写权限,直接操作")
            saveImg()
        } else {
            L.e("没有读写权限,去申请")
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (all) {
                            L.e("获取文件读写权限成功")
                            saveImg()

                        } else {
                            L.e("获取部分权限失败")
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        if (never) {
                            L.e("被永久拒绝授权，请手动授予文件读写权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(this@SignActivity, permissions)
                        } else {
                            L.e("获取权限失败")
                        }
                    }

                })
        }
    }

    /**
     * 图片保存到本地然后上传
     */
    fun saveImg() {
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.getAbsolutePath() + File.separator + "sign_" + System.currentTimeMillis() + ".jpg"
        L.e("path=${path}")
        bind.signView.save(path, false, 100)
        //保存成功后要干嘛？要不要先上传到服务器再关闭页面
//        dataProvider.common.uploadImage(getImgBody(File(path)))
//            .sub({
//                L.e("上传成功${it.data}")//上传成功UploadImgBean(cmpImgName=null, cmpImgUrl=null, imgName=sign_1622719280612.jpg, imgUrl=upload/2021-06/2b99b3a4058940f39e37e89602cc1691.jpg)
//                val intent = Intent()
//                intent.putExtra(Constant.SIGN_URL, it.data?.imgUrl)
//                setResult(RESULT_OK, intent)
//                finish()
//            },dialog = dialog)


    }
}