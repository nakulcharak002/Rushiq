package com.example.rushiq.ui.theme.payment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rushiq.ui.theme.viewmodels.PaymentViewModel
import com.example.rushiq.ui.theme.utils.PaymentStatus
import com.example.zepto.BuildConfig
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PaymentActivity : ComponentActivity(), PaymentResultListener {

    private var checkout: Checkout? = null
    private val TAG = "PaymentActivity"

    private lateinit var paymentViewModel: PaymentViewModel

    private var totalAmount: Double = 0.0
    private var orderId: String = ""
    private var userEmail: String = ""
    private var userPhone: String = ""

    private val razorpayCleanupReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Intercepted RazorPay broadcast: ${intent?.action}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Payment activity starting")

        extractIntentData()
        initializeRazorPay()

        setContent {
            paymentViewModel = viewModel()
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                paymentViewModel.setAmount(totalAmount)
                paymentViewModel.setOrderId(orderId)
                paymentViewModel.setUserEmail(userEmail)
                paymentViewModel.setUserPhone(userPhone)
            }

            MaterialTheme {
                NavHost(navController = navController, startDestination = "payment_summary") {
                    composable("payment_summary") {
                        PaymentSummaryScreen(
                            paymentViewModel = paymentViewModel,
                            onPaymentInit = { amount -> startRazorpayPayment(amount) },
                            onNavigateBack = { finish() }
                        )
                    }
//                    composable("payment_success/{paymentId}"){backStackEntry->
//                        val paymentId = backStackEntry.arguments?.getString("paymentId")?:""
//                        PaymentSuccessScreen(
//                            paymentId = paymentId,
//                            onDone ={
//                                setResult(RESULT_OK)
//                                finish()
//                            }
//                        )
//
//                    }
                }
            }
        }
    }

    private fun extractIntentData() {
        try {
            totalAmount = intent?.getDoubleExtra("TOTAL_AMOUNT", 0.0) ?: 0.0
            orderId = intent?.getStringExtra("ORDER_ID") ?: ""
            userEmail = intent?.getStringExtra("USER_EMAIL") ?: ""
            userPhone = intent?.getStringExtra("USER_PHONE") ?: ""
            Log.d(TAG, "Extracted data: Amount=$totalAmount, Email=$userEmail, Phone=$userPhone")
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting intent data", e)
        }
    }

    private fun initializeRazorPay() {
       try {
           Checkout.preload(applicationContext)
           val intentFilter = IntentFilter().apply {
               addAction("com.google.android.gms.auth.api.phone.SMS_RETRIEVED")
               addAction("android.provider.Telephony.SMS_RECEIVED")
                addAction("rzp.device_token.shared")
           }
           registerReceiver(razorpayCleanupReceiver , intentFilter , RECEIVER_NOT_EXPORTED)

       }catch (e: Exception){
           Log.e(TAG,"error initializing razorpay, e")

       }
    }


    private fun startRazorpayPayment(amount: Double) {
        try {
            checkout = Checkout()

            val apiKey = BuildConfig.PAYMENT_API_KEY
            if (apiKey.isBlank()) {
                throw IllegalStateException("PAYMENT_API_KEY is missing or empty")
            }
            Log.d(TAG, "Using API Key: ${apiKey.take(10)}...")

            checkout?.setKeyID(apiKey)

            val options = JSONObject().apply {
                put("name", "Rushiq")
                put("description", "Order Payment")
                put("currency", "INR")
                put("amount", (amount * 100).toInt()) // amount in paise

                put("prefill", JSONObject().apply {
                    put("email", userEmail)
                    put("contact", userPhone)
                })

                put("theme", JSONObject().apply {
                    put("color", "#FF3F6C")
                })

                put("readonly", JSONObject().apply {
                    put("sms_retriever", false)
                })
            }

            checkout?.open(this, options)

        } catch (e: Exception) {
            Log.e(TAG, "Error starting Razorpay payment", e)
            Toast.makeText(this, "Payment setup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String) {
        Log.d(TAG, "Payment successful with ID: $razorpayPaymentId")

        try {
            if (!::paymentViewModel.isInitialized) {
                Log.e(TAG, "PaymentViewModel is not initialized")
                return
            }

            val amount = paymentViewModel.amount.value
            val orderId = paymentViewModel.orderId.value

            val cartItems = intent?.getStringArrayListExtra("CART_ITEMS_DATA")?.toList() ?: emptyList()

            @Suppress("UNCHECKED_CAST")
            val imageUrlsIntent = intent?.getSerializableExtra("CART_ITEM_IMAGES") as? HashMap<String, String>
            val itemImageUrls = imageUrlsIntent ?: emptyMap()

            Log.d(TAG, "Payment successful - Cart items: ${cartItems.size}, Image URLs: ${itemImageUrls.size}")

            itemImageUrls.forEach { (itemId, url) ->
                Log.d(TAG, "Item $itemId has image URL: $url")
            }

            paymentViewModel.setItemImageUrls(itemImageUrls)

            paymentViewModel.savePaymentRecord(
                paymentId = razorpayPaymentId,
                orderId = orderId,
                amount = amount,
                items = cartItems,
                itemImageUrls = itemImageUrls
            )

            val resultIntent = Intent().apply {
                putExtra("PAYMENT_ID", razorpayPaymentId)
                putExtra("ORDER_ID", orderId)
                putExtra("PAYMENT_SUCCESS", true)
            }
            setResult(RESULT_OK, resultIntent)

            paymentViewModel.getNavController()?.navigate("payment_success/$razorpayPaymentId")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing payment success", e)
            Toast.makeText(this, "Payment successful but processing failed", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        val errorDescription = response ?: "Unknown error"
        Log.e(TAG, "Payment failed with code: $code, description: $errorDescription")

        Toast.makeText(this, "Payment failed: $errorDescription", Toast.LENGTH_LONG).show()

        paymentViewModel.updatePaymentStatus(PaymentStatus.Error(errorDescription))
        paymentViewModel.getNavController()?.popBackStack()

        cleanup()
    }

    private fun cleanup() {
        checkout = null
        try {
            Checkout.clearUserData(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error in Razorpay cleanup", e)
        }
    }

    override fun onStop() {
        super.onStop()
        cleanup()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(razorpayCleanupReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
}
