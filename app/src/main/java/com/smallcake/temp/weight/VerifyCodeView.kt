package com.smallcake.temp.weight

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.smallcake.temp.R

/**
 * 验证码6位框框输入，输入完成监听
 * @property editText EditText
 * @property textViews Array<TextView?>
 * @property inputCompleteListener InputCompleteListener?
 * @constructor
 * 1.首先布局文件中写
<com.yx.singer.home.weight.VerifyCodeView
android:layout_marginTop="16dp"
android:layout_marginLeft="32dp"
android:layout_marginRight="32dp"
android:id="@+id/verify_code_view"
android:layout_width="match_parent"
android:layout_height="56dp"/>
 2.然后页面中进行监听输入是否完毕，完毕就立即登录
verifyCodeView.setInputCompleteListener(object : VerifyCodeView.InputCompleteListener{
override fun inputComplete(editContent: String) {
  //去登录
}
override fun invalidContent(editContent: String) {}
})
 */
class VerifyCodeView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private val editText: EditText
    private val textViews: Array<TextView?>

    private fun setEditTextListener() {
        editText.doAfterTextChanged {
            val editContent = editText.text.toString()
            if (inputCompleteListener != null) {
                if (editContent.length >= MAX) {
                    inputCompleteListener?.inputComplete(editContent)
                } else {
                    inputCompleteListener?.invalidContent(editContent)
                }
            }
            for (i in 0 until MAX) {
                if (i < editContent.length) {
                    textViews[i]!!.text = editContent[i].toString()
                } else {
                    textViews[i]!!.text = ""
                }
            }
        }

    }

    private var inputCompleteListener: InputCompleteListener? = null
    fun setInputCompleteListener(inputCompleteListener: InputCompleteListener?) {
        this.inputCompleteListener = inputCompleteListener
    }

    interface InputCompleteListener {
        fun inputComplete(editContent: String)
        fun invalidContent(editContent: String)
    }

    companion object {
        private const val MAX = 6
    }

    init {
        inflate(context, R.layout.view_verify_code, this)
        textViews = arrayOfNulls(MAX)
        textViews[0] = findViewById(R.id.tv0)
        textViews[1] = findViewById(R.id.tv1)
        textViews[2] = findViewById(R.id.tv2)
        textViews[3] = findViewById(R.id.tv3)
        textViews[4] = findViewById(R.id.tv4)
        textViews[5] = findViewById(R.id.tv5)
        editText = findViewById(R.id.et)
        editText.isCursorVisible = false //隐藏光标
        setEditTextListener()
    }
}