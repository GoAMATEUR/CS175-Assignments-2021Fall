package me.hsy.mycanvas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatRadioButton
import me.hsy.mycanvas.ui.home.beans.Classification
import me.hsy.mycanvas.ui.home.beans.Course
import me.hsy.mycanvas.ui.home.beans.State
import me.hsy.mycanvas.ui.home.db.TodoContract
import me.hsy.mycanvas.ui.home.db.TodoDbHelper

class NoteActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var addBtn: Button? = null
    private var radioGroup: RadioGroup? = null // group of priority selection button
    private var todoRadio: AppCompatRadioButton? = null
    private var dbHelper: TodoDbHelper? = null
    private var database: SQLiteDatabase? = null
    private var courseSpinner: Spinner? = null
    private var courseList: List<String> = arrayListOf("Digital Image Process","Artificial Intelligence", "Computer Networks")

    private var selectedId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        title = "Create a new event."

        dbHelper = TodoDbHelper(this)
        database = dbHelper!!.writableDatabase

        editText = findViewById(R.id.edit_text)
        editText?.isFocusable = true
        editText?.requestFocus()
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager?.showSoftInput(editText, 0)

//        inputTag = findViewById(R.id.input_tag)
//        inputTag?.visibility = GONE

        radioGroup = findViewById(R.id.radio_group)
        todoRadio = findViewById(R.id.btn_todo)
        todoRadio?.isChecked = true

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.btn_homework) {
                courseSpinner?.visibility = VISIBLE
            } else {
                courseSpinner?.visibility = GONE
            }
        }

        courseSpinner = findViewById(R.id.course_spinner)
        courseSpinner?.visibility = GONE
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner?.adapter = adapter

        courseSpinner?.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        addBtn = findViewById(R.id.btn_add)
        addBtn?.setOnClickListener {
            val content: CharSequence = this.editText?.text.toString()

            if (TextUtils.isEmpty(content)) {
                Toast.makeText(
                    this@NoteActivity,
                    "No content to add", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            var courseId: Int = if (getSelectedType() == Classification.Homework) {
                selectedId
            } else {
                Course.CUSTOM.intValue
            }

            val succeed: Boolean = saveNote2Database(
                content.toString().trim { it <= ' ' },
                getSelectedType(),
                courseId
            )

            if (succeed) {
                Toast.makeText(
                    this@NoteActivity,
                    "Note added", Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_OK)
            }
            else {
                Toast.makeText(
                    this@NoteActivity,
                    "Error", Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }
    }

    private fun saveNote2Database(content: String, classification: Classification, intCourse: Int): Boolean {
        if (database == null || TextUtils.isEmpty(content)) {
            return false
        }
        val values = ContentValues()
        values.put(TodoContract.TodoNote.COLUMN_CONTENT, content)
        values.put(TodoContract.TodoNote.COLUMN_STATE, State.TODO.intValue)
        values.put(TodoContract.TodoNote.COLUMN_DATE, System.currentTimeMillis())
        values.put(TodoContract.TodoNote.COLUMN_CLASSIFICATION, classification.intValue)
        values.put(TodoContract.TodoNote.COLUMN_COURSE, intCourse)
        // save to TABLE_NAME
        val rowId = database!!.insert(TodoContract.TodoNote.TABLE_NAME, null, values)
        return rowId != -1L
    }

    private fun getSelectedType(): Classification {
        return when (radioGroup!!.checkedRadioButtonId) {
            (R.id.btn_todo) -> Classification.Todo
            (R.id.btn_homework) -> Classification.Homework
            else -> Classification.Notification
        }
    }

    private fun getSelectedCourse(tag: String): Int{
        return when (tag) {
            "CN" -> {
                Course.NETWORK.intValue
            }
            "AI" -> {
                Course.AI.intValue
            }
            "DIP" -> {
                Course.DIP.intValue
            }
            else -> {
                Course.CUSTOM.intValue
            }
        }
    }
}