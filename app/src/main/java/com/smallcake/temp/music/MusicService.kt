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


/**
 * MediaBrowserServiceCompat:是一个Service，封装了媒体相关的一些功能，可用于控制已通过MediaSessionCompat来与UI通讯
 * 1.需要在AndroidManifest中配置
<service android:name=".MusicService" >
    <intent-filter>
    <action android:name="android.media.browse.MediaBrowserService" />
    </intent-filter>
</service>

通过onGetRoot的返回值决定是否允许客户端连接。
onLoadChildren回调在Sercive中异步获取的数据给到MediaBrowser。也包含媒体播放器实例（比如我们本篇实践的ExoPlayer）
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
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        val playbackState = PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
        mediaSession.setPlaybackState(playbackState)
        //2. 设置mediaSession回调
        mediaSession.setCallback(mediaSessionCallBack)
        //3. 设置mediaSessionToken
        sessionToken = mediaSession.sessionToken
        //创建播放器实例
        exoPlayer = SimpleExoPlayer.Builder(applicationContext).build()


    }
    /**
     * 用于接收由MediaControl触发的改变，内部封装实现播放器和播放状态的改变
     */
    private val  mediaSessionCallBack = object:MediaSessionCompat.Callback(){
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

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            Log.i(TAG, "onSeekTo:$pos ")
            exoPlayer?.seekTo(pos)
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
        exoPlayer?.apply {addListener(object : Player.Listener{
                override fun onPlaybackStateChanged(state: Int) {
                    Log.i(TAG,"currentPositon=$currentPosition duduration=$duration state=$state")
                    var playbackState=PlaybackStateCompat.STATE_NONE
                    when(state){
                        Player.STATE_IDLE->playbackState =PlaybackStateCompat.STATE_NONE
                        Player.STATE_BUFFERING->playbackState =PlaybackStateCompat.STATE_BUFFERING
                        Player.STATE_READY->playbackState =if (playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
                        Player.STATE_ENDED->playbackState = PlaybackStateCompat.STATE_STOPPED
                    }
                    setPlaybackState(playbackState)
                }
        })}

    }

    /**
     * 设置mediaSession中的播放状态
     * @param playbackState Int
     */
    private fun setPlaybackState(playbackState: Int) {
        val speed = exoPlayer?.playbackParameters?.speed?:0f
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder().setState(playbackState, exoPlayer?.currentPosition?:0, speed).build())
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
     * 当服务关闭，释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}