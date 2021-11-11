package com.smallcake.temp.music

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.NonNull
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoMusicBinding


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
    private var mediaBrowser:MediaBrowserCompat?=null                                   //流媒体：MediaBrowser执行两个重要功能：它连接到MediaBrowserService，连接后，将为您的UI创建MediaController。
    private var mediaController:MediaControllerCompat?=null                             //控制器
    private var transportControls: MediaControllerCompat.TransportControls?=null        //控制器

    private var durationSet = false                                                     //是否是总进度设置

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("音乐播放")
        mediaBrowser = MediaBrowserCompat(MyApplication.instance,ComponentName(this, MusicService::class.java),mConnectionCallbacks,null)
        mediaBrowser?.connect()
        onEvent()
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
    }
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when(v?.id){
            //播放和暂停
            R.id.btn_play->transportControls?.apply {
                        val state = mediaController?.playbackState?.state
                        if (state == PlaybackStateCompat.STATE_PLAYING)pause() else play()
                    }

            R.id.prev->transportControls?.skipToPrevious()//上一首
            R.id.next->transportControls?.skipToNext()    //下一首
            R.id.speed->{
                val speed: Float = getSpeed()
                bind.speed.text = "倍速 $speed"
                transportControls?.setPlaybackSpeed(speed)
            }
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

    /**
     * 用于接收与Server端连接的状态回调
     */
    private val mConnectionCallbacks = object :MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()

            //MediaBrowser和MediaBrowerService建立连接之后会回调该方法
            val sessionToken = mediaBrowser?.sessionToken
            Log.e(TAG, "onConnected：$TAG 已与 $sessionToken 建立连接")
            //通过媒体会话token创建媒体控制器并与之关联
            mediaController = MediaControllerCompat(this@ExoMusicActivity, sessionToken!!)
            //此处关联之后，我们在界面上操作某些UI的时候就可以通过当前上下文Context来获取当前的MediaControllerCompat
            MediaControllerCompat.setMediaController(this@ExoMusicActivity, mediaController)
            //订阅监听媒体文件变化
            subscribe()
            //MediaController发送命令
            mediaController?.registerCallback(mediaControllerCallback)
            transportControls = mediaController?.transportControls

            mHandler.sendEmptyMessage(0)
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.w(TAG, "onConnectionSuspended：$TAG 已与 ${mediaBrowser?.sessionToken} 断开连接")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.e(TAG, "onConnectionFailed：$TAG 已与 ${mediaBrowser?.sessionToken} 连接失败")
        }
    }
    private fun sendAction(cmdStr:String){
        mediaBrowser?.sendCustomAction(cmdStr,null,object :MediaBrowserCompat.CustomActionCallback(){
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                super.onResult(action, extras, resultData)
                when(cmdStr){
                    GET_PROGRESS->{
                        val currentPosition = resultData?.getInt("currentPosition",0)?:0
                        bind.startText.text = DateUtils.formatElapsedTime(currentPosition.toLong())
                        bind.seekbar.progress = currentPosition
                    }

                }

            }


        })
    }


    /**
     * 订阅媒体文件来发起数据请求
     * 客户端通过调用subscribe方法，传递MediaID，在SubscriptionCallback的方法中进行处理
     * MediaBrowser是通过订阅方式向Service请求数据的
     */
    private fun subscribe() {
        mediaBrowser?.apply {
            //先取消订阅
            unsubscribe(root)
            subscribe(root, object :MediaBrowserCompat.SubscriptionCallback(){
                //数据获取成功后的回调
                override fun onChildrenLoaded(@NonNull parentId: String, @NonNull children: List<MediaBrowserCompat.MediaItem>) {
                    super.onChildrenLoaded(parentId, children)
                    //children 为来自Service的列表数据
                    Log.i(TAG, "onChildrenLoaded: parentId=$parentId children=${children[0].description.mediaUri}")
                    val buffer = StringBuffer()
                    children.forEach {
                        buffer.append("${it.description.title}--${it.description.mediaUri}").append("\n")
                    }
                    bind.tvDesc.text = buffer.toString()

                }

                //数据获取失败的回调
                override fun onError(@NonNull parentId: String) {
                    super.onError(parentId)
                    Log.i(TAG, "onError: parentId=$parentId")
                }
            })
        }

    }




    /**
     * 控制器播放状态回调给Service
     */
    val mediaControllerCallback =object: MediaControllerCompat.Callback() {
        //这里的回调，只有用户触发的才会有相应的回调。
        //ExoPlayer getDuration : https://stackoverflow.com/questions/35298125/exoplayer-getduration
        //播放状态变化回调：usicService: mediaSession.setPlaybackState(playbackState)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            super.onPlaybackStateChanged(state)
            Log.i(TAG, "播放状态改变为：" + if (PlaybackStateCompat.STATE_PLAYING == state.state)"播放中" else  "暂停")
            bind.btnPlay.text=if (PlaybackStateCompat.STATE_PLAYING == state.state)"暂停" else  "播放"
            val metadata: MediaMetadataCompat = mediaController!!.metadata
            updateDuration(metadata)
        }
        //播放的媒体数据发生变化时的回调：MusicService: mediaSession.setMetadata(mediaMetadataCompat)
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            super.onMetadataChanged(metadata)
            durationSet = false
            updateDuration(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.i(TAG, "onSessionDestroyed: ")
        }
        //连接MusicService时触发
        override fun onSessionReady() {
            super.onSessionReady()
            Log.i(TAG, "onSessionReady: ")
        }

        override fun onQueueChanged(queue: List<MediaSessionCompat.QueueItem>) {
            super.onQueueChanged(queue)
            Log.i(TAG, "onQueueChanged: ")
        }

        override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo) {
            super.onAudioInfoChanged(info)
            Log.i(TAG, "onAudioInfoChanged: ")
        }

        override fun onSessionEvent(event: String, extras: Bundle) {
            super.onSessionEvent(event, extras)
            Log.i(TAG, "onSessionEvent: ")
        }
    }



    /**
     * 初始化歌曲时长
     * @param metadata MediaMetadataCompat?
     * 连接服务时
     * 歌曲资源切换
     * 歌曲状态改变
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
        sendAction(GET_PROGRESS)
        it.target.sendEmptyMessageDelayed(0,1000)
        false
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaController?.unregisterCallback(mediaControllerCallback)
        mHandler.removeCallbacksAndMessages(null)
    }


}