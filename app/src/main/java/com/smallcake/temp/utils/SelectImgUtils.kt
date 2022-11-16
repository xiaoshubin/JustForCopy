package com.smallcake.temp.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.px
import com.smallcake.temp.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Date:2021/7/14 13:38
 * Author:SmallCake
 * Desc:用于选择多张图片
 * 1.可删除选择的图片
 * 2.限制最大图片选择数量
 * 3.点击已选图片查看大图
 **/
object SelectImgUtils {
    private const val TAG = "SelectImgUtils"

    /**
     * 绑定一个RecyclerView用于显示图片选择
     * @param activity AppCompatActivity
     * @param recyclerView RecyclerView
     * @param maxCount Int 图片数量，默认九张
     * @param lineImgNum Int 每一排显示的图片数据
     * @param cb Function1<MutableList<ImgSelectBean>?, Unit>
     */
    fun bindRecyclerView(
        activity: AppCompatActivity,
        recyclerView: RecyclerView,
        maxCount: Int = 9,
        lineImgNum: Int=3,
        cb: (List<ImgSelectBean>?) -> Unit
    ) {
        //清理数据，避免重复显示
        val mAdapter = ImgSelectAdapter(lineImgNum)
        mAdapter.addData(ImgSelectBean(isAdd = true))

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, lineImgNum)
            adapter = mAdapter
        }

        mAdapter.apply {
            addChildClickViewIds(R.id.iv_add, R.id.iv_show, R.id.iv_del)
            setOnItemChildClickListener { adapter, view, position ->
                val item = adapter.getItem(position) as ImgSelectBean
                when (view.id) {
                    R.id.iv_add -> checkPermission(activity, mAdapter,maxCount,cb)
                    R.id.iv_show -> PopShowUtils.showBigPic(view as ImageView, File(item.path))
                    R.id.iv_del -> {
                        removeAt(position)
                        val list = mAdapter.data.filter { !it.isAdd }
                        cb.invoke(list)
                        if (!haveAddImg(mAdapter)) mAdapter.addData(ImgSelectBean(isAdd = true))
                    }
                }

            }

        }

    }

    /**
     * 是否已经有了添加图片
     * @return Boolean
     */
    private fun haveAddImg(mAdapter: ImgSelectAdapter): Boolean {
        val list = mAdapter.data.filter { it.isAdd }
        return list.sizeNull() > 0
    }

    /**
     * 权限检测，要拍照和选择图片需要用到
     * @param activity AppCompatActivity
     */
    private fun checkPermission(
        activity: AppCompatActivity,
        mAdapter: ImgSelectAdapter,
        maxCount: Int,
        cb: (List<ImgSelectBean>?) -> Unit
    ) {
        val permissions = arrayOf(Permission.MANAGE_EXTERNAL_STORAGE, Permission.CAMERA)
        if (XXPermissions.isGrantedPermission(activity, permissions)) {
            getPhoto(activity, mAdapter,maxCount,cb)
        } else {
            XXPermissions.with(activity)
                .permission(permissions)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            getPhoto(activity, mAdapter, maxCount, cb)
                        } else {
                            Log.e("TAG","获取部分权限成功,但部分权限未正常授予")
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions)
                        } else {
                            Log.e("TAG","获取权限失败")
                        }
                    }

                })
        }
    }

    /**
     * 根据适配器选择图片数量，确定还能选择的图片数量
     * @param activity AppCompatActivity
     */
    private fun getPhoto(
        activity: AppCompatActivity,
        mAdapter: ImgSelectAdapter,
        maxCount: Int,
        cb: (List<ImgSelectBean>?) -> Unit
    ) {
        PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(maxCount - mAdapter.data.filter { !it.isAdd }.sizeNull())
            .setImageSpanCount(3)
            .isDisplayCamera(true)// 是否显示拍照按钮
            .isPreviewImage(false)//不能预览，避免本来想选中，
            .setCompressEngine(LuBanCompressEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    for (media in result) {
                        printFileInfo(media)
                        val filePath = if (media.isCompressed)media.compressPath else media.realPath
                        //添加到已经选择的图片末尾，不包括最后的添加图片位置
                        val list = mAdapter.data.filter { !it.isAdd }
                        mAdapter.addData(list.size, ImgSelectBean(filePath))
                    }
                    //添加图片完毕后，回调给页面选择的图片结果，如果已经选择了最大图片数，移除最后的添加图片
                    val list = mAdapter.data.filter { !it.isAdd }
                    cb.invoke(list)
                    if (list.sizeNull() == maxCount) mAdapter.removeAt(list.size)
                }

                override fun onCancel() {
                    showToast("取消了图片选择")
                }
            })
    }

    /**
     * 打印选择的文件信息
     * @param media LocalMedia
     */
    private fun printFileInfo(media: LocalMedia) {
        Log.i(TAG,
            "是否压缩:" + media.isCompressed +
                    "\n压缩:" + media.compressPath +
                    "\n原图:" + media.path +
                    "\n绝对路径:" + media.realPath +
                    "\n是否裁剪:" + media.isCut +
                    "\n裁剪:" + media.cutPath +
                    "\n是否开启原图:" + media.isOriginal +
                    "\n原图路径:" + media.originalPath +
                    "\n宽高: " + media.width + "x" + media.height +
                    "\n图片大小: " + media.size
        )
    }

    /**
     * 选择所有类型的文件，如pdf,doc,jpeg,png,gif,mp3,mp4,
     * 具体看：
     */
    fun selectFile(activity:AppCompatActivity, cb: (String?) -> Unit){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        val startActivityLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val file = uriToFileQ(activity,it!!)
                Log.e("TAG","uri:${file}")
                cb(file?.path)
            }

//            val data = it.data
//            //是否正确返回
//            if (it.resultCode == Activity.RESULT_OK){
//                val uri = data?.data
//
//                Log.e("TAG","${it.resultCode} ==data:${uri?.path}")
//                if (data==null){
//                    cb(it)
//                }else{
//                    cb(null)
//                }
//            }else if (it.resultCode == Activity.RESULT_CANCELED){
//                showToast("已取消")
//            }

        }
        startActivityLauncher.launch(arrayOf("image/*","text/plain"))

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = "${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))}"
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null
}


/**
 * 图片选择适配器
 */
private class ImgSelectAdapter(lineImgNum: Int) : BaseQuickAdapter<ImgSelectBean, BaseViewHolder>(R.layout.item_img_selecter) {
    private val spaceWidth = 16.px //RecyclerView左右margin的总和
    private val layoutParams = LinearLayoutCompat.LayoutParams(
        (Screen.width - spaceWidth) / lineImgNum,
        (Screen.width - spaceWidth) / lineImgNum
    )

    override fun convert(holder: BaseViewHolder, item: ImgSelectBean) {
        val isAdd = item.isAdd
        //布局动态换算
        val layoutSelect = holder.getView<FrameLayout>(R.id.layout_select)
        layoutSelect.layoutParams =layoutParams
        //根据是否是添加图片来显示和隐藏图片，添加图片，删除按钮
        val addImgIv = holder.getView<ImageFilterView>(R.id.iv_add)
        val iv = holder.getView<ImageFilterView>(R.id.iv_show)
        val ivDel = holder.getView<ImageFilterView>(R.id.iv_del)
        addImgIv.visibility = isAdd.visiable()
        iv.visibility = isAdd.visiableReverse()
        ivDel.visibility = isAdd.visiableReverse()
        //有图片就显示
        if (!TextUtils.isEmpty(item.path)) iv.load(File(item.path))
    }
}

/**
 * 图片选择类
 * @property path String 选择图片后的文件路径
 * @property isAdd Boolean 是否是添加图片
 */
data class ImgSelectBean(val path: String = "", var isAdd: Boolean = false)