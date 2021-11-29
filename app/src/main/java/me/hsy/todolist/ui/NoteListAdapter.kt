package me.hsy.todolist.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.hsy.todolist.NoteOperator
import me.hsy.todolist.R
import me.hsy.todolist.beans.Note

class NoteListAdapter(private val operator: NoteOperator): RecyclerView.Adapter<NoteViewHolder>() {
    private val notes: MutableList<Note> = ArrayList()
    fun refresh(newNotes: List<Note>?) {
        notes.clear()
        if (newNotes != null) {
            notes.addAll(newNotes)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NoteViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView, operator)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, pos: Int) {
        holder.bind(notes[pos])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}