package com.otpui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.otpui.R
import com.otpui.databinding.CustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var _binding: CustomToolbarBinding? = null
    private val binding get() = _binding!!
    private var title: String = ""

    init {
        initializeToolbarAttributes(context, attrs)
        initView()
    }

    private fun initializeToolbarAttributes(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
            title = typedArray.getString(R.styleable.CustomToolbar_title) ?: ""
            typedArray.recycle()
        }
    }

    private fun initView() {
        _binding = CustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        binding.run {
            txtToolbarTitle.text = title
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }
}