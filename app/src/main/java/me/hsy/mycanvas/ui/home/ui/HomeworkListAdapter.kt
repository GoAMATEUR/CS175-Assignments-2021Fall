package me.hsy.mycanvas.ui.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.hsy.mycanvas.R
import me.hsy.mycanvas.ui.home.beans.Note
import me.hsy.mycanvas.ui.home.NoteOperator


class HomeworkListAdapter(private val operator: NoteOperator): RecyclerView.Adapter<HomeworkViewHolder>() {
    private val notes: MutableList<Note> = ArrayList()
    fun refresh(newNotes: List<Note>?) {
        notes.clear()
        if (newNotes != null) {
            notes.addAll(newNotes)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): HomeworkViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_homework, parent, false)
        return HomeworkViewHolder(itemView, operator)
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, pos: Int) {
        holder.bind(notes[pos])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}