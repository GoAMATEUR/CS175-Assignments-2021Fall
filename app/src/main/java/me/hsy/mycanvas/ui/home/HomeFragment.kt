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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // floating btn
        _binding!!.addTodoBtn.setOnClickListener{
            // Start event addition activity
            startActivityForResult(
                Intent(context, NoteActivity::class.java),
                REQUEST_CODE_ADD
            )
        }

        recyclerView = _binding!!.listTodo
        dbHelper = TodoDbHelper(requireContext())
        database = dbHelper!!.writableDatabase

        recyclerView!!.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        homeworkAdapter = HomeworkListAdapter(object: NoteOperator{
            override fun deleteNote(note: Note?) {
                deleteNoteFromDatabase(note)
            }

            override fun updateNote(note: Note?) {
                updateNoteFromDataBase(note)
            }

        })

        recyclerView?.adapter = homeworkAdapter
        homeworkAdapter?.refresh(loadNotesFromDatabase())


        return root
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
            homeworkAdapter!!.refresh(loadNotesFromDatabase())
        }
    }

    @SuppressLint("Range")
    private fun loadNotesFromDatabase(): List<Note>? {
        // load data from database
        if (database == null) {
            return Collections.emptyList()
        }
        val result: MutableList<Note> = LinkedList()
        var cursor: Cursor? = null
        try {
            cursor = database!!.query(
                TodoContract.TodoNote.TABLE_NAME, null,
                null, null,
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
                homework.date = Date(dateMs)
                homework.state = State.from(intState)
                result.add(homework)

            }
        } finally {
            cursor?.close()
        }
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
            homeworkAdapter?.refresh(loadNotesFromDatabase())
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
            homeworkAdapter?.refresh(loadNotesFromDatabase())
        }
    }

    companion object{
        private const val REQUEST_CODE_ADD = 1002
    }
}