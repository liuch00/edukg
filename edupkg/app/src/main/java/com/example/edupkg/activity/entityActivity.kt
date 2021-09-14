package com.example.edupkg.data
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_entity.*
import java.util.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.edupkg.R
import com.example.edupkg.data.Entity
import com.example.edupkg.data.Entry
import com.example.edupkg.data.FrontEnd
import com.example.edupkg.util.showToast
import kotlinx.android.synthetic.main.list_item.*
import kotlin.concurrent.thread


class entityActivity : AppCompatActivity() {

    //实体详情页
    private  var e=Entity("李白","chinese")
    private val listItems1 = ArrayList<String>()
    private val listItems2 = ArrayList<String>()
    private val listItems3 = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity)
        Log.d("EntityAc","yes")

        //setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        var fe = MyApplication.getFrontEnd()
        var label= intent.getStringExtra("label=")
        var subject=intent.getStringExtra("subject=")
        var i=0
        var size=0
        var excerciseList=Vector<Question>()
        Log.d("label",label.toString())
        Log.d("subject",subject.toString())

        entityLable.text=label;
        entitySubject.text="学科："+fe.switchToChinese(subject)
        thread {


            e = fe.searchForEntity(subject, label)

            if (e == null) {
                entityLable.visibility = View.INVISIBLE
                entitySubject.visibility = View.INVISIBLE
                Property.text = "网络错误"
                propertyList.visibility = View.INVISIBLE
                Relation.visibility = View.INVISIBLE
                subjectList.visibility = View.INVISIBLE
                objectList.visibility = View.INVISIBLE
                showExercise.visibility = View.INVISIBLE
            } else {
                var prop = e.property
                var sub = e.subject
                var ob = e.`object`
                if (prop != null) {
                    for (entry: Entry in prop)
                        listItems1.add(entry.key + ":" + entry.value)
                    val adapter1 =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems1)
                    runOnUiThread() {
                        propertyList.adapter = adapter1;

                        propertyList.setBackgroundColor(getResources().getColor(R.color.wheat));
                    }
                }
                val entryItems2 = ArrayList<String>()
                if (sub != null) {
                    if (sub.isEmpty()) subjectList.visibility = View.INVISIBLE
                    for (entry: Entry in sub) {
                        entryItems2.add(entry.value)
                        listItems2.add(entry.key + "<=" + entry.value)
                    }
                    val adapter2 =
                        ArrayAdapter(this, R.layout.entity_activity_list_item, listItems2)
                    runOnUiThread() {
                        subjectList.adapter = adapter2
                        subjectList.setBackgroundColor(getResources().getColor(R.color.azure))
                    }
                    subjectList.setOnItemClickListener { _, _, i, _ ->

                        val intent = Intent(MyApplication.context, entityActivity::class.java)
                        intent.putExtra("label=", entryItems2[i])
                        intent.putExtra("subject=", e.course)
                        startActivity(intent)
                    }
                } else {
                    subjectList.visibility = View.INVISIBLE
                    entity_ac_divider.visibility = View.INVISIBLE
                }
                if (ob != null) {
                    if (ob.isEmpty()) objectList.visibility = View.INVISIBLE
                    val entryItems3 = ArrayList<String>()
                    for (entry: Entry in ob) {
                        entryItems3.add(entry.value)
                        listItems3.add(entry.key + "=>" + entry.value)
                    }
                    val adapter3 =
                        ArrayAdapter(this, R.layout.entity_activity_list_item, listItems3)
                    runOnUiThread() {
                        objectList.adapter = adapter3
                        objectList.setBackgroundColor(getResources().getColor(R.color.lavender))
                    }
                    objectList.setOnItemClickListener { _, _, i, _ ->
                        val intent = Intent(MyApplication.context, entityActivity::class.java)
                        intent.putExtra("label=", entryItems3[i])
                        intent.putExtra("subject=", e.course)
                        startActivity(intent)
                    }
                } else {
                    subjectList.visibility = View.INVISIBLE
                    entity_ac_divider.visibility = View.INVISIBLE
                }
            }
        }
        showExercise.setOnClickListener {
            thread {
                if (showExercise.text == "显示习题") {
                    runOnUiThread{"出题中".showToast()}
                    excerciseList = fe.getExcercise(label)
                    runOnUiThread {
                        if (!excerciseList.isEmpty()) {
                            size = excerciseList.size
                            showExercise.text = "下一题"
                        } else
                            "没有习题".showToast()
                    }

                } else {
                    i = (i + 1) % size
                }
                runOnUiThread {
                    if (size > 0) {
                        current.visibility=View.VISIBLE
                        val text="当前习题"+(i+1)+"/"+size
                        current.text=text
                        exercise.text = excerciseList[i].body
                        exercise.visibility = View.VISIBLE
                        showAnswer.visibility = View.VISIBLE
                        showAnswer.text = "显示答案"
                        answer.text = excerciseList[i].ans
                        answer.visibility = View.INVISIBLE
                    }
                }
            }
        }
        showAnswer.setOnClickListener {
            answer.visibility=View.VISIBLE
            showAnswer.text="答案"
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.entity_activity_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId ?: 0) {
            android.R.id.home -> finish()
            R.id.favorite -> {
                thread {
                    var fe = MyApplication.getFrontEnd()
                    var bl = fe.addFavorite(e.course, e.label)
                    runOnUiThread {
                        if (bl == 0)
                            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show()
                        else if (bl == 1)
                            Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(this, "连接错误", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.share -> {
                var sendtext = ""
                val mIntent = Intent(Intent.ACTION_SEND)
                mIntent.setPackage("com.tencent.mobileqq")
                sendtext="实体"+entityLabel.text+"\n"+entityCourse.text+"\n"
                if(!listItems1.isEmpty()){
                    sendtext=sendtext+"属性：\n"
                    for( i in listItems1)
                        sendtext=sendtext+i+"\n"
                }
                if(!listItems2.isEmpty()||!listItems3.isEmpty()){
                    sendtext=sendtext+"关系：\n"
                    for( i in listItems2)
                        sendtext=sendtext+i+"\n"
                    for( i in listItems3)
                        sendtext=sendtext+i+"\n"
                }
                mIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    sendtext
                )
                mIntent.type = "text/plain"
                startActivity(Intent.createChooser(mIntent, "分享文本"))
            }

            R.id.delfavorite -> {
                thread {
                    var fe = MyApplication.getFrontEnd()
                    var bl = fe.deleteFavorite(e.course, e.label)
                    runOnUiThread {
                        if (bl == 0)
                            Toast.makeText(this, "取消收藏成功", Toast.LENGTH_SHORT).show()
                        else if (bl == 1)
                            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(this, "连接错误", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}