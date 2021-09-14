package com.example.edupkg

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText


class loginDialog(context: Context):AlertDialog(context) {

    val content=LayoutInflater.from(getContext()).inflate(R.layout.login_dialog,null)

    init {
        setView(content)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    //info:(name,passward,is_registered)
    inline fun show(crossinline info:(String,String,Boolean)->Unit){
        val usernameText=content.findViewById<EditText>(R.id.username_text)
        val passwordText=content.findViewById<EditText>(R.id.password_text)
        passwordText.clearFocus()
        usernameText.requestFocus()

        content.findViewById<Button>(R.id.login_button).setOnClickListener{
            info(usernameText.text.toString(),passwordText.text.toString(),false)
        }
        content.findViewById<Button>(R.id.register_button).setOnClickListener {
            info(usernameText.text.toString(), passwordText.text.toString(),true)
        }
        content.findViewById<Button>(R.id.skip_button).setOnClickListener {
            info("123","123",false)
        }

        super.show()
    }




}