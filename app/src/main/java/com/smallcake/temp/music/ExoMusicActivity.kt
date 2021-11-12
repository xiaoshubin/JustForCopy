package com.smallcake.temp.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.smallutils.FormatUtils
import com.smallcake.smallutils.GsonUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoMusicBinding
import com.smallcake.temp.utils.PopShowUtils
import com.smallcake.temp.utils.sizeNull


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
    private val mAdapter = MusicAdapter()

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("音乐播放")
        initView()
        onEvent()
        MusicClient.instance.registerListener(musicClientListener)
        if (!durationSet)mHandler.sendEmptyMessageDelayed(1,100)


    }

    private fun initView() {
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExoMusicActivity)
            adapter = mAdapter

        }
    }

    private val musicClientListener = object : MusicClientListener() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            bind.btnPlay.text=if (PlaybackStateCompat.STATE_PLAYING == state.state)"暂停" else  "播放"
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            durationSet = false
            updateDuration(metadata)
        }
        override fun onProgress(currentDuration: Int, totalDuration: Int) {
            bind.startText.text = DateUtils.formatElapsedTime(currentDuration.toLong())
            bind.seekbar.progress = currentDuration
        }

         override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
              val songList =  children.map { mediaToSong(it) }
                mAdapter.setList(songList)
        }

    }
    private fun mediaToSong(media:MediaBrowserCompat.MediaItem):Song{
        (media.description as MediaMetadataCompat).apply {
            val name = this.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            val singer =this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
            val size : Long = 0
            val duration:Int  =this.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
            val path : String? = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
            val albumId  : Long = 0
            val id: Long = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toLong()
            return Song(name,singer,size,duration,path,albumId,id)
        }

    }

    private val mHandler = Handler{
        when(it.what){
            1->{
                MusicClient.instance.sendAction(GET_MUSIC_MEDIA)
                if (!durationSet)it.target.sendEmptyMessageDelayed(1,300)
            }
        }
        false
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicClient.instance.unregisterListener(musicClientListener)
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun onEvent(){
        bind.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                val max = seekBar.max
                Log.i(TAG, "onStopTrackingTouch: progress=$progress max=$max")
                MusicClient.instance.transportControls?.seekTo(progress.toLong())
            }
        })
        bind.btnPlay.setOnClickListener(this)
        bind.prev.setOnClickListener(this)
        bind.next.setOnClickListener(this)
        bind.speed.setOnClickListener(this)
        bind.btnBackgroundPlay.setOnClickListener(this)
        bind.btnSearchLocaMusic.setOnClickListener(this)

    }
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when(v?.id){
            //播放和暂停
            R.id.btn_play->
                MusicClient.instance.transportControls?.apply {
                        val state =  MusicClient.instance.mediaController?.playbackState?.state
                        if (state == PlaybackStateCompat.STATE_PLAYING)pause() else play()
                    }

            R.id.prev->MusicClient.instance.transportControls?.skipToPrevious()//上一首
            R.id.next->MusicClient.instance.transportControls?.skipToNext()    //下一首
            //倍数播放
            R.id.speed->{
                val speed: Float = getSpeed()
                bind.speed.text = "倍速 $speed"
                MusicClient.instance.transportControls?.setPlaybackSpeed(speed)
            }
            //后台播放，弹出一个悬浮框
            R.id.btn_background_play->{
                PopShowUtils.showMusicFloatWeight()
            }
            R.id.btn_search_loca_music->{
                val songList = MusicProvider.getLocaMusic()
                Log.e(TAG,"查询出来歌曲：${songList.sizeNull()}首")
                GsonUtils.printList(songList)
                mAdapter.setList(songList)
            }
        }
    }



    private var speedArray = floatArrayOf(0.5f, 1f, 1.5f, 2f)
    private var curSpeedIndex = 1
    private fun getSpeed(): Float {
        if (curSpeedIndex > 3) curSpeedIndex = 0
        return speedArray[curSpeedIndex++]
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

}

class MusicAdapter:BaseQuickAdapter<Song,BaseViewHolder>(R.layout.item_music){
    override fun convert(holder: BaseViewHolder, item: Song) {
        holder.setText(R.id.tv_name,item.name)
            .setText(R.id.tv_size,FormatUtils.formatSize(item.size)+"\t\t${ DateUtils.formatElapsedTime((item.duration/1000).toLong())}")
            .setText(R.id.tv_singer,item.singer)
    }

}