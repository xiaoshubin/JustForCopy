package com.smallcake.temp.pay

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityGooglePayBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * google支付
 *
 * 1.build.gradle 引入：
implementation 'com.google.android.gms:play-services-wallet:18.1.3'
 * 2.AndroidManifest.xml 配置：
<meta-data
android:name="com.google.android.gms.wallet.api.enabled"
android:value="true" />


参考：
Google Play支付 接入配置 : https://blog.csdn.net/lxw1844912514/article/details/104657885

Android谷歌支付（Google pay）: https://www.jianshu.com/p/afe3a56b7a9f
 *
 */
class GooglePayActivity : BaseBindActivity<ActivityGooglePayBinding>() {

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    private lateinit var paymentsClient: PaymentsClient
    private lateinit var garmentList: JSONArray
    private lateinit var selectedGarment: JSONObject

    private val TAG = "GooglePayActivity"

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("Google Pay")

        selectedGarment = fetchRandomGarment()
        displayGarment(selectedGarment)


    }
    private fun fetchRandomGarment() : JSONObject {
        if (!::garmentList.isInitialized) {
//            garmentList = Json.readFromResources(this, R.raw.tshirts)
        }

        val randomIndex:Int = Math.round(Math.random() * (garmentList.length() - 1)).toInt()
        return garmentList.getJSONObject(randomIndex)
    }
    private fun displayGarment(garment:JSONObject) {
//        detailTitle.setText(garment.getString("title"))
//        detailPrice.setText("\$${garment.getString("price")}")
//
//        val escapedHtmlText:String = Html.fromHtml(garment.getString("description")).toString()
//        detailDescription.setText(Html.fromHtml(escapedHtmlText))
//
//        val imageUri = "@drawable/${garment.getString("image")}"
//        val imageResource = resources.getIdentifier(imageUri, null, packageName)
//        detailImage.setImageResource(imageResource)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }

                // Re-enables the Google Pay payment button.
                bind.googlePayButton.isClickable = true
            }
        }
    }

    /**
     * 支付异常
     * @param statusCode Int
     */
    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
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
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

//            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }
    }
}


