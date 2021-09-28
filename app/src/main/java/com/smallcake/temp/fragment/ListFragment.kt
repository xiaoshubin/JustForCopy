package com.smallcake.temp.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.SeekBar
import com.smallcake.smallutils.Captcha
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentListBinding
import com.smallcake.temp.utils.PopShowUtils
import com.smallcake.temp.utils.showToast
import com.willy.ratingbar.BaseRatingBar
import java.util.*


class ListFragment: BaseBindFragment<FragmentListBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onEvent()

    }

    private fun initView() {
        //声音波纹控件
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

        //验证码控件
        val bitmap = Captcha.createBitmap("0527")
        bind.ivAuthCode.setImageBitmap(bitmap)
        //圆环控件
        animProgress(100,60f)
        //圆球
        Handler().postDelayed({
            bind.ball.setBgColor(Color.parseColor("#F5AD06"))
            bind.ball.setBottomColor(Color.parseColor("#FECE0A"))
        },3000)


    }
    private fun animProgress(max: Int, current: Float) {
        val percentage = 100f * current / max
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(bind.circlePercentProgress, "percentage", 0f, percentage)
        animator.duration = 2000
        animator.start()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun onEvent() {
        //评分控件
        bind.scaleRatingBar.setOnRatingChangeListener{ baseRatingBar: BaseRatingBar, rating: Float, fromUser: Boolean ->
            bind.tvScaleRatingBarDesc.text = "$rating 分"
        }
        //声音波纹控件
        bind.audioRecordBtn.setOnTouchListener{ v, event->
            if (event.action == MotionEvent.ACTION_DOWN) {
                bind.lineVoice.start()
            } else if (event.action == MotionEvent.ACTION_CANCEL|| event.action == MotionEvent.ACTION_UP) {
                bind.lineVoice.stop()
                bind.lineVoice.clearAnimation()
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                //用于取消发送语音

            }
            false
        }
        //时间选择器
        bind.tvYmdhm.setOnClickListener{
            PopShowUtils.showTimePicker(requireContext()){
                val showStr = TimeUtils.timeToStr((it.time/1000).toInt(),"yyyy-MM-dd HH:mm")
                bind.tvYmdhm.text = showStr
            }
        }
        bind.tvYmd.setOnClickListener{
            PopShowUtils.showYMD(requireContext()){year,month,day->
                bind.tvYmd.text = "$year-${month+1}-$day"
            }
        }
        bind.tvHm.setOnClickListener{
            PopShowUtils.showHM(requireContext()){h,m->
                bind.tvHm.text = "${h}时${m}分"
            }
        }
        //Spinner的显示框
        bind.spinner1.setOnSpinnerItemSelectedListener<String>{
                oldIndex, oldItem, newIndex, newText ->
            showToast("选中了$newText")
        }
        //区间选择器
        bind.sbRange1.setProgress(20f, 28f)
        bind.sbRange1.setIndicatorTextDecimalFormat("0");
        bind.sbRange1.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(rangeSeekBar: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                changeSeekBarThumb(rangeSeekBar.leftSeekBar, leftValue)
                changeSeekBarThumb(rangeSeekBar.rightSeekBar, rightValue)
                val a = leftValue.toInt()
                val b = rightValue.toInt()
                val min = Math.min(a,b)
                val max = Math.max(a,b)
                bind.tvAge.text = "年龄区间：$min - $max"
            }
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })

    }

    private fun changeSeekBarThumb(seekbar: SeekBar, value: Float){
        if (value < 33){
            seekbar.setThumbDrawableId(R.drawable.thumb_green, seekbar.thumbWidth, seekbar.thumbHeight)
        }else if (value < 66){
            seekbar.setThumbDrawableId(R.drawable.thumb_yellow, seekbar.thumbWidth, seekbar.thumbHeight)
        }else{
            seekbar.setThumbDrawableId(R.drawable.thumb_red, seekbar.thumbWidth, seekbar.thumbHeight)
        }
    }








}



