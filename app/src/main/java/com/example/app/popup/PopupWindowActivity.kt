package com.example.app.popup

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.app.R


class PopupWindowActivity : Activity() {
    private lateinit var mPopupWindow: BackgroundDarkPopupWindow
    private lateinit var mTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)

        //popupWindow
        mTextView = TextView(this)
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        mTextView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        mTextView.setPadding(10, 10, 10, 10)
        mTextView.gravity = Gravity.CENTER
        mPopupWindow = BackgroundDarkPopupWindow(
            mTextView, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mPopupWindow.isFocusable = true
        mPopupWindow.setBackgroundDrawable(BitmapDrawable())
        mPopupWindow.animationStyle = android.R.style.Animation_Dialog

        //Top
        findViewById<Button>(R.id.top).setOnClickListener {
            mTextView.text = "This is a popupwindow\n\ndark on bottom"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.darkBelow(it)
            mPopupWindow.showAsDropDown(it, it.right / 2, 0)
        }
        //Left
        findViewById<Button>(R.id.left).setOnClickListener {
            mTextView.text = "This is a popupwindow\\n\\ndark on right"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.darkRightOf(it)
            mPopupWindow.showAtLocation(it, Gravity.CENTER_VERTICAL or Gravity.LEFT, 0, 0)
        }
        //Right
        findViewById<Button>(R.id.right).setOnClickListener {
            mTextView.text = "This is a popupwindow\\n\\ndark on left"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.drakLeftOf(it)
            mPopupWindow.showAtLocation(it, Gravity.CENTER_VERTICAL or Gravity.RIGHT, 0, 0)
        }
        //Bottom
        findViewById<Button>(R.id.bottom).setOnClickListener {
            mTextView.text = "This is a popupwindow\\n\\ndark on top"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.darkAbove(it)
            mPopupWindow.showAtLocation(it, Gravity.CENTER_HORIZONTAL, 0, it.top)
        }
        //Center
        findViewById<Button>(R.id.center).setOnClickListener {
            mTextView.text = "This is a popupwindow\n\ndark in center"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.drakLeftOf(findViewById<Button>(R.id.right))
            mPopupWindow.darkRightOf(findViewById<Button>(R.id.left))
            mPopupWindow.darkAbove(findViewById<Button>(R.id.bottom))
            mPopupWindow.darkBelow(findViewById<Button>(R.id.top))
            mPopupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
        }
        //All
        findViewById<Button>(R.id.all).setOnClickListener {
            mTextView.text = "This is a popupwindow\\n\\ndark fill all"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.darkFillScreen()
            mPopupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
        }
        //Fill
        findViewById<Button>(R.id.fill).setOnClickListener {
            mTextView.text = "This is a popupwindow\\n\\ndark fill view"
            mPopupWindow.resetDarkPosition()
            mPopupWindow.drakFillView(it)
            mPopupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
        }
    }
}