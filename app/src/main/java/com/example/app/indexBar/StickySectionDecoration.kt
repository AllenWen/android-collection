package com.example.app.indexBar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickySectionDecoration(val context: Context, val list: List<StickySection>) :
    RecyclerView.ItemDecoration() {
    private var mBounds: Rect = Rect()
    private var fontColor: Int = ContextCompat.getColor(context, android.R.color.black)
    private var bgColor: Int = ContextCompat.getColor(context, android.R.color.holo_blue_light)
    private var mSectionHeight: Int = dp2px(context, 28f).toInt()
    private var mPaint: Paint = Paint().apply {
        textSize = sp2px(context, 12f)
        isAntiAlias = true
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        if (list.isNullOrEmpty() || position < 0 || position >= list.size) {
            return
        }
        if (list[position].isTitle) {
            outRect[0, mSectionHeight, 0] = 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            if (list.isNullOrEmpty() || position < 0 || position >= list.size || !list[position].isTitle) {
                continue
            }
            mPaint.color = bgColor
            c.drawRect(
                left.toFloat(),
                (child.top - params.topMargin - mSectionHeight).toFloat(),
                right.toFloat(),
                (child.top - params.topMargin).toFloat(),
                mPaint
            )
            mPaint.color = fontColor
            mPaint.getTextBounds(list[position].title, 0, list[position].title.length, mBounds)
            c.drawText(
                list[position].title,
                child.paddingLeft.toFloat(),
                child.top - params.topMargin - (mSectionHeight / 2f - mBounds.height() / 2f),
                mPaint
            )
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val position =
            (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        if (list.isNullOrEmpty() || position < 0 || position >= list.size) {
            return
        }
        val title = list[position].title
        val child = parent.findViewHolderForLayoutPosition(position)!!.itemView
        var flag = false
        if (title != list[position + 1].title) {
            if (child.height + child.top < mSectionHeight) {
                c.save()
                flag = true
                c.translate(0f, (child.height + child.top - mSectionHeight).toFloat())
            }
        }
        mPaint.color = bgColor
        c.drawRect(
            parent.paddingLeft.toFloat(),
            parent.paddingTop.toFloat(),
            (parent.right - parent.paddingRight).toFloat(),
            (parent.paddingTop + mSectionHeight).toFloat(),
            mPaint
        )
        mPaint.color = fontColor
        mPaint.getTextBounds(title, 0, title.length, mBounds)
        c.drawText(
            title,
            child.paddingLeft.toFloat(),
            parent.paddingTop + mSectionHeight - (mSectionHeight / 2f - mBounds.height() / 2f),
            mPaint
        )
        if (flag) c.restore()
    }

    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

}