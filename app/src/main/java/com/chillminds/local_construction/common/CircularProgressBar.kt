package com.chillminds.local_construction.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.chillminds.local_construction.R

class CircularProgressBar(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {
    private val mOval = RectF()
    private var mSweepAngle = 0f
    private val startAngle = -90
    private val angleGap = 0
    var mEndAngle = 1.0f
    var progressPaint = Paint()
    var textPaint = TextPaint()
    var incompletePaint = Paint()
    private var strokeWidth = 30.0f
    var text: String? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentAngleGap: Float =
            if (mSweepAngle == 1.0f || mSweepAngle == 0f) 0f else angleGap.toFloat()
        val x = strokeWidth / 2
        mOval[x, x, width - x] = width - x
        canvas.drawArc(
            mOval, -startAngle + currentAngleGap, mSweepAngle * 360 - currentAngleGap, false,
            progressPaint
        )

        drawText(canvas, textPaint, "$text")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    private fun drawText(canvas: Canvas, paint: Paint, text: String) {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = canvas.width / 2 - bounds.width() / 2
        val y = canvas.height / 2 + bounds.height() / 2
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
    }

    fun setProgressColor(color: Int) {
        progressPaint.color = color
    }

    fun setIncompleteColor(color: Int) {
        incompletePaint.color = color
    }

    fun setProgress(progress: Float) {
        if (progress.isNaN()) {
            return
        }
        if (progress > 1.0f || progress < 0) {
            return
        }
        mEndAngle = progress
        startAnimation()
    }

    private fun startAnimation() {
        val anim = ValueAnimator.ofFloat(mSweepAngle, mEndAngle)
        anim.addUpdateListener { valueAnimator: ValueAnimator ->
            mSweepAngle =
                valueAnimator.animatedValue as Float
            this@CircularProgressBar.invalidate()
        }
        anim.duration = 500
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressCircle,
            0, 0
        )
        val textColor: Int
        val textSize: Float
        val progressColor: Int
        val incompleteColor: Int
        try {
            textColor = a.getColor(
                R.styleable.ProgressCircle_android_textColor,
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            )
            textSize = a.getDimension(R.styleable.ProgressCircle_android_textSize, 100f)
            text = a.getString(R.styleable.ProgressCircle_android_text)
            strokeWidth = a.getDimension(R.styleable.ProgressCircle_strokeWidth, 30.0f)
            progressColor = a.getColor(
                R.styleable.ProgressCircle_progressColor,
                ContextCompat.getColor(context, android.R.color.holo_blue_bright)
            )
            incompleteColor = a.getColor(
                R.styleable.ProgressCircle_incompleteProgressColor,
                ContextCompat.getColor(context, android.R.color.darker_gray)
            )
        } finally {
            a.recycle()
        }
        progressPaint.color = progressColor
        progressPaint.strokeWidth = 8f
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.flags = Paint.ANTI_ALIAS_FLAG
        progressPaint.setShadowLayer(10f, 1f, 2f, Color.parseColor("#FFFFFFFF"))
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.color = textColor
        textPaint.textSize = textSize
        val tf = Typeface.create("lato", Typeface.BOLD)
        textPaint.typeface = tf

    }
}
