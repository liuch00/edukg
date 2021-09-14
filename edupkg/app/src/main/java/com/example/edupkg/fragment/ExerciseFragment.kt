package com.example.edupkg.data
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.edupkg.R
import com.example.edupkg.util.showToast


class ExerciseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_exercise, container, false)


        val searchTextView=view.findViewById<TextView>(R.id.explore_search)
        val guessLikeTextView=view.findViewById<TextView>(R.id.explore_guess_like)
        val exerciseTextView=view.findViewById<TextView>(R.id.explore_exercise)
        val pathTextView=view.findViewById<TextView>(R.id.explore_path)
        val treeTextView=view.findViewById<TextView>(R.id.explore_tree)
        val talkTextView=view.findViewById<TextView>(R.id.explore_talk)
        val dataTextView=view.findViewById<TextView>(R.id.explore_data)

        searchTextView.setOnClickListener{
            "click_search".showToast()
            val intent= Intent(MyApplication.context, exploreSearchActivity::class.java)
            startActivity(intent)
        }
        guessLikeTextView.setOnClickListener{
            "click_guessLike".showToast()
            val intent= Intent(MyApplication.context, guessLikeActivity::class.java)
            startActivity(intent)
        }


        exerciseTextView.setOnClickListener{
            "click_exercise".showToast()
            val intent= Intent(MyApplication.context, exerciseActivity::class.java)
            startActivity(intent)
        }


        pathTextView.setOnClickListener{
            "click_path".showToast()
            val intent= Intent(MyApplication.context, pathActivity::class.java)
            startActivity(intent)
        }
        treeTextView.setOnClickListener{
            "click_tree".showToast()
//            val intent= Intent(MyApplication.context, exploreSearchActivity::class.java)
//            startActivity(intent)
        }
        talkTextView.setOnClickListener{
            "讨论区功能还未上线".showToast()
//            val intent= Intent(MyApplication.context, exploreSearchActivity::class.java)
//            startActivity(intent)
        }

        dataTextView.setOnClickListener{
            "click_data".showToast()
            val intent= Intent(MyApplication.context, dataActivity::class.java)
            startActivity(intent)
        }




        return view
    }


}