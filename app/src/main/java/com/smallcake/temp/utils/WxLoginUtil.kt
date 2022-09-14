package com.smallcake.temp.utils

import android.content.Context
import com.smallcake.temp.base.Constant
import com.smallcake.temp.utils.showToast
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * 1.需要在对应包路径名下创建一个WXEntryActivity作为回调
class WXEntryActivity : Activity(),IWXAPIEventHandler {
override fun onCreate(@Nullable savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
val api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, true)
api.handleIntent(intent, this)
//        finish()
}
override fun onReq(request: BaseReq?) {
L.e("微信发送请求：$request")
}

override fun onResp(response: BaseResp?) {
L.e("微信接收请求：${response.toString()}")
val result: String = when (response?.errCode) {
BaseResp.ErrCode.ERR_OK -> "登录成功"
BaseResp.ErrCode.ERR_AUTH_DENIED -> "用户拒绝授权"
BaseResp.ErrCode.ERR_USER_CANCEL -> "用户取消"
else -> "失败"
}
showToast("${response?.errCode}:$result")
}
}
 2.使用
 首先通过initWx获取api
 *@see initWx
 * 然后通过api去登录
 *@see longinWx
 */
object WxLoginUtil {
    val WX_APP_ID="你申请的微信APP_ID"
    /**
     * 初始化
     */
    fun initWx(context: Context):IWXAPI {
        val api = WXAPIFactory.createWXAPI(context, WX_APP_ID, true)
        api.registerApp(WX_APP_ID)
        val msgApi = WXAPIFactory.createWXAPI(context, null)
        // 将该app注册到微信
        msgApi.registerApp(WX_APP_ID)
        return api
    }

    /**
     * 微信登录
     */
    fun longinWx(api:IWXAPI) {
        if (!api.isWXAppInstalled) {
            showToast("未安装微信客户端")
            return
        }
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wechat_sdk_demo_test"
        api.sendReq(req)
    }
}