

package com.example.edupkg.data

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.edupkg.R
import com.example.edupkg.util.showToast
import kotlinx.android.synthetic.main.activity_expert.*
import android.widget.AdapterView.OnItemClickListener
import kotlin.concurrent.thread

//新闻搜索
class exploreSearchActivity : AppCompatActivity() {
    val setupType= listOf("default","label","category","visited")
    val setupTitleList= listOf("默认排序","按名称排序","按属性排序","按是否访问排序")
    var search_key:String=""
    var search_subject:String=""
    var search_sort_method:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // "取消"的点击事件：销毁本活动，返回上一级
        val searchCancelButton = findViewById<TextView>(R.id.search_cancel_button)
        searchCancelButton.setOnClickListener { finish() }
        // 搜索框
        val searchEditText = findViewById<EditText>(R.id.home_edit_text)

        searchEditText.setOnEditorActionListener { _, keyCode, _ ->
            // 如果点击了回车键，即搜索键，就弹出一个toast
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                "你输入了${searchEditText.text}".showToast()

                val intent= Intent(MyApplication.context, exploreSearchResultActivity::class.java)
                search_key=searchEditText.text.toString()
                //暂时默认为英语，后面改为默认为全部学科
                search_subject="english"
                search_sort_method=setupType[0]

                intent.putExtra("search_key_link=",search_key)
                intent.putExtra("search_subject_link=",search_subject)
                intent.putExtra("search_sort_method_link=",search_sort_method)

                startActivity(intent)


                true
            } else {
                false
            }
        }
        val setupListView=findViewById<ListView>(R.id.search_setup_list)
        val setupAdapter =
            ArrayAdapter(MyApplication.context,android.R.layout.simple_list_item_1 , setupTitleList)
        setupListView.adapter=setupAdapter

        setupListView.setOnItemClickListener(OnItemClickListener { setupAdapter, view, i, l ->
            val t: String = setupListView.getAdapter().getItem(i) as String
            search_setup(t)
        })



    }

    fun search_setup(stType:String){
        if (stType==setupTitleList[0]){
            search_sort_method=setupType[0]
            "已切换到默认排序".showToast()
        }else if(stType==setupTitleList[1]){
            search_sort_method=setupType[1]
            "已切换到按名称排序".showToast()
        }else if(stType==setupTitleList[2]){
            search_sort_method=setupType[2]
            "已切换到按属性排序".showToast()
        }else if(stType==setupTitleList[3]){
            search_sort_method=setupType[3]
            "已切换到按是否访问排序".showToast()
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