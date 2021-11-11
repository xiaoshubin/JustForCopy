package com.smallcake.temp.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.NonNull
import com.hjq.permissions.Permission.SYSTEM_ALERT_WINDOW
import com.hjq.permissions.XXPermissions
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoMusicBinding
import okhttp3.internal.wait


/**
 * 采用Exo播放音乐
 *
 * 参考：
 * ExoPlayer简单使用：                            https://www.jianshu.com/p/6e466e112877
 * 音视频开发之旅（45)-ExoPlayer 音频播放器实践(一)：https://www.jianshu.com/p/1bb4ca733b55
 * ScheduledExecutorService的使用：               https://blog.csdn.net/ma969070578/article/details/82863477
 *
 * MediaBrowserCompat : connect -> onConnected -> subscribe -> onChildrenLoaded
 */
class ExoMusicActivity : BaseBindActivity<ActivityExoMusicBinding>(), View.OnClickListener {

    private val TAG = "ExoMusicActivity"
    private var durationSet = false  //是否是总进度设置

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("音乐播放")
        onEvent()
        MCManager.instance.registerCallback(object :MusicClientListener{
            override fun onConnected() {
                //连接成功后，每秒获取播放进度
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                bind.btnPlay.text=if (PlaybackStateCompat.STATE_PLAYING == state.state)"暂停" else  "播放"
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat) {
                durationSet = false
                updateDuration(metadata)
            }

        })
        mHandler.sendEmptyMessage(0)
        if (!durationSet)mHandler.sendEmptyMessageDelayed(1,100)


    }

    private fun onEvent(){
        bind.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                val max = seekBar.max
                Log.i(TAG, "onStopTrackingTouch: progress=$progress max=$max")
                mediaController?.transportControls?.seekTo(progress.toLong())
            }
        })
        bind.btnPlay.setOnClickListener(this)
        bind.prev.setOnClickListener(this)
        bind.next.setOnClickListener(this)
        bind.speed.setOnClickListener(this)
        bind.btnBackgroundPlay.setOnClickListener(this)

    }
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when(v?.id){
            //播放和暂停
            R.id.btn_play->
                MCManager.instance.transportControls?.apply {
                        val state =  MCManager.instance.mediaController?.playbackState?.state
                        if (state == PlaybackStateCompat.STATE_PLAYING)pause() else play()
                    }

            R.id.prev->MCManager.instance.transportControls?.skipToPrevious()//上一首
            R.id.next->MCManager.instance.transportControls?.skipToNext()    //下一首
            //倍数播放
            R.id.speed->{
                val speed: Float = getSpeed()
                bind.speed.text = "倍速 $speed"
                MCManager.instance.transportControls?.setPlaybackSpeed(speed)
            }
            //后台播放，弹出一个悬浮框
            R.id.btn_background_play->{
                showMusicFloatWeight()
            }
        }
    }

    /**
     * 显示一个音乐播放的悬浮小控件
     * 需要悬浮窗权限
     */
    private fun showMusicFloatWeight() {
        XXPermissions.with(this).permission(SYSTEM_ALERT_WINDOW).request { _, all ->
                if (!all) return@request
                EasyFloat.with(this)
                    .setLayout(R.layout.music_weight){
                        it.findViewById<ImageView>(R.id.iv_close).setOnClickListener{EasyFloat.dismissAppFloat("MusicWeight")}
                    }
                    .setTag("MusicWeight")
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .setDragEnable(true)
                    .setGravity(Gravity.CENTER_VERTICAL or Gravity.END)
                    .show()


        }

    }

    private var speedArray = floatArrayOf(0.5f, 1f, 1.5f, 2f)
    private var curSpeedIndex = 1
    private fun getSpeed(): Float {
        if (curSpeedIndex > 3) {
            curSpeedIndex = 0
        }
        return speedArray[curSpeedIndex++]
    }

    private fun sendAction(cmdStr:String){
        if (!MCManager.instance.isConnect)return
        MCManager.instance.mediaBrowser.sendCustomAction(cmdStr,null,object :MediaBrowserCompat.CustomActionCallback(){
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                super.onResult(action, extras, resultData)
                when(cmdStr){
                    GET_PROGRESS->{
                        val currentPosition = resultData?.getInt("currentPosition",0)?:0
                        val bufferedPosition = resultData?.getInt("bufferedPosition",0)?:0
                        bind.startText.text = DateUtils.formatElapsedTime(currentPosition.toLong())
                        bind.seekbar.progress = currentPosition
                        bind.seekbar.secondaryProgress = bufferedPosition
                    }

                }

            }


        })
    }

    /**
     * 初始化歌曲时长
     */
    private fun updateDuration(metadata: MediaMetadataCompat?) {
        if (metadata != null) {
            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            if (duration > 0) {
                Log.i(TAG, "更新总进度: duration=$duration")
                bind.seekbar.max = duration.toInt()
                bind.endText.text = DateUtils.formatElapsedTime(duration)
                durationSet = true
            }
        }
    }

    private val mHandler = Handler{
        when(it.what){
            0->{
                sendAction(GET_PROGRESS)
                it.target.sendEmptyMessageDelayed(0,1000)
            }
            1->{
                sendAction(GET_MUSIC_MEDIA)
                if (!durationSet)it.target.sendEmptyMessageDelayed(1,300)
            }
        }

        false
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

}