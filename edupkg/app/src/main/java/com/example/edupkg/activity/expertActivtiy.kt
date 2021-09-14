
package com.example.edupkg.data
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edupkg.R
import kotlinx.android.synthetic.main.activity_expert.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.IllegalArgumentException

//知识问答页

//定义消息的实体类
class Msg(val content:String,val type:Int){
    //定义静态成员
    companion object{
        const val RIGHT = 0
        const val LEFT = 1
    }
};
class expertActivity : AppCompatActivity() {
    //建立消息数据列表
    private val msgList = ArrayList<Msg>()
    var sub="chinese"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert)
        // setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        val fe=FrontEnd()
        fe.logIn("usersong","passsong")
        initMsg()   //初始化聊天机器人的见面语
        recyclerView.layoutManager = LinearLayoutManager(this)  //布局为线性垂直
        val adapter = MsgAdapter(msgList)   //建立适配器实例
        recyclerView.adapter = adapter  //传入适配器
        send_button.setOnClickListener{
            val content: String = editText.text.toString()
            if (content.isNotEmpty()) {
                msgList.add(Msg(content, Msg.RIGHT))
                adapter.notifyItemInserted(msgList.size - 1)   //为RecyclerView添加末尾子项
                recyclerView.scrollToPosition(msgList.size - 1) //跳转到当前位置
                editText.setText("")
                Log.d("content:", "content")
                Thread {
                    Log.d("content:", "content")
                    val answer = fe.askFor(sub, "李白字什").value
                    //获取输入框的文本
                    runOnUiThread {
                        if (content.isNotEmpty()) {
                            //将输入的消息及其类型添加进消息数据列表中
                            //清空输入框文本
                            if (answer.isEmpty())
                                msgList.add(Msg("不知道", Msg.LEFT))
                            else
                                msgList.add(Msg(answer, Msg.LEFT))
                            adapter.notifyItemInserted(msgList.size - 1)   //为RecyclerView添加末尾子项
                            recyclerView.scrollToPosition(msgList.size - 1) //跳转到当前位置
                        }
                    }
                }.start()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.subject_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun initMsg(){
        msgList.add(Msg("Hello",Msg.LEFT))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId ?: 0) {
            android.R.id.home -> finish()
            R.id.chinese -> {
                sub="chinese"
                experttitle.text="语文问答"
            }
            R.id.math -> {
                sub="math"
                experttitle.text="数学问答"
            }
            R.id.english -> {
                sub="english"
                experttitle.text="英语问答"
            }
            R.id.history -> {
                sub="history"
                experttitle.text="历史问答"
            }
            R.id.physics -> {
                sub="physics"
                experttitle.text="物理问答"
            }
            R.id.chemistry -> {
                sub="chemistry"
                experttitle.text="化学问答"
            }
            R.id.geo -> {
                sub="geo"
                experttitle.text="地理问答"
            }
            R.id.biology -> {
                sub="biology"
                experttitle.text="生物问答"
            }
            R.id.politics -> {
                sub = "politics"
                experttitle.text = "政治问答"
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
class MsgAdapter(val msgList:List<Msg>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //载入右聊天框布局控件
    inner class RightViewHolder(val view: View):RecyclerView.ViewHolder(view){
        val rightMsg: TextView = view.findViewById(R.id.rightText)
    }
    //载入左聊天框布局控件
    inner class LeftViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val leftMsg:TextView = view.findViewById(R.id.leftText)
    }
    //获取消息类型(左或者右),返回到onCreateViewHolder()方法的viewType参数里面
    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position] //根据当前数据源的元素类型
        return msg.type
    }
    //根据viewType消息类型的不同,构建不同的消息布局(左&右)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==Msg.LEFT){
            val leftView = LayoutInflater.from(parent.context).inflate(R.layout.msg_left_layout,parent,false)
            return LeftViewHolder(leftView) //返回控件+布局
        }else{
            val rightView = LayoutInflater.from(parent.context).inflate(R.layout.msg_right_layout,parent,false)
            return RightViewHolder(rightView)
        }

    }
    //对聊天控件的消息文本进行赋值
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when(holder){
            is LeftViewHolder -> holder.leftMsg.text = msg.content
            is RightViewHolder -> holder.rightMsg.text = msg.content
            else -> throw IllegalArgumentException()
        }
    }
    //返回项数
    override fun getItemCount(): Int {
        return msgList.size
    }
}
