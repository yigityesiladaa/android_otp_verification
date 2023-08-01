package com.otpui.helpers

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.otpui.MainActivity
import com.otpui.SmsReceiverListener
import com.otpui.components.CustomOtpView
import com.otpui.receivers.SmsReceiver

@RequiresApi(Build.VERSION_CODES.Q)
class OtpHelper(private val context: Activity, private val customOtpView: CustomOtpView) {

    private var smsReceiver: SmsReceiver? = null

    init {
        startSmsListener()
        startSmartUserConsent()
    }

    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(context)
        client.startSmsUserConsent(null)
    }

    private fun startSmsListener() {
        smsReceiver = SmsReceiver().apply {
            setCustomOtpView(customOtpView)
            smsReceiverListener = object : SmsReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    intent?.let {
                        handleSmsReceived(intent)
                    }
                }

                override fun onFailure() {
                    showToast("Error")
                }
            }
        }

        val intentFilter = android.content.IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        context.registerReceiver(smsReceiver, intentFilter)
    }

    fun stopSmsListener() {
        smsReceiver?.let {
            context.unregisterReceiver(it)
            smsReceiver = null
        }
    }

    private fun handleSmsReceived(intent: Intent) {
        context.startActivityForResult(intent, MainActivity.REQ_USER_CONSENT)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
