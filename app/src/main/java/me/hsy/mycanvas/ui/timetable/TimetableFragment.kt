package me.hsy.mycanvas.ui.timetable

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.gson.GsonBuilder
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentTimetableBinding
import me.hsy.mycanvas.ui.curriculum.beans.Curriculum
import me.hsy.mycanvas.ui.curriculum.beans.CurriculumJson
import me.hsy.mycanvas.ui.curriculum.beans.LectureTime
import me.hsy.mycanvas.ui.main.pie.PieChart
import java.io.InputStream

class TimetableFragment : Fragment() {

    private lateinit var timetableViewModel: TimetableViewModel
    private var _binding: FragmentTimetableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var day1List: CoordinatorLayout? = null
    private var day2List: CoordinatorLayout? = null
    private var day3List: CoordinatorLayout? = null
    private var day4List: CoordinatorLayout? = null
    private var day5List: CoordinatorLayout? = null

    // load course info json
    private val gson = GsonBuilder().create()

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timetableViewModel = ViewModelProvider(this).get(TimetableViewModel::class.java)

        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        val root: View = binding.root
        day1List = _binding!!.day1
        day2List = _binding!!.day2
        day3List = _binding!!.day3
        day4List = _binding!!.day4
        day5List = _binding!!.day5

        val inputStream: InputStream? = activity?.assets?.open("courses.json") ?: null

        val bodyString: String? = inputStream?.bufferedReader().use{it?.readText()}
        if (inputStream != null) {
            Log.d("@=>", "Json loaded")
        }
        val curriculumBean = gson.fromJson(bodyString, CurriculumJson::class.java)
        val currentCurriculum: List<Curriculum> = curriculumBean.currentCurriculum

        for (i in currentCurriculum.indices) {
            val lectureTimeList: List<LectureTime> = currentCurriculum[i].lectureTime
            val name = currentCurriculum[i].name
            val info = currentCurriculum[i].info
            val credit: Int = currentCurriculum[i].credit
            val color = Color.parseColor(currentCurriculum[i].theme)






            for (i in lectureTimeList.indices) {
                val day = lectureTimeList[i].day
                val no = lectureTimeList[i].no
                Log.d("@=>", "$day, $name, $no")
                var itemView = layoutInflater.inflate(R.layout.item_timetable, null)
                itemView.findViewById<TextView>(R.id.course_name).text = name
                itemView.findViewById<TextView>(R.id.course_info).text = info
                itemView.setBackgroundColor(color)
                val height = if (no != 3) {
                    120f
                } else {
                    220f
                }
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PieChart.dp2px(requireContext(), height))
                params.setMargins(0,getMargin(no), 0, 0)
                itemView.layoutParams = params
                val parent: ViewGroup? = itemView.parent as ViewGroup?
                parent?.removeView(itemView)
                addToTimetable(day, itemView)
            }
        }

//        val textView: TextView = binding.textSlideshow
//        timetableViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    private fun addToTimetable(day: Int, view: View) {
        when(day) {
            1->day1List!!.addView(view)
            2->day2List!!.addView(view)
            3->day3List!!.addView(view)
            4->day4List!!.addView(view)
            5->day5List!!.addView(view)
        }
    }

    private fun getMargin(no: Int): Int {
        val dp = when(no) {
            1->3f
            2->126f
            3->249f
            4->349f
            5->472f
            else -> 605f
        }
        return PieChart.dp2px(requireContext(), dp)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}