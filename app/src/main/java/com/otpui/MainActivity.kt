package com.otpui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.otpui.databinding.ActivityMainBinding
import com.otpui.helpers.CountDownHelper
import com.otpui.helpers.KeyboardHelper
import com.otpui.helpers.OtpHelper

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQ_USER_CONSENT = 200
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var otpHelper: OtpHelper
    private val countDownHelper = CountDownHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupTimer()
        setupResendOtpCodeButton()
    }

    private fun setupViews() {
        otpHelper = OtpHelper(this)
    }

    private fun setupTimer() {
        countDownHelper.initializeCountDownHelper(5 * 60 * 1000, 1000, { onTick(it) }, { onFinish() }).start()
    }

    private fun onTick(millisUntilFinished: Long) {
        val remainingSeconds = millisUntilFinished / 1000
        updateUI(remainingSeconds)
    }

    private fun onFinish() {
        updateUI(0)
    }

    private fun setupResendOtpCodeButton() {
        binding.btnResendOtpCode.setOnClickListener {
            with(binding) {
                countDownHelper.restart()
                customOtpView.clearOtp()
                customOtpView.setFocus()
                KeyboardHelper.showSoftKeyboard(this@MainActivity, customOtpView)
                btnResendOtpCode.visibility = View.GONE
            }
        }
    }

    private fun updateUI(remainingSeconds: Long) {
        val formattedTime = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)
        with(binding) {
            btnResendOtpCode.visibility = if (remainingSeconds == 0L) View.VISIBLE else View.GONE
            txtTimer.text = if (remainingSeconds == 0L) "" else getString(R.string.timer_text, formattedTime)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT && resultCode == RESULT_OK && data != null) {
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let {
                binding.run {
                    countDownHelper.cancel()
                    customOtpView.setOtpFromSms(it)
                    txtTimer.text = ""
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        otpHelper.stopSmsListener()
        countDownHelper.cancel()
    }
}
