package com.example.app.indexBar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.Math.max
import java.lang.Math.min

class IndexBar : View {
    private var mWidth = 0
    private var mHeight = 0
    private var mPaint: Paint
    private var mPressColor: Int
    private var mGap = 0f
    private val mOffset = 12
    private var mIndexStrings = arrayListOf<String>()
    private var mListener: IndexBarListener? = null

    interface IndexBarListener {
        fun onPress(pos: Int, index: String)
        fun onRelease()
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
//        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.s)
        val textColor = ContextCompat.getColor(context, android.R.color.holo_green_light)
        val indexTextSize = sp2px(context, 12f).toInt()
        mPressColor = ContextCompat.getColor(context, android.R.color.darker_gray)
        mPaint = Paint().apply {
            color = textColor
            textSize = indexTextSize.toFloat()
            isAntiAlias = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measureWidth = 0
        var measureHeight = 0
        val indexRect = Rect()
        mIndexStrings.forEach {
            mPaint.getTextBounds(it, 0, it.length, indexRect)
            measureWidth = max(indexRect.width() + mOffset, measureWidth)
            measureHeight = max(indexRect.height() + mOffset, measureHeight)
        }
        measureHeight *= mIndexStrings.size
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> measureWidth = MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> measureWidth =
                min(measureWidth, MeasureSpec.getSize(widthMeasureSpec))

            MeasureSpec.UNSPECIFIED -> {
            }
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> measureHeight = MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> measureHeight =
                min(measureHeight, MeasureSpec.getSize(heightMeasureSpec))

            MeasureSpec.UNSPECIFIED -> {
            }
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        mIndexStrings.forEachIndexed { index, s ->
            val baseline = (mGap - mPaint.fontMetrics.bottom - mPaint.fontMetrics.top) / 2
            canvas?.drawText(
                s,
                (mWidth - mPaint.measureText(s)) / 2,
                paddingTop + mGap * index + baseline,
                mPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    setBackgroundColor(mPressColor)
                }
                val y = event.y
                var index = ((y - paddingTop) / mGap).toInt()
                when {
                    index < 0 -> index = 0
                    index >= mIndexStrings.size -> index = mIndexStrings.size - 1
                }
                mListener?.onPress(index, mIndexStrings[index])
            }

            else -> {
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                mListener?.onRelease()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        computeGap()
    }

    private fun computeGap() {
        mGap = (mHeight - paddingTop - paddingBottom) / mIndexStrings.size.toFloat()
    }

    fun setListener(listener: IndexBarListener) {
        mListener = listener
    }

    fun setIndexList(list: List<String>) {
        mIndexStrings.clear()
        if (list.isNullOrEmpty()) {
            mIndexStrings.addAll(
                arrayListOf(
                    "A",
                    "B",
                    "C",
                    "D",
                    "E",
                    "F",
                    "G",
                    "H",
                    "I",
                    "J",
                    "K",
                    "L",
                    "M",
                    "N",
                    "O",
                    "P",
                    "Q",
                    "R",
                    "S",
                    "T",
                    "U",
                    "V",
                    "W",
                    "X",
                    "Y",
                    "Z",
                    "#"
                )
            )
        } else {
            mIndexStrings.addAll(list)
        }
        computeGap()
    }

    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

}