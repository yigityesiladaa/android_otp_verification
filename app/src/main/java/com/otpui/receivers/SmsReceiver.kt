package com.otpui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.otpui.SmsReceiverListener
import com.otpui.components.CustomOtpView

class SmsReceiver : BroadcastReceiver() {
    private lateinit var customOtpView: CustomOtpView
    var smsReceiverListener: SmsReceiverListener? = null

    fun setCustomOtpView(customOtpView: CustomOtpView) {
        this.customOtpView = customOtpView
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onReceive(context: Context, intent: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    smsReceiverListener?.onSuccess(messageIntent)
                }

                CommonStatusCodes.TIMEOUT -> {
                    smsReceiverListener?.onFailure()
                }
            }
        }

    }
}
