package com.example.edupkg.data

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.example.edupkg.data.FrontEnd

class MyApplication:Application() {

    companion object{
        lateinit var context: Context
        val fe= FrontEnd()

        public fun getFrontEnd()
                :FrontEnd{
            return this.fe;
        }
    }

    override fun onCreate() {
        super.onCreate()
        fe.init()
        //一个APP如果在主线程中请求网络操作，将会抛出此异常。Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生，我们采用暴力法
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        fe.logIn("123","123")
        Log.d("isLogin111",fe.isLogin.toString())
        context=baseContext


    }

}