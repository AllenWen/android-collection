package com.example.app.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat

/**
 * SilentSwitchCompat: 屏蔽programmatically setChecked，只响应用户操作的Check事件
 */
class SilentSwitchCompat : SwitchCompat {
    private var mListener: OnCheckedChangeListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        if (listener != null && this.mListener != listener) {
            this.mListener = listener
        }
        super.setOnCheckedChangeListener(listener)
    }

    fun setCheckedSilently(checked: Boolean) {
        this.setOnCheckedChangeListener(null)
        this.isChecked = checked
        this.setOnCheckedChangeListener(mListener)
    }
}