package com.smallcake.temp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.smallcake.smallutils.MediaUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMainBinding
import com.smallcake.temp.utils.BottomNavUtils
import com.smallcake.temp.utils.ldd

/**
 * @see com.smallcake.temp.fragment.PageFragment 页面
 * @see com.smallcake.temp.fragment.ListFragment 控件
 * @see com.smallcake.temp.fragment.MineFragment 我的
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        initView()
        onEvent()
    }

    private fun onEvent() {}

    private fun initView() {
        BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("REQUEST_CODE","requestCode:$requestCode")
        if ( requestCode == 8) {
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            Log.e("MainActivity", "${result.contents}")
            if (result.contents == null) {
                val originalIntent: Int = result.orientation
                if (originalIntent == null) {
                    Log.e("MainActivity", "Cancelled scan")
                } else if (originalIntent.hashCode() == Intents.Scan.MIXED_SCAN) {
                    Log.e("MainActivity", "Cancelled scan due to missing camera permission")
                }
            } else {
                Log.d("MainActivity", "Scanned")
            }
        }

    }




}


