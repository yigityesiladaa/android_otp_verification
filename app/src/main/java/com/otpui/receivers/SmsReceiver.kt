package com.otpui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.otpui.SmsReceiverListener

class SmsReceiver : BroadcastReceiver() {

    var smsReceiverListener: SmsReceiverListener? = null

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            intent.extras?.let { extras ->
                val smsRetrieverStatus = extras.get(SmsRetriever.EXTRA_STATUS) as? Status
                smsRetrieverStatus?.let { status ->
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                            smsReceiverListener?.onSuccess(messageIntent)
                        }

                        CommonStatusCodes.TIMEOUT, CommonStatusCodes.ERROR -> {
                            smsReceiverListener?.onFailure()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
