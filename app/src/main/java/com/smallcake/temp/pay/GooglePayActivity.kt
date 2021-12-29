package com.smallcake.temp.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import com.android.billingclient.api.*
import com.google.android.gms.common.api.Status
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityGooglePayBinding
import com.smallcake.temp.utils.showToast
import com.smallcake.temp.utils.sizeNull
import com.smallcake.temp.utils.visiable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * google内购支付
 *
 * 1.build.gradle 引入：

    implementation "com.android.billingclient:billing:4.0.0"
    implementation 'com.google.android.gms:play-services-wallet:18.1.3'
    implementation 'com.google.android.gms:play-services-ads-identifier:17.1.0'
    implementation platform('com.google.firebase:firebase-bom:29.0.3')
    implementation 'com.google.firebase:firebase-analytics-ktx'

 * 2.AndroidManifest.xml 配置：
    <uses-permission android:name="com.android.vending.BILLING" />
    <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
    <meta-data
        android:name="com.google.android.gms.wallet.api.enabled"
        android:value="true" />

 * 最新 Google支付 Google Play 结算库 4.0 版：从创建定价、商品到测试、支付成功等步骤
 * https://blog.csdn.net/stone20011983/article/details/120883404
 *
 * bill支付的官方文档
 * https://developer.android.com/google/play/billing/integrate
 * 应用配置控制台
 * https://play.google.com/console
 *
 *海外Google Play-v4.0结算库流程
 * https://www.jianshu.com/p/7ad672555920
 *
 * 如何获取google-service.json，去firebase配置下载
 * https://console.firebase.google.com/
 *
 * 并添加firebase的依赖项
implementation platform('com.google.firebase:firebase-bom:29.0.3')
implementation 'com.google.firebase:firebase-analytics-ktx'
注意：
支付测试的账号要绑定了银行卡，要添加到测试人员中


 *
 */
@SuppressLint("SetTextI18n")
class GooglePayActivity : BaseBindActivity<ActivityGooglePayBinding>() {

    private val TAG = "GooglePayActivity"

    private var isConnectGooglePay=false//是否连接到GooglePay服务器
    private var skuDetails:SkuDetails?=null

    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("Google Pay")
        initView()


    }

    private fun initView() {
        //1.初始化google pay内购订单客户端
        billingClient = BillingClient.newBuilder(this.applicationContext)
            .setListener { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    Log.e(TAG, "支付完成")
                    showToast("支付完成")
                    //如果成功购买商品，系统还会生成购买令牌，它是一个唯一标识符，表示用户及其所购应用内商品的商品 ID
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Log.e(TAG, "用户取消了支付")
                    showToast("用户取消了支付")
                } else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    //商品已经购买过（重复购买了此商品，如果需要支持重复购买，需要将商品购买成功后消费掉）
                    Log.e(TAG, "商品已经购买过")
                    showToast("商品已经购买过")
                } else {
                    // Handle any other error codes.
                    Log.e(
                        TAG,
                        "result = [${result.responseCode}:${result.debugMessage}] $purchases"
                    )
                }
            }
            .enablePendingPurchases()
            .build()
        //2.连接 Google Play Service
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "断开连接")
                isConnectGooglePay = false
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "连接成功")
                    isConnectGooglePay = true
                    // 5. 查询商品详情

                    // 6. 支付商品
                } else {
                    Log.e(TAG, "连接失败")
                    isConnectGooglePay = false
                }
            }
        })
    }


    /**
     * 处理支付成功后的商品
     * 1。验证购买交易。
     * 2.向用户提供内容，并确认内容已传送给用户。还可以选择性地将商品标记为已消费，以便用户可以再次购买商品。
     *
     *
     * @param purchase Purchase?
     *
     * PurchaseState
     * 0 UNSPECIFIED_STATE 未指定状态
     * 1 PURCHASED         已经购买的
     * 2 PENDING           待处理
     */
    private fun handlePurchase(purchase: Purchase) {
        val purchaseToken = purchase.purchaseToken
        Log.e(TAG,"purchaseToken:$purchaseToken")
        if (TextUtils.isEmpty(purchaseToken))return

        //如果是订阅，需要确认订阅,订阅续订不需要确认
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            if (!purchase.isAcknowledged) {
//                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
//                    .setPurchaseToken(purchase.purchaseToken)
//                val ackPurchaseResult = withContext(Dispatchers.IO) {
//                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(),object :AcknowledgePurchaseResponseListener{
//                        override fun onAcknowledgePurchaseResponse(p0: BillingResult) {
//                            Log.e(TAG,"acknowledgePurchase结果:$p0")
//                        }
//
//                    })
//                }
//            }
//        }
        //如果是消耗类型商品，在后端确定收到token有效后，消耗此商品
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val consumeParams =ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
            billingClient.consumeAsync(consumeParams) { result, token ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK){
                    showToast("消费成功")
                }else{
                    showToast(result.debugMessage)
                }
                Log.e(TAG, "result = [${result.responseCode}:${result.debugMessage}] token:$token")
            }
        }


    }

    /**
     * 查询商品并开始支付
    int SERVICE_TIMEOUT = -3;          //服务超时
    int FEATURE_NOT_SUPPORTED = -2;    //不支持功能
    int SERVICE_DISCONNECTED = -1;     //服务单元已断开
    int OK = 0;                        //成功
    int USER_CANCELED = 1;             //用户按上一步或取消对话框
    int SERVICE_UNAVAILABLE = 2;       //网络连接断开
    int BILLING_UNAVAILABLE = 3;       //所请求的类型不支持 Google Play 结算服务 AIDL 版本
    int ITEM_UNAVAILABLE = 4;          //请求的商品已不再出售。
    int DEVELOPER_ERROR = 5;           //提供给 API 的参数无效。此错误也可能说明应用未针对结算服务正确签名或设置，或者在其清单中缺少必要的权限。
    int ERROR = 6;                     //API 操作期间出现严重错误
    int ITEM_ALREADY_OWNED = 7;        //未能购买，因为已经拥有此商品
    int ITEM_NOT_OWNED = 8;            //未能消费，因为尚未拥有此商品
     */
    private var skuDetailsList: List<SkuDetails>?=null
    private fun loadSkuAndPay(productId:String) {
        if (!isConnectGooglePay){
            showToast("没有Google服务组件")
            return
        }
        val selectSkuDetails = skuDetailsList?.find { it.sku==productId }
        if (selectSkuDetails==null){
            showToast("没有查询到可购买的商品")
            return
        }
        googlePay(selectSkuDetails)
    }

    /**
     * 开始google支付
     * 在提供待售商品之前，检查用户是否尚未拥有该商品。如果用户的消耗型商品仍在他们的商品库中，他们必须先消耗掉该商品，然后才能再次购买。
     * @param skuDetails SkuDetails?
     * ↓
     * onPurchasesUpdated()
     * ↓
     * PurchasesUpdatedListener
     * 在初始化客户端的setListener()中监听结果
     *
     * {
    "orderId": "GPA.3331-8453-4136-35343",
    "packageName": "com.xsd.wanan",
    "productId": "68_diamond",
    "purchaseTime": 1640488331214,
    "purchaseState": 0,
    "purchaseToken": "iljokkilelcapnhinendepad.AO-J1Ozp5H4mPadd4vq7k9oOHEzlYaYCI0T39g2E0ABRefoeTkBFhFcl6L-gJhAak5sZqZsXJdBmqNefMjtZe6XrKeoyuvjW1Q",
    "quantity": 1,
    "acknowledged": false
    }
     *
     */
    private fun googlePay(selectSkuDetails:SkuDetails?) {
        //购买前查询商品是否已经购买过，且没有被消费
        billingClient.queryPurchasesAsync(selectSkuDetails?.type?:"") { p0, p1 ->
            Log.e(TAG, "purchase:$p1")
        }
        selectSkuDetails?.let { it1 ->
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it1)
                .build()
            val responseCode = billingClient.launchBillingFlow(this, flowParams).responseCode
            if (responseCode==BillingClient.BillingResponseCode.OK){
//                showToast("成功启动...")
            }
            Log.e(TAG, "启动支付结果：$responseCode")
        }
    }


}


