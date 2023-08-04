package com.otpui.components

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.otpui.R
import com.otpui.helpers.CountDownHelper
import com.otpui.helpers.KeyboardHelper

class CustomOtpView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnKeyListener {

    companion object {
        private const val NUMBER_OF_BOXES = 4
    }

    private val boxSize = getDimensionPixelSize(R.dimen.box_size)
    private val boxMargin = getDimensionPixelSize(R.dimen.box_margin)
    private val otpBoxes = ArrayList<EditText>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        repeat(NUMBER_OF_BOXES) {
            val editText = createOtpBox().apply {
                setOnKeyListener(this@CustomOtpView)
            }
            addView(editText)
            otpBoxes.add(editText)
        }
        setFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createOtpBox(): EditText = EditText(context).apply {
        layoutParams = getOtpBoxLayoutParams()
        gravity = Gravity.CENTER
        textAlignment = EditText.TEXT_ALIGNMENT_CENTER
        setBackgroundResource(R.drawable.circle_empty)
        setTextColor(context.getColor(android.R.color.transparent))
        textSize = 20f
        maxLines = 1
        isCursorVisible = false
        inputType = InputType.TYPE_CLASS_NUMBER
        isFocusable = true

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setFocus()
            }
            true
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentBoxIndex = otpBoxes.indexOf(this@apply)
                if (count == 1) {
                    val nextBoxIndex = currentBoxIndex + 1
                    if (nextBoxIndex < otpBoxes.size) {
                        otpBoxes[nextBoxIndex].requestFocus()
                    } else {
                        checkOtpStatus()
                    }
                }
                this@apply.setBackgroundResource(
                    if (text.isNotEmpty()) R.drawable.circle_filled
                    else R.drawable.circle_empty
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun setFocus() {
        otpBoxes.find { it.text.isEmpty() }?.requestFocus()
    }

    private fun checkOtpStatus() {
        val userEnteredOtpCode = otpBoxes.joinToString("") { it.text.toString() }
        val isOtpCorrect = userEnteredOtpCode == context.getString(R.string.otp_code)

        val message = if (isOtpCorrect) {
            CountDownHelper.cancel()
            clearFocusAndKeyboard()
            context.getString(R.string.otp_correct_text)
        } else {
            CountDownHelper.restart()
            requestFocusAndKeyboard()
            context.getString(R.string.otp_wrong_text)
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearFocusAndKeyboard() {
        isFocusable = false
        clearFocus()
        KeyboardHelper.hideSoftKeyboard(context, this)
    }

    private fun requestFocusAndKeyboard() {
        clearOtp()
        setFocus()
    }

    fun setOtpFromSms(smsContent: String) {
        val otpCodeRegex = Regex("""otp code: (\d{4})""", RegexOption.IGNORE_CASE)
        val matchedOtp = otpCodeRegex.find(smsContent)?.groupValues?.get(1)

        matchedOtp?.takeIf { it.length <= NUMBER_OF_BOXES }?.let { otp ->
            otpBoxes.forEachIndexed { index, box ->
                val isFilled = index < otp.length
                box.run {
                    setText(if (isFilled) otp[index].toString() else "")
                    setBackgroundResource(if (isFilled) R.drawable.circle_filled else R.drawable.circle_empty)
                }

                if (index == otp.lastIndex) {
                    if (isFilled) clearFocusAndKeyboard()
                } else if (isFilled) {
                    box.requestFocus()
                }
            }
        }
    }

    private fun getOtpBoxLayoutParams(): LayoutParams {
        val params = LayoutParams(boxSize, boxSize)
        params.marginEnd = boxMargin
        return params
    }

    fun clearOtp() {
        otpBoxes.forEachIndexed { index, box ->
            box.run {
                setText("")
                setBackgroundResource(R.drawable.circle_empty)
                isFocusable = true
            }
        }
        setFocus()
    }

    private fun getDimensionPixelSize(id: Int): Int {
        return resources.getDimensionPixelSize(id)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (v is EditText) {
            val currentBoxIndex = otpBoxes.indexOf(v)

            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN) {
                if (currentBoxIndex > 0 && v.text.isBlank()) {
                    val prevBox = otpBoxes[currentBoxIndex - 1]
                    prevBox.run {
                        requestFocus()
                        setBackgroundResource(R.drawable.circle_empty)
                        setText("")
                    }
                }
            }
        }

        return false
    }
}
