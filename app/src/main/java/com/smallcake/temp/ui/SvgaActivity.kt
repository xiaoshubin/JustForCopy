package com.smallcake.temp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.opensource.svgaplayer.SVGACache
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivitySvgaBinding
import com.smallcake.temp.utils.L
import java.io.File

/**
 * svga动画演示
 * 1.引入包：
//SVAG动画库
implementation 'com.github.yyued:SVGAPlayer-Android:2.6.1'
 2.在MyApplication初始化
//必须在使用 SVGAParser 单例前初始化
SVGAParser.shareParser().init(this)
//SVGAParser 依赖 URLConnection, URLConnection 使用 HttpResponseCache 处理缓存
val cacheDir = File(applicationContext.cacheDir, "svga")
HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
 3.获取单例解析器
/**
 * 获取svga解析器
 * @return SVGAParser
*/
fun getSVGAParser(): SVGAParser {
    return SVGAParser.shareParser()
}
 */
class SvgaActivity : BaseBindActivity<ActivitySvgaBinding>() {
    private val TAG = "SvgaActivity"


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("SVGA")
        var index=0
        val list = listOf(
            "https://biubiu-static-1306772580.file.myqcloud.com/gifts/SVIP/ayzc.svga",
            "https://biubiu-static-1306772580.file.myqcloud.com/gifts/SVIP/axzj.svga",
            "https://biubiu-static-1306772580.file.myqcloud.com/gifts/SVIP/lmcb.svga",
            "https://biubiu-static-1306772580.file.myqcloud.com/gifts/SVIP/mghc.svga",
        )
        bind.btnPlay.setOnClickListener{

            val url = list[index]
            showSvgaCache(url)
            if (index<3){
                index++
            }else index=0



        }
    }

    /**
     * 显示svga动画
     * @param svgUrl String
     */
    private fun showSvga(svgUrl:String){
        MyApplication.instance.getSVGAParser().decodeFromURL(java.net.URL(svgUrl),object : SVGAParser.ParseCompletion{
            override fun onComplete(videoItem: SVGAVideoEntity) {
                L.e("svga 完成")
                SVGACache.buildCacheKey(svgUrl)
                bind.svgaView.apply {
                    setVideoItem(videoItem)
                    stepToFrame(0, true)
                    loops = 1
                    clearsAfterDetached = true
                    fillMode = SVGAImageView.FillMode.Clear
                }
            }
            override fun onError() {
                L.e("svga 异常")
            }

        })
    }

    /**
     * 从缓存中读取svga动画
     * @param svgUrl String
     */
    private fun showSvgaCache(svgUrl:String){
       val isCache =  SVGACache.isCached(svgUrl)
        Log.e(TAG,"[${svgUrl}]是否缓存：$isCache")
        if (!isCache){
            showSvga(svgUrl)
        }else MyApplication.instance.getSVGAParser().decodeFromSVGAFileCacheKey(svgUrl,object : SVGAParser.ParseCompletion{
            override fun onComplete(videoItem: SVGAVideoEntity) {

                L.e("svga 完成")
                bind.svgaView.apply {
                    setVideoItem(videoItem)
                    stepToFrame(0, true)
                    loops = 1
                    clearsAfterDetached = true
                    fillMode = SVGAImageView.FillMode.Clear
                }
            }
            override fun onError() {
                L.e("svga 异常")
            }

        },object :SVGAParser.PlayCallback{
            override fun onPlay(file: List<File>) {
                L.e("svga 播放的文件：$file")
            }

        })
    }
}