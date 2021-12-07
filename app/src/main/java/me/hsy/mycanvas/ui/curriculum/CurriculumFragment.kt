package me.hsy.mycanvas.ui.curriculum

import android.os.Bundle
import android.system.Os.open
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentCurriculumBinding
import me.hsy.mycanvas.ui.curriculum.beans.CurriculumJson
import java.io.InputStream


class CurriculumFragment : Fragment() {

    private lateinit var curriculumViewModel: CurriculumViewModel
    private var _binding: FragmentCurriculumBinding? = null
    private var scrollView: ScrollView? = null
    private var courseList: Array<String>? = null

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

        // read JSON
        val inputStream: InputStream? = activity?.assets?.open("courses.json") ?: null

        val bodyString: String? = inputStream?.bufferedReader().use{it?.readText()}
        if (inputStream != null) {
            Log.d("@=>", "Json loaded")
        }
        val curriculumBean = gson.fromJson(bodyString, CurriculumJson::class.java)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}