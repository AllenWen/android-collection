package com.example.app.suffix

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.app.R

class SuffixActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suffix)

        val suffixTextView = findViewById<SuffixTextView>(R.id.suffixTextView)
        suffixTextView.onMoreClick {
            Toast.makeText(this, "点击展开", Toast.LENGTH_SHORT).show()
        }
    }
}