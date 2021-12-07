package me.hsy.mycanvas.ui.curriculum

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.system.Os.open
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import me.hsy.mycanvas.CoursePage
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentCurriculumBinding
import me.hsy.mycanvas.ui.curriculum.beans.Curriculum
import me.hsy.mycanvas.ui.curriculum.beans.CurriculumJson
import java.io.InputStream


class CurriculumFragment : Fragment() {

    private lateinit var curriculumViewModel: CurriculumViewModel
    private var _binding: FragmentCurriculumBinding? = null
    private var scrollView: ScrollView? = null
    private var currentCourseList: LinearLayout? = null
    private var pastCourseList: LinearLayout? = null
    //private var courseList: Array<String>? = null

    private val binding get() = _binding!!

    private val gson = GsonBuilder().create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        curriculumViewModel =
            ViewModelProvider(this).get(CurriculumViewModel::class.java)

        _binding = FragmentCurriculumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scrollView = _binding!!.scrollView
        currentCourseList = scrollView?.findViewById(R.id.current_courses)
        pastCourseList = scrollView?.findViewById(R.id.past_courses)
        // read JSON
        val inputStream: InputStream? = activity?.assets?.open("courses.json") ?: null

        val bodyString: String? = inputStream?.bufferedReader().use{it?.readText()}
        if (inputStream != null) {
            Log.d("@=>", "Json loaded")
        }
        val curriculumBean = gson.fromJson(bodyString, CurriculumJson::class.java)
        val currentCurriculum: List<Curriculum> = curriculumBean.currentCurriculum
        val pastCurriculum: List<Curriculum> = curriculumBean.pastCurriculum

        for (i in currentCurriculum.indices) {
            // add course item to scroll view
            val intValue: Int = currentCurriculum[i].intValue
            val id: String = currentCurriculum[i].id
            val name: String = currentCurriculum[i].name
            val term: String = currentCurriculum[i].term
            val info: String = currentCurriculum[i].info
            val credit: Int = currentCurriculum[i].credit
            val theme: String = currentCurriculum[i].theme

            val newItem = layoutInflater.inflate(R.layout.item_course, null)
            newItem.findViewById<TextView>(R.id.id).text = id
            newItem.findViewById<TextView>(R.id.text_content).text = info
            newItem.findViewById<TextView>(R.id.term).text = term

            val courseName = newItem.findViewById<TextView>(R.id.course_name)
            courseName.text = name
            courseName.setTextColor(Color.parseColor(theme))
            newItem.findViewById<View>(R.id.theme_color).setBackgroundColor(Color.parseColor(theme))
            newItem.setOnClickListener {
                val intent = Intent(context, CoursePage::class.java)
                intent.putExtra("course_name", name)
                intent.putExtra("theme", theme)
                startActivity(intent)
            }
            currentCourseList?.addView(newItem)
        }

        for (i in pastCurriculum.indices) {
            // add course item to scroll view
            val intValue: Int = pastCurriculum[i].intValue
            val id: String = pastCurriculum[i].id
            val name: String = pastCurriculum[i].name
            val term: String = pastCurriculum[i].term
            val info: String = pastCurriculum[i].info
            val credit: Int = pastCurriculum[i].credit
            val theme: String = pastCurriculum[i].theme

            val newItem = layoutInflater.inflate(R.layout.item_course, null)
            newItem.findViewById<TextView>(R.id.id).text = id
            newItem.findViewById<TextView>(R.id.text_content).text = info
            newItem.findViewById<TextView>(R.id.term).text = term

            val courseName = newItem.findViewById<TextView>(R.id.course_name)
            courseName.text = name
            courseName.setTextColor(Color.parseColor(theme))
            newItem.findViewById<View>(R.id.theme_color).setBackgroundColor(Color.parseColor(theme))

            pastCourseList?.addView(newItem)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}