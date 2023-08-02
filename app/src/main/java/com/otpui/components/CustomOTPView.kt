package com.otpui.components

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.otpui.R

@RequiresApi(Build.VERSION_CODES.Q)
class CustomOtpView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnKeyListener {

    companion object {
        private const val NUMBER_OF_BOXES = 4
    }
    private val boxWidth = getDimensionPixelSize(R.dimen.box_width)
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
        otpBoxes[0].requestFocus()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createOtpBox(): EditText = EditText(context).apply {
        layoutParams = getOtpBoxLayoutParams()
        gravity = Gravity.CENTER
        textAlignment = EditText.TEXT_ALIGNMENT_CENTER
        setBackgroundResource(R.drawable.circle_empty)
        setTextColor(context.getColor(android.R.color.transparent))
        textSize = 20f
        maxLines = 1
        inputType = InputType.TYPE_CLASS_NUMBER
        setPadding(0, 0, 0, 0)
        textCursorDrawable = null

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentBoxIndex = otpBoxes.indexOf(this@apply)
                if(count == 1){
                    val nextBoxIndex = currentBoxIndex + 1
                    if (nextBoxIndex < otpBoxes.size) {
                        otpBoxes[nextBoxIndex].requestFocus()
                    } else {
                        clearFocus()
                    }
                    this@apply.setBackgroundResource(R.drawable.circle_filled)
                }
                if (currentBoxIndex == otpBoxes.size - 1 && count == 1) clearFocus()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    fun setOtpFromSms(smsContent: String) {
        val otpCodeRegex = Regex("""otp code: (\d{4})""", RegexOption.IGNORE_CASE)
        otpCodeRegex.find(smsContent)?.groupValues?.get(1)?.forEachIndexed { index, char ->
            if (index < otpBoxes.size) {
                val box = otpBoxes[index]
                box.apply {
                    setText(char.toString())
                    setBackgroundResource(R.drawable.circle_filled)
                    if (index == otpBoxes.size - 1) {
                        clearFocus()
                    } else {
                        otpBoxes[index + 1].requestFocus()
                    }
                }
            }
        }
    }

    private fun getOtpBoxLayoutParams(): LayoutParams {
        val params = LayoutParams(boxWidth, boxWidth)
        params.marginEnd = boxMargin
        return params
    }

    fun clearOtp() {
        otpBoxes.forEachIndexed { index, box ->
            box.apply {
                setText("")
                setBackgroundResource(R.drawable.circle_empty)
                if (index == 0) requestFocus()
            }
        }
    }

    private fun getDimensionPixelSize(id: Int): Int {
        return resources.getDimensionPixelSize(id)
    }

    fun focusFirstOtpBox() {
        otpBoxes[0].requestFocus()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (v is EditText) {
            val currentBoxIndex = otpBoxes.indexOf(v)

            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN) {
                if (currentBoxIndex > 0 && v.text.isBlank()) {
                    val prevBox = otpBoxes[currentBoxIndex - 1]
                    prevBox.apply {
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
