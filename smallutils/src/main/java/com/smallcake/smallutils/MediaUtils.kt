package com.smallcake.smallutils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.*
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.widget.MediaController
import android.widget.VideoView
import java.io.File
import java.io.IOException
import java.util.*


object MediaUtils{
    private const val TAG = "MediaUtils"
    /**
     * 播放res/raw资源下的mp3音频文件
     * 例如：MediaUtils.playMp3("zltx.mp3",R.raw::class.java)
     * 坑：如果需要在息屏状态下播放语音文件，需要申明MediaPlayer为全部变量，而不是局部变量
     */
    fun playMp3(name: String, cls: Class<*>) {
        val resId: Int = ResourceUtils.findResId(name.replace(".mp3", ""), cls)
        MediaPlayer.create(SmallUtils.context, resId).start()
    }

    /**
     * 使用MediaPlayer播放raw文件下的 mp4到SurfaceView
     * @param context Context
     * @param holder SurfaceHolder 外部页面SurfaceView提供
     * 开始播放 mPlayer.start()
     * 暂停播放 mPlayer.pause()
     * 结束播放 mPlayer.stop()
     */
    fun playMp4(context: Context, holder: SurfaceHolder){
        val mPlayer = MediaPlayer.create(context, R.raw.lesson)
        val audioAttr = AudioAttributes.Builder()
            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
            .setLegacyStreamType(AudioManager.STREAM_ALARM)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .build()
        mPlayer.setAudioAttributes(audioAttr)
        mPlayer.setDisplay(holder)
        mPlayer.start()
    }

    /**
     * 使用VideoView播放在线视频
     * @param context Context
     * @param videoView VideoView
     */
    fun playVideo(context: Context,videoView: VideoView){
        val videoUrl = "http://poss.videocloud.cns.com.cn/oss/2020/07/19/chinanews/MEIZI_YUNSHI/onair/F1B171FB2ECB4319ADAC3FF2915C7E4B.mp4"
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        val video: Uri = Uri.parse(videoUrl)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(video)
        videoView.start()
    }

    var mMediaRecorder: MediaRecorder?=null
    var filePath = ""

    /**
     * 开始录音
     */
    fun startRecord(activity: Activity) {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null) mMediaRecorder = MediaRecorder()
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC) // 设置麦克风
            /*
         * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
         * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
         */mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */mMediaRecorder?.setAudioEncoder(
                MediaRecorder.AudioEncoder.AAC
            )
            val fileName = "${System.currentTimeMillis()}.m4a"

            filePath = "${activity.externalCacheDir}/$fileName"
            /* ③准备 */mMediaRecorder?.setOutputFile(filePath)
            mMediaRecorder?.prepare()
            /* ④开始 */mMediaRecorder?.start()
        } catch (e: IllegalStateException) {
            Log.i(">>>", "call startAmr(File mRecAudioFile) failed!" + e.message)
        } catch (e: IOException) {
            Log.i(">>>", "call startAmr(File mRecAudioFile) failed!" + e.message)
        }
    }
    /**
     * 结束录音
     */
    fun stopRecord(cb: (String) -> Unit) {
        if (mMediaRecorder==null)return
        try {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            cb.invoke(filePath)
        } catch (e: RuntimeException) {
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            val file = File(filePath)
            if (file.exists()) file.delete()
            filePath = ""
        }

    }

    /**
     * 播放音频
     * @param audioPath String
     */
    fun playVoice(audioPath: String){
        try {
            val mediaPlayer =  MediaPlayer()
            mediaPlayer.setDataSource(audioPath)
            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
            mediaPlayer.setAudioAttributes(attributes)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener{
                mediaPlayer.start()
            }

        } catch (e: Exception) {
        }
    }

    /**
     * 获取音频时长
     * @param filePath String? 本地路径|网络url路径
     * @return Int 秒
     */
    fun getAudioFileVoiceTime(filePath: String?): Int {
        var mediaPlayerDuration = 0L
        if (filePath == null || filePath.isEmpty()) {
            return 0
        }
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            mediaPlayerDuration = mediaPlayer.duration.toLong()
        } catch (ioException: IOException) {
            ioException.message?.let { Log.e(TAG, it) }
        }
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        return (mediaPlayerDuration/1000).toInt()
    }
    fun createVideoThumbnail(filePath: String, kind: Int): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            if (filePath.startsWith("http://")
                || filePath.startsWith("https://")
                || filePath.startsWith("widevine://")
            ) {
                retriever.setDataSource(filePath, Hashtable<String, String>())
            } else {
                retriever.setDataSource(filePath)
            }
            bitmap = retriever.getFrameAtTime(
                0,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) //retriever.getFrameAtTime(-1);
        } catch (ex: IllegalArgumentException) {
            // Assume this is a corrupt video file
            ex.printStackTrace()
        } catch (ex: java.lang.RuntimeException) {
            // Assume this is a corrupt video file.
            ex.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: java.lang.RuntimeException) {
                // Ignore failures while cleaning up.
                ex.printStackTrace()
            }
        }
        if (bitmap == null) {
            return null
        }
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) { //压缩图片 开始处
            // Scale down the bitmap if it's too large.
            val width = bitmap.width
            val height = bitmap.height
            val max = Math.max(width, height)
            if (max > 512) {
                val scale = 512f / max
                val w = Math.round(scale * width)
                val h = Math.round(scale * height)
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
            } //压缩图片 结束处
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                96,
                96,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
        }
        return bitmap
    }
}
