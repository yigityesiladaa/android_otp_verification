package com.otpui

import android.content.Intent

interface SmsReceiverListener {
    fun onSuccess(intent: Intent?)
    fun onFailure()
}