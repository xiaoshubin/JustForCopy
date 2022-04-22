package com.smallcake.temp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityLoginBinding
import com.smallcake.temp.utils.GoogleLoginProvider

/**
 * 快速集成各种第三方登录
 * 1.google登录
 * @see GoogleLoginProvider
 * 2.facebook 登录
 *  2.0平台申请相关key和id,
 * [facebook开发者平台](https://developers.facebook.com/)
 * [facebook-login](https://developers.facebook.com/docs/facebook-login/android)
 *
 *   2.0 AndroidManifest.xml配置setApplicationId，并在MyApplication中初始化
 *   2.1复制回调
 *   private lateinit var callbackManager: CallbackManager
 *   2.2创建和注册回调
 *   2.3在onActivityResult接收回调
 *
 */
class LoginActivity : BaseBindActivity<ActivityLoginBinding>() {
    private val TAG = "LoginActivity"
    private lateinit var gLogin: GoogleLoginProvider//google登录提供者
    private lateinit var callbackManager: CallbackManager//2.1facebook回调管理类

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        //google登录相关
        gLogin = GoogleLoginProvider(this)
        gLogin.setLoginCallback {
            bind.tvDesc.text = "Google登录成功：ID:${it?.id}\nemail:${it?.email}"
        }
        gLogin.isLoginCallback {
            bind.tvDesc.text = "是否已经登录：ID:${it?.id}\nemail:${it?.email}"
        }
        bind.btnGoogleLogin.setOnClickListener{
            gLogin.signIn()
        }

        //2.2facebook创建和注册回调
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val id = result?.accessToken?.userId?:""//此id就是唯一标示
                Log.d(TAG,"FaceBook登录成功id:${id}\n同意：${result?.recentlyGrantedPermissions}\n拒绝：${result?.recentlyDeniedPermissions}")
                // 拿到id，执行自己的login()操作
                bind.tvDesc.text = "FaceBook登录成功id:${id}"
            }
            override fun onCancel() {Log.d(TAG,"FaceBook登录取消")}
            override fun onError(error: FacebookException?) {Log.d(TAG,"FaceBook登录异常：$error")}
        })
        bind.btnFacebookLogin.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity,listOf("public_profile","email"))
        }
    }
    /**
     * 2.3接收回调
     * facebook只能用这种方式
     * @param requestCode Int
     * @param resultCode Int
     * @param data Intent?
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}