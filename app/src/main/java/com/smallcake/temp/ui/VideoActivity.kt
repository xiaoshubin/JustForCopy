package com.smallcake.temp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import coil.load
import coil.request.videoFrameMillis
import com.hw.videoprocessor.VideoProcessor
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.smallcake.smallutils.FileUtils
import com.smallcake.smallutils.FormatUtils
import com.smallcake.smallutils.SdUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityVideoBinding
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
 */
@SuppressLint("SetTextI18n")
class VideoActivity : BaseBindActivity<ActivityVideoBinding>() {
    private var videoPath =""
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("视频相关")
        onEvent()
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