package com.example.edupkg.data

import android.content.Intent
import com.example.edupkg.data.FrontEnd



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edupkg.R
import com.example.edupkg.util.showToast
import kotlinx.android.synthetic.main.activity_expert.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.IllegalArgumentException


class pathActivity : AppCompatActivity() {
    var path_search_sour=""

    var path_search_des=""
    var search_subject="english"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)
        val sourseEditText = findViewById<EditText>(R.id.path_sour)
        val destinationEditText = findViewById<EditText>(R.id.path_des)
        val pathTextView=findViewById<TextView>(R.id.path_search)
        sourseEditText.setOnEditorActionListener { _, keyCode, _ ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
            path_search_sour=sourseEditText.text.toString()
                "你输入了${sourseEditText.text}".showToast()
                true
            } else {
                false
            }
        }
        destinationEditText.setOnEditorActionListener { _, keyCode, _ ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                path_search_des=destinationEditText.text.toString()
                "你输入了${destinationEditText.text}".showToast()
                true
            } else {
                false
            }
        }

        pathTextView.setOnClickListener{
            "click_search".showToast()
            val intent= Intent(MyApplication.context, pathResultActivity::class.java)
            intent.putExtra("path_search_sour=",path_search_sour)
            intent.putExtra("path_search_des=",path_search_des)
            intent.putExtra("path_search_sub=",search_subject)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.subject_list, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId ?: 0) {
            android.R.id.home -> finish()
            R.id.chinese -> {
                search_subject="chinese"
                "已选择语文".showToast()
            }
            R.id.math -> {
                search_subject="math"
                "已选择数学".showToast()
            }
            R.id.english -> {
                search_subject="english"
                "已选择英语".showToast()
            }
            R.id.history -> {
                search_subject="history"
                "已选择历史".showToast()
            }
            R.id.physics -> {
                search_subject="physics"
                "已选择物理".showToast()
            }
            R.id.chemistry -> {
                search_subject="chemistry"
                "已选择化学".showToast()
            }
            R.id.geo -> {
                search_subject="geo"
                "已选择地理".showToast()
            }
            R.id.biology -> {
                search_subject="biology"
                "已选择生物".showToast()
            }
            R.id.politics -> {
                search_subject = "politics"
                "已选择政治".showToast()
            }

        }
        return super.onOptionsItemSelected(item)
    }


}
