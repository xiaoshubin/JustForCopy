package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityLoginBinding
import com.smallcake.temp.utils.GoogleLoginProvider

class LoginActivity : BaseBindActivity<ActivityLoginBinding>() {

    private lateinit var gLogin: GoogleLoginProvider
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
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
    }


}