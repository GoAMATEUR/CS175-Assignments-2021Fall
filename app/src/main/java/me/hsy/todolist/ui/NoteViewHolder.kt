package me.hsy.todolist.ui

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.hsy.todolist.NoteOperator
import me.hsy.todolist.R
import me.hsy.todolist.beans.Note
import me.hsy.todolist.beans.State
import java.text.SimpleDateFormat
import java.util.*


class NoteViewHolder(itemView: View, private val operator: NoteOperator): RecyclerView.ViewHolder(itemView){
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    private val contentText: TextView = itemView.findViewById(R.id.text_content)
    private val dateText: TextView = itemView.findViewById(R.id.text_date)
    private val deleteBtn: View = itemView.findViewById(R.id.btn_delete)

    fun bind(note: Note) {
        contentText.text = note.content
        dateText.text = SIMPLE_DATE_FORMAT.format(note.date)

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = (note.state == State.DONE)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            note.state=(if (isChecked) State.DONE else State.TODO)
            operator.updateNote(note)
        }
        deleteBtn.setOnClickListener{
            operator.deleteNote(note)
        }

        if (note.state === State.DONE) {
            // STRIKE THROUGH TEXT IF DONE
            contentText.setTextColor(Color.GRAY)
            contentText.paintFlags = contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            Log.d("@=>", contentText.paintFlags.toString())
        }
        else {
            contentText.setTextColor(Color.BLACK)
            contentText.paintFlags = contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            Log.d("@=>", contentText.paintFlags.toString())
        }

        // SET COLOR ACCORDING TO THE PRIORITY
        note.priority?.color?.let {
            itemView.setBackgroundColor(it)
        }
    }

    companion object{
        private val SIMPLE_DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
    }
}