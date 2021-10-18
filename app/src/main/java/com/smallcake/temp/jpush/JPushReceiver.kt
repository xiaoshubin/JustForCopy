package com.smallcake.temp.jpush

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.CmdMessage
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver

/**
 * 极光推送接收回调
 * 参考：https://docs.jiguang.cn/jpush/client/Android/android_api/#_77
 *
 * 主要关注onAliasOperatorResult（别名设置操作）onNotifyMessageArrived（消息到达）
 *
 *
1.通过mavenCentral()动态引入极光推送
    //极光推送
    implementation 'cn.jiguang.sdk:jpush:4.3.0'
    implementation 'cn.jiguang.sdk:jcore:2.9.0'

2.moudel的build.gradle中的defaultConfig{}配置相关ndk和manifestPlaceholders

    ndk {
        //选择要添加的对应 cpu 类型的 .so 库。
        abiFilters 'armeabi', 'armeabi-v7a', 'arm64-v8a'
        // 还可以添加 'x86', 'x86_64', 'mips', 'mips64'
    }
    manifestPlaceholders = [
        JPUSH_PKGNAME : applicationId,
        JPUSH_APPKEY : "你的Appkey", //JPush 上注册的包名对应的 Appkey.
        JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
    ]
 3.新建class JPushService : JCommonService()和class JPushReceiver: JPushMessageReceiver()并在AndroidManifest.xml中配置
    <!-- 极光推送 开始-->
        <service android:name="com.smallcake.temp.jpush.JPushService"
        android:enabled="true"
        android:exported="true"
        android:process=":pushcore">
        <intent-filter>
        <action android:name="cn.jiguang.user.service.action" />
        </intent-filter>
        </service>
        <receiver
        android:name="com.smallcake.temp.jpush.JPushReceiver"
        android:enabled="true"
        android:exported="false" >
        <intent-filter>
        <action android:name="cn.jpush.android.intent.RECEIVE_MESSAGE" />
        <category android:name="${applicationId}" />
        </intent-filter>
        </receiver>
    <!-- 极光推送 结束-->

 4.初始化sdk,并在10秒后再设置别名，
    //极光推送初始化
    JPushInterface.setDebugMode(true)
    JPushInterface.init(this)
    //延迟10秒后设置别名，不然很容易设置别名失败
    Handler().postDelayed({
        JPushInterface.setAlias(this,0,"xiao")
    },10000)


 */
class JPushReceiver: JPushMessageReceiver() {
    private  val TAG = "JPushReceiver"

    //别名相关操作
    override fun onAliasOperatorResult(p0: Context?, msg: JPushMessage?) {
        super.onAliasOperatorResult(p0, msg)
        Log.d(TAG,"onAliasOperatorResult:${msg.toString()}")
    }
    //收到自定义消息回调
    override fun onMessage(p0: Context?, msg: CustomMessage?) {
        super.onMessage(p0, msg)
        Log.d(TAG,"onMessage:${msg.toString()}")
    }
    /**
     *  收到通知回调
     *  附加字段notificationExtras为一个json字段串，与后端商议相关字段
     *
     */
    override fun onNotifyMessageArrived(context: Context?, msg: NotificationMessage?) {
        super.onNotifyMessageArrived(context, msg)
        Log.d(TAG,"onNotifyMessageArrived:${msg.toString()}")
    }

    /**
     * 点击通知回调
     * 不想默认打开主页，注释super.onNotifyMessageOpened(p0, msg)
     */
    override fun onNotifyMessageOpened(context: Context?, msg: NotificationMessage?) {
//        super.onNotifyMessageOpened(context, msg)
        Log.d(TAG,"onNotifyMessageOpened:${msg.toString()}")

//        val agreementBean =  GsonUtils.jsonToObj(msg?.notificationExtras,AgreementBean::class.java)
//        val intent = Intent()
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.setClass(context!!,AgreementInfoActivity::class.java)
//        intent.putExtra("id",agreementBean?.contractAgreementId)
//        context.startActivity(intent)
    }
    //清除通知回调
    override fun onNotifyMessageDismiss(p0: Context?, msg: NotificationMessage?) {
        super.onNotifyMessageDismiss(p0, msg)
        Log.d(TAG,"onNotifyMessageDismiss:${msg.toString()}")
    }
    //注册成功回调
    override fun onRegister(p0: Context?, msg: String?) {
        super.onRegister(p0, msg)
        Log.d(TAG,"onRegister:${msg}")
    }
    //长连接状态回调
    override fun onConnected(p0: Context?, b: Boolean) {
        super.onConnected(p0, b)
        Log.d(TAG,"onConnected:${b}")
    }
    //交互事件回调
    override fun onCommandResult(p0: Context?, msg: CmdMessage?) {
        super.onCommandResult(p0, msg)
        Log.d(TAG,"onCommandResult:${msg.toString()}")
    }
    //通知的MultiAction回调
    override fun onMultiActionClicked(p0: Context?, intent: Intent?) {
        super.onMultiActionClicked(p0, intent)
        Log.d(TAG,"onMultiActionClicked:${intent.toString()}")
    }

}