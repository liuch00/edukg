package com.example.edupkg.data
import android.Manifest
import com.example.edupkg.loginDialog

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.edupkg.R
import com.example.edupkg.data.FrontEnd

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


data class accountInput(val username:String,val password:String)
enum class loginResult{success,invalidInput,usernamePasswordNotMatching}
enum class registerResult{success,invalidAccount,usernameOccupied}


//用户系统
class accountSystem(val context: Context){

    fun register(input: accountInput):registerResult{
        if(input.username.isEmpty()||input.password.isEmpty()){
            return registerResult.invalidAccount
        }
        var fe = MyApplication.getFrontEnd()
        val isSuccess=fe.signUp(input.username,input.password)
        Log.d("debugLog-register:", isSuccess.toString())
        if(!isSuccess){
            return registerResult.usernameOccupied
        }else{
            return registerResult.success
        }
    }

    fun login(input: accountInput):loginResult{
        if(input.username.isEmpty()||input.password.isEmpty()){
            return loginResult.invalidInput
        }
        var fe = MyApplication.getFrontEnd()
        val isSuccess=fe.logIn(input.username,input.password)
        Log.d("debugLog-login:", isSuccess.toString())
        if(!isSuccess){
            return loginResult.usernamePasswordNotMatching
        }else{
            return loginResult.success
        }
    }
}



//登陆控制和捕捉界面跳转

class MainActivity : AppCompatActivity() {

    val fragmentList= listOf(HomeFragment() ,ExerciseFragment(),PersonFragment())


    var PermissionGranted = false
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        } else {
            PermissionGranted = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        //在调用基类中的onCreate方法
        super.onCreate(savedInstanceState)
        verifyStoragePermissions(this);
        var fe = MyApplication.getFrontEnd()

        //一个APP如果在主线程中请求网络操作，将会抛出此异常。Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生，我们采用暴力法
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Log.d("debugLog-01:", FrontEnd.CachedEntityList.toString())


        val accounts = accountSystem(this)
        val dialog = loginDialog(this)


        dialog.show { username, password, isRegister ->
            val input = accountInput(username, password)
            Log.e("input", "$input")
            if (isRegister) {
                when (accounts.register(input)) {
                    registerResult.success -> {
                        dialog.dismiss()
                    }
                    registerResult.usernameOccupied -> Toast.makeText(
                        this,
                        "用户名已经存在",
                        Toast.LENGTH_SHORT
                    ).show()
                    registerResult.invalidAccount -> Toast.makeText(
                        this,
                        "用户名或密码不能为空",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                when (accounts.login(input)) {
                    loginResult.success -> {
                        dialog.dismiss()
                    }
                    loginResult.usernamePasswordNotMatching -> Toast.makeText(
                        this,
                        "用户名不存在或密码错误",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setContentView(R.layout.activity_main)
        val contentViewPager=findViewById<androidx.viewpager.widget.ViewPager>(R.id.content_view_pager)

        contentViewPager.adapter=MyAdapter(supportFragmentManager)

        contentViewPager.offscreenPageLimit=3

        val bottomNav=findViewById<BottomNavigationView>(R.id.bottom_nav)



        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home-> contentViewPager.currentItem=0
                R.id.nav_exercise-> contentViewPager.currentItem=1
                R.id.nav_person-> contentViewPager.currentItem=2
            }
            false
        }

        contentViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked=true
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })






    }
    inner class MyAdapter(fm: FragmentManager):
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }


    }
}