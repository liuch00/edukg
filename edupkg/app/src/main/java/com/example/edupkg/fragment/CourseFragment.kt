package com.example.edupkg.data
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edupkg.data.MyApplication
import com.example.edupkg.R
import com.example.edupkg.util.showToast
import java.util.*
import kotlin.concurrent.thread

class CourseFragment(val courseType:String):Fragment() {

    lateinit var entityRecyclerview: RecyclerView
    private var entityList= Vector<Entity>()


    fun list_refresh_0(){


            var fe = MyApplication.getFrontEnd()
            entityList = fe.searchBySubject(courseType)
            Log.d("list:::",entityList.size.toString())

            activity?.runOnUiThread{
                entityRecyclerview.adapter=EntityListAdapter(entityList)

            }



    }

      fun list_refresh(){
        thread {

            var fe = MyApplication.getFrontEnd()
            entityList = fe.searchBySubject(courseType)
                Log.d("list:::",entityList.size.toString())

                activity?.runOnUiThread{
                    entityRecyclerview.adapter=EntityListAdapter(entityList)

            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("isHere::",courseType)
        list_refresh()
       val view =inflater.inflate(R.layout.fragment_course,container,false)
        entityRecyclerview=view.findViewById<RecyclerView>(R.id.entity_recycler_view)

        return  view
    }

    inner class EntityListAdapter(val entity_vector:Vector<Entity>):RecyclerView.Adapter<EntityListAdapter.EntityViewHolder>(){

        inner class EntityViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
            val label: TextView =itemView.findViewById(R.id.entityLabel)
            val category: TextView =itemView.findViewById(R.id.entityCategory)
            val course: TextView =itemView.findViewById(R.id.entityCourse)
            val button: Button =itemView.findViewById(R.id.entityButton)


        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        entityRecyclerview.layoutManager= LinearLayoutManager(MyApplication.context)
        entityRecyclerview.adapter=EntityListAdapter(entityList)
       // Log.d("CrAc:",entityRecyclerview.adapter.toString())
    }



}