package me.hsy.todolist

import me.hsy.todolist.beans.Note

interface NoteOperator {
    fun deleteNote(note: Note?)
    fun updateNote(note: Note?)
}