package com.otpui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.otpui.databinding.ActivityMainBinding
import com.otpui.helpers.CountDownHelper
import com.otpui.helpers.KeyboardHelper
import com.otpui.helpers.OtpHelper

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {

    companion object {
        const val REQ_USER_CONSENT = 200
    }

    lateinit var binding: ActivityMainBinding
    private var isTimerRunning = false
    private lateinit var otpHelper: OtpHelper
    private val countDownHelper = CountDownHelper

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupTimer()
        registerResendOtpCodeButton()
    }

    private fun setupViews() {
        otpHelper = OtpHelper(this)
    }

    private fun setupTimer() {
        countDownHelper.initializeCountDownHelper(
            5 * 60 * 1000,
            1000,
            { millisUntilFinished -> onTick(millisUntilFinished) },
            { onFinish() },
        ).start()
    }

    private fun onTick(millisUntilFinished: Long) {
        isTimerRunning = true
        val remainingSeconds = millisUntilFinished / 1000
        updateUI(remainingSeconds)
    }

    private fun onFinish() {
        isTimerRunning = false
        updateUI(0)
        binding.customOtpView.clearFocus()
        KeyboardHelper.hideSoftKeyboard(this@MainActivity, binding.customOtpView)
    }

    private fun registerResendOtpCodeButton() {
        binding.btnResendOtpCode.setOnClickListener {
            countDownHelper.cancel()
            countDownHelper.start()
            KeyboardHelper.showSoftKeyboard(this@MainActivity, binding.customOtpView)
            binding.run {
                customOtpView.clearOtp()
                customOtpView.isEnabled = true
                customOtpView.focusFirstOtpBox()
                btnResendOtpCode.visibility = View.GONE
            }
        }
    }

    private fun updateUI(remainingSeconds: Long) {
        val formattedTime = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)
        binding.run {
            btnResendOtpCode.visibility = if (remainingSeconds == 0L) View.VISIBLE else View.GONE
            txtTimer.text = if (remainingSeconds == 0L) "" else getString(R.string.timer_text, formattedTime)
            customOtpView.isEnabled = remainingSeconds == 0L
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                message?.let {
                    countDownHelper.cancel()
                    binding.run {
                        customOtpView.setOtpFromSms(it)
                        txtTimer.text = ""
                    }
                    Toast.makeText(this@MainActivity, "Success! message: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        otpHelper.stopSmsListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        otpHelper.stopSmsListener()
    }
}

