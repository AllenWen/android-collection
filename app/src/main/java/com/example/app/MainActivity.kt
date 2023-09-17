package com.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.app.filter.InputFilterActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //输入过滤器
        findViewById<Button>(R.id.input_filter).setOnClickListener {
            startActivity(Intent(this, InputFilterActivity::class.java))
        }
    }
}