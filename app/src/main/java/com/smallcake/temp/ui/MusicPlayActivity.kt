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
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMusicPlayBinding

/**
 * 采用MediaPlayer进行音乐播放
 * 1.播放
 * 2.暂停
 * 3.停止
 * 4.播放进度监听
 * 5.进度设置
 *
 *
 */
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

    }

    private fun checkManagerEx() {
        XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request { permissions, all ->
            if (all)initMediaPlayer()
        }
    }


    private fun initMediaPlayer() {
        mediaPlayer.apply {
            setDataSource(music2Url)//指定音频文件路径
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
                        seekTo(progress)
                    }else{
                        Log.e(TAG,"非用户触摸==onProgressChanged:$progress")
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.e(TAG,"onStartTrackingTouch")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.e(TAG,"onStopTrackingTouch")

                }

            })
            //监听播放进度
            mHandler.sendEmptyMessageDelayed(0,1000)
        }
    }
    private val mHandler = Handler{
        Log.e(TAG, "播放时间==${mediaPlayer.currentPosition}")
        val seconds = mediaPlayer.currentPosition/1000
        val minutes = seconds/60    //分钟
        val str = if (minutes>0)"${minutes}分${seconds%60}秒" else "${seconds}秒"
        bind.seekBar.progress = seconds
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