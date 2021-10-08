package com.smallcake.temp.weight

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.tencent.smtt.sdk.TbsReaderView
import java.io.File

/**
 * 封装tbs显示 doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub等文件
 */
class SuperFileView @JvmOverloads constructor(@NonNull context: Context, @Nullable attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var mTbsReaderView: TbsReaderView?

    private fun getTbsReaderView(context: Context): TbsReaderView {
        return TbsReaderView(context, null)
    }

    fun displayFile(mFile: File?) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTemp = "/storage/emulated/0/TbsReaderTemp"
            val bsReaderTempFile = File(bsReaderTemp)
            if (!bsReaderTempFile.exists()) {
                Log.d(TAG,"准备创建/storage/emulated/0/TbsReaderTemp！！")
                val mkdir = bsReaderTempFile.mkdir()
                if (!mkdir) {
                    Log.e(TAG,"创建/storage/emulated/0/TbsReaderTemp失败！！！！！")
                }
            }

            //加载文件
            val localBundle = Bundle()
           Log.d(TAG,mFile.toString())
            localBundle.putString("filePath", mFile.toString())
            localBundle.putString("tempPath",Environment.getExternalStorageDirectory().toString() + "/" + "TbsReaderTemp")
            if (mTbsReaderView == null) mTbsReaderView = getTbsReaderView(context)
            val bool = mTbsReaderView!!.preOpen(getFileType(mFile.toString()), false)
            if (bool) {
                mTbsReaderView!!.openFile(localBundle)
            }
        } else {
           Log.e(TAG,"文件路径无效！")
        }
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
           Log.d(TAG,"paramString---->null")
            return str
        }
       Log.d(TAG,"paramString:$paramString")
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
           Log.d(TAG,"i <= -1")
            return str
        }
        str = paramString.substring(i + 1)
       Log.d(TAG,"paramString.substring(i + 1)------>$str")
        return str
    }

    fun onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView!!.onStop()
        }
    }

    companion object {
        private const val TAG = "SuperFileView"
    }

    init {
        mTbsReaderView = TbsReaderView(context, null)
        this.addView(mTbsReaderView, LinearLayout.LayoutParams(-1, -1))

    }
}