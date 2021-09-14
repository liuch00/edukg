package com.example.edupkg.util

import android.widget.Toast
import com.example.edupkg.data.MyApplication

fun String.showToast(){
    Toast.makeText(MyApplication.context,this, Toast.LENGTH_SHORT).show()
}