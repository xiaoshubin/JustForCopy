package com.smallcake.temp.ui

import android.os.Bundle
import android.util.Log
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityPdfBinding
import com.smallcake.temp.utils.DownloadUtils
import com.tencent.smtt.export.external.TbsCoreSettings
import me.jessyan.progressmanager.body.ProgressInfo
import java.io.File


/**
 * 1.引入控件sdk
//腾讯tas
implementation 'com.tencent.tbs:tbssdk:44085'
//一行进度监听器
implementation 'me.jessyan:progressmanager:1.5.0'
2.引入两个自定义控件
* @see DownloadUtils
* @see com.smallcake.temp.weight.SuperFileView
3.在MyApplication种初始化
// 腾讯tbs优化:在调用TBS初始化、创建WebView之前进行如下配置
val map = HashMap<String, Any>()
map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
QbSdk.initTbsSettings(map)
QbSdk.initX5Environment(this,object :QbSdk.PreInitCallback{
override fun onCoreInitFinished() {
L.e("onCoreInitFinished")
}
override fun onViewInitFinished(b: Boolean) {
L.e("onViewInitFinished:$b")
}
})

注意：superFileView.onStopDisplay()释放应该放在页面结束或者pop关闭时

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
                Log.e("TAG","下载成功的路径：$downloadPath")
                bind.superFileView.displayFile(File(downloadPath))
            }

            override fun onDownloading(progress: ProgressInfo?) {
                Log.e("TAG","进度："+progress?.percent+"%")
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