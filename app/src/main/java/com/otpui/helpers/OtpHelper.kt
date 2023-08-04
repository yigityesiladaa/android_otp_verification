package com.otpui.helpers

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.otpui.MainActivity
import com.otpui.SmsReceiverListener
import com.otpui.receivers.SmsReceiver

class OtpHelper(private val context: Activity) {

    private var smsReceiver: SmsReceiver? = null

    init {
        startSmsListener()
        startSmartUserConsent()
    }

    private fun startSmartUserConsent() {
        SmsRetriever.getClient(context).startSmsUserConsent(null)
    }

    private fun startSmsListener() {
        smsReceiver = SmsReceiver().apply {
            smsReceiverListener = object : SmsReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    intent?.let {handleSmsReceived(intent)}
                }

                override fun onFailure() {
                    showToast("Error")
                }
            }
        }

        context.registerReceiver(smsReceiver, android.content.IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
    }

    private fun handleSmsReceived(intent: Intent) {
        context.startActivityForResult(intent, MainActivity.REQ_USER_CONSENT)
    }

    fun stopSmsListener() {
        smsReceiver?.let {
            context.unregisterReceiver(it)
            smsReceiver = null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
