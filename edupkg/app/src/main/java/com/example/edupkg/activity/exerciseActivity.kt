package com.example.edupkg.data

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.edupkg.R
import com.example.edupkg.data.MyApplication
import com.example.edupkg.data.Question
import com.example.edupkg.util.showToast
import kotlinx.android.synthetic.main.activity_entity.*
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*
import kotlin.concurrent.thread

class exerciseActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        //setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        var fe = MyApplication.getFrontEnd()
        var i=0
        var size=10
        var rans=Vector<Char>()
        var yans=Vector<Char>()
        var excerciseList=Vector<Question>()
        "出题中".showToast()
        thread {
            excerciseList = fe.getRandomExercise()
            for(index in 0 until 10) {
                rans.add(excerciseList[index].ans[0])
                yans.add('_')
            }
            runOnUiThread {
                exercisePre.visibility= View.VISIBLE
                exerciseAfter.visibility= View.VISIBLE
                qA.visibility= View.VISIBLE
                qB.visibility= View.VISIBLE
                qC.visibility= View.VISIBLE
                qD.visibility= View.VISIBLE
                qBody.visibility= View.VISIBLE
                ExerciseNumber.visibility= View.VISIBLE
                yourChoice.visibility= View.VISIBLE
                Submit.visibility= View.VISIBLE
                current.visibility= View.VISIBLE
                qA.text=excerciseList[i].A
                qB.text=excerciseList[i].B
                qC.text=excerciseList[i].C
                qD.text=excerciseList[i].D
                qBody.text=excerciseList[i].qbody
                var cur="当前习题"+(i+1)+"/10"
                current.text=cur
                var ychoice="你选择了:"+yans[i]
                yourChoice.text=ychoice
            }
        }
        qA.setOnClickListener {
            yans[i]='A'
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        qB.setOnClickListener {
            yans[i]='B'
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        qC.setOnClickListener {
            yans[i]='C'
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        qD.setOnClickListener {
            yans[i]='D'
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        exercisePre.setOnClickListener {
            i=i-1
            if(i<0)i=9
            qA.text=excerciseList[i].A
            qB.text=excerciseList[i].B
            qC.text=excerciseList[i].C
            qD.text=excerciseList[i].D
            qBody.text=excerciseList[i].qbody
            var cur="当前习题"+(i+1)+"/10"
            current.text=cur
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        exercisePre.setOnClickListener {
            i=(i+1)%10
            qA.text=excerciseList[i].A
            qB.text=excerciseList[i].B
            qC.text=excerciseList[i].C
            qD.text=excerciseList[i].D
            qBody.text=excerciseList[i].qbody
            var cur="当前习题"+(i+1)+"/10"
            current.text=cur
            var ychoice="你选择了:"+yans[i]
            yourChoice.text=ychoice
        }
        Submit.setOnClickListener {
            Submit.visibility=View.INVISIBLE
            var wrong="错误的题有："
            var score=100
            for(j in 0 until 10){
                if(yans[j]!=rans[j]){
                    score=score-10
                    if(yans[j]=='_')
                        wrong=wrong+"\n第"+j+"题，你未选择,正确答案为"+rans[j]
                    else
                        wrong=wrong+"\n第"+j+"题，你选择了"+yans[j]+",正确答案为"+rans[j]
                }
            }
            FrontEnd.addCachedRates(score);
            var result=""
            if(score==100){
                result="你的得分为:100\n恭喜你，完全正确！"
            }
            else{
                result="你的得分为:"+score+"\n"+wrong+"\n请再接再厉！"
            }
            scoring.text=result
            scoring.visibility=View.VISIBLE
        }
    }
}