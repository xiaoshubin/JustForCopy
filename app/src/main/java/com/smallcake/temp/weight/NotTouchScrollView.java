package com.smallcake.temp.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;

/**
 * Author: Relin
 * Describe:ScrollView滑动冲突
 * Date:2020/5/7 9:43
 */
public class NotTouchScrollView extends RelativeLayout {

    private NestedScrollView scrollView;

    public NotTouchScrollView(Context context) {
        super(context);
    }

    public NotTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollView(NestedScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollView==null)return false;
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


}