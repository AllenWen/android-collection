package com.example.app.filter

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import com.example.app.R

class InputFilterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_filter)

        findViewById<EditText>(R.id.edittext).filters = arrayOf(NumRangeInputFilter())
    }
}