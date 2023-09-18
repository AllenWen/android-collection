package com.example.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

/**
 * CustomViewPager: 指定忽略类型，解决ViewPager与scroll类组件滑动冲突
 */
class CustomViewPager : ViewPager {
    private var ignoreViewClass: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setIgnoreViewClass(className: String) {
        ignoreViewClass = className
    }

    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = v.childCount
            for (i in count - 1 downTo 0) {
                val child = v.getChildAt(i)
                if (x + scrollX >= child.left && x + scrollX < child.right && y + scrollY >= child.top && y + scrollY < child.bottom && canScroll(
                        child,
                        true,
                        dx,
                        x + scrollX - child.left,
                        y + scrollY - child.top
                    )
                ) {
                    return true
                }
            }
        }
        return if (v::class.java.name == ignoreViewClass) {
            checkV
        } else {
            checkV && v.canScrollHorizontally(-dx)
        }
    }
}