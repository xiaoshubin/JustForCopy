package com.smallcake.temp.ui

import android.app.ProgressDialog
import android.os.Bundle
import com.smallcake.smallutils.FormatUtils
import com.smallcake.smallutils.SdUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityDownloadDataBinding
import com.smallcake.temp.utils.AppUtils
import com.smallcake.temp.utils.DownloadUtils
import com.smallcake.temp.utils.L
import com.smallcake.temp.utils.showToast
import me.jessyan.progressmanager.body.ProgressInfo


/**
 * 下载页面
 */
class DownloadDataActivity : BaseBindActivity<ActivityDownloadDataBinding>() {
    private lateinit var  progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
       bar.setTitle("下载")
       onEvent()

         progressDialog = ProgressDialog(this).apply {
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setMessage("更新进度")
        }

    }

    private fun onEvent() {
        bind.btnDownload.setOnClickListener{
            progressDialog.show()
            val url = "https://down.qq.com/qqweb/QQ_1/android_apk/Android_8.8.23.6010_537092388.apk"
            val path = SdUtils.getAppCachePath()
            val name = "qq.apk"
            DownloadUtils.download(url,path,name,(object :
                DownloadUtils.OnDownloadListener {
                override fun onDownloadSuccess() {
                    progressDialog.dismiss()
                    val successDownloadApkPath: String = path + name
                    L.e("已下载到${successDownloadApkPath}开始安装...")
                    showToast("已下载到${successDownloadApkPath}开始安装...")
                    AppUtils.installApk(this@DownloadDataActivity, successDownloadApkPath)
                }

                override fun onDownloading(progressInfo: ProgressInfo?) {
                    progressDialog.progress = progressInfo?.percent?:0
                    val finish = progressInfo?.isFinish?:false
                    if (!finish) {
                        val speed = progressInfo?.speed?:0
                        val speedStr = FormatUtils.formatSize(speed)
                        progressDialog.setMessage("下载速度($speedStr/s)")
                    } else {
                        progressDialog.setMessage("下载完成！")
                    }
                }

                override fun onDownloadFailed() {
                    progressDialog.dismiss()
                }


            }))
        }
    }
}