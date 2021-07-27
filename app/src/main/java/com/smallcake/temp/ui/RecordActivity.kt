package com.smallcake.temp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.MediaUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityRecordBinding
import com.smallcake.temp.utils.L
import com.smallcake.temp.utils.showToast

/**
 * 录音页面
 * 1.权限申请
    1.1 AndroidManifest.xml中配置录音权限
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    1.2 动态申请权限
 * @see checkPermission
 * 2.按下录音
 * @see onStartAudioRecord
 * 3.显示语音动画
 * 4.获取语音文件时长
 * @see  MediaUtils.getAudioFileVoiceTime
 * 5.播放语音
 * @see  MediaUtils.playVoice
 */
class RecordActivity : BaseBindActivity<ActivityRecordBinding>() {

    private var touched = false//是否按着
    private var started = false//是否开始录音
    private var cancelled = false//是否取消了发送
    private var voicePath=""//音频文件路径
    private var voiceFileUri=""//音频文件上传后的路径

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
          bar.setTitle("录音")
          initView()
          onEvent()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onEvent() {
        //按钮按下录音，抬起录音结束，移动超出按钮范围取消
        bind.audioRecordBtn.setOnTouchListener{ v, event->
            val isPass = checkPermission()
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (isPass){
                    touched = true
                    onStartAudioRecord()
                }

            } else if (event.action == MotionEvent.ACTION_CANCEL|| event.action == MotionEvent.ACTION_UP) {
                touched = false
                onEndAudioRecord(isCancelled(v, event))
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                touched = true
                cancelAudioRecord(isCancelled(v, event))
            }
            false
        }
        //播放语音
        bind.btnVoicePlay.setOnClickListener{
            MediaUtils.playVoice(voicePath)
        }
    }
    /**
     * 开始录音
     */
    private fun onStartAudioRecord() {
        L.e("开始录音")
        cancelled = false
        bind.layoutRecording.visibility = View.VISIBLE
        bind.lineVoice.start()
        MediaUtils.startRecord(this)
    }
    /**
     * 结束录音
     * @param cancelled Any
     */
    private fun onEndAudioRecord(cancelled: Any) {
        L.e("结束录音")
        started = false
        bind.layoutRecording.visibility = View.GONE
        bind.lineVoice.stop()
        bind.lineVoice.clearAnimation()
        MediaUtils.stopRecord{
            L.e("录音文件地址为：$it")
            if (!TextUtils.isEmpty(it)){
                voicePath = it
                val voiceTime = MediaUtils.getAudioFileVoiceTime(it)
                bind.btnVoicePlay.text = "$voiceTime''"
                bind.btnVoicePlay.visibility = View.VISIBLE
            }
        }


    }



    /**
     * 上滑取消录音判断
     */
    private fun isCancelled(view: View, event: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        if (event.rawX < location[0] || event.rawX > location[0] + view.width || event.rawY < location[1] - 40) {
            return true
        }
        return false
    }
    /**
     * 取消录音
     * @param cancelled Boolean
     */
    private fun cancelAudioRecord(cancel: Boolean) {
        if (!started) {
            return
        }
        if (cancelled == cancel) {
            return
        }
        cancelled = cancel
        if (cancel) {
            L.e("手指松开，取消发送")
        } else {
            L.e("手指上滑，取消发送")
        }
    }
    fun checkPermission():Boolean {
        var isAgreePermission = false
        if (XXPermissions.isGrantedPermission(this, Permission.RECORD_AUDIO)) {
            isAgreePermission =  true
        } else {
            XXPermissions.with(this)
                .permission(Permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            isAgreePermission =  true
                        } else {
                            isAgreePermission =  false
                            L.e("获取部分权限成功,但部分权限未正常授予")
                            showToast("获取部分权限成功,但部分权限未正常授予")
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            isAgreePermission =  false
                            L.e("被永久拒绝授权，请手动授予权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(
                                this@RecordActivity,
                                permissions
                            )
                        } else {
                            showToast("获取权限失败")
                            isAgreePermission =  false
                        }
                    }

                })
        }
        return isAgreePermission
    }

    private fun initView() {
        bind.lineVoice.apply {
            duration = 150
            lineWidth = 3f
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
        }
    }

}