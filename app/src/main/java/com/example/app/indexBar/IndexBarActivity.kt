package com.example.app.indexBar

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

class IndexBarActivity : Activity() {
    private val mList = arrayListOf(
        "Aaaa",
        "Bbbb",
        "Ccc",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Qx",
        "R",
        "S",
        "T",
        "U",
        "V",
        "Www",
        "X",
        "Y",
        "Z",
        "#"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index_bar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val indexBar = findViewById<IndexBar>(R.id.indexBar)
        val indexTextView = findViewById<TextView>(R.id.index)

        val layoutManager = LinearLayoutManager(this)
        indexBar.setIndexList(arrayListOf())
        indexBar.setListener(object : IndexBar.IndexBarListener {
            override fun onPress(pos: Int, index: String) {
                indexTextView.visibility = View.VISIBLE
                indexTextView.text = index
                layoutManager.scrollToPositionWithOffset(
                    mList.indexOfFirst { it[0].toString() == index },
                    0
                )
            }

            override fun onRelease() {
                indexTextView.visibility = View.GONE
            }
        })
        val decoration = StickySectionDecoration(
            this,
            mList.distinctBy { it[0] }.map { StickySection(true, it[0].toString()) })
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = IndexAdapter(this, mList)
    }
}