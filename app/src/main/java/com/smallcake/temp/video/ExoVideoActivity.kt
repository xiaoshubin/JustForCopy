package com.smallcake.temp.video

import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ui.PlayerControlView
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoVideoBinding


/**
 * 官网：https://exoplayer.dev/hello-world.html
 * 简书博客：https://www.jianshu.com/p/80b7cb7bc2a8
 *
 * Github :https://github.com/google/ExoPlayer  18.8k Star
 * 1.引入
 * implementation 'com.google.android.exoplayer:exoplayer:2.16.1'
 *
 * 2.播放器类型
 * com.google.android.exoplayer2.ui.PlayerView为控制器在底部的播放器
 * com.google.android.exoplayer2.ui.StyledPlayerView为内嵌控制器的播放器
 * 3.功能扩展
 * 自定义PlaybackControlView播放控制界面，在xml布局中
 * app:controller_layout_id="@layout/custom_controls"
 *
 * 注意：
 * 1.多次打开页面，会创建多个播放器实例
 * 解决：onDestroy()中停止视频并释放资源
 */
class ExoVideoActivity : BaseBindActivity<ActivityExoVideoBinding>() {

    private val url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"//网络视频
    private lateinit var simpleExoPlayer:SimpleExoPlayer
    private val  buffer = StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("EXO播放器")
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        //1.创建播放器实例
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        //2.绑定视频播放基本界面，并监听播放状态
        bind.videoView.player = simpleExoPlayer
        simpleExoPlayer.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when(state){
                    //播放器停止或播放失败
                    Player.STATE_IDLE->buffer.append("播放器初始化...\n")
                    Player.STATE_BUFFERING->buffer.append("播放器缓冲中...\n")
                    Player.STATE_READY->buffer.append("播放器已准备完毕\n")
                    Player.STATE_ENDED->buffer.append("播放完毕\n")
                }
                bind.tvDesc.text = buffer.toString()
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
            }
        })
        //3.准备播放资源
        val mediaItem = MediaItem.fromUri(url)
        //4.设置播放资源
        simpleExoPlayer.setMediaItem(mediaItem)
        //5.预加载播放资源
        simpleExoPlayer.prepare()
        //6.开始播放
        simpleExoPlayer.play()
    }

    private fun initCustom(){


    }

    override fun onResume() {
        super.onResume()
        if (!simpleExoPlayer.isPlaying)simpleExoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
    }
}