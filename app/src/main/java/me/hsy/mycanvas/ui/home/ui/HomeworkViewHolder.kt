package me.hsy.mycanvas.ui.home.ui

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.hsy.mycanvas.R
import me.hsy.mycanvas.util.TimeParser
import me.hsy.mycanvas.ui.home.beans.Note
import me.hsy.mycanvas.ui.home.beans.State
import me.hsy.mycanvas.ui.home.NoteOperator
import me.hsy.mycanvas.ui.home.beans.Course


import java.text.SimpleDateFormat
import java.util.*

// View Holder of homework on the timelist
// TODO: 1. set content 2. set theme color 3. on task finished: change text 4. submission status

class HomeworkViewHolder(itemView: View, private val operator: NoteOperator): RecyclerView.ViewHolder(itemView){
    private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    private val contentText: TextView = itemView.findViewById(R.id.text_content)
    private val dateText: TextView = itemView.findViewById(R.id.text_date)
    private val deleteBtn: View = itemView.findViewById(R.id.btn_delete)

    private val courseName: TextView = itemView.findViewById(R.id.course_name)
    private val themeBar: View = itemView.findViewById(R.id.theme_bar)
    private val ptsText: TextView = itemView.findViewById(R.id.text_pts)
    private val submitStatus: TextView = itemView.findViewById(R.id.submit_status)

    fun bind(note: Note) {
        contentText.text = note.content // set content
        dateText.text = TimeParser.millTodate(note.date!!)

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = (note.state == State.DONE)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            note.state = if (isChecked) State.DONE else State.TODO
            operator.updateNote(note)
        }

        deleteBtn.setOnClickListener{
            operator.deleteNote(note)
        }

        if (note.state === State.DONE) {
            submitStatus.text = "SUBMITTED"
            contentText.setTextColor(Color.GRAY)
            contentText.paintFlags = contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            Log.d("@=>", contentText.paintFlags.toString())
        }
        else {
            submitStatus.text = "MISSING"
            contentText.setTextColor(Color.BLACK)
            contentText.paintFlags = contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            Log.d("@=>", contentText.paintFlags.toString())
        }

        when (note.course!!.intValue) {
            Course.NETWORK.intValue -> {
                setThemeColor("#FF6200EE")
                courseName.text = Course.NETWORK.courseName
                ptsText.text = "100pts"
                ptsText.visibility = VISIBLE
                submitStatus.visibility= VISIBLE
            }
            Course.AI.intValue -> {
                setThemeColor("#0DC341")
                courseName.text = Course.AI.courseName
                ptsText.text = "100pts"
                ptsText.visibility = VISIBLE
                submitStatus.visibility= VISIBLE
            }
            Course.DIP.intValue -> {
                setThemeColor("#038995")
                courseName.text = Course.DIP.courseName
                ptsText.text = "100pts"
                ptsText.visibility = VISIBLE
                submitStatus.visibility= VISIBLE
            }
            else -> {
                setThemeColor("#A30B0B")
                courseName.text = Course.CUSTOM.courseName
                submitStatus.visibility=INVISIBLE
                ptsText.visibility = INVISIBLE
            }
        }

    }

    private fun setThemeColor(color: String) {
        courseName.setTextColor(Color.parseColor(color))
        themeBar.setBackgroundColor(Color.parseColor(color))
    }

}