package com.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.app.chart.ChartActivity
import com.example.app.filter.InputFilterActivity
import com.example.app.suffix.SuffixActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //输入过滤器
        findViewById<Button>(R.id.input_filter).setOnClickListener {
            startActivity(Intent(this, InputFilterActivity::class.java))
        }

        //图表
        findViewById<Button>(R.id.chart).setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }

        //带后缀文本
        findViewById<Button>(R.id.suffix).setOnClickListener {
            startActivity(Intent(this, SuffixActivity::class.java))
        }
    }
}