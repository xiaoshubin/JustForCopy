package com.smallcake.temp.utils

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.smallcake.temp.R

/**
 *
    Google登录工具类

# 一 文档和注意事项
    [google API 库](https://console.cloud.google.com/apis/library)
    [Google Cloud Platform 凭据](https://console.cloud.google.com/apis/credentials)
    [登录 Android文档](https://developers.google.com/identity/sign-in/android/start)
    [错误码](https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes#DEVELOPER_ERROR)
    注意：1.配置server_client_id一定要写凭据中创建的Web client凭据 否则错误码会报10
         2.网络一定要可以连接外网，否则报错误码7

# 二 集成步骤
    1.引入登录服务SDK
    //Google登录
    implementation 'com.google.android.gms:play-services-auth:19.2.0'
    2.AndroidStudio中的AndroidSDK->SDK Tools ->Googles Play services需要安装
    3.在https://console.cloud.google.com/projectselector2/apis/credentials中生成一个Android类型客服端
    配置对应的【包名】，【SHA1】，Google会自动创建一个对应的Web client,下面第4步就需要用到对应的Web client客服端ID
    4.在strings.xml中配置<string name="server_client_id">Web client客服端ID</string> 方便在配置参数中使用
# 三 如何使用
    1.创建此对象并监听回调
    private lateinit var gLogin:GoogleLoginProvider
    //在onCreate方法中创建此对象
    gLogin = GoogleLoginProvider(this)
    gLogin.setLoginCallback {
        bind.tvDesc.text = "Google登录成功：ID:${it?.id}\nemail:${it?.email}"
    }
    gLogin.isLoginCallback {
        bind.tvDesc.text = "是否已经登录：ID:${it?.id}\nemail:${it?.email}"
    }
    2.点击按钮调用登录操作
    bind.btnGoogleLogin.setOnClickListener{
        gLogin.signIn()
    }

 */
class GoogleLoginProvider(val activity: AppCompatActivity) {
    private val TAG = "GoogleLoginUtils"
    //google登录客户端
    private var mGoogleSignInClient: GoogleSignInClient ?=null
    private var cb:((GoogleSignInAccount?)->Unit)?=null
    private var isLoginCb:((GoogleSignInAccount?)->Unit)?=null
    //点击登录回调监听
    fun setLoginCallback(callback:((GoogleSignInAccount?)->Unit)){
        cb = callback
    }
    //是否已经登录监听
    fun isLoginCallback(callback:((GoogleSignInAccount?)->Unit)){
        isLoginCb = callback
    }
    init {
       //登录配置
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestEmail()
            .requestId()
            .requestIdToken(activity.getString(R.string.server_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        activity.lifecycle.addObserver(object :LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart(){
                val account = GoogleSignIn.getLastSignedInAccount(activity)
                L.e("google是否已经登录：${account?.account}")
                isLoginCb?.invoke(account)
            }
        })

    }

    //选择地址后返回
    private val register = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            val account = completedTask?.getResult(ApiException::class.java)
            cb?.invoke(account)
        }catch (e: ApiException) {
            when (e.statusCode) {
                7 -> { Log.e(TAG, "网络不给力，请再试一次")}
                10 -> {
                    Log.e(TAG, "开发者配置错误")}
                12500 -> {
                    Log.e(TAG, "当前设备不支持")}
                else -> { Log.e(TAG, "google signInResult:failed code=${e.statusCode} msg:${e.message}"  )}
            }

        }
    }

    fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        register.launch(signInIntent)
    }
}