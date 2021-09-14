
package com.example.edupkg.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class exploreSearchResultActivity() : AppCompatActivity() {


    private var entityList= Vector<Entity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity_list)


        var search_key= intent.getStringExtra("search_key_link=")
        var search_subject= intent.getStringExtra("search_subject_link=")
        var search_sort_method= intent.getStringExtra("search_sort_method_link=")


        Log.d("search_key_link",search_key.toString())
        Log.d("search_subject_link",search_subject.toString())
        Log.d("search_sort_method_link",search_sort_method.toString())

        var entityRecyclerview: RecyclerView =findViewById(R.id.local_entity_recycler_view)

        var fe = MyApplication.getFrontEnd()
        thread {
            entityList = fe.LinkKnowlege(search_key,search_subject)
            if (search_sort_method == "label")
                entityList.sortBy { it.label }
            else if (search_sort_method == "category")
                entityList.sortBy { it.category }
            else if (search_sort_method == "visited")
                entityList.sortBy { it.visited }
            //Log.d("localAc",entityList.toString())

            runOnUiThread {

                entityRecyclerview.layoutManager = LinearLayoutManager(MyApplication.context)

                entityRecyclerview.adapter = EntityListAdapter(entityList)

            }
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



}