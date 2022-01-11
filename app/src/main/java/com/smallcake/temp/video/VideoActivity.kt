package com.smallcake.temp.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import coil.load
import coil.request.videoFrameMillis
import com.bumptech.glide.Glide
import com.hw.videoprocessor.VideoProcessor
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.smallcake.smallutils.FileUtils
import com.smallcake.smallutils.FormatUtils
import com.smallcake.smallutils.SdUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityVideoBinding
import com.smallcake.temp.ui.SingleVideoPlayActivity
import com.smallcake.temp.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat


/**
 * 1.视频选择
 * 2.视频显示
 * 3.视频压缩
 *
 */
@SuppressLint("SetTextI18n")
class VideoActivity : BaseBindActivity<ActivityVideoBinding>() {
    private var videoPath =""//本地视频
    private val url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"//网络视频
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("视频相关")
        initVideoPlayer()
        onEvent()
    }

    private fun initVideoPlayer() {
        //1.设置url地址
        bind.videoPlayer.setUp(url,true,"")
        //2.设置封面
        val img = ImageView(this)
        Glide.with(img).load(url).into(img)
        bind.videoPlayer.apply {
            fullscreenButton.setOnClickListener{
                bind.videoPlayer.startWindowFullscreen(this@VideoActivity,true,true)
            }
        }
        //3.外部辅助的旋转，帮助全屏
        val orientationUtils = OrientationUtils(this, bind.videoPlayer)
        orientationUtils.isEnable = false
        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption.setThumbImageView(img)
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(true)
            .setNeedLockFull(false)
            .setUrl(url)
            .setCacheWithPlay(true)
            .setVideoTitle("")
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils.isEnable = true
                }
                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[0]) //title
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[1]) //当前非全屏player
                    orientationUtils.backToProtVideo()
                }
            }).setLockClickListener { view, lock ->
                orientationUtils.isEnable = !lock
            }.build(bind.videoPlayer)
    }
    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }
    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }





    private fun onEvent() {
        bind.btnSelect.setOnClickListener{
            selectVideo(this@VideoActivity){
                videoPath = it
                bind.iv.load(File(it), coilImageLoader) {
                    videoFrameMillis(1000)
                }
            }
        }
        bind.btnCompress.setOnClickListener{
            if (TextUtils.isEmpty(videoPath))return@setOnClickListener
            compressVideo(this@VideoActivity,videoPath){compressPath->
                bind.tvProgress.text =  "压缩后视频路径:$compressPath  大小:${FormatUtils.formatSize(FileUtils.getFileSize(compressPath))}"
            }

        }
        bind.btnPlaySingleVideo.setOnClickListener{
            SingleVideoPlayActivity.go(this@VideoActivity, url)
        }
    }

    /**
     * 选择视频
     * @param activity Activity
     * @param cb Function1<String, Unit>
     */
    private fun selectVideo(activity:Activity,cb:(String)->Unit){
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofVideo())
            .imageEngine(GlideEngine.createGlideEngine())
            .isWeChatStyle(true)
            .videoMaxSecond(60)
            .maxVideoSelectNum(1)
            .isCamera(true) // 是否显示拍照按钮
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    if (result.sizeNull()>0){
                        val media = result[0]
                        val realPath = media.realPath
                        cb.invoke(realPath)
                        printFileInfo(media)
                    }
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
       bind.tvVideoInfo.text =  "\n原路径:" + media.path +
                    "\n绝对路径:" + media.realPath +
                    "\n宽高: " + media.width + "x" + media.height +
                    "\n文件大小: " + FormatUtils.formatSize(media.size)

    }

    private fun compressVideo(context: Context, selectedVideoUri: String,cb:(String)->Unit){
        val suffix = FileUtils.getFileSuffix(selectedVideoUri)
        val outPath = SdUtils.getAppCachePath()+"compress_"+System.currentTimeMillis()+"."+suffix
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(selectedVideoUri))
        val originWidth =retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
        val originHeight =retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)!!.toInt()
        val outWidth = originWidth / 2
        val outHeight = originHeight / 2
        val outBitrate = bitrate / 2
        CoroutineScope(Dispatchers.IO).launch {
            VideoProcessor.processor(context)
                .input(selectedVideoUri)
                .output(outPath)
                .outWidth(outWidth)
                .outHeight(outHeight)
                .bitrate(outBitrate)
                .progressListener {
                    CoroutineScope(Dispatchers.Main).launch{
                        val nf: NumberFormat = NumberFormat.getPercentInstance()
                        nf.maximumFractionDigits = 0
                        val progress = nf.format(it)
                        bind.tvProgress.text="压缩进度：$progress"
                        if (it==1f){
                            //压缩完成
                            cb.invoke(outPath)
                        }
                    }


                }
                .process()
        }

    }


}