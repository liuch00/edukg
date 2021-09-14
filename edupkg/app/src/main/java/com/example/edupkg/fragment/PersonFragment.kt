package com.example.edupkg.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.edupkg.R

import android.widget.AdapterView.OnItemClickListener
import com.example.edupkg.util.showToast
import kotlin.concurrent.thread
import android.content.Intent
import android.widget.*


class PersonFragment : Fragment() {

    val setupType= listOf("delete_cache","save_flow","version_info")
    val setupTitleList= listOf("清除缓存","省流模式","版本信息")

    fun setup(stType:String){
        if (stType==setupTitleList[0]){
            thread {
                var fe = MyApplication.getFrontEnd()
                fe.deleteCache()
                activity?.runOnUiThread() {
                    "缓存已清除".showToast()
                }
            }


        }else if(stType==setupTitleList[1]){
            "待开发功能".showToast()
        }else if(stType==setupTitleList[2]){
            "Version:1.0.0,created by lch mby sjh".showToast()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        val setupListView=view.findViewById<ListView>(R.id.setup_list)

        val setupAdapter =
            ArrayAdapter(MyApplication.context,android.R.layout.simple_list_item_1 , setupTitleList)
        setupListView.adapter=setupAdapter

        setupListView.setOnItemClickListener(OnItemClickListener { setupAdapter, view, i, l ->
            val t: String = setupListView.getAdapter().getItem(i) as String
            setup(t)
        })


        val favoriteTextView=view.findViewById<TextView>(R.id.my_favorite)
        val localTextView=view.findViewById<TextView>(R.id.my_local)
        val historyTextView=view.findViewById<TextView>(R.id.my_history)


        favoriteTextView.setOnClickListener{
            "click_favorite".showToast()
            val intent= Intent(MyApplication.context, favoriteActivity::class.java)
            startActivity(intent)

        }
        localTextView.setOnClickListener{
            "click_local".showToast()
            val intent= Intent(MyApplication.context, localActivity::class.java)
            startActivity(intent)
        }
        historyTextView.setOnClickListener{
            "click_history".showToast()
            val intent= Intent(MyApplication.context, historyActivity::class.java)
            startActivity(intent)
        }




        return view
    }






}