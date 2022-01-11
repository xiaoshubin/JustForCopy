package com.smallcake.temp.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
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
 * google支付
 *
 *
 * 1.build.gradle 引入：
 *
    //谷歌支付钱包服务
    implementation 'com.google.android.gms:play-services-wallet:18.1.3'
    //Android内购订单
    implementation 'com.android.billingclient:billing:4.0.0'

  2.AndroidManifest.xml 配置：启用服务并设置内购权限

    <uses-permission android:name="com.android.vending.BILLING" />

    <meta-data
    android:name="com.google.android.gms.wallet.api.enabled"
    android:value="true" />

3.创建BillingClient客户端
val billingClient: BillingClient = BillingClient.newBuilder(this.applicationContext).setListener { result, purchases ->
    if (result.responseCode==BillingClient.BillingResponseCode.OK&&purchases!=null){
    // 支付完成
    }else if (result.responseCode==BillingClient.BillingResponseCode.USER_CANCELED){
    // 用户取消了支付
    }else if (result.responseCode==BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
    //商品已经购买过（重复购买了此商品，如果需要支持重复购买，需要将商品购买成功后消费掉）
    }else{
    // Handle any other error codes.
    }
}
.build()


参考：
Google Play支付 接入配置 : https://blog.csdn.net/lxw1844912514/article/details/104657885
Android谷歌支付（Google pay）: https://www.jianshu.com/p/afe3a56b7a9f
Google Pay 谷歌支付（gateway = stripe）：https://blog.csdn.net/xp_panda/article/details/113187561
Android Google Pay 集成:https://blog.csdn.net/q4878802/article/details/88058343
 *
 */
@SuppressLint("SetTextI18n")
class GooglePayActivity : BaseBindActivity<ActivityGooglePayBinding>() {

    private val SHIPPING_COST_CENTS = 9 * PaymentsUtil.CENTS.toLong()

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    private lateinit var paymentsClient: PaymentsClient
    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject

    private val TAG = "GooglePayActivity"
    private var skuDetails:SkuDetails?=null

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("Google Pay")
        //模拟的商品数据
        selectedGarment = fetchRandomGarment()
        displayGarment(selectedGarment)

        paymentsClient = PaymentsUtil.createPaymentsClient(this)

        PaymentsUtil.possiblyShowGooglePayButton(paymentsClient){
//            Log.e(TAG,"是否支持Google支付：$it")
//            bind.googlePayButton.visibility = it.visiable()
        }

        //3. 初始化
        val billingClient: BillingClient = BillingClient.newBuilder(this.applicationContext)
            .setListener { result, purchases ->
                if (result.responseCode==BillingClient.BillingResponseCode.OK&&purchases!=null){
                    // TODO 支付完成
                }else if (result.responseCode==BillingClient.BillingResponseCode.USER_CANCELED){
                    // TODO 用户取消了支付
                }else if (result.responseCode==BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                    //TODO 商品已经购买过（重复购买了此商品，如果需要支持重复购买，需要将商品购买成功后消费掉）
                }else{
                    // Handle any other error codes.
                }
            }
            .enablePendingPurchases()
            .build()
        bind.googlePayButton.setOnClickListener{
//            requestPayment()
            //6. 支付商品
            skuDetails?.let { it1 ->
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it1)
                    .build()
                billingClient.launchBillingFlow(this, flowParams)
            }

        }

        //4. 连接 Google Play Service
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.e(TAG,"断开连接")
            }
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG,"连接成功")
                    // 5. 查询商品详情
                    // 6. 支付商品
                } else {
                    Log.e(TAG,"连接失败")
                }
            }
        })

        //5. 获取商品信息
        val params = SkuDetailsParams.newBuilder().apply {
            setSkusList(ArrayList<String>().apply {
                add("68_diamond") // 可以单个查询也可以多个查询
            }).setType(BillingClient.SkuType.SUBS)
        }
        billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
            Log.e(TAG,"responseCode = $responseCode   skuDetailsList = ${skuDetailsList?.size}")
            // responseCode 为响应码
            // skuDetailsList 为查询的商品信息列表
            if (skuDetailsList.sizeNull()>0){
                skuDetails = skuDetailsList?.get(0)
            }else{

                /**
                 * 'android.test.purchased',
                'point_1000',
                '5000_point',
                'android.test.canceled',
                 */
                val json  = JSONObject().apply {
                    put("productId","android.test.purchased")
                    put("price_amount_micros","point_1000")
                    put("price_currency_code","5000_point")
                    put("type","inapp")
                }
                skuDetails = SkuDetails(json.toString())
            }

        }


    }



    /**
     * 点击按钮开始支付
     */
    private fun requestPayment() {
        bind.googlePayButton.isClickable = false
        val garmentPrice = selectedGarment.getDouble("price")
        val priceCents =  Math.round(garmentPrice * PaymentsUtil.CENTS.toLong())+SHIPPING_COST_CENTS
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents.toString())
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
    }

    /**
     * 模拟商品数据
     * @return JSONObject
     */
    private fun fetchRandomGarment() : JSONObject {
        if (!::garmentList.isInitialized) {
            garmentList = Json.readFromResources(this,R.raw.tshirts)
        }

        val randomIndex:Int = Math.round(Math.random() * (garmentList.length() - 1)).toInt()
        return garmentList.getJSONObject(randomIndex)
    }

    private fun displayGarment(garment:JSONObject) {
        bind.detailTitle.text = garment.getString("title")
        bind.detailPrice.text = "\$${garment.getString("price")}"

        val escapedHtmlText:String = Html.fromHtml(garment.getString("description")).toString()
        bind.detailDescription.text = Html.fromHtml(escapedHtmlText)

        val imageUri = "@drawable/${garment.getString("image")}"
        val imageResource = resources.getIdentifier(imageUri, null, packageName)
        bind.detailImage.setImageResource(imageResource)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> data?.let { intent ->PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)}
                    RESULT_CANCELED -> showToast("cancle Google Pay")
                    AutoResolveHelper.RESULT_ERROR -> AutoResolveHelper.getStatusFromIntent(data)?.let { handleError(it)}
                }
                bind.googlePayButton.isClickable = true
            }
        }
    }

    /**
     * 支付异常
     * @param statusCode Int
     * 405 此商家未启用Google Pay
     */
    private fun handleError(status: Status) {
        Log.e("loadPaymentData failed", String.format("Error code: %d  msg: %s", status.statusCode,status.statusMessage))
    }

    /**
     * 处理支付成功结果
     * @param paymentData PaymentData
     */
    fun handlePaymentSuccess(paymentData: PaymentData){
        val paymentInformation = paymentData.toJson() ?: return
        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)
            // Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()
            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }
    }
}


