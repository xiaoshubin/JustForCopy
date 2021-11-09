package com.smallcake.temp.music

import android.os.Binder
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener


/**
 * MediaBrowserServiceCompat:是一个Service，为运行在后台的音频服务。封装了媒体相关的一些功能，可用于控制已通过MediaSessionCompat来与UI通讯
 1.需要在AndroidManifest中配置
    <service android:name=".MusicService" >
        <intent-filter>
        <action android:name="android.media.browse.MediaBrowserService" />
        </intent-filter>
    </service>




# 四个对象：
1.MediaBrowserServiceCompat
该类是Service的子类实现，是音乐播放的后台服务，但是该类作为一个后台播放服务却不是通过其自身直接实现的，而是通过MediaSessionCompat媒体会话这个类来实现的
而对于获取数据，则是通过MediaBrowserServiceCompat的如下两个方法来进行控制
    1.1 onGetRoot的返回值决定是否允许客户端连接。
    1.2 onLoadChildren回调在Sercive中异步获取的数据给到MediaBrowser。也包含媒体播放器实例：如ExoPlayer

2.MediaSessionCompat
MediaBrowserServiceCompat的媒体播放其实是通过关联的MediaSessionCompat来实现的，
MediaSessionCompat的播放控制则又全部是通过接口MediaSessionCompat.Callback来实现的
UI->点击按钮->MediaControllerCompat.TransportControls.play() -> MediaSessionCompat.Callback{override fun onPlay()}->exoPlayer?.play()

3.MediaBrowserCompat
4.MediaControllerCompat

# 两个回调：
MediaSessionCompat.Callback，
MediaControllerCompat.Callback

# 流程图

MediaBrowserServiceCompat【Service】(MediaSessionCompat【Session】) <-----> MediaBrowserCompat【UI】(MediaControllerCompat【Controller】)
     ↓      ↑                                                                                  ↑                       ↓
     ↓      ↑-------------------------MediaSessionCompat.Callback------------------------------↑-----------------------↓
     ↓                                                                                         ↑
     ↓--------------------------------MediaControllerCompat.Callback---------------------------↑


参考：
MediaBrowserCompat和MediaBrowserServiceCompat： https://blog.csdn.net/xiaxl/article/details/78780691
关于媒体浏览器服务(MediaBrowserService):          https://blog.csdn.net/huyongl1989/article/details/71037284
Android基于MediaBroswerService的App实现概述:     https://segmentfault.com/a/1190000014375066


 */
class MusicService: MediaBrowserServiceCompat() {
    private val TAG = "MusicService"
    private var exoPlayer:SimpleExoPlayer?=null
    private lateinit var mediaSession:MediaSessionCompat

    /**
     * 当服务收到onCreate（）生命周期回调方法时，它应该执行以下步骤：
     * 1. 创建并初始化media session
     * 2. 设置media session回调
     * 3. 设置media session token
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG,"音乐服务已创建...")
        //1. 创建并初始化MediaSession
        mediaSession = MediaSessionCompat(applicationContext, TAG)
        //2. 设置mediaSessionToken
        sessionToken = mediaSession.sessionToken
        //3. 设置mediaSession回调
        mediaSession.setCallback(mediaSessionCallBack)
        //4.设置标签
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val playbackState = PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
        mediaSession.setPlaybackState(playbackState)


        //4.创建播放器实例
        exoPlayer = SimpleExoPlayer.Builder(applicationContext).build()


    }

    /**
     * 接收客服端发送过来的自定义消息
     * @param action String
     * @param extras Bundle
     * @param result Result<Bundle>
     */
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        when(action){
            "getPosition"->{
                val isPlay = exoPlayer?.isPlaying
                val speed = exoPlayer?.playbackParameters?.speed?:0f
                val totalDuration = exoPlayer?.duration?:0
                val currentPosition = exoPlayer?.currentPosition?:0
                Log.i(TAG,"速度：${speed}倍 总时长：${(totalDuration/1000).toInt()}s  已播放：${(currentPosition/1000).toInt()}s isPlay：$isPlay")
                val bundle = Bundle()
                bundle.putInt("currentPosition",(currentPosition/1000).toInt())
                result.sendResult(bundle)
            }
        }
    }
    /**
     * 用户对UI的操作将最终回调到这里。通过MediaSessionCallback 操作播放器
     * 用于接收由MediaControl触发的改变，内部封装实现播放器和播放状态的改变
     */
    private val mediaSessionCallBack = object:MediaSessionCompat.Callback(){
        override fun onPlay() {
            super.onPlay()
            Log.i(TAG, "onPlay: ")
            exoPlayer?.play()
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onPause() {
            super.onPause()
            Log.i(TAG, "onPause: ")
            exoPlayer?.pause()
            setPlaybackState(PlaybackStateCompat.STATE_PAUSED)
        }

        override fun onStop() {
            super.onStop()
            Log.i(TAG, "onStop: ")
            exoPlayer?.stop()
            setPlaybackState(PlaybackStateCompat.STATE_STOPPED)
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            Log.i(TAG, "onSeekTo:$pos ")
            exoPlayer?.seekTo(pos)
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            Log.i(TAG, "onPlayFromMediaId:mediaId=$mediaId ")
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            Log.i(TAG, "onSkipToPrevious:")
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            Log.i(TAG, "onSkipToNext:")
        }



    }

    /**
     * 告诉MediaBrowser是否连接连接成功
     * 返回非空，表示连接成功
     * @param clientPackageName String
     * @param clientUid Int
     * @param rootHints Bundle?
     * @return BrowserRoot?
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        Log.i(TAG,"onGetRoot: clientPackageName=$clientPackageName clientUid=$clientUid pid=" + Binder.getCallingPid().toString() + " uid=" + Binder.getCallingUid())
        //返回非空，表示连接成功
        return BrowserRoot("media_root_id", rootHints)
    }

    /**
     * 获取音视频信息（这个更应该是在业务层处理事情）
     * @param parentId String
     * @param result Result<MutableList<MediaItem>>
     */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.i(TAG, "onLoadChildren: parentId=$parentId")
        val mediaItems: ArrayList<MediaBrowserCompat.MediaItem> = ArrayList()

        if (TextUtils.equals("media_root_id", parentId)) {
        }
        val musicEntityList: ArrayList<MusicEntity> = getMusicEntityList()

        for (i in 0 until musicEntityList.size) {
            val musicEntity: MusicEntity = musicEntityList[i]
            val metadataCompat: MediaMetadataCompat = buildFromLocal(musicEntity)
            if (i == 0) {
                mediaSession.setMetadata(metadataCompat)
            }

            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    metadataCompat.description,
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                )
            )
            exoPlayer?.addMediaItem(MediaItem.fromUri(musicEntity.url))
        }
        //当设置多首歌曲组成队列时报错
        // IllegalStateException: sendResult() called when either sendResult() or sendError() had already been called for: media_root_id
        //原因，之前在for处理了，应该在设置好mediaItems列表后，统一设置result
        result.sendResult(mediaItems)
        Log.i(TAG, "onLoadChildren: addMediaItem")

        initExoPlayerListener()
        exoPlayer?.prepare()
        Log.i(TAG, "onLoadChildren: prepare")

    }

    /**
     * 初始化ExoPlayer播放器监听
     * 1. 用户触发的  比如： 手动切歌曲、暂停、播放、seek等；
     * 2. 播放器内部触发 比如： 播放结束、自动切歌曲等）
     * UI通过setPlaybackState设置
     */
    private fun initExoPlayerListener() {
        exoPlayer?.apply {
            addListener(object : Player.Listener{
                    //播放状态回调
                    override fun onPlaybackStateChanged(state: Int) {
                        Log.i(TAG,"exoPlayer播放状态：currentPositon=$currentPosition duduration=$duration state=$state")
                        var playbackState=PlaybackStateCompat.STATE_NONE
                        when(state){
                            Player.STATE_IDLE     ->playbackState =PlaybackStateCompat.STATE_NONE
                            Player.STATE_BUFFERING->playbackState =PlaybackStateCompat.STATE_BUFFERING
                            Player.STATE_READY    ->playbackState =if (playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
                            Player.STATE_ENDED    ->playbackState = PlaybackStateCompat.STATE_STOPPED
                        }
                        setPlaybackState(playbackState)
                    }

            })
            addAnalyticsListener(object :AnalyticsListener{

            })

        }

    }

    /**
     * 设置mediaSession中的播放状态
     * @param state Int
     */
    private fun setPlaybackState(state: Int) {
        val speed = exoPlayer?.playbackParameters?.speed?:0f
        val totalDuration = exoPlayer?.duration?:0
        val currentPosition = exoPlayer?.currentPosition?:0
        Log.i(TAG,"速度：${speed}倍 总时长：${(totalDuration/1000).toInt()}s  已播放：${(currentPosition/1000).toInt()}s 状态：$state")
        val playbackState = PlaybackStateCompat.Builder()
                                .setState(state,currentPosition,speed)
                                .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun buildFromLocal(song: MusicEntity): MediaMetadataCompat {
        val title: String = song.name
        val source: String = song.url
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, System.currentTimeMillis().toString())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, source)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .build()
    }

    private fun getMusicEntityList():ArrayList<MusicEntity> {
        val list =  ArrayList<MusicEntity>()
        val musicEntity =  MusicEntity("Geisha","http://music.163.com/song/media/outer/url?id=447925558.mp3")
        list.add(musicEntity)
        return list
    }


    /**
     * 断开连接时会触发
     * 当服务关闭，释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")

    }
}