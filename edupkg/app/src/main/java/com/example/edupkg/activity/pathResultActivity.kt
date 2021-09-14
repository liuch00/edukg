package com.example.edupkg.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edupkg.R
import com.example.edupkg.util.showToast
import java.util.*
import javax.security.auth.Subject
import kotlin.concurrent.thread


class pathResultActivity() : AppCompatActivity() {



    private var entityList= Vector<Entity>()
    lateinit var entityListBase: Vector<Vector<Entity>>
    private var path_num=0
    private var path_now=0
    private fun  refresh(){

        if(path_num==0)
            entityList= Vector<Entity>()
        else
            entityList= entityListBase[path_now]

        runOnUiThread {
            var entityRecyclerview: RecyclerView =findViewById(R.id.local_entity_recycler_view)
            entityRecyclerview.layoutManager = LinearLayoutManager(MyApplication.context)

            entityRecyclerview.adapter = EntityListAdapter(entityList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity_list)


        var path_search_sour= intent.getStringExtra("path_search_sour=")
        var path_search_des= intent.getStringExtra("path_search_des=")
        var path_search_sub= intent.getStringExtra("path_search_sub=")

        var fe = MyApplication.getFrontEnd()
        thread {
            //先取第一条

            entityListBase = fe.path(path_search_sour, path_search_des, path_search_sub)
            Log.d("pathAc",entityListBase.toString())
            path_num=entityListBase.size
            Log.d("path_num",path_num.toString())
            refresh()





        }




    }



    inner class EntityListAdapter(val entity_vector: Vector<Entity>): RecyclerView.Adapter<EntityListAdapter.EntityViewHolder>(){

        inner class EntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val label: TextView =itemView.findViewById(R.id.entityLabel)
            val category: TextView =itemView.findViewById(R.id.entityCategory)
            val course: TextView =itemView.findViewById(R.id.entityCourse)
            val button: Button =itemView.findViewById(R.id.entityButton)


        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
            val itemView=
                LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
            return EntityViewHolder(itemView )
        }

        override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
            val entity=entity_vector[position]
            holder.label.text=entity.label
            holder.category.text=entity.category
            holder.course.text=entity.course
            holder.button.setOnClickListener{
                "click_entity".showToast()
                val intent= Intent(MyApplication.context, entityActivity::class.java)

                intent.putExtra("label=",entity_vector[holder.adapterPosition].label)
                intent.putExtra("subject=",entity_vector[holder.adapterPosition].course)

                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return  entity_vector.size
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.guesslike_list, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId ?: 0) {
            android.R.id.home -> finish()
            R.id.front -> {
                if(path_now>0) {
                    path_now--
                    refresh()
                }
                else{
                    "已经是最前".showToast()
                }
            }
            R.id.next -> {
                if(path_now<(path_num-1)){
                    path_now++
                    refresh()
                }
                else{
                    "已经是最后".showToast()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }


}