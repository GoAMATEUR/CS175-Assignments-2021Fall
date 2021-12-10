package me.hsy.mycanvas.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.selects.select
import me.hsy.mycanvas.NoteActivity
import me.hsy.mycanvas.R
import me.hsy.mycanvas.databinding.FragmentHomeBinding
import me.hsy.mycanvas.ui.home.beans.Classification
import me.hsy.mycanvas.ui.home.beans.Course
import me.hsy.mycanvas.ui.home.beans.Note
import me.hsy.mycanvas.ui.home.beans.State
import me.hsy.mycanvas.ui.home.db.TodoContract
import me.hsy.mycanvas.ui.home.db.TodoDbHelper
import me.hsy.mycanvas.ui.home.ui.HomeworkListAdapter
import me.hsy.mycanvas.ui.main.AssignmentActivity
import java.lang.String
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //recyclerView related
    private var recyclerView: RecyclerView? = null
    private var homeworkAdapter: HomeworkListAdapter? = null

    //database
    private var dbHelper: TodoDbHelper? = null
    private var database: SQLiteDatabase? = null

    private var addBtn: FloatingActionButton? = null
    private var noteList: List<Note>? =null
    private var selectClassId: Int = -1 // -1: All
    private var lastAssignment: Int? = null
    private var classSpinner: Spinner? = null

    private var classList: MutableList<kotlin.String> = arrayListOf("All", "Hw", "Todo")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // floating btn
        addBtn = _binding!!.addTodoBtn
        addBtn?.setOnClickListener{
            // Start event addition activity
            startActivityForResult(
                Intent(context, NoteActivity::class.java),
                REQUEST_CODE_ADD
            )
        }

        // class filter
        classSpinner = _binding!!.classSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, classList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner?.adapter = adapter

        classSpinner?.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when(position) {
                    0 -> {
                        selectClassId = -1
                    }
                    1 -> {
                        selectClassId = 2
                    }
                    2 ->{
                        selectClassId = 0
                    }
                }
                homeworkAdapter!!.refresh(loadNotesFromDatabase(selectClassId))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        recyclerView = _binding!!.listTodo
        dbHelper = TodoDbHelper(requireContext())
        database = dbHelper!!.writableDatabase

        recyclerView!!.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )



        // hide the floating btn when scrolling down
        recyclerView!!.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && _binding!!.addTodoBtn.visibility == View.VISIBLE) {
                    _binding!!.addTodoBtn.hide()
                }
                else if (dy < 0 && _binding!!.addTodoBtn.visibility != View.VISIBLE) {
                    _binding!!.addTodoBtn.show()
                }
            }
        })

        homeworkAdapter = HomeworkListAdapter(object: NoteOperator{
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
        homeworkAdapter?.refresh(loadNotesFromDatabase(selectClassId))
        return root
    }

    override fun onResume() {
        super.onResume()
        homeworkAdapter!!.refresh(loadNotesFromDatabase(selectClassId))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if successfully added, refresh the list
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            homeworkAdapter!!.refresh(loadNotesFromDatabase(selectClassId))
        }
        if (requestCode == REQUEST_CODE_SUBMIT && resultCode == RESULT_OK) {
            // user submit assignment in assignment activity, update state in dataset
            homeworkAdapter!!.refresh(loadNotesFromDatabase(selectClassId))
            noteList!![lastAssignment!!].state = State.DONE
            updateNoteFromDataBase(noteList!![lastAssignment!!])
        }
    }

    @SuppressLint("Range")
    private fun loadNotesFromDatabase(classId: Int?): List<Note>? {
        // load data from database
        if (database == null) {
            return Collections.emptyList()
        }
        val result: MutableList<Note> = LinkedList()
        var cursor: Cursor? = null
        var rowQuery: kotlin.String? = null
        when(classId) {
            Classification.Homework.intValue -> {
                rowQuery =
                    TodoContract.TodoNote.COLUMN_CLASSIFICATION + " = " + Classification.Homework.intValue.toString()
            }
            Classification.Todo.intValue -> {
                rowQuery =
                    TodoContract.TodoNote.COLUMN_CLASSIFICATION + " = " + Classification.Todo.intValue.toString()
            }
            -1 -> {
                rowQuery = null
            }
        }

        try {
            cursor = database!!.query(
                TodoContract.TodoNote.TABLE_NAME, null,
                rowQuery, null,
                null, null,
                TodoContract.TodoNote.COLUMN_DATE + " ASC"
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

        if (result.size <= 7 && addBtn?.visibility != View.VISIBLE) {
            addBtn!!.show()
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
            homeworkAdapter?.refresh(loadNotesFromDatabase(selectClassId))
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
            homeworkAdapter?.refresh(loadNotesFromDatabase(selectClassId))
        }
    }

    companion object{
        private const val REQUEST_CODE_ADD = 1002
        private const val REQUEST_CODE_SUBMIT = 1145
    }
}