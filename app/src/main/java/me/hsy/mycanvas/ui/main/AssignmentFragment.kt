package me.hsy.mycanvas.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentAssignmentBinding
import me.hsy.mycanvas.ui.home.ui.HomeworkListAdapter

/**
 * A placeholder fragment containing a simple view.
 */
class AssignmentFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentAssignmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var homeworkAdapter: HomeworkListAdapter? = null

    //database


    // filter
    private var courseIntValue: Int = 0

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

        _binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        val root = binding.root
        courseIntValue = arguments?.getInt(ARG_COURSE_NUMBER) ?: 2


        recyclerView  = _binding?. listAssignment

        return root
    }

    companion object {
        private const val ARG_COURSE_NUMBER = "course_number"
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int, courseIntValue: Int): AssignmentFragment {
            return AssignmentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COURSE_NUMBER, courseIntValue)
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}