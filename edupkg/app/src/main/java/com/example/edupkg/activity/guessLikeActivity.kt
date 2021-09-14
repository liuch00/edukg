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
import kotlinx.android.synthetic.main.activity_expert.*
import java.util.*
import javax.security.auth.Subject
import kotlin.concurrent.thread


class guessLikeActivity() : AppCompatActivity() {


    private var recommend_subject:String="chinese"
    private var fe = MyApplication.getFrontEnd()

    private var entityList= Vector<Entity>()
    fun refresh(){
        thread {
            entityList = fe.getRecommendation(recommend_subject)
            runOnUiThread {
                var entityRecyclerview: RecyclerView =findViewById(R.id.local_entity_recycler_view)
                entityRecyclerview.layoutManager = LinearLayoutManager(MyApplication.context)
                entityRecyclerview.adapter = EntityListAdapter(entityList)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity_list)

        refresh()








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
        menuInflater.inflate(R.menu.subject_list, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId ?: 0) {
            android.R.id.home -> finish()
            R.id.chinese -> {
                recommend_subject="chinese"
                refresh()

            }
            R.id.math -> {
                recommend_subject="math"
                refresh()

            }
            R.id.english -> {
                recommend_subject="english"
                refresh()

            }
            R.id.history -> {
                recommend_subject="history"
                refresh()

            }
            R.id.physics -> {
                recommend_subject="physics"
                refresh()

            }
            R.id.chemistry -> {
                recommend_subject="chemistry"

                refresh()

            }
            R.id.geo -> {
                recommend_subject="geo"

                refresh()

            }
            R.id.biology -> {
                recommend_subject="biology"

                refresh()

            }
            R.id.politics -> {
                recommend_subject = "politics"
                refresh()

            }
        }
        return super.onOptionsItemSelected(item)
    }


}