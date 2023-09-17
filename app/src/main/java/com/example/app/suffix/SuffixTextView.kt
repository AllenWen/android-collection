package com.example.app.suffix

import android.content.Context
import android.graphics.Color
import android.text.DynamicLayout
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView

class SuffixTextView : AppCompatTextView {
    private val mLineBreaker = '\n'
    private var mSuffixText = "展开更多"
    private var mSuffixTextColor = Color.GREEN
    private var mOnMoreClickListener: OnClickListener? = null

    private var mIsShowMore = false
    private var mOriginText: CharSequence? = null
    private var mBufferType: BufferType? = BufferType.NORMAL

    private var mLayout: Layout? = null
    private var mLayoutWidth = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, def: Int) : super(
        context,
        attributeSet,
        def
    ) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet) {
        movementMethod = LinkMovementMethod.getInstance()
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                setTextInternal(getSpannableText(), mBufferType)
            }
        })
    }

    private fun getSpannableText(): CharSequence? {
        //没设置最大行数 或者 无文案
        if (maxLines == -1 || mOriginText.isNullOrEmpty()) {
            return mOriginText
        }
        mLayout = layout
        mLayoutWidth = mLayout?.width ?: 0
        if (mLayoutWidth <= 0) {
            if (width == 0) {
                return mOriginText
            } else {
                mLayoutWidth = width - paddingStart - paddingEnd
            }
        }
        mLayout = DynamicLayout(
            mOriginText!!,
            paint,
            mLayoutWidth,
            Layout.Alignment.ALIGN_NORMAL,
            1.0f,
            0.0f,
            false
        )
        if ((mLayout?.lineCount ?: 0) >= maxLines) {
            val lineEndIndex = mLayout?.getLineEnd(maxLines - 1) ?: 0
            val lineStartIndex = mLayout?.getLineStart(maxLines - 1) ?: 0
            val mustShowText = mOriginText!!.subSequence(0, lineStartIndex)
            //加0.5 避免round_down出现少距离
            val tailWidth = paint.measureText(mSuffixText) + 0.5f
            val lastLineText: CharSequence = if (mLineBreaker == mOriginText!![lineEndIndex - 1]) {
                mOriginText!!.subSequence(lineStartIndex, lineEndIndex - 1)
            } else {
                mOriginText!!.subSequence(lineStartIndex, lineEndIndex)
            }
            val ssb = SpannableStringBuilder(mustShowText)
            val availableWidth = measuredWidth - paddingStart - paddingEnd
            val ellipsize = TextUtils.ellipsize(
                lastLineText, paint, availableWidth - tailWidth,
                TextUtils.TruncateAt.END
            )
            if (mIsShowMore) {
                if (ellipsize != lastLineText) {
                    ssb.append(ellipsize)
                    appendMore(ssb, true)
                } else {
                    ssb.append(lastLineText)
                    appendMore(ssb, true)
                }
            } else {
                if (lineEndIndex != mOriginText!!.length) {
                    ssb.append(ellipsize)
                    appendMore(ssb, true)
                } else {
                    ssb.append(lastLineText)
                    appendMore(ssb, false)
                }
            }
            return ssb
        } else {
            val ssb = SpannableStringBuilder(mOriginText)
            appendMore(ssb, mIsShowMore)
            return ssb
        }
    }

    private fun appendMore(ssb: SpannableStringBuilder, isAppend: Boolean) {
        if (isAppend) {
            ssb.append(mSuffixText)
            ssb.setSpan(
                createSpan(),
                ssb.length - mSuffixText.length,
                ssb.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
    }

    private fun createSpan(): ClickableSpan {
        return object : ClickableSpan() {
            override fun onClick(widget: View) {
                mOnMoreClickListener?.onClick(widget)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = mSuffixTextColor
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mBufferType = type
        if (text.isNullOrEmpty()) {
            mOriginText = ""
        } else {
            var trimText = text
            while (trimText.toString().endsWith("\n")) {
                trimText = trimText!!.subSequence(0, trimText.length - 1)
            }
            mOriginText = trimText
        }
        setTextInternal(getSpannableText(), mBufferType)
    }

    private fun setTextInternal(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }

    fun showMore(isShow: Boolean) {
        mIsShowMore = isShow
        setTextInternal(getSpannableText(), mBufferType)
    }

    fun onMoreClick(listener: OnClickListener) {
        mOnMoreClickListener = listener
    }
}