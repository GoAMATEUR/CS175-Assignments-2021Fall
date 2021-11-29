package me.hsy.todolist

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.hsy.todolist.beans.Note
import me.hsy.todolist.beans.Priority
import me.hsy.todolist.beans.State
import me.hsy.todolist.databinding.ActivityMainBinding
import me.hsy.todolist.db.TodoContract
import me.hsy.todolist.db.TodoDbHelper
import me.hsy.todolist.ui.NoteListAdapter
import java.lang.String
import java.util.*

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var notesAdapter: NoteListAdapter? = null
    private var dbHelper: TodoDbHelper? = null
    private var database: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            startActivityForResult(
                Intent(this, NoteActivity::class.java),
                REQUEST_CODE_ADD
            )
        }

        dbHelper = TodoDbHelper(this.applicationContext)
        database = dbHelper!!.writableDatabase
        recyclerView = findViewById(R.id.list_todo)

        recyclerView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        notesAdapter = NoteListAdapter(object: NoteOperator{
            override fun deleteNote(note: Note?) {
                this@MainActivity.deleteNote(note)
            }

            override fun updateNote(note: Note?) {
                this@MainActivity.updateNote(note)
            }
        })
        recyclerView?.adapter = notesAdapter;
        notesAdapter?.refresh(loadNotesFromDatabase());
    }

    override fun onDestroy() {
        super.onDestroy()
        database?.close()
        database = null
        dbHelper?.close()
        dbHelper = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            // I abandoned this activity.
//            R.id.action_debug -> {
//                startActivity(Intent(this, DebugActivity::class.java))
//                return true
//            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            notesAdapter!!.refresh(loadNotesFromDatabase())
        }
    }
    @SuppressLint("Range")
    private fun loadNotesFromDatabase(): List<Note>? {
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
                TodoContract.TodoNote.COLUMN_PRIORITY + " DESC" // descend order by priority
            )
            while (cursor.moveToNext()) {
                // load note info
                val id: Long = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                val content: kotlin.String =
                    cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT))
                val dateMs: Long = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DATE))
                val intState: Int = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE))
                val intPriority: Int =
                    cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY))
                val note = Note(id)

                // add to the list
                note.content=(content)
                note.date=(Date(dateMs))
                note.state=(State.from(intState))
                note.priority=(Priority.from(intPriority))
                result.add(note)
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    private fun deleteNote(note: Note?) {
        if (database == null) {
            return
        }
        val rows: Int = database!!.delete(
            TodoContract.TodoNote.TABLE_NAME,
            TodoContract.TodoNote._ID + "=?", arrayOf(String.valueOf(note?.id))
        )
        if (rows > 0) {
            notesAdapter?.refresh(loadNotesFromDatabase())
        }
    }

    private fun updateNote(note: Note?) {
        // SWITCH THE STATE OF THE NOTE: DONE OR TODO
        if (database == null) {
            return
        }
        val values = ContentValues()
        values.put(TodoContract.TodoNote.COLUMN_STATE, note?.state?.intValue)
        val rows: Int = database!!.update(
            TodoContract.TodoNote.TABLE_NAME, values,
            TodoContract.TodoNote._ID + "=?", arrayOf(String.valueOf(note?.id))
        )
        if (rows > 0) {
            notesAdapter?.refresh(loadNotesFromDatabase())
        }
    }

    companion object{
        private val REQUEST_CODE_ADD = 1002
    }
}