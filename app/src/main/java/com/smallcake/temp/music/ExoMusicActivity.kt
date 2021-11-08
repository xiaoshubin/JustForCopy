package com.smallcake.temp.music

import android.annotation.SuppressLint
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
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
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoMusicBinding
import com.smallcake.temp.utils.sizeNull
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


/**
 * 采用Exo播放音乐
 *
 */
class ExoMusicActivity : BaseBindActivity<ActivityExoMusicBinding>(), View.OnClickListener {
    private val TAG = "ExoMusicActivity"
    private var mediaBrowser:MediaBrowserCompat?=null                                   //流媒体：MediaBrowser执行两个重要功能：它连接到MediaBrowserService，连接后，将为您的UI创建MediaController。
    private var mediaController:MediaControllerCompat?=null                             //控制器
    private var mSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback? = null  //订阅的回调监听
    private var durationSet = false                                                     //进度设置
    private var mLastPlaybackState: PlaybackStateCompat? = null                         //播放状态
    private val mExecutorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var mScheduleFuture: ScheduledFuture<*>? = null


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("音乐播放")
        mediaBrowser = MediaBrowserCompat(this,ComponentName(this, MusicService::class.java),mConnectionCallbacks,null)
        onEvent()
    }

    private fun onEvent(){
        bind.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
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
            R.id.btn_play->mediaController?.apply {
                    val state = playbackState.state
                    Log.i(TAG, "onClick: state=$state")
                    //通过 mediaController.getTransportControls 触发MediaSessionCompat.Callback回调--》进行播放控制
                    transportControls.apply {if (state == PlaybackStateCompat.STATE_PLAYING)pause() else play()}
            }
            R.id.prev->mediaController?.transportControls?.skipToPrevious()//上一首
            R.id.next->mediaController?.transportControls?.skipToNext()    //下一首
            R.id.speed->{
                val speed: Float = getSpeed()
                bind.speed.text = "倍速 $speed"
                mediaController?.transportControls?.setPlaybackSpeed(speed)
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
            Log.i(TAG, "onConnected")
            //MediaBrowser和MediaBrowerService建立连接之后会回调该方法
            val sessionToken = mediaBrowser?.sessionToken
            //建立连接之后再创建MediaController控制器，并设置
            mediaController = MediaControllerCompat(this@ExoMusicActivity, sessionToken!!)
            MediaControllerCompat.setMediaController(this@ExoMusicActivity, mediaController)
            //订阅监听媒体文件变化
            subscribe()
            //MediaController发送命令
            mediaController?.registerCallback(mediaControllerCallback)

            val state = mediaController!!.playbackState
            updatePlaybackState(state)
            updateProgress()
            if (state != null && (state.state == PlaybackStateCompat.STATE_PLAYING ||state.state == PlaybackStateCompat.STATE_BUFFERING)) {
                scheduleSeekbarUpdate()
            }
            //通过mediaController获取MediaMetadataCompat
            val metadata = mediaController!!.metadata
            updateDuration(metadata)
        }
    }


    /**
     * 订阅媒体文件
     */
    private fun subscribe() {
        //取消上一次的订阅
        val mediaId = mediaBrowser!!.root
        mediaBrowser?.unsubscribe(mediaId)
        if (mSubscriptionCallback == null) {
            //mediaBrowser 和mServiceBinderImpl建立联系
            mSubscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(@NonNull parentId: String, @NonNull children: List<MediaBrowserCompat.MediaItem>) {
                    super.onChildrenLoaded(parentId, children)
                    Log.i(TAG, "onChildrenLoaded: parentId=$parentId children=$children")
                    if (children.sizeNull() > 0) updateShowMediaInfo(children[0].description)
                }

                override fun onError(@NonNull parentId: String) {
                    super.onError(parentId)
                    Log.i(TAG, "onError: parentId=$parentId")
                }
            }
        }
        mediaBrowser?.subscribe(mediaId, mSubscriptionCallback!!)
    }


    /**
     * 更新显示媒体信息到界面上
     * @param description MediaDescriptionCompat?
     */
    private fun updateShowMediaInfo(description: MediaDescriptionCompat?) {
        if (description == null) return
        val mediaUri: Uri? = description.mediaUri
        val iconUri: Uri? = description.iconUri
        Log.i(TAG,"onChildrenLoaded: title=${description.title} mediaUri=$mediaUri iconUri=$iconUri")
    }

    /**
     * 控制器播放状态监听
     */
    val mediaControllerCallback =object: MediaControllerCompat.Callback() {
        //这里的回调，只有用户触发的才会有相应的回调。
        //播放结束 这里没有
        //ExoPlayer getDuration : https://stackoverflow.com/questions/35298125/exoplayer-getduration
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            super.onPlaybackStateChanged(state)
            Log.i(TAG, "onPlaybackStateChanged: state=" + state.state)
            bind.btnPlay.text=if (PlaybackStateCompat.STATE_PLAYING == state.state)"暂停" else  "播放"
            updatePlaybackState(state)
            val metadata: MediaMetadataCompat = mediaController!!.metadata
            updateDuration(metadata)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            super.onMetadataChanged(metadata)
            durationSet = false
            Log.i(TAG, "onMetadataChanged: metadata=$metadata")
            updateDuration(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.i(TAG, "onSessionDestroyed: ")
        }

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

    private fun updatePlaybackState(state: PlaybackStateCompat) {
        mLastPlaybackState = state
        Log.i(TAG,"updatePlaybackState: state=" + state.state + " position=" + mLastPlaybackState?.position + " mLastPlaybackState=" + mLastPlaybackState)

        val description = mediaController?.metadata?.description
        if (state.state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS || state.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT) {
            updateShowMediaInfo(description)
        }
        scheduleSeekbarUpdate()
    }

    /**
     * 更新歌曲时长
     * @param metadata MediaMetadataCompat?
     */
    private fun updateDuration(metadata: MediaMetadataCompat?) {
        if (metadata != null && !durationSet) {
            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
//            Log.i(TAG, "updateDuration: duration=$duration")
            if (duration > 0) {
                bind.seekbar.max = duration.toInt()
                bind.endText.text = DateUtils.formatElapsedTime(duration)
                durationSet = true
            }
        }
    }

    /**
     * 更新进度
     */
    private fun updateProgress() {
        if (mLastPlaybackState != null) {
            val metadata = mediaController!!.metadata
            updateDuration(metadata)
            mLastPlaybackState?.apply {
//                Log.i(TAG,"updateProgress: position()=$position playbackState=$playbackState state=$state playbackSpeed=$playbackSpeed")
                var curPos = position.toDouble()
                if (state == PlaybackStateCompat.STATE_PLAYING){
                    val timeDelta: Long = SystemClock.elapsedRealtime() -lastPositionUpdateTime
                    curPos += timeDelta.toInt() * playbackSpeed
                }
                curPos /= 1000
                bind.seekbar.progress = curPos.toInt()
                bind.startText.text = DateUtils.formatElapsedTime(curPos.toLong())

            }

        }
    }
    private fun scheduleSeekbarUpdate() {
        if (!mExecutorService.isShutdown) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                { mHandler.post(mUpdateProgressTask) },
                100,
                1000,
                TimeUnit.MILLISECONDS)
        }
    }
    private val mHandler: Handler = Handler()
    private val mUpdateProgressTask = Runnable { updateProgress() }

    override fun onStart() {
        super.onStart()
        mediaBrowser?.connect()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser?.disconnect()
    }


}