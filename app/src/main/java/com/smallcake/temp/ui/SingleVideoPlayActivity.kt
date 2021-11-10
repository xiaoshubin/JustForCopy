package com.smallcake.temp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivitySingleVideoPlayBinding

/**
 * 单个视频播放
 */
class SingleVideoPlayActivity : BaseBindActivity<ActivitySingleVideoPlayBinding>() {

    companion object {
        fun go(context: AppCompatActivity, videoUrl: String) {
            val intent = Intent(context, SingleVideoPlayActivity::class.java)
            intent.putExtra("videoUrl", videoUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("单个视频播放")
        bar.hide()
        val url = intent.getStringExtra("videoUrl")
        initVideoPlayer(url)
    }
    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }
    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    private fun initVideoPlayer(url: String?) {
        if (TextUtils.isEmpty(url)) return
        //1.设置url地址
        bind.videoPlayer.setUp(url, true, "")
        //2.设置封面
        val img = ImageView(this)
        Glide.with(img).load(url).into(img)
        bind.videoPlayer.apply {
            fullscreenButton.setOnClickListener {
                bind.videoPlayer.startWindowFullscreen(this@SingleVideoPlayActivity, true, true)
            }
            backButton.setOnClickListener{finish()}
        }
        //3.外部辅助的旋转，帮助全屏
        val orientationUtils = OrientationUtils(this, bind.videoPlayer)
        orientationUtils.isEnable = false
        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption.setThumbImageView(img)
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(true)
            .setNeedLockFull(false)
            .setUrl(url)
            .setCacheWithPlay(true)
            .setVideoTitle("")
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils.isEnable = true
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[0]) //title
                    Debuger.printfError("***** onQuitFullscreen **** " + objects[1]) //当前非全屏player
                    orientationUtils.backToProtVideo()
                }
            }).setLockClickListener { view, lock ->
                orientationUtils.isEnable = !lock
            }.build(bind.videoPlayer)
    }
}