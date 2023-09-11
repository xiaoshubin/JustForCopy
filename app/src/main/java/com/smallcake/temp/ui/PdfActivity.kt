package com.smallcake.temp.ui

import android.os.Bundle
import android.webkit.WebViewClient
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityPdfBinding
import com.smallcake.temp.utils.TabUtils


/**
 腾讯tbs支持打开文件格式: doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub
 接入参考：https://www.jianshu.com/p/3f57d640b24d
 由于2023年4月腾讯已下架文档浏览功能，改为付费。故去掉tbs，改用ofd网址方式浏览

 */
class PdfActivity : BaseBindActivity<ActivityPdfBinding>() {
    private val mUrl = "https://ofd.xdocin.com/demo/fapiao.ofd"
    private val url1 = "https://view.xdocin.com/demo/view.docx"
    private val url2 = "http://www.doe.zju.edu.cn/_upload/article/files/a0/22/595bdc2b4ca28f90f51ca3b3ffc5/45c3e922-550f-45a2-8602-64b9c202b33e.pdf"

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("文档浏览器")
        TabUtils.initTabCreate(bind.tabLayout, listOf("ofd","docx","pdf")){index->
            when(index){
                0->loadUrl(mUrl)
                1->loadUrl(url1)
                2->loadUrl(url2)
            }
        }
        bind.webView.settings.apply {
            javaScriptEnabled=true
            domStorageEnabled=true
        }
        bind.webView.webViewClient = WebViewClient()
        loadUrl(mUrl)


    }
    private fun loadUrl(url:String){
        val isOfd = url.endsWith(".ofd")
        if (isOfd){
            val urlAll = "https://ofd.xdocin.com/view?src=${url}"
            bind.webView.loadUrl(urlAll)
            return
        }else{
            val urlAll = "https://view.xdocin.com/view?src=${url}"
            bind.webView.loadUrl(urlAll)
        }
    }


}