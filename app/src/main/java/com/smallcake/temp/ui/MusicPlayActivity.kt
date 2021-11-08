package com.smallcake.temp.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaTimestamp
import android.media.TimedText
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.renderscript.Allocation
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMusicPlayBinding

/**
 * 一 采用MediaPlayer进行音乐播放
 * 缺点：播放网络音频进度设置无效，回到了开头的位置进行播放
 * 1.播放
 * 2.暂停
 * 3.停止
 * 4.播放进度监听
 * 5.进度设置 ? 设置无效
 *参考：
 * Android MediaPlayer的状态管理：https://www.jianshu.com/p/55afaa0a96f7
 * Android MediaPlayer控制进度播放音频:https://blog.csdn.net/wenzhi20102321/article/details/103787884
 *
 * 二 采用ExoPlayer进行音乐播放
 *
 * 参考：
 * ExoPlayer简单使用：https://www.jianshu.com/p/6e466e112877
 *音视频开发之旅（45)-ExoPlayer 音频播放器实践(一)：https://www.jianshu.com/p/1bb4ca733b55
 *
 * 后台服务来播放音乐更合理
 * @see com.smallcake.temp.music.ExoMusicActivity
 */
@java.lang.Deprecated
@SuppressLint("SetTextI18n")
class MusicPlayActivity : BaseBindActivity<ActivityMusicPlayBinding>() {

    private val TAG = "MusicPlayActivity"
    private val mediaPlayer = MediaPlayer()
    private val musicUrl = "http://music.163.com/song/media/outer/url?id=447925558.mp3"
    private val music2Url = "http://music.163.com/song/media/outer/url?id=447925559.mp3"

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("音乐播放")
        checkManagerEx()
        onEvent()

        initExo()


    }

    private fun initExo() {

    }

    private fun checkManagerEx() {
        XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request { permissions, all ->
            if (all)initMediaPlayer()
        }
    }


    private fun initMediaPlayer() {
        mediaPlayer.apply {
            setDataSource(musicUrl)//指定音频文件路径
            isLooping = true       //设置为循环播放
            prepare()              //初始化播放器MediaPlayer
            val duration = mediaPlayer.duration
            val seconds = duration/1000 //一共多少秒
            val minutes = seconds/60    //分钟

            bind.tvTotalTime.text = "音频总时长：${minutes}分${seconds%60}秒"
            //如果缓存好了，直接就percent==100
            setOnBufferingUpdateListener { mp, percent -> Log.e(TAG, "缓存进度==$percent") }
            bind.seekBar.max = seconds
            bind.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser){
                        Log.e(TAG,"用户触摸==onProgressChanged:$progress")
                    }else{
                        Log.e(TAG,"非用户触摸==onProgressChanged:$progress")
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.e(TAG,"onStartTrackingTouch")
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.e(TAG,"onStopTrackingTouch：${seekBar?.progress?.toLong()}")
                    mediaPlayer.pause()
                    Handler().postDelayed({
                        mediaPlayer.seekTo(seekBar?.progress?.toLong()?:0L,MediaPlayer.SEEK_CLOSEST)
                    },1000)

                }

            })
            //监听播放进度
//            mHandler.sendEmptyMessageDelayed(0,1000)
        }
    }
    private val mHandler = Handler{
        Log.e(TAG, "播放时间==${mediaPlayer.currentPosition}")
        val seconds = mediaPlayer.currentPosition/1000
        val minutes = seconds/60    //分钟
        val str = if (minutes>0)"${minutes}分${seconds%60}秒" else "${seconds}秒"
//        bind.seekBar.progress = seconds
        bind.tvTime.text = "已播放：$str"
        it.target.sendEmptyMessageDelayed(0,1000)
        false
    }

    private fun onEvent() {
        bind.apply {
            btnPlay.setOnClickListener{
                if (!mediaPlayer.isPlaying)mediaPlayer.start()
            }
            btnPause.setOnClickListener{
                if (mediaPlayer.isPlaying)mediaPlayer.pause()
            }
            btnStop.setOnClickListener{
                if (mediaPlayer.isPlaying){
                    mediaPlayer.reset()
                    initMediaPlayer()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.apply {
            stop()
            release()
        }
        mHandler.removeCallbacksAndMessages(null)
    }
}