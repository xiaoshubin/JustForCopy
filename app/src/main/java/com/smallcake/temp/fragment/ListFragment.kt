package com.smallcake.temp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentListBinding
import com.willy.ratingbar.BaseRatingBar

class ListFragment: BaseBindFragment<FragmentListBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEvent()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onEvent() {
        //评分控件
        bind.scaleRatingBar.setOnRatingChangeListener{ baseRatingBar: BaseRatingBar, rating: Float, fromUser: Boolean ->
            bind.tvScaleRatingBarDesc.text = "$rating 分"
        }
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
        bind.audioRecordBtn.setOnTouchListener{ v, event->

            if (event.action == MotionEvent.ACTION_DOWN) {
                bind.lineVoice.start()

            } else if (event.action == MotionEvent.ACTION_CANCEL|| event.action == MotionEvent.ACTION_UP) {
                bind.lineVoice.stop()
                bind.lineVoice.clearAnimation()
            } else if (event.action == MotionEvent.ACTION_MOVE) {

            }
            false
        }
    }


}