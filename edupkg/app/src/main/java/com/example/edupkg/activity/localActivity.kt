package com.example.edupkg.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edupkg.R
import com.example.edupkg.util.showToast
import kotlinx.android.synthetic.main.activity_entity.*
import java.util.*
import kotlin.concurrent.thread


class localActivity : AppCompatActivity() {


    private var entityList= Vector<Entity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity_list)

        var entityRecyclerview: RecyclerView =findViewById(R.id.local_entity_recycler_view)

        var fe = MyApplication.getFrontEnd()
        //getcache 有问题
        entityList=fe.getCache()
        Log.d("localAc",entityList.toString())

        entityRecyclerview.layoutManager= LinearLayoutManager(MyApplication.context)

       entityRecyclerview.adapter=EntityListAdapter(entityList)



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





}