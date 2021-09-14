
package com.example.edupkg.data
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.edupkg.data.FrontEnd
import com.example.edupkg.data.MyApplication
import com.example.edupkg.R
import com.example.edupkg.data.Entity
import com.example.edupkg.util.showToast
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_expert.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import android.content.Intent.getIntent
import android.util.Log
import android.view.ViewGroup





class HomeFragment : Fragment() {

    val courseTypeList= mutableListOf("chinese","math","english","physics","chemistry","history", "geo","biology","politics")
    val titleList= mutableListOf("语文","数学", "英语", "物理", "化学", "历史", "地理", "生物", "政治")



    var fragmentList=ArrayList<CourseFragment>()
    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager:ViewPager
    private lateinit var editText: EditText
    private lateinit var toolbar: Toolbar




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPager.offscreenPageLimit=9

        for (courseType in  courseTypeList){
            fragmentList.add(CourseFragment(courseType))
        }
        viewPager.adapter = MyAdapter( requireActivity().supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)




        // 动态加载菜单
        toolbar.inflateMenu(R.menu.home_tool_bar_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId ) {
                R.id.chinese -> {

                    if(courseTypeList.indexOf("chinese")==-1 ){

                        courseTypeList.add("chinese")
                        titleList.add("语文")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)



                    }else {

                            courseTypeList.removeAt(courseTypeList.indexOf("chinese"))
                            titleList.removeAt(titleList.indexOf("语文"))
                            fragmentList = ArrayList<CourseFragment>()
                            for (courseType in courseTypeList) {
                                fragmentList.add(CourseFragment(courseType))
                            }

                                viewPager.adapter =
                                    MyAdapter(childFragmentManager)
                                tabLayout.setupWithViewPager(viewPager)



                    }
                }
                R.id.math -> {
                   // "点击数学".showToast()
                    if(courseTypeList.indexOf("math")==-1 ){
                        courseTypeList.add("math")
                        titleList.add("数学")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)




                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("math"))
                        titleList.removeAt(titleList.indexOf("数学"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))
                            Log.d("math",courseType.toString())
                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)

                        tabLayout.setupWithViewPager(viewPager)


                    }

                }
                R.id.english -> {
                   // "点击英语".showToast()
                    if(courseTypeList.indexOf("english")==-1 ){
                        courseTypeList.add("english")
                        titleList.add("英语")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("english"))
                        titleList.removeAt(titleList.indexOf("英语"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))

                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }
                }
                R.id.history -> {
                   // "点击历史".showToast()
                    if(courseTypeList.indexOf("history")==-1 ){
                        courseTypeList.add("history")
                        titleList.add("历史")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)



                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("history"))
                        titleList.removeAt(titleList.indexOf("历史"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))

                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }
                }
                R.id.chemistry -> {
                  //  "点击化学".showToast()
                    if(courseTypeList.indexOf("chemistry")==-1 ){
                        courseTypeList.add("chemistry")
                        titleList.add("化学")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("chemistry"))
                        titleList.removeAt(titleList.indexOf("化学"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))

                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }

                }
                R.id.geo -> {
                    //"点击地理".showToast()
                    if(courseTypeList.indexOf("geo")==-1 ){
                        courseTypeList.add("geo")
                        titleList.add("地理")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)



                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("geo"))
                        titleList.removeAt(titleList.indexOf("地理"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))

                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }
                }
                R.id.biology -> {
                  //  "点击生物".showToast()
                    if(courseTypeList.indexOf("biology")==-1 ){
                        courseTypeList.add("biology")
                        titleList.add("生物")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("biology"))
                        titleList.removeAt(titleList.indexOf("生物"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))

                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }
                }
                R.id.politics -> {
                 //   "点击政治".showToast()
                    if(courseTypeList.indexOf("politics")==-1 ){
                        courseTypeList.add("politics")
                        titleList.add("政治")
                        fragmentList=ArrayList<CourseFragment>()
                        for (courseType in  courseTypeList){
                            fragmentList.add(CourseFragment(courseType))
                        }
                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)



                    }else{
                        courseTypeList.removeAt(courseTypeList.indexOf("politics"))
                        titleList.removeAt(titleList.indexOf("政治"))
                        fragmentList = ArrayList<CourseFragment>()
                        for (courseType in courseTypeList) {
                            fragmentList.add(CourseFragment(courseType))


                        }

                        viewPager.adapter =
                            MyAdapter(childFragmentManager)
                        tabLayout.setupWithViewPager(viewPager)


                    }
                }
            }

            false
        }


        // 设置home页面的搜索框 不可编辑
        editText.keyListener = null

        // 设置home页面的搜索框的点击事件: 打开SearchActivity这个页面，即搜索页面
        editText.setOnClickListener {
            val intent = Intent(MyApplication.context, searchActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =inflater.inflate(R.layout.fragment_home, container, false)
         tabLayout=view.findViewById(R.id.course_tab_layout)
        toolbar = view.findViewById(R.id.home_tool_bar)
         editText = view.findViewById(R.id.home_edit_text)
         viewPager=view.findViewById(R.id.entity_view_pager)
        val expertTextView=view.findViewById<TextView>(R.id.expert)


        expertTextView.setOnClickListener{
            "click_expert".showToast()
            val intent= Intent(MyApplication.context, expertActivity::class.java)
            startActivity(intent)

        }




        return view
    }






    inner class MyAdapter(fm: FragmentManager):
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            Log.d("getItem",position.toString())
            return fragmentList[position]

        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }
    }

}

