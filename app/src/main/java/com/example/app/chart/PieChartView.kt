package com.example.app.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat

class PieChartView : View {
    private val defaultWidth = dp2px(context, 128f) //默认宽度
    private val defaultHeight = dp2px(context, 128f) //默认高度
    private val defaultStroke = dp2px(context, 20f)//默认环宽度
    private var mTypeCount = 6 //默认6种

    private var mWidth = 0
    private var mHeight = 0

    private lateinit var mList: ArrayList<Triple<Int, String, Double>>
    private lateinit var mRingPaint: Paint
    private lateinit var mDrawPath: Path
    private lateinit var mPathMeasure: PathMeasure
    private var mValueAnimator: ValueAnimator? = null

    private var mPathLength = 0f //线段总长
    private var mEndDistance = 0f //当前结束位置
    private var mPathStarts: MutableList<Float> = arrayListOf() //每段的起始位置
    private var mPathEnds: MutableList<Float> = arrayListOf() //每段的结束位置

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mRingPaint = Paint()
        mRingPaint.isAntiAlias = true
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.color = Color.parseColor("#5198FA")
        mRingPaint.strokeWidth = defaultStroke

        mList = arrayListOf()
        mDrawPath = Path()
        mPathMeasure = PathMeasure()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = if (widthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            defaultWidth.toInt()
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = if (heightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            defaultHeight.toInt()
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mEndDistance != 0f) {
            for (i in 0 until mTypeCount) {
                mDrawPath.reset()
                mDrawPath.rLineTo(0f, 0f)
                mRingPaint.color = ContextCompat.getColor(context, mList[i].first)
                if (mEndDistance >= mPathEnds[i]) {
                    mPathMeasure.getSegment(mPathStarts[i], mPathEnds[i], mDrawPath, true)
                } else {
                    mPathMeasure.getSegment(mPathStarts[i], mEndDistance, mDrawPath, true)
                }
                canvas.drawPath(mDrawPath, mRingPaint)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mValueAnimator?.isRunning == true) mValueAnimator?.cancel() //取消动画
    }

    //绘制圆环
    private fun drawRing() {
        val path = Path()
        val radius = minOf(mWidth, mHeight) / 2f
        path.addCircle(radius, radius, radius - mRingPaint.strokeWidth / 2f, Path.Direction.CW)
        mPathMeasure.setPath(path, false)
        mPathLength = mPathMeasure.length
        calculatePaths()
        mValueAnimator = ValueAnimator.ofFloat(0f, mPathLength)
        mValueAnimator?.duration = 2000
        mValueAnimator?.interpolator = AccelerateDecelerateInterpolator()
        mValueAnimator?.addUpdateListener {
            mEndDistance = it.animatedValue as Float
            invalidate()
        }
        mValueAnimator?.start()
    }

    //计算
    private fun calculatePaths() {
        var percentSum = 0.0
        mList.forEach {
            mPathStarts.add((percentSum * mPathLength).toFloat())
            percentSum += it.third
            mPathEnds.add((percentSum * mPathLength).toFloat())
        }
    }

    fun setPieData(list: ArrayList<Triple<Int, String, Double>>) {
        mWidth = measuredWidth
        mHeight = measuredHeight
        mList = list
        mTypeCount = list.size

        mPathStarts.clear()
        mPathEnds.clear()
        drawRing()
    }

    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

}