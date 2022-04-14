package com.smallcake.temp.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.smallcake.temp.ui.CustomCaptureActivity
import com.xuexiang.xqrcode.XQRCode
import com.xuexiang.xqrcode.ui.CaptureActivity

/**
 * Date: 2020/1/14
 * author: SmallCake
 * 1.需要引入
    implementation 'com.google.zxing:core:3.3.3'
    implementation 'com.github.xuexiangjys:XQRCode:1.1.0'
2.AndroidManifest.xml中添加如下Activity
    <activity
        android:exported="true"
        android:name="com.xuexiang.xqrcode.ui.CaptureActivity"
        android:configChanges="screenSize|keyboardHidden|orientation|keyboard"
        android:screenOrientation="portrait"
        android:theme="@style/XQRCodeTheme"
        android:windowSoftInputMode="stateAlwaysHidden" >
        <intent-filter>
        <action android:name="com.xuexiang.xqrcode.ui.captureactivity" />
        <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </activity>

 参考：
https://github.com/xuexiangjys/XQRCode

 */
object ZxingUtils {
    private const val FRAGMENT_TAG ="ScanInvisibleFragment"
    /**
     * 生成二维码,默认500大小
     * @param contents 需要生成二维码的文字、网址等
     * @return bitmap
     */
    fun createQRCode(contents: String?): Bitmap? {
        return XQRCode.createQRCodeWithLogo(contents,500, 500,null)

    }

    /**
     *
     *  cb:(Boolean,String?) 是否解析成功，解析成功的返回数据
     *  获得扫描二维码的结果和识别二维码的结果
     *
     */
    fun scanQRCode(activity: AppCompatActivity, cb:(Boolean,String?) -> Unit){
        val fragmentManager = activity.supportFragmentManager
        val existedFragment  = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
        val fragment = if (existedFragment!=null){
            existedFragment as ScanInvisibleFragment
        }else{
            val invisibleFragment = ScanInvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNow()
            invisibleFragment
        }
        fragment.startScan(activity,cb)
    }

}

/**
 * 扫码二维码的隐藏Fragment，用于简化回调onActivityResult
 */
class ScanInvisibleFragment : Fragment() {
    private var callback:((Boolean,String?) -> Unit)?=null

    fun startScan(activity: FragmentActivity, cb:(Boolean,String?) -> Unit) {
        callback = cb
        val intent = Intent(activity, CaptureActivity::class.java)
        this.startActivityForResult(intent, 78778)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //处理二维码扫描结果
        if (requestCode == 78778 && resultCode == AppCompatActivity.RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data)
        }
    }
    /**
     * 处理二维码扫描结果
     * @param data
     */
    private fun handleScanResult(data: Intent?) {
        data?.extras?.apply {
            when(getInt(XQRCode.RESULT_TYPE)){
                XQRCode.RESULT_SUCCESS->{
                    val result = getString(XQRCode.RESULT_DATA)
                   Log.e("解析结果","$result")
                    callback?.invoke(true,result)
                }
                XQRCode.RESULT_FAILED->{
                    Log.e("解析结果","解析二维码失败")
                    callback?.invoke(false,"")
                }
            }
        }
    }
}