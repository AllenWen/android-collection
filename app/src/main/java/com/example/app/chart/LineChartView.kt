package com.example.app.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.app.R


class LineChartView : View {
    private val defaultWidth = getScreenWidth(context) - dp2px(context, 24f) //默认宽度
    private val defaultHeight = dp2px(context, 150f) //默认高度
    private val mTopOffset = dp2px(context, 15f) //坐标系相对顶部的偏移量
    private val mBottomOffset = dp2px(context, 20f) //坐标系相对于底部的偏移量

    private var mWidth = 0
    private var mHeight = 0
    private var mMinValue = 0.0
    private var mMaxValue = 3.0

    private var mLinePaints: MutableList<Paint> = arrayListOf()//走势线画笔
    private lateinit var mCoordPaint: Paint//坐标画笔
    private lateinit var mTextPaint: Paint//文字画笔

    private var mDrawPaths: MutableList<Path> = arrayListOf() //动画路径
    private var mAnimList: MutableList<ValueAnimator> = arrayListOf() //动画集合
    private var mLines: MutableList<Pair<Int, ArrayList<Double>>> = arrayListOf() //(颜色，数值)
    private var mIsHide = false

    //可调参数
    var duration = 1500L//动画时长
    var abscissa: ArrayList<String> = arrayListOf()//横坐标
    var columns = 3//默认3行
    var rows = 6//默认6列

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        //初始化坐标线画笔
        mCoordPaint = Paint()
        mCoordPaint.isAntiAlias = true
        mCoordPaint.color = ContextCompat.getColor(context, R.color.purple_500)
        mCoordPaint.strokeWidth = dp2px(context, 0.5f)
        //初始化文字画笔
        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = ContextCompat.getColor(context, android.R.color.holo_blue_bright)
        mTextPaint.textSize = sp2px(context, 10f)
    }

    private fun initConfig(list: List<Pair<Int, ArrayList<Double>>>) {
        mWidth = measuredWidth
        mHeight = measuredHeight
        mMinValue = 0.0
        mMaxValue = 0.0

        mAnimList.clear()
        mDrawPaths.clear()
        mLinePaints.clear()

        mLines.clear()
        mLines.addAll(list)
        mLines.forEach {
            //初始化走势线画笔
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = ContextCompat.getColor(context, it.first)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp2px(context, 2f)
            mLinePaints.add(paint)
            //初始化最大值和最小值
            it.second.forEach { value ->
                if (mMinValue > value) mMinValue = value
                if (mMaxValue < value) mMaxValue = value
            }
        }
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
        drawY(canvas)
        drawX(canvas)
        mDrawPaths.forEachIndexed { index, path ->
            canvas.drawPath(path, mLinePaints[index])
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //取消动画
        if (mAnimList.isEmpty()) return
        mAnimList.forEach {
            if (it.isRunning) it.cancel()
        }
    }

    //绘制x轴和垂直文字
    private fun drawX(canvas: Canvas) {
        if (columns == 0) return //避免算术异常
        val interval = (mMaxValue - mMinValue) / columns
        val verticalSpac = (mHeight - mTopOffset - mBottomOffset) / columns
        val fontOffset = -mTextPaint.fontMetrics.ascent + mTextPaint.fontMetrics.descent
        for (i in 0..columns) {
            //x轴
            canvas.drawLine(
                0f,
                mTopOffset + i * verticalSpac,
                width.toFloat(),
                mTopOffset + i * verticalSpac,
                mCoordPaint
            )
            //垂直文字，如 10K
            if (i != columns) {
                val str = if (mIsHide) "******" else "$i"
                canvas.drawText(str, 0f, i * verticalSpac + fontOffset, mTextPaint)
            }
        }
    }

    //绘制y轴和水平文字
    private fun drawY(canvas: Canvas) {
        if (abscissa.isEmpty() || rows <= 1) return
        val horizontalSpac = (mWidth / (rows - 1)).toFloat()
        val fontOffset = -mTextPaint.fontMetrics.ascent + dp2px(context, 5f)
        abscissa.forEachIndexed { i, s ->
            //y轴
            canvas.drawLine(
                i * horizontalSpac,
                mTopOffset,
                i * horizontalSpac,
                mHeight - mBottomOffset,
                mCoordPaint
            )
            //水平文字，如 11/14
            val textWidth = mTextPaint.measureText(s)
            if (i == 0) {
                canvas.drawText(
                    s,
                    i * horizontalSpac,
                    mHeight - mBottomOffset + fontOffset,
                    mTextPaint
                )
            } else if (i == abscissa.size - 1) {
                canvas.drawText(
                    s,
                    i * horizontalSpac - textWidth,
                    mHeight - mBottomOffset + fontOffset,
                    mTextPaint
                )
            } else {
                canvas.drawText(
                    s,
                    i * horizontalSpac - textWidth / 2,
                    mHeight - mBottomOffset + fontOffset,
                    mTextPaint
                )
            }
        }
    }

    //绘制走势线
    private fun drawTrendLine() {
        mLines.forEach { line ->
            val dropHeight = mHeight - mTopOffset - mBottomOffset //坐标系的落差高度
            val dropValue = mMaxValue - mMinValue //数据源的落差值
            if (dropValue == 0.0 || line.second.size == 1) return //避免算术异常
            val horizontalSpac = (mWidth / (line.second.size - 1).toFloat()) //水平间距
            val path = Path()
            val drawPath = Path()

            var lastX = 0f
            var lastY = 0f
            line.second.forEachIndexed { i, value ->
                val x = i * horizontalSpac
                val y = ((1 - (value - mMinValue) / dropValue) * dropHeight + mTopOffset).toFloat()
                if (i == 0) {
                    path.moveTo(x, y)
                } else {//贝塞尔曲线
                    path.cubicTo((x + lastX) / 2, lastY, (x + lastX) / 2, y, x, y)
                }
                lastX = x
                lastY = y
            }
            //绘制动画
            val pathMeasure = PathMeasure(path, false)
            val valueAnimator = ValueAnimator.ofFloat(0f, pathMeasure.length)
            valueAnimator.duration = duration
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            valueAnimator.addUpdateListener {
                val endDistance = it.animatedValue as Float
                drawPath.reset()
                drawPath.rLineTo(0f, 0f)
                pathMeasure.getSegment(0f, endDistance, drawPath, true)
                invalidate()
            }
            valueAnimator.start()
            mAnimList.add(valueAnimator)
            mDrawPaths.add(drawPath)
        }
    }

    fun addLine(line: Pair<Int, ArrayList<Double>>) {
        addLines(listOf(line))
    }

    fun addLines(lines: List<Pair<Int, ArrayList<Double>>>) {
        initConfig(lines)
        drawTrendLine()
    }

    fun hideAmount(isHide: Boolean) {
        mIsHide = isHide
        invalidate()
    }

    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }
}