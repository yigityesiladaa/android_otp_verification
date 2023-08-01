package com.otpui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.otpui.R

class CustomCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val emptyPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var isCircleFilled: Boolean = false

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.CustomCircleView, defStyleAttr, 0
        )

        val circleColor = typedArray.getColor(
            R.styleable.CustomCircleView_circleColor, context.getColor(R.color.circle_empty)
        )
        fillPaint.color = if (isCircleFilled) circleColor else context.getColor(R.color.circle_empty)
        emptyPaint.color = circleColor

        isCircleFilled = typedArray.getBoolean(R.styleable.CustomCircleView_isCircleFilled, false)

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = width / 2f

        canvas?.drawCircle(radius, height / 2f, radius, if (isCircleFilled) fillPaint else emptyPaint)
    }

}
