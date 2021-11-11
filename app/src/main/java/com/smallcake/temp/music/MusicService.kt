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
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes


/**
 * MediaBrowserServiceCompat:是一个Service，为运行在后台的音频服务。封装了媒体相关的一些功能，可用于控制已通过MediaSessionCompat来与UI通讯
 # 步骤：
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
关于媒体浏览器服务MediaBrowserService:            https://blog.csdn.net/huyongl1989/article/details/71037284
Android基于MediaBroswerService的App实现概述:     https://segmentfault.com/a/1190000014375066
android-UniversalMusicPlayer 学习笔记（一）:     https://www.jianshu.com/p/d3a329e787ad
Android音乐播放器实战：                          https://github.com/android/uamp

 关键点：
 1.如何把播放音乐的进度传递给UI界面
    问题：通过mediaBrowser?.sendCustomAction发送事件，主动获取，但当退出界面关闭连接后，重新连接获取的exoPlayer播放进度一直是0?
    原因：MediaBrowserCompat创建的时候第一个参数传入了当前页面，导致每次都会新建MusicService和ExoPlayer，且断开了连接mediaBrowser?.disconnect()
    解决：传入当前应用MyApplication,这样只会不会多次创建MusicService和ExoPlayer，不断开连接，否则会导致当前MusicService执行onDestroy
 2.在退出界面关闭连接后，再次进入UI界面，如何重新关联进度
    通过发送获取自定义action来获取，
 3.播放状态同步
    setMetadata(android.media.MediaMetadata))
    setPlaybackState(android.media.session.PlaybackState))

 已完成的功能：
     1.播放
     2.暂停
     3.上一首，下一首
     4.进度回调到UI,缓冲进度
     5.时长总时长更新到UI:sendMediaDataToUI()
     6.拖动进度改变播放位置
     7.倍速播放
 未完成的功能：
    1.缓存
    2.播放模式：单曲循环，顺序播放
    3.常驻后台播放




 */
const val GET_PROGRESS = "getProgress"//获取音乐播放进度
const val GET_MUSIC_MEDIA = "getMusicMedia"//获取音乐媒体信息
class MusicService: MediaBrowserServiceCompat() {

    private val TAG = "MusicService"
    private lateinit var mediaSession:MediaSessionCompat
    private var isPlayingIndex=0//正在播放的歌曲位置


    /**
     * 当服务收到onCreate（）生命周期回调方法时，它应该执行以下步骤：
     * 1. 创建并初始化media session
     * 2. 设置media session回调
     * 3. 设置media session token
     * 会多次执行
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG,"音乐服务已创建...")
        //1. 创建并初始化MediaSession
        mediaSession = MediaSessionCompat(applicationContext, TAG)
        //2. 设置mediaSessionToken用于和控制器配对的令牌并通知浏览器连接服务成功
        sessionToken = mediaSession.sessionToken
        //3. 设置mediaSession回调
        mediaSession.setCallback(mediaSessionCallBack)


    }

    private val uAmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()
    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)//样式设置
            setHandleAudioBecomingNoisy(true)//降噪
            addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                    printPlaybackState(playbackState)
                    var state = PlaybackStateCompat.STATE_NONE
                    when(playbackState){
                        Player.STATE_IDLE     ->state =PlaybackStateCompat.STATE_NONE
                        Player.STATE_BUFFERING->state =PlaybackStateCompat.STATE_BUFFERING
                        Player.STATE_READY    ->{
                            sendMediaDataToUI()
                            state =if (playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
                        }
                        Player.STATE_ENDED    ->state = PlaybackStateCompat.STATE_STOPPED
                    }
                    setPlaybackState(state)
                }

            })
        }
    }

    /**
     * 发送媒体信息给UI
     */
    private fun sendMediaDataToUI() {
        val mediaMetadata = exoPlayer.mediaMetadata
        mediaMetadata.apply {
            val totalDuration = exoPlayer.duration/1000
            val mediaMetadataCompat = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, System.currentTimeMillis().toString())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title.toString())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, totalDuration)
                .build()
            mediaSession.setMetadata(mediaMetadataCompat)
        }

    }

    /**
     * 打印播放状态变更
     * @param playbackState Int
     */
    private fun printPlaybackState(playbackState: Int){
        val totalDuration = exoPlayer.duration/1000
        val currentPosition = exoPlayer.currentPosition/1000
        val str = when(playbackState){
            Player.STATE_IDLE     ->"无音频资源状态"
            Player.STATE_BUFFERING->"音频缓冲状态"
            Player.STATE_READY    ->if (exoPlayer.playWhenReady) "播放中" else "暂停"
            Player.STATE_ENDED    ->"已停止"
            else ->"未知的播放状态"
        }
        Log.i(TAG,"exoPlayer：已播放=${currentPosition} s 音频总时长=${totalDuration} s 播放状态=${playbackState}[${str}]")
    }



    /**
     * 接收客服端发送过来的自定义消息
     * @param action String
     * @param extras Bundle
     * @param result Result<Bundle>
     */
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        when(action){
            GET_PROGRESS->{
                val bundle = Bundle()
                bundle.putInt("currentPosition",(exoPlayer.currentPosition/1000).toInt())
                bundle.putInt("bufferedPosition",(exoPlayer.bufferedPosition/1000).toInt())
                result.sendResult(bundle)
                printPlayProgress()
            }
            GET_MUSIC_MEDIA->{
                result.detach()
                sendMediaDataToUI()
                val state =if (exoPlayer.playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
                setPlaybackState(state)
            }
        }
    }
    /**
     * 打印播放进度变更
     */
    private fun printPlayProgress(){
        exoPlayer.apply {
            val speed = playbackParameters.speed
            Log.i(TAG,"速度：${speed}倍 总时长：${(duration/1000).toInt()}s  已播放：${(currentPosition/1000).toInt()}s 已缓存：${(bufferedPosition/1000).toInt()}s isPlay：$isPlaying")
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
            exoPlayer.play()
        }

        override fun onPause() {
            super.onPause()
            Log.i(TAG, "onPause: ")
            exoPlayer.pause()
        }

        override fun onStop() {
            super.onStop()
            Log.i(TAG, "onStop: ")
            exoPlayer.stop()
        }
        //注意这里传递过来是秒，但seekTo是毫秒，所以要*1000
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            Log.i(TAG, "onSeekTo:$pos ")
            exoPlayer.seekTo(pos*1000)
        }
        //选择某个媒体资源id来播放，例如点击了音乐列表中的某首歌曲
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            Log.i(TAG, "onPlayFromMediaId:mediaId=$mediaId ")

        }
        //从歌曲列表中拿到上一首歌曲进行播放
        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            Log.i(TAG, "onSkipToPrevious:")
            if (isPlayingIndex>0){
                isPlayingIndex--
                val musicList = getMusicEntityList()
                val musicEntity = musicList[isPlayingIndex]
                val mediaItem = MediaItem.fromUri(musicEntity.url)
                exoPlayer.setMediaItem(mediaItem)
            }

        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            Log.i(TAG, "onSkipToNext:")
            val musicList = getMusicEntityList()
            if (isPlayingIndex<musicList.size-1){
                isPlayingIndex++
                val musicEntity = musicList[isPlayingIndex]
                val mediaItem = MediaItem.fromUri(musicEntity.url)
                exoPlayer.setMediaItem(mediaItem)
            }
        }

        override fun onSetPlaybackSpeed(speed: Float) {
            super.onSetPlaybackSpeed(speed)
            exoPlayer.setPlaybackSpeed(speed)
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
//            if (i == 0) {
//                mediaSession.setMetadata(metadataCompat)
//            }

            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    metadataCompat.description,
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                )
            )
            exoPlayer.addMediaItem(MediaItem.fromUri(musicEntity.url))
        }
        //当设置多首歌曲组成队列时报错
        // IllegalStateException: sendResult() called when either sendResult() or sendError() had already been called for: media_root_id
        //原因，之前在for处理了，应该在设置好mediaItems列表后，统一设置result
        result.sendResult(mediaItems)
        Log.i(TAG, "onLoadChildren: addMediaItem")


        exoPlayer.prepare()

        Log.i(TAG, "onLoadChildren: prepare")
        //每次连接，设置媒体资源和播放状态
        sendMediaDataToUI()
        val state =if (exoPlayer.playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        setPlaybackState(state)

    }



    /**
     * 设置mediaSession中的播放状态
     * @param state Int
     */
    private fun setPlaybackState(state: Int) {
        exoPlayer.apply {
            val speed = playbackParameters.speed
            Log.i(TAG,"设置mediaSession中的播放状态 速度：${speed}倍 总时长：${(duration/1000).toInt()}s  已播放：${(currentPosition/1000).toInt()}s 状态：$state")
            val playbackState = PlaybackStateCompat.Builder()
                .setState(state,currentPosition,speed)
                .build()
            mediaSession.setPlaybackState(playbackState)

        }

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
        val musicEntity =  MusicEntity("纯音乐1","http://music.163.com/song/media/outer/url?id=447925558.mp3")
        val musicEntity2 =  MusicEntity("纯音乐2","http://music.163.com/song/media/outer/url?id=447925559.mp3")
        list.add(musicEntity)
        list.add(musicEntity2)
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