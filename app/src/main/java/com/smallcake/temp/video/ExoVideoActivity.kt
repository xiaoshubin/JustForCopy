package com.smallcake.temp.video

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityExoVideoBinding

/**
 * Github :https://github.com/google/ExoPlayer
 * 1.引入
 * implementation 'com.google.android.exoplayer:exoplayer:2.16.1'
 */
class ExoVideoActivity : BaseBindActivity<ActivityExoVideoBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("EXO播放器")
        initVideoPlayer()
    }

    private fun initVideoPlayer() {

    }
}