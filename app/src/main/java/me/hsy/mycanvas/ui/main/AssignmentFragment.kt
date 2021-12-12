package me.hsy.mycanvas.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentAssignmentBinding
import me.hsy.mycanvas.ui.home.HomeFragment
import me.hsy.mycanvas.ui.home.NoteOperator
import me.hsy.mycanvas.ui.home.beans.Classification
import me.hsy.mycanvas.ui.home.beans.Course
import me.hsy.mycanvas.ui.home.beans.Note
import me.hsy.mycanvas.ui.home.beans.State
import me.hsy.mycanvas.ui.home.db.TodoContract
import me.hsy.mycanvas.ui.home.db.TodoDbHelper
import me.hsy.mycanvas.ui.home.ui.HomeworkListAdapter
import java.lang.String
import java.util.*

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
    private var dbHelper: TodoDbHelper? = null
    private var database: SQLiteDatabase? = null
    private var noteList: List<Note>? =null

    private var lastAssignment: Int? = null
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
        dbHelper = TodoDbHelper(requireContext())
        database = dbHelper!!.writableDatabase

        recyclerView!!.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        homeworkAdapter = HomeworkListAdapter(object: NoteOperator {
            override fun deleteNote(note: Note?) {
                deleteNoteFromDatabase(note)
            }

            override fun updateNote(note: Note?) {
                updateNoteFromDataBase(note)
            }

        })
        recyclerView?.adapter = homeworkAdapter
        homeworkAdapter!!.setItemClickListener(object: HomeworkListAdapter.ItemClickListener {
            override fun onItemClick(pos: Int) {
                if (noteList?.get(pos)!!.classification == Classification.Homework) {
                    val intent = Intent(requireContext(), AssignmentActivity::class.java)
                    val assignmentName =  noteList?.get(pos)?.content
                    intent.putExtra("assignmentName", assignmentName)
                    intent.putExtra("courseNameInt", noteList?.get(pos)!!.course!!.intValue)
                    intent.putExtra("submissionState", noteList?.get(pos)!!.state!!.intValue)
                    intent.putExtra("dueTime", noteList?.get(pos)!!.date!!)
                    Log.d("@=>", "$pos, $assignmentName ${noteList?.get(pos)!!.state!!.intValue}")
                    lastAssignment = pos
                    startActivityForResult(
                        intent,
                        REQUEST_CODE_SUBMIT
                    )
                }

            }
        })
        homeworkAdapter?.refresh(loadNotesFromDatabase(courseIntValue))
        return root
    }



    @SuppressLint("Range")
    private fun loadNotesFromDatabase(courseIntValue: Int?): List<Note>? {
        // load data from database
        if (database == null) {
            return Collections.emptyList()
        }
        val result: MutableList<Note> = LinkedList()
        var cursor: Cursor? = null
        var rowQuery: kotlin.String? = null

        rowQuery =
            TodoContract.TodoNote.COLUMN_COURSE + " = " + courseIntValue.toString()
        try {
            cursor = database!!.query(
                TodoContract.TodoNote.TABLE_NAME, null,
                rowQuery, null,
                null, null,
                TodoContract.TodoNote.COLUMN_STATE+", " + TodoContract.TodoNote.COLUMN_DATE + " ASC"
            )
            while (cursor.moveToNext()) {
                // load homework info
                val id: Long = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                val content: kotlin.String =
                    cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT))
                val dateMs: Long = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DATE))
                val intState: Int = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE))
                val intCourse: Int = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_COURSE))
                val intClassification: Int = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CLASSIFICATION))

                val homework = Note(id)
                homework.content = content
                homework.course = Course.from(intCourse)
                homework.classification = Classification.from(intClassification)
                homework.date = dateMs
                homework.state = State.from(intState)
                result.add(homework)

            }
        } finally {
            cursor?.close()
        }

        noteList = result
        return result
    }

    private fun deleteNoteFromDatabase(note: Note?) {
        if (database == null) {
            return
        }
        val rows: Int = database!!.delete(
            TodoContract.TodoNote.TABLE_NAME,
            TodoContract.TodoNote._ID + "=?", arrayOf(String.valueOf(note?.id))
        )
        if (rows > 0) {
            homeworkAdapter?.refresh(loadNotesFromDatabase(courseIntValue))
        }
    }

    private fun updateNoteFromDataBase(note: Note?) {
        // param: note after change of state
        if (database == null) {
            return
        }
        val values = ContentValues()
        values.put(TodoContract.TodoNote.COLUMN_STATE, note?.state?.intValue) // Change the value of state column
        val rows: Int = database!!.update(
            TodoContract.TodoNote.TABLE_NAME, values,
            TodoContract.TodoNote._ID + "=?", arrayOf(String.valueOf(note?.id))
        )
        if (rows > 0) {
            homeworkAdapter?.refresh(loadNotesFromDatabase(courseIntValue))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SUBMIT && resultCode == Activity.RESULT_OK) {
            // user submit assignment in assignment activity, update state in dataset
            homeworkAdapter!!.refresh(loadNotesFromDatabase(courseIntValue))
            noteList!![lastAssignment!!].state = State.DONE
            updateNoteFromDataBase(noteList!![lastAssignment!!])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // close the database
        database?.close()
        database = null
        dbHelper?.close()
        dbHelper = null
        _binding = null
    }

    companion object {
        private const val ARG_COURSE_NUMBER = "course_number"
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val REQUEST_CODE_SUBMIT = 1145
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
}