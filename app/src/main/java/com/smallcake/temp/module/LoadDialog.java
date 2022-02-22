package com.smallcake.temp.module;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smallcake.temp.R;


public class LoadDialog extends Dialog {
	private String text;
	private AnimationDrawable frameAnimation;//创建帧动画的对象

	public LoadDialog(Context context) {
		this(context, "");
	}
	public LoadDialog(Context context, String text) {
		super(context, R.style.Theme_Ios_Dialog);
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smallcake_utils_loading_dialog);
		setCanceledOnTouchOutside(false);
		((TextView)findViewById(R.id.tv_load_dialog)).setText(TextUtils.isEmpty(text)?"加载中...":text);
		ImageView iv = findViewById(R.id.iv);
		frameAnimation = (AnimationDrawable) iv.getDrawable();

	}

	@Override
	public void show() {
		super.show();
		frameAnimation.start();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		frameAnimation.stop();
	}
}
