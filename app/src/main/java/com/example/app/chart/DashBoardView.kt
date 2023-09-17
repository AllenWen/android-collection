package com.example.app.chart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.app.R
import kotlin.math.absoluteValue

class DashBoardView : View {
    private val defaultWidth = dp2px(context, 36f).toInt()//默认宽度
    private var boardHeight = 0
    private var boardWidth = 0
    private var ringStroke = dp2px(context, 4f).toFloat()//默认环宽度
    private var bottomPadding = dp2px(context, 1f).toFloat()
    private lateinit var ringPaint: Paint
    private lateinit var bitmapPaint: Paint
    private lateinit var bitmap: Bitmap
    private var mValueAnimator: ValueAnimator? = null

    private var pairs = listOf(
        Triple(0.0, 0.6, android.R.color.holo_green_light),
        Triple(0.6, 0.9, android.R.color.holo_orange_light),
        Triple(0.9, 1.0, android.R.color.holo_red_light)
    )
    private var percent: Double = 0.0
    private var maxPercent = 1.0//默认最大仪表数是1.0
    private var animate = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        ringPaint = Paint()
        ringPaint.isAntiAlias = true
        ringPaint.style = Paint.Style.STROKE

        bitmapPaint = Paint()
        bitmapPaint.isAntiAlias = true

        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.needle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        boardWidth = if (widthMode == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            defaultWidth
        }
        if (boardWidth > getScreenWidth(context)) {
            boardWidth = getScreenWidth(context)
        }
        boardHeight = boardWidth / 2
        //按比例设置下底部间距
        bottomPadding = 2f / 19f * boardHeight
        //按比例设置下圆环宽度
        ringStroke = 3.5f / 19f * boardHeight
        ringPaint.strokeWidth = ringStroke
        setMeasuredDimension(boardWidth, boardHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val rectF = RectF(
            ringStroke / 2f,
            ringStroke / 2f,
            boardWidth.toFloat() - ringStroke / 2f,
            boardWidth.toFloat() - ringStroke / 2f - bottomPadding
        )
        //绘制仪表
        pairs.forEach {
            ringPaint.color = ContextCompat.getColor(context, it.third)
            canvas?.drawArc(
                rectF,
                ((1 - it.first / maxPercent) * -180f).toFloat(),
                ((it.second - it.first) / maxPercent * 180f).toFloat(),
                false,
                ringPaint
            )
        }
        //按比例设置下指针宽高
        val scale: Float = (12f / 19f * boardHeight) / bitmap.height
        bitmap = scale(bitmap, scale, scale)!!
        canvas?.save()

        canvas?.translate(
            (boardWidth - bitmap.width) / 2f,
            (boardHeight - bitmap.height).toFloat()
        )
        //获取旋转的角度
        val actualPer = percent / maxPercent
        val degree = if (actualPer <= 0.5) {
            (90f * actualPer / 0.5f) - 90f
        } else {
            90f * ((actualPer - 0.5f) / 0.5f)
        }
        canvas?.rotate(
            degree.toFloat(),
            bitmap.width / 2f,
            //减去半个宽度，使旋转中心刚好在指针的小圈里
            bitmap.height.toFloat() - bitmap.width / 2f
        )
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
        canvas?.restore()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //取消动画
        if (mValueAnimator != null && mValueAnimator!!.isRunning) mValueAnimator?.cancel()
    }

    fun setData(newParis: List<Triple<Double, Double, Int>>) {
        if (newParis.isNullOrEmpty()) {
            return
        }
        pairs = newParis
        maxPercent = newParis[pairs.size - 1].second
        invalidate()
    }

    fun setPercent(newPercent: Double, animate: Boolean = true) {
        val validPercent = when {
            newPercent < 0 -> 0.0
            newPercent > maxPercent -> maxPercent
            else -> newPercent
        }
        if (animate) {
            mValueAnimator = ValueAnimator.ofFloat((-percent).toFloat(), validPercent.toFloat())
            mValueAnimator?.duration =
                (500f * ((percent + validPercent) / maxPercent * 2f)).toLong()
            mValueAnimator?.interpolator = AccelerateDecelerateInterpolator()
            mValueAnimator?.addUpdateListener {
                percent = (it.animatedValue as Float).absoluteValue.toDouble()
                invalidate()
            }
            mValueAnimator?.start()
        } else {
            percent = validPercent
            invalidate()
        }
    }

    fun getPercent() = percent

    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
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

    fun scale(
        src: Bitmap?,
        scaleWidth: Float,
        scaleHeight: Float,
        recycle: Boolean = false
    ): Bitmap? {
        if (src == null) return null
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setScale(scaleWidth, scaleHeight)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }
}