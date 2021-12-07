package me.hsy.mycanvas.ui.home

import me.hsy.mycanvas.ui.home.beans.Note


interface NoteOperator {
    fun deleteNote(note: Note?)
    fun updateNote(note: Note?)
}