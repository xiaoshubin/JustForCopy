package com.smallcake.temp.music

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.NonNull
import com.smallcake.temp.MyApplication

/**
 * 音乐客户端管理类
 * 参考：
 * 打造基于MediaSessionCompat的音乐播放（二）：https://blog.csdn.net/ckwccc/article/details/80568188
 */
class MusicClient {
    private val TAG = "MusicClientManager"
    var isConnect:Boolean = false//是否已经连接
    lateinit var mediaBrowser:MediaBrowserCompat
    var mediaController:MediaControllerCompat?=null
    var transportControls: MediaControllerCompat.TransportControls?=null
    private var listeners: ArrayList<MusicClientListener> = ArrayList()

    fun registerListener(musicClientListener: MusicClientListener){
        listeners.add(musicClientListener)
    }
    fun unregisterListener(musicClientListener: MusicClientListener){
        listeners.remove(musicClientListener)
    }



    companion object {
        // 懒汉式，使用 LazyThreadSafetyMode.SYNCHRONIZED 双重同步锁
        val instance: MusicClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MusicClient()
        }
    }

    init {
        mediaBrowser = MediaBrowserCompat(MyApplication.instance,
            ComponentName(MyApplication.instance, MusicService::class.java),
            object : MediaBrowserCompat.ConnectionCallback() {
                override fun onConnected() {
                    super.onConnected()
                    val sessionToken = mediaBrowser.sessionToken
                    Log.e(TAG, "onConnected：$TAG 已与 $sessionToken 建立连接")
                    //通过媒体会话token创建媒体控制器并与之关联
                    mediaController = MediaControllerCompat(MyApplication.instance, sessionToken)
                    //订阅监听媒体文件变化
                    subscribe()
                    //MediaController发送命令
                    mediaController?.registerCallback(mediaControllerCallback)
                    transportControls = mediaController?.transportControls
                    isConnect = true
                    mHandler.sendEmptyMessage(0)
                }
            },
            null)
        mediaBrowser.connect()
    }
    /**
     * 订阅媒体文件来发起数据请求
     * 客户端通过调用subscribe方法，传递MediaID，在SubscriptionCallback的方法中进行处理
     * MediaBrowser是通过订阅方式向Service请求数据的
     */
    private fun subscribe() {
        mediaBrowser.apply {
            //先取消订阅
            unsubscribe(root)
            subscribe(root, object :MediaBrowserCompat.SubscriptionCallback(){
                //数据获取成功后的回调
                override fun onChildrenLoaded(@NonNull parentId: String, @NonNull children: List<MediaBrowserCompat.MediaItem>) {
                    super.onChildrenLoaded(parentId, children)
                    listeners.forEach{it.onChildrenLoaded(parentId,children)}
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
    val mediaControllerCallback = object: MediaControllerCompat.Callback() {
        //这里的回调，只有用户触发的才会有相应的回调。
        //ExoPlayer getDuration : https://stackoverflow.com/questions/35298125/exoplayer-getduration
        //播放状态变化回调：usicService: mediaSession.setPlaybackState(playbackState)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            super.onPlaybackStateChanged(state)
            Log.i(TAG, "播放状态改变为：" + if (PlaybackStateCompat.STATE_PLAYING == state.state)"播放中" else  "暂停")
            listeners.forEach{it.onPlaybackStateChanged(state)}

        }
        //播放的媒体数据发生变化时的回调：MusicService: mediaSession.setMetadata(mediaMetadataCompat)
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            super.onMetadataChanged(metadata)
            listeners.forEach{it.onMetadataChanged(metadata)}
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

    private val mHandler = Handler{
        when(it.what){
            0->{
                sendAction(GET_PROGRESS)
                it.target.sendEmptyMessageDelayed(0,1000)
            }
        }

        false
    }

     fun sendAction(cmdStr:String){
        if (!isConnect)return
        mediaBrowser.sendCustomAction(cmdStr,null,object :MediaBrowserCompat.CustomActionCallback(){
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                super.onResult(action, extras, resultData)
                when(cmdStr){
                    GET_PROGRESS->{
                        val currentPosition = resultData?.getInt("currentPosition",0)?:0
                        val duration = resultData?.getInt("duration",0)?:0
                        listeners.forEach{it.onProgress(currentPosition,duration)}

                    }

                }

            }


        })
    }



}