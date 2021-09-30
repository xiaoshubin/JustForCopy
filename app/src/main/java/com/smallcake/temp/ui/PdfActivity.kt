package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityPdfBinding
import com.smallcake.temp.utils.L
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import es.voghdev.pdfviewpager.library.util.FileUtil
import java.lang.Exception


/**

//PDF查看器
implementation 'es.voghdev.pdfviewpager:library:1.1.2'
 */
class PdfActivity : BaseBindActivity<ActivityPdfBinding>(), DownloadFile.Listener {
    private val mUrl = "http://testing.cloudjoytech.com.cn:50011/upload/2021-09/04f700dcb0634d6e959887f02e10789d.pdf"
    private val wordUrl = "http://testing.cloudjoytech.com.cn:50011//upload/2021-09/a760e230757b4203879d6fe994c74d2e.docx"

    private var remotePDFViewPager:RemotePDFViewPager?=null
    private var adapter:PDFPagerAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("PDF查看器")
        setDownloadListener()
    }

    /*设置监听*/
    private fun setDownloadListener() {
         remotePDFViewPager = RemotePDFViewPager(this, mUrl, this)
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        L.e("onSuccess:下载PDF url:${url}  path:$destinationPath")
        adapter = PDFPagerAdapter(this@PdfActivity, FileUtil.extractFileNameFromURL(url))
        remotePDFViewPager?.adapter = adapter
        bind.layoutRoot.addView(remotePDFViewPager)
    }

    override fun onFailure(e: Exception?) {
        L.e("onFailure:下载PDF:${e?.message}")
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        L.e("onProgressUpdate:下载PDF:$progress/$total")
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.close()
    }
}