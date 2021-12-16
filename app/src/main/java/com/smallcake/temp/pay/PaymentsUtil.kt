package com.smallcake.temp.pay

import android.app.Activity
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

/**
 * 参考：
 *  Google Pay官方教程:https://developers.google.cn/pay/api/android/guides/tutorial
 *  直接令牌化文档：https://developers.google.cn/pay/api/android/reference/request-objects#direct
 */
object PaymentsUtil {
    val CENTS = BigDecimal(100)
    /**
     * 第 1 步：指定您的 Google Pay API 版本
     */
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    /**
     * 第 2 步：为您的付款服务机构申请付款令牌
     * 将 example 和 exampleGatewayMerchantId 替换为付款服务机构的相应值
     * https://docs.aciworldwide.com/tutorials/mobile-sdk/google-pay
     */
    private fun gatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject(Constants.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS))
        }
    }

    /**
     * 第 2 步：直接令牌化
     * @return JSONObject
     */
    private fun getDirectTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "DIRECT")
            put("parameters", JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS))
        }
    }

    /**
     * 第 3 步：指定支持的支付卡网络
     */
    private val allowedCardNetworks = JSONArray(listOf(
        "AMEX",
        "DISCOVER",
        "INTERAC",
        "JCB",
        "MASTERCARD",
        "VISA"))
    /**
     * 支付卡的验证字段，
     * PAN_ONLY：返回的付款数据包括个人账号(PAN)以及到期月份和到期年份
     * CRYPTOGRAM_3DS：返回的付款数据包括在设备上生成的3d安全(3DS)密码
     */
    private val allowedCardAuthMethods = JSONArray(listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"))

    /**
     * 第 4 步：说明您允许的付款方式
     * allowedAuthMethods：     验证卡交易字段
     * allowedCardNetworks：    卡支持的网络
     * billingAddressRequired： 是否需要账单地址
     *billingAddressParameters：预期字段 format(MIN:名称、国家代码和邮政编码(默认  FULL:名称、街道地址、位置、地区、国家代码和邮政编码))
     */
    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {
            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {put("format", "FULL") })
            }
            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    /**
     * 扩展基本的卡【付款方式】对象，以说明预计会返回给您的应用的信息，其中必须包括令牌化的付款数据
     * @return JSONObject
     */
    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        // 设置stripe为付款方式或直接令牌化
        cardPaymentMethod.put("tokenizationSpecification", getDirectTokenizationSpecification())
        return cardPaymentMethod
    }

    /**
     * 第 5 步：创建 PaymentsClient 实例
     * @param activity [ERROR : Activity]
     * @return PaymentsClient
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(Constants.PAYMENTS_ENVIRONMENT)
            .setEnvironment(1)
            .build()
        return Wallet.getPaymentsClient(activity, walletOptions)

    }

    /**
     * 第 6 步：确定是否能使用 Google Pay API 进行付款
     */
    private fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * 在显示 Google Pay 按钮之前，请调用 isReadyToPay API 以确定用户是否能使用 Google Pay API 进行付款。如需查看配置属性的完整列表
     *
     * [IsReadyToPayRequest](https://developers.google.cn/pay/api/android/reference/request-objects#IsReadyToPayRequest)
     *
     */
     fun possiblyShowGooglePayButton(paymentsClient:PaymentsClient,cb:(Boolean)->Unit) {

        val isReadyToPayJson = isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.apply(cb)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
    }

    /**
     * 显示或隐藏支付按钮
     * @param b Boolean
     */
    fun setGooglePayAvailable(b: Boolean) {

    }

    /**
     * 金额信息
     * 第 7 步：创建 PaymentDataRequest 对象
     * @param price String
     * @return JSONObject
     * PaymentDataRequest JSON 对象描述了您要通过 Google Pay 付款表格向付款人请求的信息
     * 重要提示：如需在欧洲经济区 (EEA) 或其他任何遵守增强型客户身份验证 (SCA) 的国家/地区处理交易，
     * 商家必须添加 countryCode、totalPrice 和 merchantName 参数才能满足 SCA 要求
     */
    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode", Constants.COUNTRY_CODE)
            put("currencyCode", Constants.CURRENCY_CODE)
        }
    }

    /**
     * 商家名称
     */
    private val merchantInfo: JSONObject = JSONObject()
        .put("merchantName", "BIUBIU LIMIT")
        .put("merchantId", "BCR2DN6TQ7V6ZRSY")

    /**
     * 请求付款数据
     * @param price String
     * @return JSONObject?
     */
    fun getPaymentDataRequest(price: String): JSONObject? {
        try {
            return baseRequest.apply {
                // 指定是否支持 Google Pay API 所支持的一种或多种付款方式。
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                // 有关根据用户是否同意交易来为交易授权的详细信息。包含总价和价格状态
                put("transactionInfo", getTransactionInfo(price))
                //商家信息
                put("merchantInfo", merchantInfo)

                // An optional shipping address requirement is a top-level property of the
                // PaymentDataRequest JSON object.
                val shippingAddressParameters = JSONObject().apply {
                    put("phoneNumberRequired", false)
                    put("allowedCountryCodes", JSONArray(listOf("US", "GB")))
                }
                put("shippingAddressParameters", shippingAddressParameters)
                put("shippingAddressRequired", true)
            }
        } catch (e: JSONException) {
            return null
        }
    }
    /**
     * 第 8 步：为用户手势注册事件处理程序
     */

//    googlePayButton.setOnClickListener { requestPayment() }

    /**
     * 第 9 步：处理响应对象
     * 当付款人在 Google Pay 付款表格中成功提供所请求的信息后，系统会将 PaymentData 对象返回给 onActivityResult
     */

//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            // Value passed in AutoResolveHelper
//            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
//                when (resultCode) {
//                    RESULT_OK ->
//                        data?.let { intent ->
//                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
//                        }
//
//                    RESULT_CANCELED -> {
//                        // The user cancelled the payment attempt
//                    }
//
//                    AutoResolveHelper.RESULT_ERROR -> {
//                        AutoResolveHelper.getStatusFromIntent(data)?.let {
//                            handleError(it.statusCode)
//                        }
//                    }
//                }
//
//                // Re-enables the Google Pay payment button.
//                googlePayButton.isClickable = true
//            }
//        }
//    }

}


