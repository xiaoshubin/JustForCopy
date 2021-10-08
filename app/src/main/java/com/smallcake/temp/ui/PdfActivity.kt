package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityPdfBinding
import com.smallcake.temp.utils.DownloadUtils
import com.smallcake.temp.utils.L
import me.jessyan.progressmanager.body.ProgressInfo
import java.io.File


/**

//PDF查看器
implementation 'es.voghdev.pdfviewpager:library:1.1.2'
//腾讯tas
implementation 'com.tencent.tbs:tbssdk:44085'
 腾讯tbs支持打开文件格式: doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub
 接入参考：https://www.jianshu.com/p/3f57d640b24d
 */
class PdfActivity : BaseBindActivity<ActivityPdfBinding>() {
    private val mUrl = "http://testing.cloudjoytech.com.cn:50011/upload/2021-09/04f700dcb0634d6e959887f02e10789d.pdf"
    private val wordUrl = "http://testing.cloudjoytech.com.cn:50011//upload/2021-09/a760e230757b4203879d6fe994c74d2e.docx"

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("腾讯TBS查看器")
        DownloadUtils.download(mUrl,object :DownloadUtils.OnDownloadListener{
            override fun onDownloadSuccess(downloadPath:String) {
                L.e("下载成功的路径：$downloadPath")
                bind.superFileView.displayFile(File(downloadPath))
            }

            override fun onDownloading(progress: ProgressInfo?) {
                L.e("进度："+progress?.percent+"%")
            }
            override fun onDownloadFailed() {
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        bind.superFileView.onStopDisplay()
    }
}