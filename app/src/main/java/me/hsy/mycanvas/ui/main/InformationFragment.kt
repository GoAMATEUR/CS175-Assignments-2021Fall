package me.hsy.mycanvas.ui.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentInformationBinding
import me.hsy.mycanvas.databinding.WeekBarBinding
import me.hsy.mycanvas.ui.curriculum.beans.CurriculumJson
import me.hsy.mycanvas.ui.main.beans.Grading
import me.hsy.mycanvas.ui.main.beans.InfoBean
import me.hsy.mycanvas.ui.main.beans.Time
import me.hsy.mycanvas.ui.main.pie.PieBean
import me.hsy.mycanvas.ui.main.pie.PieChart
import java.io.InputStream

/**
 * A placeholder fragment containing a simple view.
 */
class InformationFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentInformationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val gson = GsonBuilder().create()
    private var pieChart: PieChart? = null
    private var itemList: MutableList<PieBean>? = null
    private var weekBar: WeekBarBinding? = null
    private var locationBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root = binding.root

        // load json
        val inputStream: InputStream? = activity?.assets?.open("networks_info.json") ?: null
        val bodyString: String? = inputStream?.bufferedReader().use{it?.readText()}
        if (inputStream != null) {
            Log.d("@=>", "Json loaded")
        }
        val infoBean = gson.fromJson(bodyString, InfoBean::class.java)

        // draw pie chart
        pieChart = _binding?.PieChart
        val gradings: List<Grading> = infoBean.grading
        itemList = ArrayList()
        for (i in gradings.indices) {
            val itemName = gradings[i].item
            val value = gradings[i].account.toFloat()
            val bean = PieBean()
            bean.item = itemName
            bean.value = value
            itemList?.add(bean)
        }
        pieChart?.setData(itemList!!)
        pieChart?.startAnimation(2000)



        // TODO: map widget
        locationBtn = _binding!!.location
        locationBtn!!.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("geo:27.00025,116.2365214?q=花果山")

            startActivity(intent)
        }

        // lecture time
        // week
        weekBar = _binding!!.weekBar
        val lTime: List<Time> = infoBean.time
        for (i in lTime.indices) {
            when (lTime[i].day) {
                1 -> {
                    weekBar?.day1!!.setBackgroundColor(Color.parseColor("#FF3700B3"))
                }
                2 -> {
                    weekBar?.day2!!.setBackgroundColor(Color.parseColor("#FF3700B3"))
                }
                3 -> {
                    weekBar?.day3!!.setBackgroundColor(Color.parseColor("#FF3700B3"))
                }
                4 -> {
                    weekBar?.day4!!.setBackgroundColor(Color.parseColor("#FF3700B3"))
                }
                5 -> {
                    weekBar?.day5!!.setBackgroundColor(Color.parseColor("#FF3700B3"))
                }
            }
        }

        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): InformationFragment {
            return InformationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // TODO: Map recycle
    }
}