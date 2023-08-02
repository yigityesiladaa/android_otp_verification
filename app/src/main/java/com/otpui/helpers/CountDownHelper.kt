package com.otpui.helpers

import android.os.CountDownTimer

object CountDownHelper {

    private var durationInMillis: Long = 0
    private var tickInterval: Long = 0
    private var onTick: ((Long) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null
    private var countDownTimer: CountDownTimer? = null
    private var isInitialized = false

    fun initializeCountDownHelper(
        durationInMillis: Long,
        tickInterval: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit,
    ): CountDownHelper {
        this.durationInMillis = durationInMillis
        this.tickInterval = tickInterval
        this.onTick = onTick
        this.onFinish = onFinish
        isInitialized = true
        return this
    }

    fun start() {
        checkInitialization()
        countDownTimer = object : CountDownTimer(durationInMillis, tickInterval) {
            override fun onTick(millisUntilFinished: Long) {
                onTick?.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish?.invoke()
            }
        }
        countDownTimer?.start()
    }

    fun cancel() {
        checkInitialization()
        countDownTimer?.cancel()
    }

    private fun checkInitialization() {
        if (!isInitialized) {
            throw IllegalStateException("CountDownHelper is not initialized. Call initializeCountDownHelper first.")
        }
    }
}
